package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.Treads;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.DateUtil;
import com.zdhx.androidbase.util.InputTools;
import com.zdhx.androidbase.util.NetUtils;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.zdhx.androidbase.ui.xlistview.XListViewUtils.onLoad;

/**
 * Created by lizheng on 2012/12/24.
 *
 * 点击GridView：是重新加载当前资源（清空原有的搜索条件）
 * 下拉刷新：重新加载当前资源（清空原有的搜索条件）
 * 左右滑动刷新：按照当前搜索添加展示各个类型文件资源
 * 点击左上角菜单：重新刷新资源，搜索条件不清空
 * 主页
 */

public class HomeFragment extends Fragment {
    /**
     * 记录当前加载的是否有下一页
     */
    private String status = "0";

    private int loadMorePager;

    private GridView grid;

    private List<String> gridDatas;

    private HomeGridAdapter homeGridAdapter;

    private ViewPager vp;
    /**
     * 动态数据的五个标题源
     */
    private List<View> viewPagerListDatas;
    /**
     * 动态数据的数据源
     */
    private List<Treads.DataListBean> viewPagerListTreadsDatas;
    /**
     * 动态数据的标题源适配器
     */
    private HomeViewPagerAdapter homeViewPagerAdapter;
    /**
     * 动态数据的适配器
     */
    private TreadsListViewAdapter treadsListViewAdapter;

    public XListView allreadsListView;
    private XListView interactreadsTtListView;
    private XListView myTreadsListView;
    private XListView resourcesTreadsListView;
    private XListView attendsTreadsListView;
    //上下文
    private Activity context;
    //碎片五个布局初始化
    private View allTreadsView ;
    private View interacttreadsTreadsView ;
    private View myTreadsView ;
    private View resourcesTreadsView ;
    private View attendsTreadsView ;
    //该五个数据源用来在每次滑动时判定的数据源
    private List<Treads.DataListBean> allDatas;
    private List<Treads.DataListBean> interactDatas;
    private List<Treads.DataListBean> myDatas;
    private List<Treads.DataListBean> resDatas;
    private List<Treads.DataListBean> attDatas;


    public static LinearLayout linear;
    private static EditText replyET;
    public static String communcationId = "";
    public static int position;
    private Button sendBtn;

    private int gridAdapterIndex ;

//    private static boolean workSpaceDatasIsLoad;

    /**
     * 清空缓存的展示数据
     */
    public void setDatasToNull(){
        allDatas = null;
        interactDatas = null;
        myDatas = null;
        resDatas = null;
        attDatas = null;
    }

    /**
     * 将当前展示的数据存放到对应的缓存中
     * @param datas
     */
    private void setDatasToNotNull(List<Treads.DataListBean> datas){
        switch (HomeGridAdapter.index){
            case 0:
                allDatas = datas;
                break;
            case 1:
                interactDatas = datas;
                break;
            case 2:
                myDatas = datas;
                break;
            case 3:
                resDatas = datas;
                break;
            case 4:
                attDatas = datas;
                break;
        }
    }

