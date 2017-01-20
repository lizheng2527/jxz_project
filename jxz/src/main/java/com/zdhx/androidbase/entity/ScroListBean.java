
package com.zdhx.androidbase.entity;

import java.io.Serializable;

/**
 * 互动交流动态类
 */
public class ScroListBean implements Serializable {

    /**
     * score : 35287
     * rank : 1
     * down : 2446
     * upload : 3693
     * name : 杨淑明
     */

    private float score;
    private int rank;
    private int down;
    private int upload;
    private String name;

    public ScroListBean(float score, int rank, int down, int upload, String name) {
        this.score = score;
        this.rank = rank;
        this.down = down;
        this.upload = upload;
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
