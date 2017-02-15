package com.moparisthebest.filelist;

/**
 * Created by mopar on 2/10/17.
 */
@FunctionalInterface
public interface TransformComparator<K, T> {

    int compareTransform(K o1, T o2);

}
