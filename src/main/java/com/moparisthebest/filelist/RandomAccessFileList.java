package com.moparisthebest.filelist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractList;
import java.util.Objects;

/**
 * Created by mopar on 2/9/17.
 */
public class RandomAccessFileList<T> extends AbstractList<T> {

    private final RandomAccessFile raf;
    private final byte[] buffer;
    private final ByteArrayConverter<T> bac;

    public RandomAccessFileList(final RandomAccessFile raf, final ByteArrayConverter<T> bac) {
        Objects.requireNonNull(raf);
        Objects.requireNonNull(bac);
        if(bac.numBytes() < 1)
            throw new IllegalArgumentException("bytesPerEntry must be > 0");
        this.raf = raf;
        this.buffer = new byte[bac.numBytes()];
        this.bac = bac;
    }

    public RandomAccessFileList(final String name, final ByteArrayConverter<T> bac) throws FileNotFoundException {
        this(new RandomAccessFile(name, "rw"), bac);
    }

    public RandomAccessFileList(final File file, final ByteArrayConverter<T> bac) throws FileNotFoundException {
        this(new RandomAccessFile(file, "rw"), bac);
    }

    public T get(final int index) {
        try {
            return this.get((long) index);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T get(final long index) throws IOException {
        raf.seek(index * buffer.length);
        if(raf.read(buffer) != buffer.length)
            throw new IOException("no full buffer to read, corrupted file?");
        return bac.fromBytes(buffer);
    }

    public int size() {
        try {
            return (int) this.longSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long longSize() throws IOException {
        return raf.length() / buffer.length;
    }

    @Override
    public boolean add(final T o) {
        try {
            raf.seek(raf.length());
            bac.toBytes(o, buffer);
            raf.write(buffer);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T set(final int index, final T o) {
        try {
            return this.set((long)index, o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T set(final long index, final T o) throws IOException {
        final T ret = get(index);
        raf.seek(index * buffer.length);
        bac.toBytes(o, buffer);
        raf.write(buffer);
        return ret;
    }

    @Override
    public void clear() {
        try {
            raf.setLength(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
