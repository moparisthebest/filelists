package com.moparisthebest.filelist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

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
        if (bac.numBytes() < 1)
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
        if (raf.read(buffer) != buffer.length)
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
            return this.set((long) index, o);
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

    @Override
    public void sort(final Comparator<? super T> c) {
        try {
            //this.selectionSort(c);
            //this.inPlaceMergeSort(c);
            this.heapSort(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectionSort(final Comparator<? super T> c) throws IOException {
        // a[0] to a[n-1] is the array to sort
        final long n = this.longSize(), n1 = n - 1;
        final byte[] tmp = new byte[this.buffer.length];

        // advance the position through the entire array
        //   (could do j < n-1 because single element is also min element)
        for (long j = 0; j < n1; ++j) {
            /* find the min element in the unsorted a[j .. n-1] */

            /* assume the min is the first element */
            long iMin = j;
            /* test against elements after j to find the smallest */
            for (long i = j + 1; i < n; ++i) {
                // if this element is less, then it is the new minimum
                // this order is important, we want get(iMin) done last so it's already in buffer below if we need to swap!
                // in all cases except when iMin is last, in which case we have to seek back and read it, this is less common though usually
                final T iObj = this.get(i);
                if (c.compare(iObj, this.get(iMin)) <= 0){
                    // found new minimum; remember its index
                    iMin = i;
                }
            }

            if (iMin != j) {
                //swap(a[j], a[iMin]);

                // buffer already has iMin value in it unless iMin was last value
                if (iMin == n1) {
                    // grab iMin to buffer
                    raf.seek(iMin * buffer.length);
                    if (raf.read(buffer) != buffer.length)
                        throw new IOException("no full buffer to read, corrupted file?");
                }

                // grab j to tmp
                raf.seek(j * buffer.length);
                if (raf.read(tmp) != buffer.length)
                    throw new IOException("no full buffer to read, corrupted file?");

                //System.out.printf("iMin: %d j: %d buffer: %d tmp: %d%n", iMin, j, bac.fromBytes(buffer), bac.fromBytes(tmp));

                // write buffer to j
                raf.seek(j * buffer.length);
                raf.write(buffer);

                // write tmp to iMin
                raf.seek(iMin * buffer.length);
                raf.write(tmp);
            }
        }
    }

    /**
     * In-Place Merge Sort.
     * <p>
     * Building on the algorithm core found in
     * http://www.cs.ubc.ca/~harrison/Java/MergeSortAlgorithm.java.html
     * http://penguin.ewu.edu/cscd300/Topic/AdvSorting/MergeSorts/InPlace.html
     * http://penguin.ewu.edu/cscd300/Topic/AdvSorting/MergeSorts/MergeSort.java
     */
    public void inPlaceMergeSort(final Comparator<? super T> c) throws IOException {
        this.inPlaceMergeSort(c, 0, this.longSize() - 1);
    }

    public void inPlaceMergeSort(final Comparator<? super T> c, final long first, final long last) throws IOException {
        long mid, lt, rt;

        if (first >= last) return;

        mid = (first + last) / 2;

        inPlaceMergeSort(c, first, mid);
        inPlaceMergeSort(c, mid + 1, last);

        lt = first;
        rt = mid + 1;
        // One extra check:  can we SKIP the merge?
        if (c.compare(this.get(mid), this.get(rt)) <= 0)
            return;

        byte[] tmp = new byte[buffer.length], tmp2 = new byte[buffer.length], tmp3;

        while (lt <= mid && rt <= last) {
            // Select from left:  no change, just advance lt
            if (c.compare(this.get(lt), this.get(rt)) <= 0)
                ++lt;
                // Select from right:  rotate [lt..rt] and correct
            else {
                // buffer contains rt here due to this.get(rt) being called last above, will move to [lt]

                // scoot everything else over one todo: can do this in bigger chunks than buffer.length to speed things up...
                //System.out.printf("lt: %d, rt: %d, rt-lt: %d, buffer: %d%n", lt, rt, rt-lt, bac.fromBytes(buffer));
                raf.seek(lt * buffer.length);
                raf.read(tmp);
                for(long dst = lt + 1; dst <= lt + (rt - lt); ++dst){
                    //raf.seek(dst * buffer.length);
                    raf.read(tmp2);
                    raf.seek(dst * buffer.length);
                    raf.write(tmp);
                    tmp3 = tmp2;
                    tmp2 = tmp;
                    tmp = tmp3;
                }

                // write buffer to lt
                raf.seek(lt * buffer.length);
                raf.write(buffer);

                // EVERYTHING has moved up by one
                lt++;
                mid++;
                rt++;
            }
        }
        // Whatever remains in [rt..last] is in place
    }

    public void heapSort(final Comparator<? super T> c) throws IOException {
        long n = this.longSize();
        final byte[] tmp = new byte[this.buffer.length];

        for (long k = n/2; k > 0; k--) {
            downheap(c, tmp, k, n);
        }
        do {
            // swap 0 and n - 1
            n = n - 1;

            // grab 0 to tmp
            raf.seek(0);
            if (raf.read(tmp) != buffer.length)
                throw new IOException("no full buffer to read, corrupted file?");

            // grab n - 1 to buffer
            raf.seek(n * buffer.length);
            if (raf.read(buffer) != buffer.length)
                throw new IOException("no full buffer to read, corrupted file?");

            // write tmp to n - 1
            raf.seek(n * buffer.length);
            raf.write(tmp);

            // write buffer to 0
            raf.seek(0);
            raf.write(buffer);


            downheap(c, tmp, 1, n);
        } while (n > 1);
    }

    private void downheap(final Comparator<? super T> c, final byte[] tmp, long k, final long n) throws IOException {
        final T t = this.get(k - 1);
        // save k - 1 to tmp, already in buffer
        System.arraycopy(buffer, 0, tmp, 0, buffer.length);

        while (k <= n/2) {
            long j = k + k;
            if ((j < n) && (c.compare(this.get(j - 1), this.get(j)) < 0)) {
                ++j;
            }
            if (c.compare(t, this.get(j - 1)) >= 0) {
                break;
            } else {
                // write j - 1 to k - 1, already in buffer from last this.get(j - 1) above
                raf.seek((k - 1) * buffer.length);
                raf.write(buffer);
                k = j;
            }
        }
        // write tmp to k - 1
        raf.seek((k - 1) * buffer.length);
        raf.write(tmp);
    }
}
