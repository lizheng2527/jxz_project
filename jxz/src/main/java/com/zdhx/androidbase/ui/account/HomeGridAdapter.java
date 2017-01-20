package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zdhx.androidbase.R;

import java.util.List;

/**
 * Created by lizheng on 2016/12/24.
 * flag=false表示第一次加载。
 */

public class HomeGridAdapter extends BaseAdapter {
    private List<String> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean flag;
    /**
     * 当前展示的页码
     */
    public static int index = 0;


    public HomeGridAdapter(List<String> list, Context context, boolean flag){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (view == null){
            vh = new ViewHolder();
            view = inflater.inflate(R.layout.fragment_home_grid_item,null);
            vh.tv = (TextView) view.findViewById(R.id.fragment_home_grid_item_tv);
            vh.tvLine = (TextView) view.findViewById(R.id.fragment_home_grid_item_tvline);
            vh.tvRightLine = (TextView) view.findViewById(R.id.fragment_home_grid_item_tvrightline);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }
        if (index != 0&&flag == false){
            vh.tvLine.setVisibility(View.INVISIBLE);
        }
        for (int j = 0; j <= i; j++) {
            if (index == i){
                vh.tvLine.setVisibility(View.VISIBLE);
                vh.tv.setTextColor(Color.parseColor("#4cbbda"));
            } else{
                vh.tvLine.setVisibility(View.INVISIBLE);
                vh.tv.setTextColor(Color.parseColor("#000000"));
            }
        }
        if (i == list.size()-1){
            vh.tvRightLine.setVisibility(View.INVISIBLE);
        }
        vh.tv.setText(list.get(i));
        return view;
    }
    class ViewHolder{
        private TextView tv;
        private TextView tvLine;
        private TextView tvRightLine;
    }
}
