package com.nepu.gpa_map.maps;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;

public class OverLayer {
    public static GroundOverlayOptions GetOverLayer(String path,LatLng southwest,LatLng northeast){
        //定义Ground的显示地理范围
//        LatLng southwest = new LatLng(21.144, 97.528);
//        LatLng northeast = new LatLng(29.226,  106.194);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .build();
        //定义Ground显示的图片
        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromPath(path);
        //定义GroundOverlayOptions对象
        GroundOverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0f); //覆盖物透明度
        return ooGround;
    }
}
