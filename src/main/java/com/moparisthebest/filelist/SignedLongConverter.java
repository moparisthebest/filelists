package com.moparisthebest.filelist;

/**
 * Created by mopar on 2/9/17.
 */
public class SignedLongConverter implements ByteArrayConverter<Long> {

    private static final boolean debug = false;

    private final int numBytes;

    public SignedLongConverter(final int numBytes) {
        this.numBytes = numBytes;
    }

    @Override
    public int numBytes() {
        return this.numBytes;
    }

    @Override
    public Long fromBytes(final byte[] buffer) {
        /*
        if(true)
        return (((long)buffer[0] << 56) +
                ((long)(buffer[1] & 255) << 48) +
                ((long)(buffer[2] & 255) << 40) +
                ((long)(buffer[3] & 255) << 32) +
                ((long)(buffer[4] & 255) << 24) +
                ((buffer[5] & 255) << 16) +
                ((buffer[6] & 255) <<  8) +
                ((buffer[7] & 255) <<  0));
        */
        int y = 0, x = (buffer.length * 8) - 8;
        if(debug) System.out.printf("l = (long)(buffer[%d] << %d);%n", y, x);
        long l = (long)(buffer[y] << x);
        x = x - 8;
        if(x > 23)
            for (++y; x > 23; x = x - 8, ++y) {
                if(debug) System.out.printf("l += ((long)(buffer[%d] & 255) << %d);%n", y, x);
                l += ((long)(buffer[y] & 255) << x);
            }
        for (; y < buffer.length; x = x - 8, ++y) {
            if(debug) System.out.printf("l += (buffer[%d] & 255) << %d;%n", y, x);
            l += (buffer[y] & 255) << x;
        }
        return l;
    }

    @Override
    public void toBytes(final Long l, final byte[] buffer) {
        /*
        writeBuffer[0] = (byte)(v >>> 56);
        writeBuffer[1] = (byte)(v >>> 48);
        writeBuffer[2] = (byte)(v >>> 40);
        writeBuffer[3] = (byte)(v >>> 32);
        writeBuffer[4] = (byte)(v >>> 24);
        writeBuffer[5] = (byte)(v >>> 16);
        writeBuffer[6] = (byte)(v >>>  8);
        writeBuffer[7] = (byte)(v >>>  0);
        */
        for(int y = 0, x = (buffer.length * 8) - 8; x > -1; x = x - 8, ++y) {
            if(debug) System.out.printf("buffer[%d] = (byte) (l >>> %d);%n", y, x);
            buffer[y] = (byte) (l >>> x);
        }
    }

    public static void main(String[] args) {
        final ByteArrayConverter<Long> bac = new SignedLongConverter(5);
        final byte[] buf = new byte[3]; // 5 99999999999L
        //System.out.println(4294967296L); System.out.println(Integer.MAX_VALUE * 2L); if(true)return;
        //System.out.println(Long.MIN_VALUE + 500);
        //bac.toBytes(99999999999L, buf); System.out.println(java.util.Arrays.toString(buf)); System.out.println(bac.fromBytes(buf)); if(true) return;
        for(long l = 0, c = 0; ; ++l) {
            bac.toBytes(l, buf);
            c = bac.fromBytes(buf);
            if(l != c) {
                System.out.printf("limit for %d bytes l = %d c = %d%n", buf.length, l, c);
                return;
            }
            if(l == 99999999999L) {
                System.out.println("yay");
                return;
            }
        }
    }
}
