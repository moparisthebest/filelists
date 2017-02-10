package com.moparisthebest.filelist;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by mopar on 2/9/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("str " + new Date());
        //final List<Long> list = new RandomAccessFileList<>("/home/mopar/raf2.list", new UnsignedLongConverter(5));
        final List<Long> list = new RandomAccessFileList<>("/home/mopar/raf2.list", LongConverter40Bit.instance); list.clear();
        /*
        System.out.println(list.get(0));
        System.out.println(((RandomAccessFileList)list).longSize());
        System.out.println(((RandomAccessFileList)list).get(((RandomAccessFileList)list).longSize() - 1));
        */
        //list.clear();
        /*
        long max = Integer.MAX_VALUE + 1000L;
        for(long l = 0; l < max; ++l)
            list.add(l);
        System.out.println("yay " + new Date());
        for(long l : list)
            System.out.println(l);
        */
        //if(true) return;
        System.out.println(Integer.MAX_VALUE);
        System.out.println(list);
        list.add(5L);
        System.out.println(list);
        System.out.println(list.get(0));
        list.add(99999999999L);
        System.out.println(list.get(1));
        list.add(6L);
        list.add(4L);
        System.out.println(list);
        list.sort(Long::compareTo);
        System.out.println(list);
        System.out.println("------");
        //if(true) return;
        for(long l = 0; l < 1000; ++l)
        //for(long l = 7; l < 11; ++l)
            list.add(l);
        System.out.println(list);
        list.sort(Long::compareTo);
        list.sort(Comparator.reverseOrder());
        System.out.println(list);
    }
}
