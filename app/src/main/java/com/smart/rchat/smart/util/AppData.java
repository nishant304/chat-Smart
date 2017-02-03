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

    public static AppData getInstance() {
        return ourInstance;
    }

    private AppData() {
        lruCache = new LruCache<String, Bitmap>(50);
    }


}
