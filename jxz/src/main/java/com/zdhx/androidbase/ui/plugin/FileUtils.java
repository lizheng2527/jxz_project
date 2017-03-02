/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.zdhx.androidbase.ui.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.util.FileAccessor;
import com.zdhx.androidbase.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * 文件工具类
 * Created by 容联•云通讯 Modify By Li.Xin @ 立思辰合众 on 2015/3/18.
 */
public class FileUtils {

    /**
     * 通过Uri获取文件在本地存储的真实路径
     *
     * @param act
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Activity act, Uri contentUri) {
        // can post image
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = act.managedQuery(contentUri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        LogUtil.w("s:"+s);
        return s;
    }

    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    String s = Environment.getExternalStorageDirectory() + "/" + split[1];
                    LogUtil.w(s);
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                LogUtil.w(getDataColumn(context, contentUri, null, null));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                LogUtil.w(getDataColumn(context, contentUri, selection, selectionArgs));
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            LogUtil.w(getDataColumn(context, uri, null, null));
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            LogUtil.w(uri.getPath());
            return uri.getPath();
        }
        LogUtil.w("null");
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 打开指定文件夹
     * @param path
     * @param context
     */
    public static void openAssignFolder(String path,Context context){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse(path), "*/*");
        try {
            context.startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param root
     * @param fileName
     * @return
     */
    public static String getMD5FileDir(String root , String fileName) {
        // FileAccessor.IMESSAGE_IMAGE + File.separator + FileAccessor.getSecondLevelDirectory(fileNameMD5)+ File.separator;
        if(TextUtils.isEmpty(root)) {
            return null;
        }
        File file = new File(root);
        if(!file.exists()) {
            file.mkdirs();
        }

        File fullPath = new File(file , FileAccessor.getSecondLevelDirectory(fileName));
        if(!fullPath.exists()) {
            fullPath.mkdirs();
        }
        return fullPath.getAbsolutePath();
    }


    /**
     * 转换成单位
     * @param length
     * @return
     */
    public static String formatFileLength(long length) {
        if (length >> 30 > 0L) {
            float sizeGb = Math.round(10.0F * (float) length / 1.073742E+009F) / 10.0F;
            return sizeGb + " GB";
        }
        if (length >> 20 > 0L) {
            return formatSizeMb(length);
        }
        if (length >> 9 > 0L) {
            float sizekb = Math.round(10.0F * (float) length / 1024.0F) / 10.0F;
            return sizekb + " KB";
        }
        return length + " B";
    }

    /**
     * 转换成Mb单位
     * @param length
     * @return
     */
    public static String formatSizeMb(long length) {
        float mbSize = Math.round(10.0F * (float) length / 1048576.0F) / 10.0F;
        return mbSize + " MB";
    }

    /**
     * 检查SDCARD是否可写
     * @return
     */
    public static boolean checkExternalStorageCanWrite() {
        try {
            boolean mouted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if(mouted) {
                boolean canWrite = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).canWrite();
                if(canWrite) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }




    /**
     * 返回文件的图标
     * @param fileName
     * @return
     */
    public static int getFileIcon(String fileName) {
        String fileType = fileName.toLowerCase();
        if(isDocument(fileType)) {
            return R.drawable.attach_file_icon_mailread_doc;
        }
        if(isPic(fileType)) {
            return R.drawable.attach_file_icon_mailread_img;
        }

        if(isCompresseFile(fileType)) {
            return R.drawable.attach_file_icon_mailread_zip;
        }
        if(isTextFile(fileType)) {
            return R.drawable.attach_file_icon_mailread_txt;
        }
        if(isPdf(fileType)) {
            return R.drawable.attach_file_icon_mailread_pdf;
        }

        if(isPPt(fileType)) {
            return R.drawable.attach_file_icon_mailread_ppt;
        }

        if(isXls(fileType)) {
            return R.drawable.attach_file_icon_mailread_xls;
        }

        return R.drawable.attach_file_icon_mailread_default;
    }

    /**
     * 是否图片
     * @param fileName
     * @return
     */
    public static boolean isPic(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".bmp")
                || lowerCase.endsWith(".png")
                || lowerCase.endsWith(".jpg")
                || lowerCase.endsWith(".jpeg")
                || lowerCase .endsWith(".gif");
    }

    /**
     * 是否压缩文件
     * @param fileName
     * @return
     */
    public static boolean isCompresseFile(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".rar")
                || lowerCase.endsWith(".zip")
                || lowerCase.endsWith(".7z")
                || lowerCase.endsWith("tar")
                || lowerCase.endsWith(".iso");
    }

