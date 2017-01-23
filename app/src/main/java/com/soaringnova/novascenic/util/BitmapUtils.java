package com.soaringnova.novascenic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by albert on 8/1/14.
 */

public class BitmapUtils {
    private static final int DEFAULT_TARGET_WIDTH = 300;
    private static final int DEFAULT_TARGET_HEIGHT = 300;

    /**
     * 计算最接近的inSampleSize值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize1(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 计算需要的缩小比率
     *
     * @param originalWidth
     * @param originalHeight
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static double calScaleRatio(int originalWidth, int originalHeight, int targetWidth, int targetHeight) {
        if (originalWidth == 0 || originalHeight == 0) {
            return 1;
        } else {
            double ratio = Math.sqrt((targetWidth * targetHeight * 1.0) / (originalWidth * originalHeight));
            if (ratio > 1) {
                return 1;
            } else {
                return ratio;
            }
        }
    }

    /**
     * 计算需要的缩小比率
     *
     * @param originalWidth
     * @param originalHeight
     * @return
     */
    public static double calScaleRatio(int originalWidth, int originalHeight) {
        return calScaleRatio(originalWidth, originalHeight, DEFAULT_TARGET_WIDTH, DEFAULT_TARGET_HEIGHT);
    }

    /**
     * 缩小图片
     *
     * @param filename
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap scaleBitmap(String filename, int targetWidth, int targetHeight, boolean needPortrait) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(filename, options);
        }
        catch (OutOfMemoryError e)
        {
            return null;
        }
        catch(Exception ee)
        {
            return null;
        }

        return scaleBitmap(bitmap, targetWidth, targetHeight, needPortrait);
    }



    /**
     * 缩小图片
     *
     * @param bitmap
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int targetWidth, int targetHeight, boolean needPortrait) {
        if (bitmap == null) {
            return null;
        }

        float ratio = (float) calScaleRatio(bitmap.getWidth(), bitmap.getHeight(), targetWidth, targetHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        if (needPortrait) {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                matrix.postRotate(90);
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    //   fPath 文件路径  maxWidth 最大宽； maxHei 最大高   (如果最大宽高 大于原图片宽高  这提供原图)
    public static Bitmap decodeBitmapFromImageFile(String imageFilePath,float maxWidth, float maxHei) {
        if(TextUtils.isEmpty(imageFilePath)) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, options);

        if(options.outHeight==0 || options.outWidth ==0) return null;

        float scaleRadio = Math.min(maxWidth/options.outWidth, maxHei/options.outHeight);  //取缩放比
        scaleRadio = Math.min(1,scaleRadio);                                               //防止拉伸图片

        options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = (int)(1f/(scaleRadio));

        return BitmapFactory.decodeFile(imageFilePath, options);
    }




    /**
     * Bitmap 转成文件
     *
     * @param bitmap
     * @return
     */
    public static File bitmapToFile(Context context, Bitmap bitmap) {
        File file = FileUtils.getRandomFilePath(context,"png");
        if(file==null || !file.exists()) return null;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }



    //计算图片的缩放值
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }









}





