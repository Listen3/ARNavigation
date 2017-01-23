package com.soaringnova.novascenic.util;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by liuyang on 16/6/1.
 */
public class FileUtils {


    /**
     * 获取 随机文件   可设置后缀名
     *
     * @param context
     * @param suffixName 后缀名
     * @return
     */
    @Nullable
    public static File getRandomFilePath(Context context, String suffixName) {
        String path;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getPath() + "/guanjian";
        } else {
            path = context.getFilesDir().getPath() + "/guanjian";                             //无SD卡时取appFile目录保存 （可惜 外部程序无法播放次文件）
        }

        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        if (suffixName != null && !suffixName.startsWith(".")) {
            suffixName = "." + suffixName;
        }

        path = path + "/" + System.currentTimeMillis() + suffixName;
        pathFile = new File(path);

        if (!pathFile.exists()) {
            try {
                pathFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return pathFile;
    }


    // 获取 文件路径
    public static String getFilePath(Uri fileUri) {
        if (fileUri == null) {
            return null;
        }
        String filePathName = Uri.decode(fileUri.toString());
        filePathName = filePathName.replace("file://", "");
        return filePathName;
    }

    // 获取 文件后缀名
    public static String getFileSuffixName(Uri fileUri) {

        if (fileUri == null) {
            return null;
        }
        String filePathName = getFilePath(fileUri);
        String[] fileNameSplit = filePathName.split("\\.");

        if (fileNameSplit.length == 1) {
            return null;
        } else {
            return fileNameSplit[fileNameSplit.length - 1];
        }

    }

    // 获取 文件名字
    public static String getFileSimpleName(Uri fileUri) {
        if (fileUri == null) {
            return null;
        }
        String filePathName = getFilePath(fileUri);
        String[] fileNameSplit = filePathName.split("/");
        return fileNameSplit[fileNameSplit.length - 1];
    }



    /**
     * 根据Uri获取文件绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param imageUri
     * @author yaoxing
     * @date 2014-10-12
     */
    public static Uri getContentUriAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    String filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    return Uri.fromFile(new File(filePath));
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                String filePath = imageUri.getLastPathSegment();
                if (!android.text.TextUtils.isEmpty(filePath)) {
                    return Uri.fromFile(new File(filePath));
                }
            }
            return getDataColumn(context, imageUri, null, null);
        } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            // File
            String filePath = imageUri.getPath();
            if (!android.text.TextUtils.isEmpty(filePath)) {
                return Uri.fromFile(new File(filePath));
            }
        }
        return null;
    }

    private static Uri getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                String filePath = cursor.getString(index);
                if (!android.text.TextUtils.isEmpty(filePath)) {
                    return Uri.fromFile(new File(filePath));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


}
