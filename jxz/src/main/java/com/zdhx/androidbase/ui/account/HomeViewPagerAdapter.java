package com.zdhx.androidbase.ui.account;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lizheng on 2016/12/24.
 */

public class HomeViewPagerAdapter extends PagerAdapter {

    private List<View> list;
    private List<String> listStr;

    public HomeViewPagerAdapter(List<View> list,List<String> listStr) {
        this.list = list;
        this.listStr = listStr;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (listStr != null){
            return listStr.get(position);
        }else{
            return null;
        }
    }


    @Override
    public int getCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (container != null)
        container.removeView((View) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
