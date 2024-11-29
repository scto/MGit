package com.xinglan.mgit.common;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class OperatorOverloads {

    // Kotlin operator overloading for get with a String path
    public static DirCacheEntry getEntry(DirCache dirCache, String path) {
        return dirCache.getEntry(path);
    }

    // Kotlin operator overloading for get with an integer index
    public static DirCacheEntry getEntry(DirCache dirCache, int index) {
        return dirCache.getEntry(index);
    }
}
