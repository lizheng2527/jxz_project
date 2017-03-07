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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.ScroListBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.util.DateUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private LinearLayout gridLinear;

    private TextView downTV;
    private TextView upTV;
    private TextView scroTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scro, container, false);
    }

    public void setListDatasToNull(){
        teacherListDatas.clear();
        studentListDatas.clear();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        downTV = (TextView) getView().findViewById(R.id.ondownsort);
        upTV = (TextView) getView().findViewById(R.id.onupsort);
        scroTV = (TextView) getView().findViewById(R.id.onscrosort);
        //下载量降序排列
        downTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                    switch (ScroGridAdapter.index){
                        case 0:
                            if (teacherListDatas != null){
                                Collections.sort(teacherListDatas, new DownComparator());
                                scroListViewAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 1:
                            if (studentListDatas != null){
                                Collections.sort(studentListDatas, new DownComparator());
                                scroListViewAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                }else{
                    if (studentListDatas != null){
                        Collections.sort(studentListDatas, new DownComparator());
                        scroListViewAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
        upTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                    switch (ScroGridAdapter.index){
                        case 0:
                            if (teacherListDatas != null){
                                Collections.sort(teacherListDatas, new UpComparator());
                                scroListViewAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 1:
                            if (studentListDatas != null){
                                Collections.sort(studentListDatas, new UpComparator());
                                scroListViewAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                }else{
                    if (studentListDatas != null){
                        Collections.sort(studentListDatas, new UpComparator());
                        scroListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        scroTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                    switch (ScroGridAdapter.index){
                        case 0:
                            if (teacherListDatas != null){
                                Collections.sort(teacherListDatas, new ScroComparator());
                                scroListViewAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 1:
                            if (studentListDatas != null){
                                Collections.sort(studentListDatas, new ScroComparator());
                                scroListViewAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                }else{
                    if (studentListDatas != null){
                        Collections.sort(studentListDatas, new ScroComparator());
                        scroListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        initGridView ();
        initScroViewPager();
    }

    // 自定义比较器：下载量降序排序
    private class DownComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            ScroListBean p1 = (ScroListBean) object1; // 强制转换
            ScroListBean p2 = (ScroListBean) object2;
            return Double.valueOf(p2.getDown()).compareTo(Double.valueOf(p1.getDown()));
        }
    }
    // 自定义比较器：上传量降序排序
    private class UpComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            ScroListBean p1 = (ScroListBean) object1; // 强制转换
            ScroListBean p2 = (ScroListBean) object2;
            return Double.valueOf(p2.getUpload()).compareTo(Double.valueOf(p1.getUpload()));
        }
    }
    // 自定义比较器：上传量降序排序
    private class ScroComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            ScroListBean p1 = (ScroListBean) object1; // 强制转换
            ScroListBean p2 = (ScroListBean) object2;
            return Double.valueOf(p2.getScore()).compareTo(Double.valueOf(p1.getScore()));
        }
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
        scroViewPagerAdapter = new HomeViewPagerAdapter(viewPagerListDatas,null);
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
                    ProgressUtil.show(context,"正在加载..");
                    initMap(position,startDate,endDate,clickId);
                    initScroDatas(position);
                }else{
                    initMap(position,"2012-01-01", DateUtil.getCurrDateString(),null);
//                    initScroDatas(position);
                    switch (position){
                        case 0:
                            if (teacherListDatas == null||teacherListDatas.size()<1){
                                ProgressUtil.show(context,"正在加载..");
                                initScroDatas(position);
                            }else{
                                ProgressUtil.hide();
                                scroListViewAdapter = new ScroListViewAdapter(teacherListDatas,context);
                                teacherLv.setAdapter(scroListViewAdapter);
                            }
                            break;
                        case 1:
                            if (studentListDatas == null||studentListDatas.size()<1){
                                ProgressUtil.show(context,"正在加载..");
                                initScroDatas(position);
                            }else{
                                ProgressUtil.hide();
                                scroListViewAdapter = new ScroListViewAdapter(studentListDatas,context);
                                studentLv.setAdapter(scroListViewAdapter);
                            }
                            break;
                    }
                }
                MainActivity.map.clear();
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
                    LogUtil.w("刷新学生积分");
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
                            final String json = ZddcUtil.getRankList(map);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final List<ScroListBean> beans = new Gson().fromJson(json, new TypeToken<List<ScroListBean>>(){}.getType());
                                    teacherListDatas = beans;
                                    if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                                        scroListViewAdapter = new ScroListViewAdapter(teacherListDatas,context);
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
                            final String json = ZddcUtil.getRankList(map);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final List<ScroListBean> beans = new Gson().fromJson(json, new TypeToken<List<ScroListBean>>(){}.getType());
                                    studentListDatas = beans;
                                    scroListViewAdapter = new ScroListViewAdapter(studentListDatas,context);
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
        gridLinear = (LinearLayout) getView().findViewById(R.id.gridLinear);
        grid = (GridView) getView().findViewById(R.id.fragment_scro_grid);
        gridListDatas = new ArrayList<String>();
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            gridListDatas.add("教师");
            gridListDatas.add("学生");
            initMap(0,"2012-01-01", DateUtil.getCurrDateString(),null);
        }else{
            grid.setNumColumns(1);
            gridListDatas.add("学生");
            gridLinear.setVisibility(View.GONE);
            initMap(1,"2012-01-01",DateUtil.getCurrDateString(),null);
        }
        gridAdapter = new ScroGridAdapter(gridListDatas,context,false);
        grid.setAdapter(gridAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                grid.setAdapter(gridAdapter);
                ProgressUtil.show(context,"正在加载..");
                MainActivity.ScroSearchTag = false;
                initMap(i,"2012-01-01",DateUtil.getCurrDateString(),null);
                if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
                    if (ScroGridAdapter.index == i){
                        initScroDatas(i);
                    }else{
                        vp.setCurrentItem(i);
                    }
                }else{
                    initScroDatas(1);
                }
                currentTag = false;
                ScroGridAdapter.index = i;

            }
        });
    }

}
