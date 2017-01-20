package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.ScroListBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.util.DateUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lizheng on 2016/12/24.
 * 主页
 */

public class ScroFragment extends Fragment {

    private GridView grid;

    private ArrayList<String> gridListDatas;

    private ScroGridAdapter gridAdapter;

    private Context context;

    private HomeViewPagerAdapter scroViewPagerAdapter;

    private ViewPager vp;

    private View studentScro;
    private View teacherScro;

    private List<View> viewPagerListDatas;

    private ListView studentLv;
    private ListView teacherLv;

    private List<ScroListBean> studentListDatas;
    private List<ScroListBean> teacherListDatas;

    private ScroListViewAdapter scroListViewAdapter;

    private HashMap<String,ParameterValue> map = new HashMap<>();

    private Handler handler = new Handler();
    //标记点击gridView时用来阻止重复访问
    private boolean currentTag = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scro, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initGridView ();
        initScroViewPager();

    }

    private String startDate;
    private String endDate;
    private String clickId;
    private TreeBean bean;

    /**
     * 初始化教师、学生ViewPager
     */
    private void initScroViewPager(){
        vp = (ViewPager) getView().findViewById(R.id.fragment_scro_viewpager);
        studentScro = LayoutInflater.from(context).inflate(R.layout.fragment_scro_viewpager_student,null);
        teacherScro = LayoutInflater.from(context).inflate(R.layout.fragment_scro_viewpager_teacher,null);
        studentLv = (ListView) studentScro.findViewById(R.id.fragment_scro_viewpager_student_listview);
        teacherLv = (ListView) teacherScro.findViewById(R.id.fragment_scro_viewpager_teacher_listview);
        viewPagerListDatas = new ArrayList<View>();
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            viewPagerListDatas.add(teacherScro);
            viewPagerListDatas.add(studentScro);
        }else{
            viewPagerListDatas.add(studentScro);
        }
        scroViewPagerAdapter = new HomeViewPagerAdapter(viewPagerListDatas);
        vp.setAdapter(scroViewPagerAdapter);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ScroGridAdapter.index = position;
                grid.setAdapter(gridAdapter);
//                if (currentTag){
                    if (MainActivity.ScroSearchTag){
                        if (MainActivity.map.size()>0){
                            startDate = (String) MainActivity.map.get("startDate");
                            endDate = (String) MainActivity.map.get("endDate");
                            bean = (TreeBean)MainActivity.map.get("ScroTreeBean");
                            if (bean != null){
                                clickId = bean.getId();
                            }
                        }
                        initMap(position,startDate,endDate,clickId);
                    }else{
                        initMap(position,"2012-01-01", DateUtil.getCurrDateString(),null);
                    }
                    initScroDatas(position);
                MainActivity.map.clear();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            initScroDatas(0);
        }else{
            initScroDatas(1);
        }
    }

    public void initMap(int tag,String startDate,String endDate,String clickId){
        this.startDate = startDate;
        this.endDate = endDate;
        this.clickId = clickId;
        map.clear();
//        map.putAll(ECApplication.getInstance().getLoginUrlMap());
        if (startDate != null){
            map.put("startDate",new ParameterValue(startDate));
        }else{
            map.put("startDate",new ParameterValue("2012-01-01"));
        }
        if (endDate != null){
            map.put("endDate",new ParameterValue(endDate));
        }else{
            map.put("endDate",new ParameterValue(DateUtil.getCurrDateString()));
        }
        if (tag == 0){
            map.put("showType",new ParameterValue("teacher"));
        }else{
            map.put("showType",new ParameterValue("student"));
        }
        if (clickId != null){
            map.put("clickId",new ParameterValue(clickId));
        }else{
            map.remove("clickId");
        }
    }

    /**
     * MainActivity调用刷新
     */
    public void refrushAdapter(int tag){
        if (tag == -1){
            //刷新学生
            vp.setCurrentItem(1);
            initScroDatas(1);
        }else{
            if (scroListViewAdapter != null){
                if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                    initScroDatas(ScroGridAdapter.index);
                }else{
                    initScroDatas(1);
                }
            }
        }

    }

    /**
     * 加载学生、教师积分数据
     * @param position
     */
    private void initScroDatas(int position){
        switch (position){
            case 0:
                new ProgressThreadWrap(context, new RunnableWrap() {
                    @Override
                    public void run() {
                        try {
                            map.putAll(ECApplication.getInstance().getLoginUrlMap());
                            String json = ZddcUtil.getRankList(map);
                            final List<ScroListBean> beans = new Gson().fromJson(json, new TypeToken<List<ScroListBean>>(){}.getType());
                            teacherListDatas = beans;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                                        scroListViewAdapter = new ScroListViewAdapter(beans,context);
                                        teacherLv.setAdapter(scroListViewAdapter);
                                        ProgressUtil.hide();
                                    }
                                }
                            },5);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case 1:
                new ProgressThreadWrap(context, new RunnableWrap() {
                    @Override
                    public void run() {
                        try {
                            map.putAll(ECApplication.getInstance().getLoginUrlMap());
                            String json = ZddcUtil.getRankList(map);
                            final List<ScroListBean> beans = new Gson().fromJson(json, new TypeToken<List<ScroListBean>>(){}.getType());
                            studentListDatas = beans;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scroListViewAdapter = new ScroListViewAdapter(beans,context);
                                    ProgressUtil.hide();
                                    studentLv.setAdapter(scroListViewAdapter);
                                }
                            },5);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
    /**
     * 初始化GridView（教师、学生）
     */
    private void initGridView (){
        grid = (GridView) getView().findViewById(R.id.fragment_scro_grid);
        gridListDatas = new ArrayList<String>();
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            gridListDatas.add("教师");
            gridListDatas.add("学生");
            initMap(0,"2012-01-01", DateUtil.getCurrDateString(),null);
        }else{
            grid.setNumColumns(1);
            gridListDatas.add("学生");
            initMap(1,"2012-01-01",DateUtil.getCurrDateString(),null);
        }
        gridAdapter = new ScroGridAdapter(gridListDatas,context,false);
        grid.setAdapter(gridAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ScroGridAdapter.index = i;
                grid.setAdapter(gridAdapter);
                ProgressUtil.show(context,"正在加载..");
                MainActivity.ScroSearchTag = false;
                if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                    initMap(i,"2012-01-01",DateUtil.getCurrDateString(),null);
                    initScroDatas(i);
                }else{
                    initMap(1,"2012-01-01",DateUtil.getCurrDateString(),null);
                    initScroDatas(1);
                }
                vp.setCurrentItem(i);
                currentTag = false;
            }
        });
    }

}
