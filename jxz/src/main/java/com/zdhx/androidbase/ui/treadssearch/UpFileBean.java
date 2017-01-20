
package com.zdhx.androidbase.ui.treadssearch;

import java.io.Serializable;

/**
 * 互动交流动态类
 */
public class UpFileBean implements Serializable {

    private String title;
    private String fileSize;
    private String userName;
    private int index;
    private String path;
    private Boolean checkTag;
    private String showName;
    private String absolutePath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Boolean getCheckTag() {
        return checkTag;
    }

    public void setCheckTag(Boolean checkTag) {
        this.checkTag = checkTag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UpFileBean() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
