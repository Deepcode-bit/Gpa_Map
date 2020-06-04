package com.nepu.gpa_map.maps;

import android.os.Environment;

import java.io.File;

public class MapImageCache {//extends WeakHashMap<String, Bitmap> {
    private static MapImageCache mNetImageViewCache = new MapImageCache();
    private static final String CACHE_FILE = "/amapdemo/Cache";

    private MapImageCache() {

    }

    public static MapImageCache getInstance() {
        return mNetImageViewCache;
    }
    /**
     * 判断图片是否存在首先判断内存中是否存在然后判断本地是否存在
     *
     * @param url
     * @return
     */
    public boolean isBitmapExit(String url) {
        boolean isExit = false;
        if (false == isExit) {
            isExit = isLocalHasBmp(url);
        }
        return isExit;
    }

    /*
     * 判断本地有没有
     */
    private boolean isLocalHasBmp(String url) {
        boolean isExit = true;
        String name = url;
        String filePath = isCacheFileIsExit();
        File file = new File(filePath, name);
        if (file.exists()) {
        } else {
            isExit = false;
        }
        return isExit;
    }

    /*
     * 判断缓存文件夹是否存在如果存在怎返回文件夹路径，如果不存在新建文件夹并返回文件夹路径
     */
    private String isCacheFileIsExit() {
        String filePath = "";
        String rootpath = "";

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            rootpath = Environment.getExternalStorageDirectory().toString();
        }
        filePath = rootpath + CACHE_FILE;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }
}
