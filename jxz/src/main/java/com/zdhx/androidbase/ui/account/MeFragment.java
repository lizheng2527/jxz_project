package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdhx.androidbase.CircleImageViewWithWhite;
import com.zdhx.androidbase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizheng on 2016/12/24.
 * 主页
 */

public class MeFragment extends Fragment {

    private CircleImageViewWithWhite userHeadImg;

    private ListView lv;

    private MeListViewAdapter adapter;

    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        lv = (ListView) getView().findViewById(R.id.fragment_me_listview);

        adapter = new MeListViewAdapter(context);
        lv.setAdapter(adapter);
    }

}

class MeListViewAdapter extends BaseAdapter {

    private List<String> list;

    private Context context;

    public MeListViewAdapter(Context context) {
        this.context = context;
        list = new ArrayList<String>();
        list.add("手机号码");
        list.add("修改密码");
        list.add("我的课堂");
        list.add("问题反馈");
        list.add("设置");
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
        ViweHolder vh ;
        if (view == null){
            vh = new ViweHolder();
            view = View.inflate(context, R.layout.fragment_me_listview,null);
            view.setTag(vh);
        }else{
            vh = (ViweHolder) view.getTag();
        }
        vh.img = (ImageView) view.findViewById(R.id.fragment_me_listview_headimg);
        vh.name = (TextView) view.findViewById(R.id.fragment_me_listview_name);
        vh.phoneNumber = (TextView) view.findViewById(R.id.fragment_me_listview_phonenumber);
        vh.name.setText(list.get(i));
        switch (i){
            case 0:
                vh.img.setImageResource(R.drawable.phone);
                vh.phoneNumber.setVisibility(View.VISIBLE);
                vh.phoneNumber.setText("15311112222");
                break;
            case 1:
                vh.img.setImageResource(R.drawable.password);
                vh.phoneNumber.setVisibility(View.INVISIBLE);
                break;
            case 2:
                vh.phoneNumber.setVisibility(View.INVISIBLE);
                break;
            case 3:
            vh.img.setImageResource(R.drawable.set_up);
            vh.phoneNumber.setVisibility(View.INVISIBLE);
            break;
            case 4:
                vh.img.setImageResource(R.drawable.feedback);
                vh.phoneNumber.setVisibility(View.INVISIBLE);
                break;
        }
        return view;
    }

    class ViweHolder{
        private ImageView img;
        private TextView phoneNumber;
        private TextView name;
    }
}
