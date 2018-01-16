package com.example.instant_deliver.tools;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by King on 2017/5/30.
 */

public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> mCache;

    /**
     * 初始化BitampCache缓存类.
     * 默认使用当前进程内存空间的1/8
     */
    public BitmapCache() {
        this((int) (Runtime.getRuntime().maxMemory() / 8));
    }

    /**
     * 初始化BitampCache缓存类.
     *
     * @param maxSize 缓存类默认存储大小,单位为字节
     */
    public BitmapCache(int maxSize) {
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        return mCache.get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        mCache.put(s, bitmap);
    }
}