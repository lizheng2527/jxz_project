package com.zdhx.androidbase.ui.xlistview;

import android.os.Handler;
import android.widget.BaseAdapter;

import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.util.DateUtil;

import java.util.List;

/**
 * Created by lizheng on 2016/12/29.
 */

public class XListViewUtils {


    public static int start = 0;
    public static int refreshCnt = 0;

    public static void onLoad(XListView listView) {
        listView.stopRefresh();
        listView.stopLoadMore();
        String time = DateUtil.getCurrDateSecondString();
        listView.setRefreshTime(time);
    }

    public static void initXListViewW(final XListView listView, List<WorkSpaceDatasBean> list, final BaseAdapter adapter){
        listView.setPullLoadEnable(true);
        final Handler mHandler = new Handler();
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start = ++refreshCnt;
//                        list.clear();
//                        list.add(new Treads());
//                        treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context);
//                        allreadsListView.setAdapter(treadsListViewAdapter);
                        adapter.notifyDataSetChanged();
                        onLoad(listView);
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        onLoad(listView);
                    }
                }, 2000);
            }
        });
    }
}
