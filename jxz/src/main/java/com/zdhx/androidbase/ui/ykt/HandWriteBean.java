
package com.zdhx.androidbase.ui.ykt;

import java.io.Serializable;

/**
 * 手写笔记
 */
public class HandWriteBean implements Serializable {
    private String questionName;
    private String dateTime;
    private String previewQuestionImg;
    private String originaQuestionlImg;

    public String getOriginaQuestionlImg() {
        return originaQuestionlImg;
    }

    public void setOriginaQuestionlImg(String originaQuestionlImg) {
        this.originaQuestionlImg = originaQuestionlImg;
    }

    private String questionId;
    private String sex;
    private String originalImageAttachmentQuestionId;
    private String previewImageAttachmentQuestionId;

    public String getOriginalImageAttachmentQuestionId() {
        return originalImageAttachmentQuestionId;
    }

    public void setOriginalImageAttachmentQuestionId(String originalImageAttachmentQuestionId) {
        this.originalImageAttachmentQuestionId = originalImageAttachmentQuestionId;
    }

    public String getPreviewImageAttachmentQuestionId() {
        return previewImageAttachmentQuestionId;
    }

    public void setPreviewImageAttachmentQuestionId(String previewImageAttachmentQuestionId) {
        this.previewImageAttachmentQuestionId = previewImageAttachmentQuestionId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPreviewQuestionImg() {
        return previewQuestionImg;
    }

    public void setPreviewQuestionImg(String previewQuestionImg) {
        this.previewQuestionImg = previewQuestionImg;
    }
}

