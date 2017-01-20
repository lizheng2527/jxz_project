package com.zdhx.androidbase.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by General on 15/8/12.
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {

    /* 是否是本地资源 */
    private boolean isFromLocal = false;
    private int resId;// 页面item布局文件
    protected List<T> items = new ArrayList<T>();
    private LayoutInflater mInflater;

    private String[] titles = null;

    public BasePagerAdapter() {
        super();
    }

    public BasePagerAdapter(Context context, int resouceId) {
        this.mInflater = LayoutInflater.from(context);
        this.resId = resouceId;
    }

    public BasePagerAdapter(Context context, int resouceId, String[] titles) {
        this.mInflater = LayoutInflater.from(context);
        this.resId = resouceId;
        this.titles = titles;
    }

    public void setData(List<T> items) {
        if (items == null) {
            this.items = new ArrayList<T>();
        } else {
            this.items = items;
        }
    }

    public List<T> getData() {
        if (items == null) {
            return new ArrayList<T>();
        }
        return this.items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null) {
            return titles[position];
        }
        return super.getPageTitle(position);
    }

    @Override
    public Object instantiateItem(View view, int position) {

        if (isFromLocal()) {
            // 本地资源
            ((ViewPager) view).addView((View) items.get(position));
            return items.get(position);
        } else {
            // 动态加载数据
            View itemView = null;

            if (itemView == null) {
                itemView = mInflater.inflate(resId, null);
            }

            T item = items.get(position);

            initPagerItem(itemView, item, position);

            ((ViewPager) view).addView(itemView, 0);

            return itemView;
        }
    }

    /**
     * 初始化PagerView
     *
     * @param view
     * @param position
     */
    protected void initPagerItem(View view, T item, int position) {
    }

    //PhotoView
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    protected boolean isFromLocal() {
        return isFromLocal;
    }
}
