
package com.soaringnova.novascenic.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.Text;
import com.amap.api.services.poisearch.PoiItemExtension;
import com.soaringnova.novascenic.R;
import com.amap.api.services.core.PoiItem;
import com.squareup.picasso.Picasso;

/**
 * Created by Neb on 2016/12/7.
 */

public class FloatView extends LinearLayout {
    private TextView poiName;
    private TextView poiDistance;
    //连接线
    private int left = 0;
    private int top = 0;
    private PoiItem poi;
    private boolean selectedState = false;
    private TextView line1;
    public LinearLayout floatview;
    public LinearLayout poiBase;
    private LinearLayout poiextension;
    private OnCheckedListener onCheckedListener;
    private FloatView floatView = this;
    private ImageView poiImg;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatView(Context context, PoiItem poi, int left, int top) {
        super(context);
        this.left = left;
        this.top = top;
        this.poi = poi;
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        //设置悬浮view的初始位置
        FrameLayout.LayoutParams llparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llparams.setMargins(left, -top, 50, 50);
        this.setLayoutParams(llparams);
        /*poiName.setBackgroundColor(Color.GREEN);
        */
        //poiDistance.setTextColor(getContext().getResources().getColor(R.color.poidistance_selector));
        //设置海拔属性，范围为根据poi距离从远至近0-10
        setOrientation(VERTICAL);
        float ele = 10f - (float) (poi.getDistance() * 0.01);
        setElevation(ele);
        floatview = (LinearLayout) View.inflate(getContext(), R.layout.floatview, null);
        poiBase = (LinearLayout) floatview.findViewById(R.id.poiBase);
        poiName = (TextView) floatview.findViewById(R.id.poiName);
        poiName.setPadding(15, 5, 15, 5);
        poiDistance = (TextView) floatview.findViewById(R.id.poiDistance);
        poiName.setText(poi.getTitle());
        poiDistance.setText(poi.getDistance() + "米");
        addView(floatview);
        //添加连接线
        addLine();
        //添加详情窗口
        addPoiExtension();

        poiBase.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "执行回调", Toast.LENGTH_SHORT).show();
                if (onCheckedListener != null) {
                    onCheckedListener.onCehcked(poi, floatView);
                }
                //setSelectedState(!getSelectedState());

            }
        });
        setAlpha(0.8f);

        //poiDistance.setTextColor();
        //设置状态选择器
        //setBackgroundDrawable();

    }

    //添加详情框
    private void addPoiExtension() {
        poiextension = (LinearLayout) View.inflate(getContext(), R.layout.floatview_extension, null);
        TextView openTime = (TextView) poiextension.findViewById(R.id.openTime);
        RatingBar rating = (RatingBar) poiextension.findViewById(R.id.rating);
        poiImg = (ImageView) poiextension.findViewById(R.id.poiImg);
        openTime.setText("OpenTime:" + poi.getPoiExtension().getOpentime());
        int ratingNum = 0;
        if (!TextUtils.isEmpty(poi.getPoiExtension().getmRating())) {
            ratingNum = Integer.parseInt(poi.getPoiExtension().getmRating().toCharArray()[0] + "");
            System.out.println("-------------------------------------" + ratingNum);
            rating.setNumStars(ratingNum);
        }
        LayoutParams poiextensionParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        poiextensionParams.gravity = Gravity.CENTER;
        poiextension.setGravity(Gravity.CENTER);
        poiextension.setLayoutParams(poiextensionParams);
        poiextension.setVisibility(INVISIBLE);
        poiextension.setClickable(false);
        poiextension.setOnClickListener(new PoiExtensionListener());
        floatview.addView(poiextension);
    }

    //动态添加一条线
    private void addLine() {
        line1 = new TextView(getContext());
        LayoutParams lineparams = new LayoutParams(3,
                550 - Math.abs(top));
        lineparams.gravity = Gravity.CENTER;
        System.out.println("top=====:" + top);
        line1.setLayoutParams(lineparams);
        line1.setBackgroundColor(Color.WHITE);
        line1.setClickable(false);
        floatview.addView(line1);
        line1.setVisibility(INVISIBLE);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatView setName(String poiName) {
        this.poiName.setText(poiName);
        return this;
    }

    public FloatView setDistance(int distance) {
        this.poiDistance.setText(distance + "米");
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSelectedState(boolean selected) {
        selectedState = selected;
        if (selectedState) {
            //如果选中将背景改成红色
            //setBackgroundColor(Color.RED);
            poiName.setBackgroundColor(Color.RED);
            poiDistance.setTextColor(Color.WHITE);
            poiDistance.setBackgroundColor(Color.RED);
            setElevation(getElevation() + 10);
            setAlpha(0.8f);
            line1.setVisibility(VISIBLE);
            poiextension.setVisibility(VISIBLE);
            poiextension.setClickable(true);

        } else {
            //非选中状态
            poiDistance.setTextColor(Color.BLACK);
            poiDistance.setBackgroundColor(Color.WHITE);
            poiName.setBackgroundColor(Color.GREEN);
            setElevation(getElevation() - 10);
            setAlpha(0.4f);
            line1.setVisibility(INVISIBLE);
            poiextension.setVisibility(INVISIBLE);
            poiextension.setClickable(false);
        }
    }

    public boolean getSelectedState() {
        return selectedState;
    }

    public int getPoiLeft() {
        return left;
    }

    public int getPoiTop() {
        return top;
    }

    public ImageView getPoiImg() {
        return poiImg;
    }

    /*    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void showPopupWindow(View view) {

            View contentView = LayoutInflater.from(getContext()).inflate(

      *//*      Button button = (Button) contentView.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(ARNavigation.this, "button is pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });*//*

        PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
 *//*       popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.selectmenu_bg_downward));
*//*
        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }*/

    /**
     * floatview选中回调
     */
    public interface OnCheckedListener {
        void onCehcked(PoiItem poiItem, FloatView floatView);
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        onCheckedListener = listener;
    }

    /**
     * poi详情窗口点击事件
     */
    class PoiExtensionListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "详情页", Toast.LENGTH_SHORT).show();
           
        }
    }
}