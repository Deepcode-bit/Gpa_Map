package com.nepu.gpa_map.maps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.UrlTileProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MTiles {
    private AMap aMap;
    private TileOverlay mtileOverlay;

    private final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory() + "/mapdemo/Cache/";

    public MTiles(AMap map){
        this.aMap=map;
    }

    /**
     * 加载在线瓦片数据
     */
    public void useOMCMap(final String url) {
        //final String url =  "http://mt0.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&x=%d&y=%d&z=%d&s=Galil";
        if (mtileOverlay != null){
            mtileOverlay.remove();
        }
        TileOverlayOptions tileOverlayOptions =
                new TileOverlayOptions().tileProvider(new UrlTileProvider(256, 256) {
                    @Override
                    public URL getTileUrl(int x, int y, int zoom) {
                        try {
                            String mFileDirName;
                            String mFileName;
                            mFileDirName = String.format("L%02d/", zoom + 1);
                            mFileName = String.format("%s", TileXYToQuadKey(x, y, zoom));//为了不在手机的图片中显示,取消jpg后缀,文件名自己定义,写入和读取一致即可,由于有自己的bingmap图源服务,所以此处我用的bingmap的文件名
                            String LJ = ALBUM_PATH +mFileDirName+ mFileName;
                            if (MapImageCache.getInstance().isBitmapExit( mFileDirName + mFileName)) {//判断本地是否有图片文件,如果有返回本地url,如果没有,缓存到本地并返回googleurl
                                return new URL("file://" + LJ);
                            } else {
                                String filePath = String.format(url, x, y, zoom);
                                Bitmap mBitmap;
                                mBitmap = getImageBitmap(getImageStream(filePath));
                                try {
                                    saveFile(mBitmap, mFileName, mFileDirName);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return new URL(filePath);
                            }
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        mtileOverlay = aMap.addTileOverlay(tileOverlayOptions);
        tileOverlayOptions.diskCacheEnabled(false)   //由于自带的缓存在关闭程序后会自动释放,所以无意义,关闭本地缓存
                .diskCacheDir("/storage/emulated/0/amap/OMCcache")
                .diskCacheSize(1024000)
                .memoryCacheEnabled(true)
                .memCacheSize(102400)
                .zIndex(-9999);
    }

    /**
     * 清除瓦片图
     */
    public void RemoveTiles(){
        if (mtileOverlay != null){
            mtileOverlay.remove();
        }
        deleteAllFiles(new File(ALBUM_PATH));
    }

    /**
     * 加载离线瓦片数据
     */
    public void useOfflineTile(final String path) {
        if (mtileOverlay != null){
            mtileOverlay.remove();
        }
        TileOverlayOptions tileOverlayOptions =
                new TileOverlayOptions().tileProvider(new UrlTileProvider(256, 256) {
                    @Override
                    public URL getTileUrl(int x, int y, int zoom) {
                        try {
                            final String LocalUrl = "file:///"+path + "/"+zoom + "/" + x + "/" + y + ".jpg";
                            return new URL(LocalUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        tileOverlayOptions.diskCacheEnabled(true)
                .diskCacheDir("/storage/emulated/0/amapdemo/tilecache")
                .diskCacheSize(100000)
                .memoryCacheEnabled(true)
                .memCacheSize(100000)
                .zIndex(-9999);
        mtileOverlay = aMap.addTileOverlay(tileOverlayOptions);
    }

    /**
     * 瓦片数据坐标转换
     */
    private String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }


    private Bitmap getImageBitmap(InputStream imputStream){
        // 将所有InputStream写到byte数组当中
        byte[] targetData = null;
        byte[] bytePart = new byte[4096];
        while (true) {
            try {
                int readLength = imputStream.read(bytePart);
                if (readLength == -1) {
                    break;
                } else {
                    byte[] temp = new byte[readLength + (targetData == null ? 0 : targetData.length)];
                    if (targetData != null) {
                        System.arraycopy(targetData, 0, temp, 0, targetData.length);
                        System.arraycopy(bytePart, 0, temp, targetData.length, readLength);
                    } else {
                        System.arraycopy(bytePart, 0, temp, 0, readLength);
                    }
                    targetData = temp;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 指使Bitmap通过byte数组获取数据
        Bitmap bitmap = BitmapFactory.decodeByteArray(targetData, 0, targetData.length);
        return bitmap;
    }

    private InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }


    /**
     * 保存文件
     */
    private void saveFile(final Bitmap bm, final String fileName, final String fileDirName)  throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(bm != null) {
                        File dirFile = new File(ALBUM_PATH + fileDirName);
                        if(!dirFile.exists()){
                            dirFile.mkdir();
                        }
                        File myCaptureFile = new File(ALBUM_PATH + fileDirName + fileName);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        bos.flush();
                        bos.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 删除文件
     */
    static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
}
