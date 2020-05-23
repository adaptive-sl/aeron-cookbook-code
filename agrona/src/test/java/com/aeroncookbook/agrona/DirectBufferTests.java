/*
 *
 *  * Copyright 2019-2020 eleventy7
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.aeroncookbook.agrona;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectBufferTests
{
    @Test
    public void unsafeLongExtras()
    {
        //allocate a buffer to store the long
        final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(8));

        //place 41 at index 0
        unsafeBuffer.putLong(0, 41);
        //add 1 to the long at index 0 and return the old value
        long originalValue = unsafeBuffer.getAndAddLong(0, 1);
        //read the value of the long at index 0
        long plus1 = unsafeBuffer.getLong(0);
        assertEquals(41, originalValue);
        assertEquals(42, plus1);

        //read current value while writing a new value
        long oldValue = unsafeBuffer.getAndSetLong(0, 43);
        //read the value of the long at index 0
        long newValue = unsafeBuffer.getLong(0);
        assertEquals(42, oldValue);
        assertEquals(43, newValue);

        //check the value was what was expected, returning true/false if it was. Then update the value a new value
        boolean wasExpected = unsafeBuffer.compareAndSetLong(0, 43, 44);
        //read the value of the long at index 0
        long updatedValue = unsafeBuffer.getLong(0);

        assertTrue(wasExpected);
        assertEquals(44, updatedValue);
    }
}
