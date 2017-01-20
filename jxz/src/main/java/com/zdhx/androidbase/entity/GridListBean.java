
package com.zdhx.androidbase.entity;

import java.io.Serializable;

/**
 * 互动交流动态类
 */
public class GridListBean implements Serializable {

    private int img;

    private String name;

    public GridListBean(String name, int img) {
        this.img = img;
        this.name = name;
    }

    public GridListBean() {
    }

    public GridListBean(String name) {

        this.name = name;
    }

    public GridListBean(int img) {

        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
