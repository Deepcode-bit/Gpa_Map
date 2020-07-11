package com.nepu.gpa_map.libs;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.nepu.gpa_map.R;
import com.nepu.gpa_map.maps.MTiles;

public class MDrawLayout extends RelativeLayout implements View.OnTouchListener{

    private int duringTime;//抽出布局的时间
    private int selectedIndex;
    public boolean isPullOut;//布局是否被抽出
    private LinearLayout[] tiles;
    private AMap aMap;
    private MTiles mTiles;

    public MDrawLayout(Context context,AMap aMap) {
        super(context);
        this.aMap=aMap;
        Init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void Init(Context context){
        duringTime=300;
        isPullOut=false;
        this.addView(inflater(context));
        this.setBackgroundColor(Color.WHITE);
        this.setGravity(Gravity.END);
        this.setOnTouchListener(this);
        InitViews(context);
    }

    /**
     * 拉出抽屉布局的方法
     */
    public void PullOut(){
        ObjectAnimator transXAnim = ObjectAnimator.ofFloat(this, "X", (float) (-Global.width *0.24));
        transXAnim.setDuration(duringTime);
        transXAnim.start();
        isPullOut=true;
    }

    /**
     * 推回抽屉布局的方法
     */
    public void PushIn(){
        ObjectAnimator transXAnim = ObjectAnimator.ofFloat(this, "X", -(float)Global.width);
        transXAnim.setDuration(duringTime);
        transXAnim.start();
        isPullOut=false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()){
                case R.id.Tile1:selectedIndex=0; aMap.setMapType(AMap.MAP_TYPE_NORMAL);break;
                case R.id.Tile2:selectedIndex=1; aMap.setMapType(AMap.MAP_TYPE_SATELLITE);break;
                case R.id.Tile3:selectedIndex=2; mTiles.useOMCMap("http://mt0.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&x=%d&y=%d&z=%d&s=Galil");break;
                case R.id.Tile4:selectedIndex=3; aMap.setMapType(AMap.MAP_TYPE_NIGHT);break;
                case R.id.Tile5:selectedIndex=4; aMap.setMapType(AMap.MAP_TYPE_NAVI);break;
            }
            if(selectedIndex!=2){
                mTiles.RemoveTiles();
            }
            for(int i=0;i<5;i++){
                if(i==selectedIndex){
                    tiles[i].setBackgroundResource(R.drawable.bg_border);
                }else{
                    tiles[i].setBackground(null);
                }
            }
        }
        return true;
    }

    private View inflater(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);
        return inflater.inflate(R.layout.drawer_layout,null);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void InitViews(Context context){
        tiles=new LinearLayout[5];
        tiles[0]=findViewById(R.id.Tile1);
        tiles[1]=findViewById(R.id.Tile2);
        tiles[2]=findViewById(R.id.Tile3);
        tiles[3]=findViewById(R.id.Tile4);
        tiles[4]=findViewById(R.id.Tile5);
        for(int i=0;i<5;i++){
            tiles[i].setOnTouchListener(this);
        }
        mTiles=new MTiles(aMap);
    }

    public void AddOnlineTile(String url){
        mTiles.useOMCMap(url);
        for(int i=0;i<5;i++){
                tiles[i].setBackground(null);
        }
    }

    public void AddOfflineTile(String path){
        mTiles.useOfflineTile(path);
        for(int i=0;i<5;i++){
            tiles[i].setBackground(null);
        }
    }
}
