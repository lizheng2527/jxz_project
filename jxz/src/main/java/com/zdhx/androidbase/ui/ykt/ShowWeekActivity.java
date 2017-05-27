package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.base.BaseActivity;

import java.util.ArrayList;


public class ShowWeekActivity extends BaseActivity{

    private ListView lv;

    private Context context;

    private ArrayList<TweekVo> beans;

    private ImageView backImg;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_showweek;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBarView().setVisibility(View.GONE);
        context = this;
        lv = (ListView) findViewById(R.id.searchWeekLV);
        beans = YKTFragment.tweekVos;
        lv.setAdapter(new SpinnerAdapter());
        backImg = (ImageView) findViewById(R.id.activity_showweek_goback);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w("YKT",beans.get(position).toString());
                ShowWeekActivity.this.finish();
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class SpinnerAdapter extends BaseAdapter{

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
            if (convertView == null){
                vh = new ViewHolder();
                convertView = View.inflate(context,R.layout.spinner_items1,null);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }
            vh.spinnerTV = (TextView) convertView.findViewById(R.id.spinnerTV);
            vh.spinnerTV.setText(beans.get(position).getName());
//            if (position == 0){
//                vh.spinnerTV.setBackgroundColor(Color.parseColor("#eeeeee"));
//            }else{
//                if (position%2 == 0){
//                    vh.spinnerTV.setBackgroundColor(Color.parseColor("#eeeeee"));
//                }
//            }
            return convertView;
        }
    }
    class ViewHolder{
        private TextView spinnerTV;
    }
}
