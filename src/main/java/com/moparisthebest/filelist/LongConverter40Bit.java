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
    public Long fromBytes(final byte[] buffer, int off) {
        off += 5;
        return (((long) buffer[--off] & 0xFFL) << 32)
             | (((long) buffer[--off] & 0xFFL) << 24)
             | (((long) buffer[--off] & 0xFFL) << 16)
             | (((long) buffer[--off] & 0xFFL) << 8)
             | ((long) buffer[--off] & 0xFFL);
    }

    @Override
    public void toBytes(final Long l, final byte[] buffer, int off) {
        --off;
        buffer[++off] = (byte) ((l) & 0xFF);
        buffer[++off] = (byte) ((l >> 8) & 0xFF);
        buffer[++off] = (byte) ((l >> 16) & 0xFF);
        buffer[++off] = (byte) ((l >> 24) & 0xFF);
        buffer[++off] = (byte) ((l >> 32) & 0xFF);
    }
}
