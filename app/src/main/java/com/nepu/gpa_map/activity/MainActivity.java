package com.nepu.gpa_map.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.nepu.gpa_map.R;
import com.nepu.gpa_map.libs.Global;
import com.nepu.gpa_map.libs.MDrawLayout;
import com.nepu.gpa_map.maps.MTiles;
import com.nepu.gpa_map.maps.OverLayer;
import com.nepu.gpa_map.util.AnimatorUtil;
import com.nepu.gpa_map.util.DynamicPermission;
import com.nepu.gpa_map.util.FilePicker;
import com.nepu.gpa_map.util.SdCardUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private AMap aMap;
    private RelativeLayout mLayout;
    private MDrawLayout drawLayout;
    private View transView;//半透明图层

    private Button mMenuButton;
    private Button mItemButton1;
    private Button mItemButton2;
    private Button mItemButton3;
    private Button mItemButton4;

    private Button importCoverage;
    private Button importOverlay;

    private boolean mIsMenuOpen = false;
    private boolean isOverLayer=false;
    private GroundOverlay overlay;

    private final int REQUESTCODE_FROM_ACTIVITY = 1000;
    private final int REQUESTCODE_TO_OVERLAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(mLayout);
        DynamicPermission.permissionAsk(MainActivity.this);//申请权限
        mMapView=findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        InitData();//初始化变量
        InitInterface();//初始化界面
        initView();//初始化菜单
        SdCardUtil.initFolder();
    }

    private void InitInterface(){
        //获取屏幕尺寸信息
        DisplayMetrics dis = getResources().getDisplayMetrics();
        Global.width = dis.widthPixels;
        Global.high = dis.heightPixels;
        mLayout.addView(transView,Global.width,Global.high);
        transView.setX(0);transView.setY(0);
        mLayout.addView(drawLayout,Global.width,Global.high);
        drawLayout.setX(-Global.width);
        drawLayout.setY(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void InitData(){
        aMap=mMapView.getMap();
        UiSettings mUiSettings= aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//缩放按钮的显示与隐藏
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        drawLayout=new MDrawLayout(this,aMap);
        transView=new View(this);
        transView.setBackgroundColor(Color.GRAY);
        transView.setAlpha(0.5f);
        transView.setVisibility(View.INVISIBLE);
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
        transView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(drawLayout.isPullOut && event.getAction() == MotionEvent.ACTION_DOWN) {
                    drawLayout.PushIn();
                    v.setVisibility(View.INVISIBLE);
                }
                return true;
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

        importCoverage = findViewById(R.id.import_coverage);
        importCoverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePicker.showListDialog(MainActivity.this, REQUESTCODE_TO_OVERLAY, drawLayout);
            }
        });

        importOverlay = findViewById(R.id.import_overlay);
        importOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOverLayer) {
                    overlay.remove();
                    importOverlay.setText("导入覆盖物");
                    isOverLayer = false;
                }else {
                    FilePicker.newFilePicker(MainActivity.this, REQUESTCODE_FROM_ACTIVITY).start();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mMenuButton) {
            if (!mIsMenuOpen) {
                mIsMenuOpen = true;
                AnimatorUtil.doAnimateOpen(mItemButton1, 0, 4, 300);
                AnimatorUtil.doAnimateOpen(mItemButton2, 1, 4, 300);
                AnimatorUtil.doAnimateOpen(mItemButton3, 2, 4, 300);
                AnimatorUtil.doAnimateOpen(mItemButton4, 3, 4, 300);
            } else {
                mIsMenuOpen = false;
                AnimatorUtil.doAnimateClose(mItemButton1, 0, 4, 300);
                AnimatorUtil.doAnimateClose(mItemButton2, 1, 4, 300);
                AnimatorUtil.doAnimateClose(mItemButton3, 2, 4, 300);
                AnimatorUtil.doAnimateClose(mItemButton4, 3, 4, 300);
            }
        } else {
            switch (v.getId()){
                case R.id.item1:
                    if(drawLayout!=null &&!drawLayout.isPullOut){
                        drawLayout.PullOut();
                        transView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //如果是文件选择模式，需要获取选择的所有文件的路径集合
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
                for (final String path : list) {
                    //path就是获取到的文件路径   由于是单选其实list里面就只有一个  就这么写着了万一哪天需要多选了呢
                    final EditText editText = new EditText(MainActivity.this);
                    AlertDialog.Builder XYDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("请分别输入覆盖物西南和东北两点经纬度(X1,Y1,Y2,Y2)")
                            .setView(editText)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //获取输入得链接  先用着后面我设置链接格式
                                    String[] XY = String.valueOf(editText.getText()).split(",");
                                    LatLng x = new LatLng(Double.parseDouble(XY[0]), Double.parseDouble(XY[1]));
                                    LatLng y = new LatLng(Double.parseDouble(XY[2]), Double.parseDouble(XY[3]));
                                    overlay = aMap.addGroundOverlay(OverLayer.GetOverLayer(path, x, y));
                                    isOverLayer = true;
                                    importOverlay.setText("清除覆盖物");
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    XYDialog.show();
                }
            } else if (requestCode == REQUESTCODE_TO_OVERLAY) {
                //如果是文件夹选择模式，需要获取选择的文件夹路径
                String path = data.getStringExtra("path");
                drawLayout.AddOfflineTile(path);
            }
        }
    }

}