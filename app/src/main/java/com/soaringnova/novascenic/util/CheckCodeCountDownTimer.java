package com.soaringnova.novascenic.util;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;


/**
 * Created by chenfei on 14-7-29.
 */
public class CheckCodeCountDownTimer extends CountDownTimer {
    private Button action_check_btn;
    private TextView action_check_tv;

    public CheckCodeCountDownTimer(Button btn, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.action_check_btn = btn;
        this.action_check_btn.setEnabled(false);
    }

    public CheckCodeCountDownTimer(TextView tv, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.action_check_tv = tv;
        this.action_check_tv.setClickable(false);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long unFinish = millisUntilFinished / 1000;
        if (action_check_btn != null) {
            action_check_btn.setText("("+String.format(Locale.getDefault(),"%02d ", unFinish) + "s)可重发");
            action_check_btn.setTextColor(0xffffffff);
        }
        if (action_check_tv != null) {
            action_check_tv.setText("("+String.format(Locale.getDefault(),"%02d", unFinish) + "s)可重发");
            action_check_tv.setTextColor(0xffffffff);
        }
    }

    @Override
    public void onFinish() {
        // 倒计时结束，设置重发验证码按钮可点击，并且修改按钮样式
        if (action_check_btn != null) {
            action_check_btn.setEnabled(true);
            action_check_btn.setText("获取验证码");
            action_check_btn.setTextColor(0xffffffff);
        }
        if (action_check_tv != null) {
            action_check_tv.setText("获取验证码");
            action_check_tv.setClickable(true);
            action_check_tv.setTextColor(0xffffffff);
        }
    }

}
