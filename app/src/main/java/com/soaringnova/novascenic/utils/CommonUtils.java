package com.soaringnova.novascenic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {


    private static final char hexCodes[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    // 显示Object 中的parame； 属性；
    public static void showObject(Object object) {
        if (object == null)
            return;
        Class targetClass = object.getClass();
        while (targetClass != Object.class) {
            showObject(object, targetClass);
            targetClass = targetClass.getSuperclass();
        }
    }

    private static void showObject(Object object, Class targetClass) {
        Field[] fields = targetClass.getFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                String stringValue = value == null ? "null" : value.toString();
                System.out.println("param___name::---->" + field.getName() + "-----" + stringValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 用于级联复制 Model 中的  （不为空）&& （不为空字符） 的同名属性；
    public static void fieldCopy(Object target, Object resource) {
        Class tarClass = target.getClass();
        Class resClass = resource.getClass();
        while (tarClass != Object.class && resClass != Object.class) {
            fieldCopy(target, resource, tarClass, resClass);
            tarClass = tarClass.getSuperclass();
            resClass = resClass.getSuperclass();
        }
    }

    // 用于复制 Model 中的  （不为空）&& （不为空字符） 的同名属性；
    private static void fieldCopy(Object target, Object resource, Class tarClass, Class resClass) {
        // target相关
        Field[] tarFields = tarClass.getFields();
        List<String> tarFileNames = new ArrayList<>();
        for (Field field : tarFields) {
            tarFileNames.add(field.getName());
        }
        // resource相关
        Field[] resFields = resClass.getFields();
        List<String> resFileNames = new ArrayList<>();
        for (Field field : resFields) {
            resFileNames.add(field.getName());
        }

        // 复制属性
        for (String fieldName : tarFileNames) {
            if (resFileNames.contains(fieldName)) {
                try {
                    Field resFiled = resClass.getDeclaredField(fieldName);
                    Field tarFiled = tarClass.getDeclaredField(fieldName);
                    if (Modifier.isStatic(resFiled.getModifiers()) || Modifier.isFinal(resFiled.getModifiers()))
                        continue;   // 排除 static 或者 final 干扰
                    if (Modifier.isStatic(tarFiled.getModifiers()) || Modifier.isFinal(tarFiled.getModifiers()))
                        continue;   // 排除 static 或者 final 干扰

                    Object value = resFiled.get(resource);
                    if (value != null && !"".equals(value)) {
                        tarFiled.set(target, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    // 字符串形式读取asset文件
    public static String convertAssetToStringByName(Context context ,String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ;
                }
            }
        }
        return sb.toString();
    }

    public static boolean checkStringUnNull(String res) {
        return (res != null) && (!"".equals(res));
    }

    // 判断字符串数组相等
    public static boolean checkStringArrayEquals(String[] tar1, String[] tar2) {
        if (tar1 == tar2) return true;
        if (tar1 == null || tar2 == null) return false;
        if (tar1.length != tar2.length) return false;
        for (int i = 0; i < tar1.length; i++) {
            if (!checkStringEquals(tar1[i], tar2[i]))
                return false;
        }

        return true;
    }

    // 判断字符串相等
    public static boolean checkStringEquals(String tar1, String tar2) {
        if (tar1 == null) return tar2 == null;
        else {
            return tar1.equals(tar2);
        }
    }

    // 得到图片Bitmap 根据ID；
    public static Bitmap getBitmapByResId(Context context, int ResID) {
        return ((BitmapDrawable) context.getResources().getDrawable(ResID)).getBitmap();
    }


    // 系统键盘的启动或者关闭
    public static void closeInputMethod(Activity activity, boolean isShow) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        if (isShow) {
            if (activity.getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            } else {
                imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        } else {
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // 系统键盘关闭
    public static void closeSoftKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        if (activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }


    // 拆分URL 携带的参数
    public static Map<String, String> splitString(String dataString) {
        String[] split = dataString == null ? null : dataString.split("@@");
        Map<String, String> keyMap = new HashMap<>();
        for (int i = 0; split != null && i < split.length; i++) {
            String key_value = split[i];
            String[] part = key_value.split("=");
            if (part.length > 1) {
                keyMap.put(part[0], part[1]);
            }
        }
        return keyMap;
    }


    // 获取String md5值
    public static String getMD5ByText(String textSource) {

        if (textSource == null || textSource.trim().length() < 1) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(textSource.trim().getBytes("UTF-8"));
            byte[] resource = md5.digest();
            StringBuilder sb = new StringBuilder();

            for (byte i : resource) {
                sb.append(toHex(i));
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String toHex(long value, int digitNum) {
        StringBuilder result = new StringBuilder(digitNum);
        while (digitNum > 0) {
            digitNum--;
            int index = (int) ((value >> (4 * digitNum)) & 15);
            result.append(hexCodes[index]);
        }
        return result.toString();
    }

    public static String toHex(byte value) {
        return toHex(value, 2);
    }

    public static String toHex(long value) {
        return toHex(value, 16);
    }


    // sp : dp : xp 互转
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //判断是不是纯数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    //校检邮箱格式
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))"
                + "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }





    /**
     * 当手机出于移动数据网络或WiFi网络时判定为有网络
     * android.Manifest.permission.ACCESS_NETWORK_STATE.
     * @param context
     * @return
     */
    public static boolean checkNetwork(Context context) {
        //ConnectivityManager
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo: 封装着网络信息
        //getActiveNetworkInfo:获取活动的网络状态,获取当前手机的网络状态
        //android.Manifest.permission.ACCESS_NETWORK_STATE.
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            //没有网络
            return false;
        }

        int type = activeNetworkInfo.getType();
        //WIFI 移动数据
        //传智人的代码
        if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }



}