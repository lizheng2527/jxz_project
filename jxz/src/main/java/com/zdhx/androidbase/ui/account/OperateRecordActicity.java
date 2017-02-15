package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.OperateBean;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class OperateRecordActicity extends BaseActivity{
    private TextView operatetitle;

    private ListView lv;

    private Context context;

    private List<OperateBean.DataListBean> beans;

    private HashMap<String,ParameterValue> map = new HashMap<>();

    private Handler handler = new Handler();

    private ImageView backImg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_operate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBarView().setVisibility(View.GONE);
        lv = (ListView) findViewById(R.id.operateLV);
        backImg = (ImageView) findViewById(R.id.activity_selectscro_goback);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        operatetitle = (TextView) findViewById(R.id.operatetitle);
        context = this;
        ProgressUtil.show(context,"正在加载数据");
        String resouceId = (String) MainActivity.map.get("resouceId");
        MainActivity.map.remove("resouceId");
        String type = (String) MainActivity.map.get("type");
        if (type.equals("1")){
            operatetitle.setText("浏览记录");
        }else{
            operatetitle.setText("下载记录");
        }
        MainActivity.map.remove("type");
        String pageNo = (String) MainActivity.map.get("pageNo");
        MainActivity.map.remove("pageNo");
        map.put("resouceId",new ParameterValue(resouceId));
        map.put("type",new ParameterValue(type));
        map.put("pageNo",new ParameterValue(pageNo));
        map.putAll(ECApplication.getInstance().getLoginUrlMap());
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                   String json = ZddcUtil.operateLog(map);
                    OperateBean bean = new Gson().fromJson(json,OperateBean.class);
                    beans = bean.getDataList();
                    if (beans !=null&&beans.size()>0){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lv.setAdapter(new OperateAdapter());
                            }
                        },1);
                    }
                    ProgressUtil.hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class OperateAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (vh == null){
                vh = new ViewHolder();
                convertView = View.inflate(context,R.layout.activity_operate_item,null);
                vh.count = (TextView) convertView.findViewById(R.id.operatecount);
                vh.name = (TextView) convertView.findViewById(R.id.operatename);
                vh.time = (TextView) convertView.findViewById(R.id.operatetime);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }
            vh.count.setText(position+1+"");
            vh.name.setText(beans.get(position).getUserName());
            vh.time.setText(beans.get(position).getCreateTime());
            return convertView;
        }
    }
    class ViewHolder{
        private TextView count,name,time;
    }
}
