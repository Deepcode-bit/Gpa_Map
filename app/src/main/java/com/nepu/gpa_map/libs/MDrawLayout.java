package com.nepu.gpa_map.libs;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class MDrawLayout extends RelativeLayout implements View.OnTouchListener{

    private int duringTime;//抽出布局的时间
    public boolean isPullOut;//布局是否被抽出

    public MDrawLayout(Context context) {
        super(context);
        Init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void Init(){
        duringTime=300;
        isPullOut=false;
        this.setBackgroundColor(Color.WHITE);
        this.setOnTouchListener(this);
    }

    /**
     * 拉出抽屉布局的方法
     */
    public void PullOut(){
        ObjectAnimator transXAnim = ObjectAnimator.ofFloat(this, "X", -(float)(Global.width * 0.3));
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
            Log.i("PBF", "touched");
        }
        return true;
    }
}
