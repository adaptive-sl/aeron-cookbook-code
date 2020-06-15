// AGRONA FLYWEIGHT GENERATED BY EIDER AT 2020-06-15T11:36:00.142928Z. SPEC: InstrumentRepositorySpec. DO NOT MODIFY
package com.aeroncookbook.cluster.rfq.instrument.gen;

import io.eider.util.IndexUpdateConsumer;
import java.lang.Integer;
import java.lang.String;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class Instrument {
  /**
   * The eider spec id for this type. Useful in switch statements to detect type in first 16bits.
   */
  public static final short EIDER_ID = 6001;

  /**
   * The eider group id for this type. Useful in switch statements to detect group in second 16bits.
   */
  public static final short EIDER_GROUP_ID = 3;

  /**
   * The offset for the EIDER_ID within the buffer.
   */
  private static final int HEADER_OFFSET = 0;

  /**
   * The offset for the EIDER_GROUP_IP within the buffer.
   */
  private static final int HEADER_GROUP_OFFSET = 2;

  /**
   * The length offset. Required for segmented buffers.
   */
  private static final int LENGTH_OFFSET = 4;

  /**
   * The byte offset in the byte array for this INT. Byte length is 4.
   */
  private static final int ID_OFFSET = 8;

  /**
   * The byte offset in the byte array for this INT. Byte length is 4.
   */
  private static final int SECURITYID_OFFSET = 12;

  /**
   * The byte offset in the byte array for this FIXED_STRING. Byte length is 9.
   */
  private static final int CUSIP_OFFSET = 16;

  /**
   * The byte offset in the byte array for this INT. Byte length is 4.
   */
  private static final int MINLEVEL_OFFSET = 25;

  /**
   * The total bytes required to store the object.
   */
  public static final int BUFFER_LENGTH = 29;

  /**
   * Indicates if this flyweight holds a fixed length object.
   */
  public static final boolean FIXED_LENGTH = true;

  /**
   * The internal DirectBuffer.
   */
  private DirectBuffer buffer = null;

  /**
   * The internal DirectBuffer used for mutatation opertions. Valid only if a mutable buffer was provided.
   */
  private MutableDirectBuffer mutableBuffer = null;

  /**
   * The internal UnsafeBuffer. Valid only if an unsafe buffer was provided.
   */
  private UnsafeBuffer unsafeBuffer = null;

  /**
   * The consumer notified of indexed field updates. Used to maintain indexes.
   */
  private IndexUpdateConsumer<Integer> indexUpdateNotifierSecurityId = null;

  /**
   * The consumer notified of indexed field updates. Used to maintain indexes.
   */
  private IndexUpdateConsumer<String> indexUpdateNotifierCusip = null;

  /**
   * The starting offset for reading and writing.
   */
  private int initialOffset;

  /**
   * Flag indicating if the buffer is mutable.
   */
  private boolean isMutable = false;

  /**
   * Flag indicating if the buffer is an UnsafeBuffer.
   */
  private boolean isUnsafe = false;

  /**
   * Internal field to support the lockKey method.
   */
  private boolean keyLocked = false;

  /**
   * Uses the provided {@link org.agrona.DirectBuffer} from the given offset.
   * @param buffer - buffer to read from and write to.
   * @param offset - offset to begin reading from/writing to in the buffer.
   */
  public void setUnderlyingBuffer(DirectBuffer buffer, int offset) {
    this.initialOffset = offset;
    this.buffer = buffer;
    if (buffer instanceof UnsafeBuffer) {
      unsafeBuffer = (UnsafeBuffer) buffer;
      mutableBuffer = (MutableDirectBuffer) buffer;
      isUnsafe = true;
      isMutable = true;
    }
    else if (buffer instanceof MutableDirectBuffer) {
      mutableBuffer = (MutableDirectBuffer) buffer;
      isUnsafe = false;
      isMutable = true;
    }
    else {
      isUnsafe = false;
      isMutable = false;
    }
    keyLocked = false;
    buffer.checkLimit(initialOffset + BUFFER_LENGTH);
  }

  /**
   * Uses the provided {@link org.agrona.DirectBuffer} from the given offset.
   * @param buffer - buffer to read from and write to.
   * @param offset - offset to begin reading from/writing to in the buffer.
   */
  public void setBufferWriteHeader(DirectBuffer buffer, int offset) {
    setUnderlyingBuffer(buffer, offset);
    writeHeader();
  }

  /**
   * Returns the eider sequence.
   * @return EIDER_ID.
   */
  public short eiderId() {
    return EIDER_ID;
  }

  /**
   * Writes the header data to the buffer.
   */
  public void writeHeader() {
    if (!isMutable) throw new RuntimeException("cannot write to immutable buffer");
    mutableBuffer.putShort(initialOffset + HEADER_OFFSET, EIDER_ID, java.nio.ByteOrder.LITTLE_ENDIAN);
    mutableBuffer.putShort(initialOffset + HEADER_GROUP_OFFSET, EIDER_GROUP_ID, java.nio.ByteOrder.LITTLE_ENDIAN);
    mutableBuffer.putInt(initialOffset + LENGTH_OFFSET, BUFFER_LENGTH, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Validates the length and eiderSpecId in the header against the expected values. False if invalid.
   */
  public boolean validateHeader() {
    final short eiderId = buffer.getShort(initialOffset + HEADER_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
    final short eiderGroupId = buffer.getShort(initialOffset + HEADER_GROUP_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
    final int bufferLength = buffer.getInt(initialOffset + LENGTH_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
    if (eiderId != EIDER_ID) return false;
    if (eiderGroupId != EIDER_GROUP_ID) return false;
    return bufferLength == BUFFER_LENGTH;
  }

  /**
   * Sets the indexed field update notifier to provided consumer.
   */
  public void setIndexNotifierForSecurityId(IndexUpdateConsumer<Integer> indexedNotifier) {
    this.indexUpdateNotifierSecurityId = indexedNotifier;
  }

  /**
   * Sets the indexed field update notifier to provided consumer.
   */
  public void setIndexNotifierForCusip(IndexUpdateConsumer<String> indexedNotifier) {
    this.indexUpdateNotifierCusip = indexedNotifier;
  }

  /**
   * Reads id as stored in the buffer.
   */
  public int readId() {
    return buffer.getInt(initialOffset + ID_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes id to the buffer. This field is marked key=true.
   * @param value Value for the id to write to buffer.
   */
  public void writeId(int value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (keyLocked) throw new RuntimeException("Cannot write key after locking");
    mutableBuffer.putInt(initialOffset + ID_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Prevents any further updates to the key field.
   */
  public void lockKeyId() {
    keyLocked = true;
  }

  /**
   * Reads securityId as stored in the buffer.
   */
  public int readSecurityId() {
    return buffer.getInt(initialOffset + SECURITYID_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes securityId to the buffer. Indexed field.
   * @param value Value for the securityId to write to buffer.
   */
  public void writeSecurityId(int value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (indexUpdateNotifierSecurityId != null) {
      indexUpdateNotifierSecurityId.accept(initialOffset, value);
    }
    mutableBuffer.putInt(initialOffset + SECURITYID_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Reads cusip as stored in the buffer.
   */
  public String readCusip() {
    return buffer.getStringWithoutLengthAscii(initialOffset + CUSIP_OFFSET, 9).trim();
  }

  /**
   * Writes cusip to the buffer. Indexed field.Warning! Does not pad the string.
   * @param value Value for the cusip to write to buffer.
   */
  public void writeCusip(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (indexUpdateNotifierCusip != null) {
      indexUpdateNotifierCusip.accept(initialOffset, value);
    }
    if (value.length() > 9) throw new RuntimeException("Field cusip is longer than maxLength=9");
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + CUSIP_OFFSET, value);
  }

  /**
   * Writes cusip to the buffer with padding. 
   * @param value Value for the cusip to write to buffer.
   */
  public void writeCusipWithPadding(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 9) throw new RuntimeException("Field cusip is longer than maxLength=9");
    final String padded = String.format("%9s", value);
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + CUSIP_OFFSET, padded);
  }

  /**
   * Reads minLevel as stored in the buffer.
   */
  public int readMinLevel() {
    return buffer.getInt(initialOffset + MINLEVEL_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes minLevel to the buffer. 
   * @param value Value for the minLevel to write to buffer.
   */
  public void writeMinLevel(int value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    mutableBuffer.putInt(initialOffset + MINLEVEL_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * True if transactions are supported; false if not.
   */
  public boolean supportsTransactions() {
    return false;
  }
}
