package com.soaringnova.novascenic.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

/**
 * 展开和收起的一个动画类
 */
public class ExpandAnimation extends Animation {
    private View targetView;

    private LayoutParams targetLayoutParams;

    private boolean actionExpand;

    public ExpandAnimation(View view, int duration) {
        setDuration(duration);
        targetView = view;
        targetLayoutParams = (LayoutParams) view.getLayoutParams();
    }


    public void expand() {
        if (targetView.getVisibility() == View.GONE) {
            // 防止 view 设置View.Visible 时 造成的突然视图突兀先全展开；6666666666
            int layoutHei = targetLayoutParams.height;
            if (layoutHei == ViewGroup.LayoutParams.WRAP_CONTENT) {
                int fatherWid = ((View) targetView.getParent()).getWidth();
                int measureWid = View.MeasureSpec.makeMeasureSpec(fatherWid, View
                        .MeasureSpec.EXACTLY);
                int measureHei = View.MeasureSpec.UNSPECIFIED;
                targetView.measure(measureWid, measureHei);
                targetLayoutParams.bottomMargin = -targetView.getMeasuredHeight();
            } else if (layoutHei == ViewGroup.LayoutParams.MATCH_PARENT) {
                int fatherWid = ((View) targetView.getParent()).getWidth();
                int fatherHei = ((View) targetView.getParent()).getHeight();
                int measureWid = View.MeasureSpec.makeMeasureSpec(fatherWid, View
                        .MeasureSpec.EXACTLY);
                int measureHei = View.MeasureSpec.makeMeasureSpec(fatherHei, View
                        .MeasureSpec.EXACTLY);
                targetView.measure(measureWid, measureHei);
                targetLayoutParams.bottomMargin = -targetView.getMeasuredHeight();

            } else {
                targetLayoutParams.bottomMargin = -layoutHei;
            }

            targetView.setVisibility(View.VISIBLE);
            targetView.requestLayout();
            actionExpand = true;
            targetView.startAnimation(this);
        }
    }

    public void contract() {
        if (targetView.getVisibility() == View.VISIBLE) {
            actionExpand = false;
            targetView.startAnimation(this);
            targetView.requestLayout();
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (actionExpand) {
            targetLayoutParams.bottomMargin = (int) (targetView.getHeight() * (interpolatedTime - 1));
            targetView.setLayoutParams(targetLayoutParams);
        } else {
            targetLayoutParams.bottomMargin = (int) (targetView.getHeight() * (-interpolatedTime));
            targetView.setLayoutParams(targetLayoutParams);

            if (interpolatedTime > 0.95f) {
                targetView.setVisibility(View.GONE);
            }
        }
    }

}