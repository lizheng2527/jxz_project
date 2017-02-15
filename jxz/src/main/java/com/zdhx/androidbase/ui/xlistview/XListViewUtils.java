package com.zdhx.androidbase.ui.xlistview;

import com.zdhx.androidbase.util.DateUtil;

/**
 * Created by lizheng on 2016/12/29.
 */

public class XListViewUtils {


    public static int start = 0;

    public static void onLoad(XListView listView) {
        listView.stopRefresh();
        listView.stopLoadMore();
        String time = DateUtil.getCurrDateSecondString();
        listView.setRefreshTime(time);
    }
}
