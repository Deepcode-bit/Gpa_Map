package com.nepu.gpa_map.util;

// 图片存到sd卡中

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class SdCardUtil {

    // 项目文件根目录
    public static final String FILEDIR = "/GPAMap";

    // 下载地图存放
    public static final String FILEIMAGE = "/images";

    // 下载地图存放
    public static final String FILEOVERLAY = "/source";

     // 应用程序缓存
    public static final String FILECACHE = "/cache";

    // 瓦片图目录
    public static final String FILETILE = "/tile";

    /**
     * getExternalStorageState 获取状态
     * Environment.MEDIA_MOUNTED 直译  环境媒体登上  表示，当前sd可用
     */
    public static boolean checkSdCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            //sd卡可用
            return true;
        else {
            //当前sd卡不可用
            return false;
        }
    }

    /**
     * 获取sd卡的文件路径
     * getExternalStorageDirectory 获取路径
     */
    public static String getSdPath(){
        return Environment.getExternalStorageDirectory()+"/";}

    /**
     * 创建一个文件夹
     */
    public static void createFileDir(String fileDir){
        String path = getSdPath()+fileDir;
        File path1 = new File(path);
        if(!path1.exists()) {
            path1.mkdirs();
            Log.i("yang", "我被创建了");
        }
    }

    public static void initFolder(){
        if(checkSdCard()){
            createFileDir(FILEDIR);
            createFileDir(FILEDIR+"/"+FILEOVERLAY);
            createFileDir(FILEDIR+"/"+FILEIMAGE);
            createFileDir(FILEDIR+"/"+FILECACHE);
            createFileDir(FILEDIR+"/"+FILETILE);
        } else {
            System.out.println("创建文件夹失败SD卡不可用");
        }
    }
}