    //    public void setWorkSpaceDatasIsLoad(boolean tag){
//        workSpaceDatasIsLoad = tag;
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        linear = (LinearLayout) getView().findViewById(R.id.rl_bottom);
        replyET = (EditText) getView().findViewById(R.id.et_reply);
        sendBtn = (Button) getView().findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String replyMes = replyET.getText().toString().trim();
                ProgressUtil.show(context,"正在上传回复内容..");
                final HashMap<String,ParameterValue > hm = new HashMap();
                new ProgressThreadWrap(context, new RunnableWrap() {
                    @Override
                    public void run() {
                        if (replyMes == null ||replyMes.equals("")){
                            ToastUtil.showMessage("回复内容不能为空！");
                            return ;
                        }else{
                            hm.put("communcationId",new ParameterValue(communcationId));
                            hm.put("content",new ParameterValue(replyMes));
                            hm.put("uploadFiles",new ParameterValue(new ArrayList<File>()));
                            hm.put("uploadFileNames",new ParameterValue(""));
                            hm.putAll(ECApplication.getInstance().getLoginUrlMap());
                            try {
                                ZddcUtil.doReply(hm);
                            } catch (IOException e) {
                                e.printStackTrace();
                                ToastUtil.showMessage("发送失败！");
                            }
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.hide();
                                    hideEdit();
                                    Treads.DataListBean chi = new Treads.DataListBean();
                                    chi.setContent(replyMes);
                                    chi.setUserName(ECApplication.getInstance().getCurrentUser().getName());
                                    viewPagerListTreadsDatas.get(position).getChild().add(chi);
                                    treadsListViewAdapter.notifyDataSetChanged();
                                }
                            },5);
                        }
                    }
                }).start();
            }
        });
        initGridView();
        initViewPager();
    }
    /**
     * 初始化gridView
     * 每次点击事件即重新加载资源（原有的搜索条件清空）。
     */
    private void initGridView(){
        grid = (GridView) getView().findViewById(R.id.fragment_home_grid);
        gridDatas = new ArrayList<String>();
        gridDatas.add("全部动态");
        gridDatas.add("互动交流");
        gridDatas.add("我的动态");
        gridDatas.add("资源动态");
        gridDatas.add("我参与的");
        homeGridAdapter = new HomeGridAdapter(gridDatas,getActivity(),false);
        grid.setAdapter(homeGridAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeGridAdapter.index = i;
                grid.setAdapter(homeGridAdapter);
                vp.setCurrentItem(i);
                initTreadsDatas(i);//点击展示资源（重新加载）
                gridAdapterIndex = i;//更改页码
//                setDatasToNull();
            }
        });
    }

    /**
     *每次查询条件更改之后，默认加载全部，可以滑动查看其它类型的互动交流
     * @param position
     */
    public void setGridCurrent(int position){
        grid.setSelection(position);
        gridAdapterIndex = position;
        vp.setCurrentItem(position);
    }

    /**
     * 初始化HomeViewPager
     * 滑动事件：
     *      每次滑动判断即将要展示的页是否有数据，如果有数据，表示搜索条件没有修改，继续展示原有数据。
     *      如果即将要展示的数据不存在，证明搜索的条件已经发生改变，需要重新加载数据。
     */
    private void initViewPager (){

        vp = (ViewPager) getView().findViewById(R.id.fragment_home_viewpager);

        //实例化布局
        allTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_alltreads,null);
        interacttreadsTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_interacttreads,null);
        myTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_mytreads,null);
        resourcesTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_resourcestreads,null);
        attendsTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_attendstreads,null);
        //构造viewPager数据源
        viewPagerListDatas = new ArrayList<View>();
        viewPagerListDatas.add(allTreadsView);
        viewPagerListDatas.add(interacttreadsTreadsView);
        viewPagerListDatas.add(myTreadsView);
        viewPagerListDatas.add(resourcesTreadsView);
        viewPagerListDatas.add(attendsTreadsView);

        homeViewPagerAdapter = new HomeViewPagerAdapter(viewPagerListDatas);
        vp.setAdapter(homeViewPagerAdapter);


        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                HomeGridAdapter.index = position;
                grid.setAdapter(homeGridAdapter);
                //判断当前是否有数据，如果有数据，直接展示，如果没数据，重新加载
                switch (position){
                    case 0:
                        if (allDatas != null&&allDatas.size()>0){
                            treadsListViewAdapter = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                            treadsListViewAdapter.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId);
                            getSwitch();
                        }
                        break;
                    case 1:
                        if (interactDatas != null&&interactDatas.size()>0){
                            treadsListViewAdapter = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                            treadsListViewAdapter.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId);
                            getSwitch();
                        }
                        break;
                    case 2:
                        if (myDatas != null&&myDatas.size()>0){
                            treadsListViewAdapter = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                            treadsListViewAdapter.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId);
                            getSwitch();
                        }
                        break;
                    case 3:
                        if (resDatas != null&&resDatas.size()>0){
                            treadsListViewAdapter = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                            treadsListViewAdapter.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId);
                            getSwitch();
                        }
                        break;
                    case 4:
                        if (attDatas != null&&attDatas.size()>0){
                            treadsListViewAdapter = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                            treadsListViewAdapter.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId);
                            getSwitch();
                        }
                        break;
                }
