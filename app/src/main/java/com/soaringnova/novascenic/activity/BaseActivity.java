package com.soaringnova.novascenic.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Be on 2017/1/23.
 */

public class BaseActivity extends AppCompatActivity {
    protected enum STATUS_BAR_STATE{
        STABLE,
        TRANSLUCENT,
    }

    private View statusView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);                                                                          //默认关闭键盘
        //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);                                            //竖屏
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBarStyle();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setStatusBarStyle();
    }

    private void setStatusBarStyle(){
        STATUS_BAR_STATE bar_state = getStatusBarState();
        if(bar_state!=null){
            switch (bar_state){
                case STABLE:
                    int color = getStatusBarColor();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        // 设置状态栏透明
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        // 生成一个状态栏大小的矩形
                        View statusView = createStatusView(color);
                        // 添加 statusView 到布局中
                        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                        decorView.addView(statusView);
                        // 设置根布局的参数
                        ViewGroup rootView = (ViewGroup)((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                        rootView.setFitsSystemWindows(true);
                        //rootView.setClipToPadding(true);
                    }
                    break;
                case TRANSLUCENT:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        // 设置状态栏透明
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


                        //      ViewGroup rootView = (ViewGroup)((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                        //       rootView.setFitsSystemWindows(true);
                    }else {
//                        getWindow().getDecorView().setSystemUiVisibility(
//                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                    break;
            }
        }
    }


    // 取得当前状态栏 的显示样式
    protected STATUS_BAR_STATE getStatusBarState(){
        return STATUS_BAR_STATE.STABLE;
    }

    // 返回状态栏颜色  当且仅当 getStatusBarState() == STATUS_BAR_STATE.STABLE 时候有效；
    protected int getStatusBarColor(){
        return 0xFF515151;
    }





    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param color 状态栏颜色值
     * @return 状态栏矩形条
     */


    public void setBarColor(int color ){
        if(statusView == null) return;
        statusView.setBackgroundColor(color);
    }

    private  View createStatusView( int color) {
        // 获得状态栏高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        statusView = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

}

