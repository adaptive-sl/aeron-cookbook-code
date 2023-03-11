/*
 * Copyright 2019-2023 Shaun Laurens.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aeroncookbook.cluster.rfq;

import com.aeroncookbook.cluster.rfq.gen.CreateRfqCommand;
import com.aeroncookbook.cluster.rfq.gen.QuoteRfqCommand;
import com.aeroncookbook.cluster.rfq.gen.RfqCreatedEvent;
import com.aeroncookbook.cluster.rfq.gen.RfqErrorEvent;
import com.aeroncookbook.cluster.rfq.gen.RfqExpiredEvent;
import com.aeroncookbook.cluster.rfq.gen.RfqQuotedEvent;
import com.aeroncookbook.cluster.rfq.instruments.Instruments;
import com.aeroncookbook.cluster.rfq.statemachine.Rfqs;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuoteRfqsTest
{
    private static final String CLORDID = "CLORDID";
    private static final String CUSIP = "CUSIP";

    @Test
    void shouldBeAbleToQuoteRfq()
    {
        final TestClusterProxy clusterProxy = new TestClusterProxy();
        final Rfqs undertest = new Rfqs(buildInstruments(), clusterProxy, 1, 200);

        final CreateRfqCommand createRfqCommand = new CreateRfqCommand();
        final DirectBuffer buffer = new ExpandableArrayBuffer(CreateRfqCommand.BUFFER_LENGTH);
        createRfqCommand.setBufferWriteHeader(buffer, 0);
        createRfqCommand.writeCorrelation(123);
        createRfqCommand.writeInstrumentId(1);
        createRfqCommand.writeUserId(1);
        createRfqCommand.writeExpireTimeMs(60_000);
        createRfqCommand.writeQuantity(200);
        createRfqCommand.writeSide((short)0);

        undertest.createRfq(createRfqCommand, 1L, 2L);

        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());

        final RfqCreatedEvent createdEvent = new RfqCreatedEvent();
        createdEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);

        assertEquals(1, createdEvent.readRfqId());

        final QuoteRfqCommand quoteRfqCommand = new QuoteRfqCommand();
        final DirectBuffer bufferQuote = new ExpandableArrayBuffer(QuoteRfqCommand.BUFFER_LENGTH);
        quoteRfqCommand.setBufferWriteHeader(bufferQuote, 0);
        quoteRfqCommand.writePrice(100);
        quoteRfqCommand.writeRfqId(createdEvent.readRfqId());
        quoteRfqCommand.writeResponderId(2);

        clusterProxy.clear();

        undertest.quoteRfq(quoteRfqCommand, 2L, 2L);
        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());
        final RfqQuotedEvent quotedEvent = new RfqQuotedEvent();
        quotedEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);
        assertEquals(100, quotedEvent.readPrice());
        assertEquals(createdEvent.readRfqId(), quotedEvent.readRfqId());
        assertEquals(1, quotedEvent.readRequesterUserId());
        assertEquals(2, quotedEvent.readResponderUserId());
    }

    @Test
    void shouldExpireIfNoActivityOnQuote()
    {
        final TestClusterProxy clusterProxy = new TestClusterProxy();
        final Rfqs undertest = new Rfqs(buildInstruments(), clusterProxy, 1, 200);

        final CreateRfqCommand createRfqCommand = new CreateRfqCommand();
        final DirectBuffer buffer = new ExpandableArrayBuffer(CreateRfqCommand.BUFFER_LENGTH);
        createRfqCommand.setBufferWriteHeader(buffer, 0);
        createRfqCommand.writeCorrelation(123);
        createRfqCommand.writeInstrumentId(1);
        createRfqCommand.writeUserId(1);
        createRfqCommand.writeExpireTimeMs(60_000);
        createRfqCommand.writeQuantity(200);
        createRfqCommand.writeSide((short)1);

        undertest.createRfq(createRfqCommand, 1, 2L);

        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());

        final RfqCreatedEvent rfqCreatedEvent = new RfqCreatedEvent();
        rfqCreatedEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);

        assertEquals(0, rfqCreatedEvent.readSide());
        assertEquals(60_000, rfqCreatedEvent.readExpireTimeMs());
        assertEquals(200, rfqCreatedEvent.readQuantity());
        assertEquals(1, rfqCreatedEvent.readRfqRequesterUserId());
        assertEquals(688, rfqCreatedEvent.readSecurityId());
        assertEquals(1, rfqCreatedEvent.readRfqId());

        clusterProxy.clear();

        final QuoteRfqCommand quoteRfqCommand = new QuoteRfqCommand();
        final DirectBuffer bufferQuote = new ExpandableArrayBuffer(QuoteRfqCommand.BUFFER_LENGTH);
        quoteRfqCommand.setBufferWriteHeader(bufferQuote, 0);
        quoteRfqCommand.writePrice(100);
        quoteRfqCommand.writeRfqId(rfqCreatedEvent.readRfqId());
        quoteRfqCommand.writeResponderId(2);

        clusterProxy.clear();

        undertest.quoteRfq(quoteRfqCommand, 2L, 2L);
        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());
        final RfqQuotedEvent quotedEvent = new RfqQuotedEvent();
        quotedEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);
        assertEquals(100, quotedEvent.readPrice());
        assertEquals(rfqCreatedEvent.readRfqId(), quotedEvent.readRfqId());
        assertEquals(1, quotedEvent.readRequesterUserId());
        assertEquals(2, quotedEvent.readResponderUserId());

        clusterProxy.clear();

        final List<TestClusterProxy.ExpiryTask> expiryTasks = clusterProxy.expiryTasksAt(202);
        assertEquals(1, expiryTasks.size());
        for (final TestClusterProxy.ExpiryTask task : expiryTasks)
        {
            undertest.expire(task.rfqId);
        }

        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());
        final RfqExpiredEvent rfqExpiredEvent = new RfqExpiredEvent();
        rfqExpiredEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);

        assertEquals(1, rfqExpiredEvent.readRfqId());
    }

    @Test
    void shouldNotBeAbleToQuoteRfqAfterAnotherReponderAlreadyHas()
    {
        final TestClusterProxy clusterProxy = new TestClusterProxy();
        final Rfqs undertest = new Rfqs(buildInstruments(), clusterProxy, 1, 200);

        final CreateRfqCommand createRfqCommand = new CreateRfqCommand();
        final DirectBuffer buffer = new ExpandableArrayBuffer(CreateRfqCommand.BUFFER_LENGTH);
        createRfqCommand.setBufferWriteHeader(buffer, 0);
        createRfqCommand.writeCorrelation(123);
        createRfqCommand.writeInstrumentId(1);
        createRfqCommand.writeUserId(1);
        createRfqCommand.writeExpireTimeMs(60_000);
        createRfqCommand.writeQuantity(200);
        createRfqCommand.writeSide((short)0);

        //user 1 creates RFQ
        undertest.createRfq(createRfqCommand, 1L, 2L);

        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());

        final RfqCreatedEvent createdEvent = new RfqCreatedEvent();
        createdEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);

        assertEquals(1, createdEvent.readRfqId());

        //user 2 quotes rfq
        final QuoteRfqCommand quoteRfqCommand = new QuoteRfqCommand();
        final DirectBuffer bufferQuote = new ExpandableArrayBuffer(QuoteRfqCommand.BUFFER_LENGTH);
        quoteRfqCommand.setBufferWriteHeader(bufferQuote, 0);
        quoteRfqCommand.writePrice(100);
        quoteRfqCommand.writeRfqId(createdEvent.readRfqId());
        quoteRfqCommand.writeResponderId(20);
        clusterProxy.clear();
        undertest.quoteRfq(quoteRfqCommand, 2L, 2L);

        clusterProxy.clear();

        //user 3 attempts to quote rfq
        quoteRfqCommand.writeResponderId(50);
        undertest.quoteRfq(quoteRfqCommand, 3L, 2L);

        assertEquals(1, clusterProxy.getReplies().size());
        assertEquals(0, clusterProxy.getBroadcasts().size());
        final RfqErrorEvent cancelEvent = new RfqErrorEvent();
        cancelEvent.setUnderlyingBuffer(clusterProxy.getReplies().get(0), 0);
        assertEquals("Cannot quote RFQ, RFQ already taken", cancelEvent.readError());
        assertEquals(createdEvent.readRfqId(), cancelEvent.readRfqId());
    }

    @Test
    void shouldNotBeAbleToUpdateQuote()
    {
        final TestClusterProxy clusterProxy = new TestClusterProxy();
        final Rfqs undertest = new Rfqs(buildInstruments(), clusterProxy, 1, 200);

        final CreateRfqCommand createRfqCommand = new CreateRfqCommand();
        final DirectBuffer buffer = new ExpandableArrayBuffer(CreateRfqCommand.BUFFER_LENGTH);
        createRfqCommand.setBufferWriteHeader(buffer, 0);
        createRfqCommand.writeCorrelation(123);
        createRfqCommand.writeInstrumentId(1);
        createRfqCommand.writeUserId(1);
        createRfqCommand.writeExpireTimeMs(60_000);
        createRfqCommand.writeQuantity(200);
        createRfqCommand.writeSide((short)0);

        //user 1 creates RFQ
        undertest.createRfq(createRfqCommand, 1L, 2L);

        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());

        final RfqCreatedEvent createdEvent = new RfqCreatedEvent();
        createdEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);

        assertEquals(1, createdEvent.readRfqId());

        //user 2 quotes rfq
        final QuoteRfqCommand quoteRfqCommand = new QuoteRfqCommand();
        final DirectBuffer bufferQuote = new ExpandableArrayBuffer(QuoteRfqCommand.BUFFER_LENGTH);
        quoteRfqCommand.setBufferWriteHeader(bufferQuote, 0);
        quoteRfqCommand.writePrice(100);
        quoteRfqCommand.writeRfqId(createdEvent.readRfqId());
        quoteRfqCommand.writeResponderId(20);
        clusterProxy.clear();
        undertest.quoteRfq(quoteRfqCommand, 2L, 2L);

        clusterProxy.clear();

        //user updates quote
        quoteRfqCommand.writePrice(50);
        undertest.quoteRfq(quoteRfqCommand, 3L, 2L);

        assertEquals(1, clusterProxy.getReplies().size());
        assertEquals(0, clusterProxy.getBroadcasts().size());
        final RfqErrorEvent cancelEvent = new RfqErrorEvent();
        cancelEvent.setUnderlyingBuffer(clusterProxy.getReplies().get(0), 0);
        assertEquals("RFQ not accepting quotes at this time", cancelEvent.readError());
        assertEquals(createdEvent.readRfqId(), cancelEvent.readRfqId());
    }

    @Test
    void shouldNotBeAbleToQuoteOwnRfq()
    {
        final TestClusterProxy clusterProxy = new TestClusterProxy();
        final Rfqs undertest = new Rfqs(buildInstruments(), clusterProxy, 1, 200);

        final CreateRfqCommand createRfqCommand = new CreateRfqCommand();
        final DirectBuffer buffer = new ExpandableArrayBuffer(CreateRfqCommand.BUFFER_LENGTH);
        createRfqCommand.setBufferWriteHeader(buffer, 0);
        createRfqCommand.writeCorrelation(123);
        createRfqCommand.writeInstrumentId(1);
        createRfqCommand.writeUserId(1);
        createRfqCommand.writeExpireTimeMs(60_000);
        createRfqCommand.writeQuantity(200);
        createRfqCommand.writeSide((short)0);

        undertest.createRfq(createRfqCommand, 1L, 2L);

        assertEquals(0, clusterProxy.getReplies().size());
        assertEquals(1, clusterProxy.getBroadcasts().size());

        final RfqCreatedEvent createdEvent = new RfqCreatedEvent();
        createdEvent.setUnderlyingBuffer(clusterProxy.getBroadcasts().get(0), 0);

        assertEquals(1, createdEvent.readRfqId());

        final QuoteRfqCommand quoteRfqCommand = new QuoteRfqCommand();
        final DirectBuffer bufferQuote = new ExpandableArrayBuffer(QuoteRfqCommand.BUFFER_LENGTH);
        quoteRfqCommand.setBufferWriteHeader(bufferQuote, 0);
        quoteRfqCommand.writePrice(100);
        quoteRfqCommand.writeRfqId(createdEvent.readRfqId());
        quoteRfqCommand.writeResponderId(1);

        clusterProxy.clear();

        undertest.quoteRfq(quoteRfqCommand, 2L, 2L);
        assertEquals(1, clusterProxy.getReplies().size());
        assertEquals(0, clusterProxy.getBroadcasts().size());
        final RfqErrorEvent cancelEvent = new RfqErrorEvent();
        cancelEvent.setUnderlyingBuffer(clusterProxy.getReplies().get(0), 0);
        assertEquals("RFQ not accepting quotes at this time", cancelEvent.readError());
        assertEquals(createdEvent.readRfqId(), cancelEvent.readRfqId());
    }

    @Test
    void shouldNotBeAbleToQuoteUnknownRfq()
    {
        final TestClusterProxy clusterProxy = new TestClusterProxy();
        final Rfqs undertest = new Rfqs(buildInstruments(), clusterProxy, 1, 200);

        final QuoteRfqCommand quoteRfqCommand = new QuoteRfqCommand();
        final DirectBuffer bufferQuote = new ExpandableArrayBuffer(QuoteRfqCommand.BUFFER_LENGTH);
        quoteRfqCommand.setBufferWriteHeader(bufferQuote, 0);
        quoteRfqCommand.writePrice(100);
        quoteRfqCommand.writeRfqId(32324);
        quoteRfqCommand.writeResponderId(1);

        clusterProxy.clear();

        undertest.quoteRfq(quoteRfqCommand, 2L, 2L);
        assertEquals(1, clusterProxy.getReplies().size());
        assertEquals(0, clusterProxy.getBroadcasts().size());
        final RfqErrorEvent cancelEvent = new RfqErrorEvent();
        cancelEvent.setUnderlyingBuffer(clusterProxy.getReplies().get(0), 0);
        assertEquals("Unknown RFQ", cancelEvent.readError());
        assertEquals(32324, cancelEvent.readRfqId());
    }


    Instruments buildInstruments()
    {
        final Instruments instruments = new Instruments();
        instruments.addInstrument(688, CUSIP, true, 100);
        instruments.addInstrument(789, "DISABLED", false, 100);
        return instruments;
    }

}