//                initDatas(startDate,endDate,name,eclassId);
//                getSwitch();
                gridAdapterIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //第一次加载默认为加载全部
        initTreadsDatas(0);
    }

    /**
     * 根据当前展示的页，刷新相对应页的数据（应用给viewPager,MainActivity）
     * 判断语句：防止第一次加载出来之后直接滑动而出现空指针异常
     */
    public void getSwitch(){
        switch (HomeGridAdapter.index){
            case 0:
                if (allreadsListView ==null ){
                    allreadsListView = (XListView) allTreadsView.findViewById(R.id.fragment_home_viewpager_alltreads_listview);
                    initXListView(allreadsListView);
                    initXListViewDatas(allreadsListView);
                    setLVonTouch(allreadsListView);
                }
                initXListViewDatas(allreadsListView);
                break;
            case 1:
                if (interactreadsTtListView ==null ){
                    interactreadsTtListView = (XListView) interacttreadsTreadsView.findViewById(R.id.fragment_home_viewpager_interacttreads_listview);
                    initXListView(interactreadsTtListView);
                    initXListViewDatas(interactreadsTtListView);
                    setLVonTouch(interactreadsTtListView);
                }
                initXListViewDatas(interactreadsTtListView);
                break;
            case 2:
                if (myTreadsListView ==null ){
                    myTreadsListView = (XListView) myTreadsView.findViewById(R.id.fragment_home_viewpager_myTreads_listview);
                    initXListView(myTreadsListView);
                    initXListViewDatas(myTreadsListView);
                    setLVonTouch(myTreadsListView);
                }
                initXListViewDatas(myTreadsListView);
                break;
            case 3:
                if (resourcesTreadsListView ==null ){
                    resourcesTreadsListView = (XListView) resourcesTreadsView.findViewById(R.id.fragment_home_viewpager_resourcesTreads_listview);
                    initXListView(resourcesTreadsListView);
                    initXListViewDatas(resourcesTreadsListView);
                    setLVonTouch(resourcesTreadsListView);
                }
                initXListViewDatas(resourcesTreadsListView);
                break;
            case 4:
                if (attendsTreadsListView ==null ){
                    attendsTreadsListView = (XListView) attendsTreadsView.findViewById(R.id.fragment_home_viewpager_attendsTreads_listview);
                    initXListView(attendsTreadsListView);
                    initXListViewDatas(attendsTreadsListView);
                    setLVonTouch(attendsTreadsListView);
                }
                initXListViewDatas(attendsTreadsListView);
                break;
        }
    }

    /**
     * 分页显示活动内容
     * @param positon
     * 第一次默认加载调用；
     * 每次gridItem点击事件调用（清空搜索条件，重新下载数据）
     */
    private void initTreadsDatas(int positon){
        switch (positon){
            case 0://加载全部
                allreadsListView = (XListView) allTreadsView.findViewById(R.id.fragment_home_viewpager_alltreads_listview);
                if (viewPagerListTreadsDatas == null){
                    viewPagerListTreadsDatas = new ArrayList<Treads.DataListBean>();
                    treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
//                    allreadsListView.setAdapter(treadsListViewAdapter);
                }
                initXListView(allreadsListView);
                initDatas(null,null,null,null);
                initXListViewDatas(allreadsListView);
                setLVonTouch(allreadsListView);
                break;
            case 1://加载互动交流
                interactreadsTtListView = (XListView) interacttreadsTreadsView.findViewById(R.id.fragment_home_viewpager_interacttreads_listview);
                treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
                initXListView(interactreadsTtListView);
                initDatas(null,null,null,null);
                initXListViewDatas(interactreadsTtListView);
                setLVonTouch(interactreadsTtListView);
                break;
            case 2://加载我的动态
                myTreadsListView = (XListView) myTreadsView.findViewById(R.id.fragment_home_viewpager_myTreads_listview);
                treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
                initXListView(myTreadsListView);
                initDatas(null,null,null,null);
                initXListViewDatas(myTreadsListView);
                setLVonTouch(myTreadsListView);
                break;
            case 3://加载资源动态
                resourcesTreadsListView = (XListView) resourcesTreadsView.findViewById(R.id.fragment_home_viewpager_resourcesTreads_listview);
                treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
                initXListView(resourcesTreadsListView);
                initDatas(null,null,null,null);
                initXListViewDatas(resourcesTreadsListView);
                setLVonTouch(resourcesTreadsListView);
                break;
            case 4://加载我参与的
                attendsTreadsListView = (XListView) attendsTreadsView.findViewById(R.id.fragment_home_viewpager_attendsTreads_listview);
                treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
                initXListView(attendsTreadsListView);
                initDatas(null,null,null,null);
                initXListViewDatas(attendsTreadsListView);
                setLVonTouch(attendsTreadsListView);
                break;
        }
    }
    public HashMap<String,ParameterValue> hashMap = ECApplication.getInstance().getCurrentUserMap();
    public Handler handler = new Handler();

    private String startDate;
    private String endDate;
    private String eclassId;
    private String name;

    /**
     * 当MainActivity的menu菜单点击时，重新加载搜索条件
     */
    public void initDatasForMain(){
        initDatas(startDate,endDate,eclassId,name);
    }

    /**
     * 根据传入的条件查询配置当前的搜索条件
     * @param startDate
     * @param endDate
     * @param name
     * @param eclassId
     */
    public void initDatas(String startDate,String endDate,String name,String eclassId){

        if (!NetUtils.isNetworkConnected()){
            ToastUtil.showMessage("当前网络不可用!");
            return;
        }
        hashMap.clear();
        //开始时间
        if (startDate == null){
            this.startDate = "2012-01-01";
        }else{
            this.startDate = startDate;
        }
        //结束时间
        if (endDate == null){
            this.endDate = DateUtil.getDateString(new Date(System.currentTimeMillis()));
        }else{
            this.endDate = endDate;
        }
        //文件名或者上传者姓名
        if (name == null){

        }else{
            hashMap.put("name",new ParameterValue(name));
        }
        this.name = name;
        //班级ID
        if (eclassId == null){

        }else{
            hashMap.put("eclassId",new ParameterValue(eclassId));
        }
        this.eclassId = eclassId;

        ProgressUtil.show(context,"正在刷新..");
        hashMap.put("startDate",new ParameterValue(this.startDate));
        hashMap.put("endDate",new ParameterValue(this.endDate));
        switch (HomeGridAdapter.index){
            case 0:
                hashMap.put("tabsClick",new ParameterValue("allcommucation"));
                break;
            case 1:
                hashMap.put("tabsClick",new ParameterValue("commucation"));
                break;
            case 2:
                hashMap.put("tabsClick",new ParameterValue("mycommucation"));
                break;
            case 3:
                hashMap.put("tabsClick",new ParameterValue("resourcecommucation"));
                break;
            case 4:
                hashMap.put("tabsClick",new ParameterValue("reply"));
                break;
        }
        switch (MainActivity.menuPosition){
            case 0 :
                hashMap.put("tabsType",new ParameterValue("eclass"));
                break;
            case 1 :
                hashMap.put("tabsType",new ParameterValue("grade"));
                break;
            case 2 :
                hashMap.put("tabsType",new ParameterValue("school"));
                break;
            case 3 :
                hashMap.put("tabsType",new ParameterValue("teacher"));
                break;
        }
        hashMap.put("pageNo",new ParameterValue("1"));
        loadMorePager = 1;
    }

    /**
     * 根据当前的加载条件来加载数据
     * @param listView
     */
    public void initXListViewDatas(final XListView listView){
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                    String treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);
                           status = treads.getStatus();//记录当前是否有下一页
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            viewPagerListTreadsDatas = treads.getDataList();
                            if (treads != null) {
                                if (viewPagerListTreadsDatas == null){
                                    viewPagerListTreadsDatas = new ArrayList<Treads.DataListBean>();
                                }
                                treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
//                                switch (HomeGridAdapter.index){
//                                    case 0:
//                                        allDatas = viewPagerListTreadsDatas;
//                                        break;
//                                    case 1:
//                                        interactDatas = viewPagerListTreadsDatas;
//                                        break;
//                                    case 2:
//                                        myDatas = viewPagerListTreadsDatas;
//                                        break;
//                                    case 3:
//                                        resDatas = viewPagerListTreadsDatas;
//                                        break;
//                                    case 4:
//                                        attDatas = viewPagerListTreadsDatas;
//                                        break;
//                                }
                                setDatasToNotNull(viewPagerListTreadsDatas);
                                if (listView != null){
                                    listView.setAdapter(treadsListViewAdapter);
                                    ProgressUtil.hide();
                                }else{
                                    allreadsListView.setAdapter(treadsListViewAdapter);
                                    ProgressUtil.hide();
                                }
                            } else {
                                ToastUtil.showMessage("请检查网络");
                                ProgressUtil.hide();
                            }
                        }
                    }, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        }).start();
    }

    /**
     * 点击回复图片按钮时，显示输入框
     * @param communcationId
     * @param position
     */
    public static void showEdit(String communcationId,int position){
        linear.setVisibility(View.VISIBLE);
        replyET.setText("");
        InputTools.KeyBoard(replyET,"open");
        HomeFragment.communcationId = communcationId;
        HomeFragment.position = position;
    }

    /**
     * 隐藏输入框
     */
    public static void hideEdit(){
        InputTools.KeyBoard(replyET,"close");
        linear.setVisibility(View.GONE);
    }

    /**
     * 当触摸外部时，输入框隐藏
     * @param lv
     */
    private void setLVonTouch(XListView lv){
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideEdit();
                return false;
            }
        });
    }
    public int start = 0;
    public int refreshCnt = 0;

    /**
     * 初始化下拉刷新，加载更多
     * 下拉刷新：配置默认搜索条件（清空当前搜索条件）
     * 加载更多：
     * @param listView
     */
    public void initXListView(final XListView listView){
        listView.setPullLoadEnable(true);
        final Handler mHandler = new Handler();
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                start = ++refreshCnt;
                ProgressUtil.hide();

                switch (gridAdapterIndex){
                    case 0:
                        initDatasForRefrush(startDate,endDate,name,eclassId,allreadsListView);
                        break;
                    case 1:
                        initDatasForRefrush(startDate,endDate,name,eclassId,interactreadsTtListView);
                        break;
                    case 2:
                        initDatasForRefrush(startDate,endDate,name,eclassId,myTreadsListView);
                        break;
                    case 3:
                        initDatasForRefrush(startDate,endDate,name,eclassId,resourcesTreadsListView);
                        break;
                    case 4:
                        initDatasForRefrush(startDate,endDate,name,eclassId,attendsTreadsListView);
                        break;
                }

            }


            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onDatasChanged(listView);

                    }
                }, 1);
            }
        });
    }

    /**
     * 供给MainActivity发布成功动态后，清空所有搜索条件来加载数据
     */
    public void refurseTreadsAdapter(){

        switch (HomeGridAdapter.index){
            case 0:
                initDatas(null,null,null,null);
                initXListViewDatas(allreadsListView);
                break;
            case 1:
                initDatas(null,null,null,null);
                initXListViewDatas(interactreadsTtListView);
                break;
            case 2:
                initDatas(null,null,null,null);
                initXListViewDatas(myTreadsListView);
                break;
            case 3:
                initDatas(null,null,null,null);
                initXListViewDatas(resourcesTreadsListView);
                break;
            case 4:
                initDatas(null,null,null,null);
                initXListViewDatas(attendsTreadsListView);
                break;
        }
    }

    /**
     * 加载更多时调用的方法
     * @param listView
     */
    private void onDatasChanged(final XListView listView){
        if (status.equals("1")){
            ToastUtil.showMessage("已无更多..");
            onLoad(listView);
            return;
        }
        hashMap.put("startDate",new ParameterValue(startDate));
        hashMap.put("endDate",new ParameterValue(endDate));
        if (name != null){
            hashMap.put("name",new ParameterValue(name));
        }
        if (eclassId != null){
            hashMap.put("eclassId",new ParameterValue(eclassId));
        }
        switch (HomeGridAdapter.index){
            case 0:
                hashMap.put("tabsClick",new ParameterValue("allcommucation"));
                break;
            case 1:
                hashMap.put("tabsClick",new ParameterValue("commucation"));
                break;
            case 2:
                hashMap.put("tabsClick",new ParameterValue("mycommucation"));
                break;
            case 3:
                hashMap.put("tabsClick",new ParameterValue("resourcecommucation"));
                break;
            case 4:
                hashMap.put("tabsClick",new ParameterValue("reply"));
                break;
        }
        switch (MainActivity.menuPosition){
            case 0 :
                hashMap.put("tabsType",new ParameterValue("eclass"));
                break;
            case 1 :
                hashMap.put("tabsType",new ParameterValue("grade"));
                break;
            case 2 :
                hashMap.put("tabsType",new ParameterValue("school"));
                break;
            case 3 :
                hashMap.put("tabsType",new ParameterValue("teacher"));
                break;
        }
        hashMap.put("pageNo",new ParameterValue(loadMorePager+1+""));
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                try {
                    String treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);
                    status = treads.getStatus();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            List<Treads.DataListBean> list = treads.getDataList();
                            switch (HomeGridAdapter.index){
                                case 0:
                                    if (list != null&&list.size()>0){
                                        allDatas.addAll(list);
                                    }
                                    break;
                                case 1:
                                    if (list != null&&list.size()>0){
                                        interactDatas.addAll(list);
                                    }
                                    break;
                                case 2:
                                    if (list != null&&list.size()>0){
                                        myDatas.addAll(list);
                                    }
                                    break;
                                case 3:
                                    if (list != null&&list.size()>0){
                                        resDatas.addAll(list);
                                    }
                                    break;
                                case 4:
                                    if (list != null&&list.size()>0){
                                        attDatas.addAll(list);
                                    }
                                    break;
                            }
                            treadsListViewAdapter.notifyDataSetChanged();
                            if (list == null||list.size()== 0 ){
//                                ToastUtil.showMessage("已无更多");
                            }else{
                                loadMorePager++;
                            }
                            onLoad(listView);
                        }
                    }, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        }).start();
    }
    /**
     * 只有下拉刷新时使用的方法
     * @param startDate
     * @param endDate
     * @param name
     * @param eclassId
     * @param listView
     */
    public void initDatasForRefrush(String startDate,String endDate,String name,String eclassId,final XListView listView){

        if (!NetUtils.isNetworkConnected()){
            ToastUtil.showMessage("当前网络不可用!");
            return;
        }
        hashMap.clear();
        //开始时间
        if (startDate == null){
            this.startDate = "2012-01-01";
        }else{
            this.startDate = startDate;
        }
        //结束时间
        if (endDate == null){
            this.endDate = DateUtil.getDateString(new Date(System.currentTimeMillis()));
        }else{
            this.endDate = endDate;
        }
        //文件名或者上传者姓名
        if (name == null){

        }else{
            hashMap.put("name",new ParameterValue(name));
        }
        //班级ID
        if (eclassId == null){

        }else{
            hashMap.put("eclassId",new ParameterValue(eclassId));
        }

        hashMap.put("startDate",new ParameterValue(this.startDate));
        hashMap.put("endDate",new ParameterValue(this.endDate));
        switch (HomeGridAdapter.index){
            case 0:
                hashMap.put("tabsClick",new ParameterValue("allcommucation"));
                break;
            case 1:
                hashMap.put("tabsClick",new ParameterValue("commucation"));
                break;
            case 2:
                hashMap.put("tabsClick",new ParameterValue("mycommucation"));
                break;
            case 3:
                hashMap.put("tabsClick",new ParameterValue("resourcecommucation"));
                break;
            case 4:
                hashMap.put("tabsClick",new ParameterValue("reply"));
                break;
        }
        switch (MainActivity.menuPosition){
            case 0 :
                hashMap.put("tabsType",new ParameterValue("eclass"));
                break;
            case 1 :
                hashMap.put("tabsType",new ParameterValue("grade"));
                break;
            case 2 :
                hashMap.put("tabsType",new ParameterValue("school"));
                break;
            case 3 :
                hashMap.put("tabsType",new ParameterValue("teacher"));
                break;
        }
        hashMap.put("pageNo",new ParameterValue("1"));
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                try {
                    String treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);
                    status = treads.getStatus();
//                    switch (HomeGridAdapter.index){
//                        case 0:
//                            allDatas = viewPagerListTreadsDatas;
//                            break;
//                        case 1:
//                            interactDatas = viewPagerListTreadsDatas;
//                            break;
//                        case 2:
//                            myDatas = viewPagerListTreadsDatas;
//                            break;
//                        case 3:
//                            resDatas = viewPagerListTreadsDatas;
//                            break;
//                        case 4:
//                            attDatas = viewPagerListTreadsDatas;
//                            break;
//                    }
                    setDatasToNotNull(viewPagerListTreadsDatas);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (treads != null) {
                                ProgressUtil.hide();
                                viewPagerListTreadsDatas = treads.getDataList();
                                if (viewPagerListTreadsDatas == null){
                                    viewPagerListTreadsDatas = new ArrayList<Treads.DataListBean>();
                                }
                                treadsListViewAdapter = new TreadsListViewAdapter(viewPagerListTreadsDatas,context,HomeFragment.this);
                                if (listView != null){
                                    listView.setAdapter(treadsListViewAdapter);
                                }else{
                                    allreadsListView.setAdapter(treadsListViewAdapter);
                                }
                                onLoad(listView);
                            } else {
                                ToastUtil.showMessage("请检查网络");
                            }
                        }
                    }, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        }).start();

    }

}


