package com.nepu.gpa_map.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.nepu.gpa_map.R;
import com.nepu.gpa_map.libs.Global;
import com.nepu.gpa_map.libs.MDrawLayout;
import com.nepu.gpa_map.maps.MTiles;
import com.nepu.gpa_map.maps.OverLayer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private AMap aMap;
    private RelativeLayout mLayout;
    private MDrawLayout drawLayout;
    private Button openBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(mLayout);
        PermissionAsk();//申请权限
        mMapView=findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        InitData();//初始化变量
        InitInterface();//初始化界面
    }

    private void InitInterface(){
       //获取屏幕尺寸信息
        DisplayMetrics dis = getResources().getDisplayMetrics();
        Global.width = dis.widthPixels;
        Global.high = dis.heightPixels;
        mLayout.addView(drawLayout,Global.width,Global.high);
        drawLayout.setX(-Global.width);
        drawLayout.setY(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void InitData(){
        aMap=mMapView.getMap();
        openBut=findViewById(R.id.open_but);
        UiSettings mUiSettings= aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        drawLayout=new MDrawLayout(this);
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。 myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //放弃定位跟随
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            aMap.setMyLocationStyle(new MyLocationStyle().myLocationType
                    (MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
        }
    }, 3000);    //延时3s执行
        //设置监听
        openBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawLayout.isPullOut)
                drawLayout.PullOut();
                else
                    drawLayout.PushIn();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    /**
     * 动态权限申请
     */
    private void PermissionAsk(){
        //动态申请权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }
}
