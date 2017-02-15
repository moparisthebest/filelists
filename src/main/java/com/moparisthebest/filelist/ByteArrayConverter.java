package com.moparisthebest.filelist;

/**
 * Created by mopar on 2/9/17.
 */
public interface ByteArrayConverter<T> {
    int numBytes();
    T fromBytes(final byte[] buff, final int off);
    void toBytes(final T o, final byte[] buff, final int off);
}
