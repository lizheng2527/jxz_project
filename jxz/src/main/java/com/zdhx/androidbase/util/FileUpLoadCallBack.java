package com.zdhx.androidbase.util;

/**
 * 附件上传进度回调
 * Created by Li.Xin @ ZDHX on 2017/4/13.
 */

public interface FileUpLoadCallBack {
    /**
     *
     * @param fileCount 文件个数
     * @param currentIndex 当前上传的下标
     * @param currentProgress 当前进度
     * @param allProgress 文件总进度
     */
    void upLoadProgress(int fileCount, int currentIndex, int currentProgress, int allProgress);
}
