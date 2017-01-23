package com.soaringnova.novascenic.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by powyin on 2016/10/24.
 */

public class ToastUtil {
    public static void show(Context context, String message){
        if(TextUtils.isEmpty(message)){
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
