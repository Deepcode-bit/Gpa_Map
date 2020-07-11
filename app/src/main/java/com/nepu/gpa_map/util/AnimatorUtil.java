package com.nepu.gpa_map.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class AnimatorUtil {
    public static AnimatorSet setPlay(AnimatorSet set, View view, int translationX, int translationY) {
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", translationX, 0),
                ObjectAnimator.ofFloat(view, "translationY", translationY, 0),
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.1f),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));
        set.setDuration(1 * 500);
        return set;
    }

    public static AnimatorSet setPlay2(AnimatorSet set, View view, int translationX, int translationY) {
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, translationX),
                ObjectAnimator.ofFloat(view, "translationY", 0, translationY),
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        //动画周期为500ms
        set.setDuration(1 * 500);
        return set;
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
    public static void doAnimateOpen(View view, int index, int total, int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.toRadians(90)/(total - 1) * index;
//        double degree = Math.PI/ ((total - 1) * 2)  * index;
        int translationX = -(int) (radius * Math.sin(degree));
        int translationY = -(int) (radius * Math.cos(degree));
        AnimatorSet set = new AnimatorSet();
        AnimatorUtil.setPlay2(set, view, translationX, translationY).start();
    }

    /**
     * 关闭菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    public static void doAnimateClose(final View view, int index, int total,
                                int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        int translationX = -(int) (radius * Math.sin(degree));
        int translationY = -(int) (radius * Math.cos(degree));
        AnimatorSet set = new AnimatorSet();
        AnimatorUtil.setPlay(set, view, translationX, translationY).start();
    }

}
