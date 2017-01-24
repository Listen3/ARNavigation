package com.soaringnova.novascenic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by powyin on 16/10/26.
 */
public class TimeUtils {

    /**
     * UTC time to format
     *
     * @param utcTime
     * @param format  yyyy-MM-dd HH:mm:ss     //
     * @return
     */
    private static SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssz",Locale.getDefault());

    public static String formatTo(String utcTime, String format) {
        if (utcTime == null) {
            return "";
        }
        try {
            String timeZone;
            if (!utcTime.endsWith("Z")) {
                //末尾不包含Z,则不需要减去8小时
                utcTime += "Z";
                timeZone = "+0800";
            } else {
                timeZone = "+0000";
            }
            Date date = localFormat.parse(utcTime.replaceAll("Z$", timeZone));
            SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.getDefault());
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return utcTime;
    }

    /**
     * long time to format
     * @param time
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatTo(long time, String format) {
        try {
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {

        }

        return "";
    }


    /**
     * date time to format
     * @param date
     * @param format  yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatTo(Date date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {

        }

        return "";
    }

    /**
     * utc时间转换为Date时间
     * @param utcTime
     * @return
     */
    public static Date convertToDate(String utcTime) {
        try {
            String timeZone;
            if (!utcTime.endsWith("Z")) {
                //末尾不包含Z,则不需要减去8小时
                utcTime += "Z";
                timeZone = "+0800";
            } else {
                timeZone = "+0000";
            }
            return  localFormat.parse(utcTime.replaceAll("Z$", timeZone));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }




























}
