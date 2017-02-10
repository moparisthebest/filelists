package com.moparisthebest.filelist;

/**
 * Created by mopar on 2/9/17.
 */
public class UnsignedLongConverter implements ByteArrayConverter<Long> {

    private final int numBytes;

    public UnsignedLongConverter(final int numBytes) {
        this.numBytes = numBytes;
    }

    @Override
    public int numBytes() {
        return this.numBytes;
    }

    @Override
    public Long fromBytes(final byte[] buffer) {
        long l = 0;
        for(int x = buffer.length - 1, y = (buffer.length * 8) - 8; x >= 0; --x, y = y - 8) {
            l |= (((long) buffer[x] & 0xFFL) << y);
        }
        return l;
    }

    @Override
    public void toBytes(final Long l, final byte[] buffer) {
        for(int x = 0, y = 0; x < buffer.length; ++x, y = y + 8) {
            buffer[x] = (byte) ((l >> y) & 0xFF);
        }
    }
}
