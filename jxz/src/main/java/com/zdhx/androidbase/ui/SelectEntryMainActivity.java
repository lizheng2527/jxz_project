package com.zdhx.androidbase.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.Treads;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.moralevaluation.MoralLevaluationActivity;
import com.zdhx.androidbase.ui.picturecarousel.PicsView;
import com.zdhx.androidbase.util.DateUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.MarqueeTextView;
import com.zdhx.androidbase.view.SimpleDraweeViewCompressed.FrescoUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lizheng on 2017/6/13.
 */

public class SelectEntryMainActivity extends BaseActivity {

    //轮播图title
    private String[] titles = { "吾问无为谓", "梦想信息", "忐忐忑忑", "更换合格", "顶顶顶顶", "啊啊啊啊" };
    //轮播空间viewPager
    private PicsView mPicsViewpager;
    private String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1497504804905&di=98f73b99062d0d6d064235dcbce52918&imgtype=0&src=http%3A%2F%2Fpic7.nipic.com%2F20100505%2F1995568_143945384546_2.jpg";

    private String url1 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1497504804905&di=52d479c05e32ad965060888e02e20ac7&imgtype=0&src=http%3A%2F%2Fpic6.nipic.com%2F20100328%2F3893684_111635088205_2.jpg";
    private String url2 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1497504804905&di=83162631316eae4ebbfdd94de051f73e&imgtype=0&src=http%3A%2F%2Fpic3.nipic.com%2F20090601%2F2399558_123447019_2.jpg";
    private String url3 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1497504804904&di=12c126a8d6feeb7e3691883a954cf37c&imgtype=0&src=http%3A%2F%2Fpic6.nipic.com%2F20100330%2F1480811_135518036625_2.jpg";
    //轮播网络图片集合
    private String[] images;

    private MarqueeTextView impNoticeContant;

    private LinearLayout btnStartMain;
    private LinearLayout btnStartMor;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_selentry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBarView().setVisibility(View.GONE);
        btnStartMain = (LinearLayout) findViewById(R.id.btnStartMain);
        btnStartMor = (LinearLayout) findViewById(R.id.btnStartMor);
        impNoticeContant = (MarqueeTextView) findViewById(R.id.impNoticeContant);
        initImpNotice();
        images = new String[]{url,url1,url2,url3};
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）

        mPicsViewpager = (PicsView) findViewById(R.id.picsviewpager);
        List<SimpleDraweeView> imgList = new ArrayList<>();
        for (String image : images) {
            SimpleDraweeView iv = new SimpleDraweeView(SelectEntryMainActivity.this);
            FrescoUtil.load(Uri.parse(image), iv, width, 220);
            imgList.add(iv);
        }
        // 初始化数据
        mPicsViewpager.addTitlesAndImages(titles, imgList);
        //mPicsViewpager.addImages(imgList);
        // 设置点击事件
        mPicsViewpager.setClickListener(new PicsView.OnClickListener() {
            @Override
            public void onClick(int position) {
                System.out.println("点击有效");
                Toast.makeText(SelectEntryMainActivity.this, "点击有效，位置为：" + position, Toast.LENGTH_SHORT).show();
            }
        });

        btnStartMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectEntryMainActivity.this,MainActivity.class));
            }
        });

        btnStartMor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectEntryMainActivity.this,MoralLevaluationActivity.class));
            }
        });
    }
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        // 停止图片轮播
        mPicsViewpager.stopAutoScroll();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 开启图片轮播
        mPicsViewpager.startAutoScroll();
        super.onResume();
    }
    private  HashMap<String,ParameterValue> hashMap;
    private static List<Treads.DataListBean> impNoticeDatas = new ArrayList<>();
    private Handler handler = new Handler();

    /***
     * 获取重要通知内容
     */
    private void initImpNotice(){
        hashMap = new HashMap<>();
        hashMap.put("tabsClick",new ParameterValue("important"));
        hashMap.put("startDate",new ParameterValue( "2012-01-01"));
        hashMap.put("tabsType",new ParameterValue("school"));
        hashMap.put("pageNo",new ParameterValue("1"));
        hashMap.put("endDate",new ParameterValue(DateUtil.getDateString(new Date(System.currentTimeMillis()))));
        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
        new ProgressThreadWrap(this, new RunnableWrap() {
            @Override
            public void run() {
                String treadsJson = null;
                try {
                    treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);
                    impNoticeDatas = treads.getDataList();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (impNoticeDatas == null || impNoticeDatas.size() == 0){
                                impNoticeContant.setText("暂无重要通知");
                            }else{
                                impNoticeContant.setText(impNoticeDatas.get(0).getContent());
                            }
                        }
                    },5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
