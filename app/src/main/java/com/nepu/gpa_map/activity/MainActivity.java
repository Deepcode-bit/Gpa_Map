package com.nepu.gpa_map.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private AMap aMap;
    private RelativeLayout mLayout;
    private MDrawLayout drawLayout;
    private Button openBut;

    private Button mMenuButton;
    private Button mItemButton1;
    private Button mItemButton2;
    private Button mItemButton3;
    private Button mItemButton4;

    private boolean mIsMenuOpen = false;

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
        initView();//初始化菜单
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
        mUiSettings.setZoomControlsEnabled(false);//缩放按钮的显示与隐藏
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
                if(!drawLayout.isPullOut) {
                    drawLayout.PullOut();
                } else {
                    drawLayout.PushIn();
                }
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

    private void initView() {
        mMenuButton = (Button) findViewById(R.id.menu);
        mMenuButton.setOnClickListener(this);

        mItemButton1 = (Button) findViewById(R.id.item1);
        mItemButton1.setOnClickListener(this);

        mItemButton2 = (Button) findViewById(R.id.item2);
        mItemButton2.setOnClickListener(this);

        mItemButton3 = (Button) findViewById(R.id.item3);
        mItemButton3.setOnClickListener(this);

        mItemButton4 = (Button) findViewById(R.id.item4);
        mItemButton4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mMenuButton) {
            if (!mIsMenuOpen) {
                mIsMenuOpen = true;
                doAnimateOpen(mItemButton1, 0, 4, 300);
                doAnimateOpen(mItemButton2, 1, 4, 300);
                doAnimateOpen(mItemButton3, 2, 4, 300);
                doAnimateOpen(mItemButton4, 3, 4, 300);
            } else {
                mIsMenuOpen = false;
                doAnimateClose(mItemButton1, 0, 4, 300);
                doAnimateClose(mItemButton2, 1, 4, 300);
                doAnimateClose(mItemButton3, 2, 4, 300);
                doAnimateClose(mItemButton4, 3, 4, 300);
            }
        } else {
            //监听事件
            Toast.makeText(this, "你点击了" + v, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序,从0开始
     * @param total 动画序列的个数
     * @param radius 动画半径
     *
     *  Math.sin(x):x -- 为number类型的弧度，角度乘以0.017(2π/360)可以转变为弧度
     */
    private void doAnimateOpen(View view, int index, int total, int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.toRadians(90)/(total - 1) * index;
//        double degree = Math.PI/ ((total - 1) * 2)  * index;
        int translationX = -(int) (radius * Math.sin(degree));
        int translationY = -(int) (radius * Math.cos(degree));
        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, translationX),
                ObjectAnimator.ofFloat(view, "translationY", 0, translationY),
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        //动画周期为500ms
        set.setDuration(1 * 500).start();
    }

    /**
     * 关闭菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateClose(final View view, int index, int total,
                                int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        int translationX = -(int) (radius * Math.sin(degree));
        int translationY = -(int) (radius * Math.cos(degree));
        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", translationX, 0),
                ObjectAnimator.ofFloat(view, "translationY", translationY, 0),
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.1f),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));


        set.setDuration(1 * 500).start();
    }
}