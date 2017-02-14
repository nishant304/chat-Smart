package com.smart.rchat.smart.util;

import android.graphics.Bitmap;

import com.bumptech.glide.util.LruCache;

/**
 * Created by nishant on 03.02.17.
 */
public class AppData {
    private static AppData ourInstance = new AppData();

    public LruCache<String, Bitmap> getLruCache() {
        return lruCache;
    }

    private LruCache<String,Bitmap> lruCache;

    private  Object dumpObject ;

    private boolean isSafeToDump = true;

    public static AppData getInstance() {
        return ourInstance;
    }

    private AppData() {
        lruCache = new LruCache<String, Bitmap>(50);
    }

    public synchronized void dumpObject(Object o){
        if(!isSafeToDump){
            throw new IllegalStateException("previous dump not cleaned");
        }
        this.isSafeToDump = false;
        this.dumpObject = o;
    }

    public synchronized Object getDumpObject() {
        this.isSafeToDump = true;
        return dumpObject;
    }

}
