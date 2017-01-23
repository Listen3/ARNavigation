package com.soaringnova.novascenic.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

	public static String CONFIG_NAME = "config";

	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,Context.MODE_PRIVATE);
//		return sp.getBoolean(key, true);
		return sp.getBoolean(key, false);
	}
	public static void putBoolean(Context context, String key,boolean value) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}
	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		return sp.getString(key,"");
	}
	public static void putString(Context context, String key,String value) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}
	public static int getInt(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		return sp.getInt(key,0);
	}
	public static void putInt(Context context, String key,int value) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}
}