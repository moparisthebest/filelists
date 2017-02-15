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
    public Long fromBytes(final byte[] buffer, final int off) {
        long l = 0;
        for(int x = this.numBytes - 1, y = (this.numBytes * 8) - 8; x >= off; --x, y = y - 8) {
            l |= (((long) buffer[x] & 0xFFL) << y);
        }
        return l;
    }

    @Override
    public void toBytes(final Long l, final byte[] buffer, final int off) {
        for(int x = off, y = 0; x < this.numBytes; ++x, y = y + 8) {
            buffer[x] = (byte) ((l >> y) & 0xFF);
        }
    }
}
