package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.GridListBean;

import java.util.List;

/**
 * Created by lizheng on 2016/12/24.
 * flag=false表示第一次加载。
 */

public class WorkSpaceGridAdapter extends BaseAdapter {
    private List<GridListBean> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean flag;
    public static int index = 0;


    public WorkSpaceGridAdapter(List<GridListBean> list, Context context, boolean flag){
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
            view = inflater.inflate(R.layout.fragment_workspace_grid_item,null);
            vh.tv = (TextView) view.findViewById(R.id.fragment_workspace_grid_item_tv);
            vh.img = (ImageView) view.findViewById(R.id.fragment_workspace_grid_item_img);
            vh.tvLine = (TextView) view.findViewById(R.id.fragment_home_workspace_item_tvline);
            vh.tvRightLine = (TextView) view.findViewById(R.id.fragment_workspace_grid_item_tvrightline);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }
        if (index != 0&&flag == false){
            vh.tvLine.setVisibility(View.INVISIBLE);
        }
        for (int j = 0; j <= i; j++) {//用来判断点击事件的下面横线的显示与否
            if (index == i){
                vh.tvLine.setVisibility(View.VISIBLE);
                vh.tv.setTextColor(Color.parseColor("#4cbbda"));
            } else{
                vh.tvLine.setVisibility(View.INVISIBLE);
                vh.tv.setTextColor(Color.parseColor("#000000"));
            }
        }
        if (i == list.size()-1){//最后一个item不显示右侧竖线
            vh.tvRightLine.setVisibility(View.INVISIBLE);
        }
        vh.tv.setText(list.get(i).getName());

        if (i != 0){
            vh.img.setImageResource(list.get(i).getImg());
            vh.img.setVisibility(View.VISIBLE);
        }else{
            vh.img.setVisibility(View.GONE);
        }

        return view;
    }
    class ViewHolder{
        private TextView tv;
        private ImageView img;
        private TextView tvRightLine;
        private TextView tvLine;
    }
}
