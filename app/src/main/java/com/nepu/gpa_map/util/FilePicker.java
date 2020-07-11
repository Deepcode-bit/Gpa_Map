package com.nepu.gpa_map.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.nepu.gpa_map.R;
import com.nepu.gpa_map.activity.MainActivity;
import com.nepu.gpa_map.libs.MDrawLayout;
import com.nepu.gpa_map.maps.OverLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FilePicker {
    public static void showListDialog(final Activity context, final int REQUESTCODE_TO_OVERLAY, final MDrawLayout drawLayout) {
        final String[] items = { "在线导入","离线导入"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context, R.style.AlertDialogBackground);
        listDialog.setTitle("请选择导入方式");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    new LFilePicker()
                            .withActivity(context)
                            .withRequestCode(REQUESTCODE_TO_OVERLAY)
                            .withStartPath("/storage/emulated/0/GPAMap")//指定初始显示路径
                            .withChooseMode(false)//设置文件夹选择模式，true（默认）选择文件，false设置文件夹
                            .start();
                } else {
                    //在线导入
                    final EditText editText = new EditText(context);
                    //String digits = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ:/?";
                    //editText.setKeyListener(DigitsKeyListener.getInstance(digits));//限制输入格式

                    AlertDialog.Builder urlDialog = new AlertDialog.Builder(context)
                            .setTitle("请输入在线地图源链接")
                            .setView(editText)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //获取输入得链接  先用着后面我设置链接格式
                                    String url = String.valueOf(editText.getText());
                                    drawLayout.AddOnlineTile(url);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    urlDialog.show();
                }
            }
        });
        listDialog.show();
    }

    public static LFilePicker newFilePicker(Activity context, int requestCodeActivity) {
        LFilePicker lFilePicker = new LFilePicker();
        lFilePicker.withActivity(context);
        lFilePicker.withRequestCode(requestCodeActivity);
        lFilePicker.withFileFilter(new String[]{".jpg", ".png", "jpeg"});//过滤文件类型
        lFilePicker.withStartPath("/storage/emulated/0/GPAMap");//指定初始显示路径
        lFilePicker.withIsGreater(false);//过滤文件大小 小于指定大小的文件
        lFilePicker.withIconStyle(Constant.ICON_STYLE_GREEN);//默认ICON_STYLE_YELLOW
        lFilePicker.withMutilyMode(false);//限制单选或者多选
        lFilePicker.withFileSize(500 * 1024);//指定文件大小为500K

        return lFilePicker;
    }

}