    /**
     * 是否音频
     * @param fileName
     * @return
     */
    public static boolean isAudio(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".mp3")
                || lowerCase.endsWith(".wma")
                || lowerCase.endsWith(".mp4")
                || lowerCase.endsWith(".rm");
    }

    /**
     * 是否文档
     * @param fileName
     * @return
     */
    public static boolean isDocument(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".doc")
                || lowerCase.endsWith(".docx")
                || lowerCase .endsWith("wps");
    }

    /**
     * 是否Pdf
     * @param fileName
     * @return
     */
    public static boolean isPdf(String fileName) {
        return nullAsNil(fileName).toLowerCase().endsWith(".pdf");
    }

    /**
     * 是否Excel
     * @param fileName
     * @return
     */
    public static boolean isXls(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".xls")
                || lowerCase.endsWith(".xlsx");
    }

    /**
     * 是否文本文档
     * @param fileName
     * @return
     */
    public static boolean isTextFile(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".txt")
                || lowerCase .endsWith(".rtf");
    }

    /**
     * 是否Ppt
     * @param fileName
     * @return
     */
    public static boolean isPPt(String fileName) {
        String lowerCase = nullAsNil(fileName).toLowerCase();
        return lowerCase.endsWith(".ppt") || lowerCase .endsWith(".pptx");
    }

    /**
     * decode file length
     * @param filePath
     * @return
     */
    public static int decodeFileLength(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return 0;
        }
        File file = new File(filePath);
        if(!file.exists()) {
            return 0;
        }
        return (int)file.length();
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     *         null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static boolean checkFile(String filePath) {
        if(TextUtils.isEmpty(filePath) || !(new File(filePath).exists())) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param filePath
     * @param seek
     * @param length
     * @return
     */
    public static byte[] readFlieToByte (String filePath , int seek , int length) {
        if(TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if(!file.exists()) {
            return null;
        }
        if(length == -1) {
            length = (int)file.length();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bs = new byte[length];
            randomAccessFile.seek(seek);
            randomAccessFile.readFully(bs);
            randomAccessFile.close();
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(LogUtil.getLogUtilsTag(FileUtils.class), "readFromFile : errMsg = " + e.getMessage());
            return null;
        }
    }


    /**
     * 拷贝文件
     * @param fileDir
     * @param fileName
     * @param buffer
     * @return
     */
    public static int copyFile(String fileDir ,String fileName , byte[] buffer) {
        if(buffer == null) {
            return -2;
        }

        try {
            File file = new File(fileDir);
            if(!file.exists()) {
                file.mkdirs();
            }
            File resultFile = new File(file, fileName);
            if(!resultFile.exists()) {
                resultFile.createNewFile();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(resultFile, true));
            bufferedOutputStream.write(buffer);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return 0;

        } catch (Exception e) {
        }
        return -1;
    }


    /**
     * 根据文件名和后缀 拷贝文件
     * @param fileDir
     * @param fileName
     * @param ext
     * @param buffer
     * @return
     */
    public static int copyFile(String fileDir ,String fileName , String ext , byte[] buffer) {
        return copyFile(fileDir, fileName + ext, buffer);
    }


    /**
     * 根据后缀名判断是否是图片文件
     *
     * @param type
     * @return 是否是图片结果true or false
     */
    public static boolean isImage(String type) {
        if (type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"))) {
            return true;
        }
        return false;
    }

    /**
     * 过滤字符串为空
     * @param str
     * @return
     */
    public static String nullAsNil(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
    /**
     * Java文件操作 获取文件扩展名
     * Get the file extension, if no extension or file name
     *
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 返回文件名
     * @param pathName
     * @return
     */
    public static String getFilename(String pathName) {
        File file = new File(pathName);
        if(!file.exists()) {
            return "";
        }
        return file.getName();
    }

    /**
     * 获取展示手机的所有视频文件
     * @param context
     * @param extension
     */
    public static ArrayList<String> getSpecificTypeOfFile(Context context, String[] extension){
        ArrayList<String > list = new ArrayList<>();
        //从外存中获取
        Uri fileUri= MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection=new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection="";
        for(int i=0;i<extension.length;i++)
        {
            if(i!=0)
            {
                selection=selection+" OR ";
            }
            selection=selection+ MediaStore.Files.FileColumns.DATA+" LIKE '%"+extension[i]+"'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder= MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver=context.getContentResolver();
        //获取游标
        Cursor cursor=resolver.query(fileUri, projection, selection, null, sortOrder);
        if(cursor==null)
            return null;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if(cursor.moveToLast())
        {
            do{
                //输出文件的完整路径
                String data=cursor.getString(0);
                list.add(data);
                Log.d("tag", data);
            }while(cursor.moveToPrevious());
        }
        cursor.close();
        return list;
    }

    /**
     * 获取文件后缀名
     * @param path
     * @return
     */
    public static String getStringEndWith (String path){

        String[] strs = path.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            if (i == strs.length-1){
                return strs[i];
            }
        }

        return null;
    }
    /**
     * 获取文件后缀名
     * @param path
     * @return
     */
    public static String getStringPathWithoutName (String path){
        StringBuffer sb = new StringBuffer();
        String[] strs = path.split("\\.");
        strs = strs[0].split("/");
        for (int i = 0; i < strs.length; i++) {
            if (i == strs.length-2){
                sb.append(strs[i]);
                return sb.toString();
            }else{
                sb.append(strs[i]+"/");
            }
        }
        return null;
    }
}
