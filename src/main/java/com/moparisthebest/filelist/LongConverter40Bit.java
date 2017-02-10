package com.moparisthebest.filelist;

/**
 * Created by mopar on 2/9/17.
 */
public class LongConverter40Bit implements ByteArrayConverter<Long> {

    public static final ByteArrayConverter<Long> instance = new LongConverter40Bit();

    private LongConverter40Bit() {
    }

    @Override
    public int numBytes() {
        return 5;
    }

    @Override
    public Long fromBytes(final byte[] buffer) {
        return (((long) buffer[4] & 0xFFL) << 32)
             | (((long) buffer[3] & 0xFFL) << 24)
             | (((long) buffer[2] & 0xFFL) << 16)
             | (((long) buffer[1] & 0xFFL) << 8)
             | ((long) buffer[0] & 0xFFL);
    }

    @Override
    public void toBytes(final Long l, final byte[] buffer) {
        buffer[0] = (byte) ((l) & 0xFF);
        buffer[1] = (byte) ((l >> 8) & 0xFF);
        buffer[2] = (byte) ((l >> 16) & 0xFF);
        buffer[3] = (byte) ((l >> 24) & 0xFF);
        buffer[4] = (byte) ((l >> 32) & 0xFF);
    }
}
