package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.Treads;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.DateUtil;
import com.zdhx.androidbase.util.InputTools;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.NetUtils;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;
import com.zdhx.androidbase.view.pagerslidingtab.PagerSlidingTabStrip;

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
 * 下拉刷新：重新加载当前资源（根据现有的搜索条件）
 * 左右滑动刷新：按照当前搜索添加展示各个类型文件资源
 * 点击左上角菜单：重新刷新资源，搜索条件不清空
 * 主页
 */

public class HomeFragment extends Fragment {

    /**
     * 记录当前加载的是否有下一页
     */
    private String status0 = "1";
    private String status1 = "1";
    private String status2 = "1";
    private String status3 = "1";
    private String status4 = "1";
    private String status5 = "1";

    private static int loadMorePager0;
    private static int loadMorePager1;
    private static int loadMorePager2;
    private static int loadMorePager3;
    private static int loadMorePager4;
    private static int loadMorePager5;

    private List<String> gridDatas;

    private ViewPager vp;
    /**
     * 动态数据的五个标题源
     */
    private List<View> viewPagerListDatas;
    /**
     * 动态数据的标题源适配器
     */
    public HomeViewPagerAdapter homeViewPagerAdapter;
    /**
     * 动态数据的适配器
     */
    private TreadsListViewAdapter treadsListViewAdapter0;
    private TreadsListViewAdapter treadsListViewAdapter1;
    private TreadsListViewAdapter treadsListViewAdapter2;
    private TreadsListViewAdapter treadsListViewAdapter3;
    private TreadsListViewAdapter treadsListViewAdapter4;
    private TreadsListViewAdapter treadsListViewAdapter5;
    public static int isSelectIndex;

    private void setTreadsAdapterNotify(int index){
        switch (index){
            case 0:
                treadsListViewAdapter0.notifyDataSetChanged();
                break;
            case 1:
                treadsListViewAdapter1.notifyDataSetChanged();
                break;
            case 2:
                treadsListViewAdapter2.notifyDataSetChanged();
                break;
            case 3:
                treadsListViewAdapter3.notifyDataSetChanged();
                break;
            case 4:
                treadsListViewAdapter4.notifyDataSetChanged();
                break;
            case 5:
                treadsListViewAdapter5.notifyDataSetChanged();
                break;
        }
    }
    public XListView allreadsListView;

    private XListView interactreadsTtListView;
    private XListView myTreadsListView;
    private XListView resourcesTreadsListView;
    private XListView attendsTreadsListView;
    private XListView impTreadsListView;
    //上下文
    private Activity context;


    //碎片五个布局初始化
    private View allTreadsView ;
    private View interacttreadsTreadsView ;
    private View myTreadsView ;
    private View resourcesTreadsView ;
    private View attendsTreadsView ;



    /**
     * 动态数据的数据源
     */
    private List<Treads.DataListBean> viewPagerListTreadsDatas;
    //该五个数据源用来在每次滑动时判定的数据源
    private static List<Treads.DataListBean> allDatas = new ArrayList<>();
    private static List<Treads.DataListBean> interactDatas = new ArrayList<>();
    private static List<Treads.DataListBean> myDatas = new ArrayList<>();
    private static List<Treads.DataListBean> resDatas = new ArrayList<>();
    private static List<Treads.DataListBean> attDatas = new ArrayList<>();
    private static List<Treads.DataListBean> impDatas = new ArrayList<>();

//    public static TextView replyTV;


    private LinearLayout linear;
    private EditText replyET;
    //临时的数据ID，即回复之后，服务器的返回值，防止在回复之后，在删除时出现ID不对情况
//    public static String communcationId = "";
    //应用给按钮点击回复，指定回复，指定删除使用的当前显示动态数据集合的下标
    public static int position;
    //发布回复的提交按钮
//    private Button sendBtn;
    private ECProgressDialog dialog;


    /**
     * 互动交流删除一条数据，对应的相应模块删除
     * @param bean = 删除的对象
     */
    public void removeForDeleteTreads(Treads.DataListBean bean){
        if (allDatas != null&&allDatas.size()>0&&isSelectIndex != 0){
            for (int i = 0; i < allDatas.size(); i++) {
                if (allDatas.get(i).getId().equals(bean.getId())){
                    allDatas.remove(i);
                    break;
                }
            }
        }
        if (interactDatas != null&&interactDatas.size()>0&&isSelectIndex != 1){
            for (int i = 0; i < interactDatas.size(); i++) {
                if (interactDatas.get(i).getId().equals(bean.getId())){
                    interactDatas.remove(i);
                    break;
                }
            }
        }
        if (myDatas != null&&myDatas.size()>0&&isSelectIndex != 2){

            for (int i = 0; i < myDatas.size(); i++) {
                if (myDatas.get(i).getId().equals(bean.getId())){
                    myDatas.remove(i);
                    break;
                }
            }
        }
        if (impDatas != null&&impDatas.size()>0&&isSelectIndex != 3){
            for (int i = 0; i < impDatas.size(); i++) {
                if (impDatas.get(i).getId().equals(bean.getId())){
                    impDatas.remove(i);
                    break;
                }
            }
        }
        if (attDatas != null&&attDatas.size()>0&&isSelectIndex != 4){
            for (int i = 0; i < attDatas.size(); i++) {
                if (attDatas.get(i).getId().equals(bean.getId())){
                    attDatas.remove(i);
                    break;
                }
            }
        }
        if (resDatas != null&&resDatas.size()>0&&isSelectIndex != 5){
            for (int i = 0; i < resDatas.size(); i++) {
                if (resDatas.get(i).getId().equals(bean.getId())){
                    resDatas.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 当互动交流数据有改变时，改变本地的其他模块数据
     * @param bean = 本地改变的对象
     */
    public void upDateTreads(Treads.DataListBean bean){
        if (allDatas != null&&allDatas.size()>0&&isSelectIndex != 0){
            upDataChild(allDatas,bean);
        }
        if (interactDatas != null&&interactDatas.size()>0&&isSelectIndex != 1){
            upDataChild(interactDatas,bean);
        }
        if (myDatas != null&&myDatas.size()>0&&isSelectIndex != 2){
            upDataChild(myDatas,bean);
        }
        if (impDatas != null&&impDatas.size()>0&&isSelectIndex != 3){
            upDataChild(impDatas,bean);
        }
        if (attDatas != null&&attDatas.size()>0&&isSelectIndex != 4){
            upDataChild(attDatas,bean);
        }
        if (resDatas != null&&resDatas.size()>0&&isSelectIndex != 5){
            upDataChild(resDatas,bean);
        }
    }

    /**
     * 本地数据改变时调用此方法
     * @param beans = 集合
     * @param bean = 对象
     */
    private void upDataChild(List<Treads.DataListBean> beans,Treads.DataListBean bean){

        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getId().equals(bean.getId())){
                beans.get(i).setBrowse(bean.getBrowse());
                beans.get(i).setDown(bean.getDown());
                beans.get(i).setPraiseNames(bean.getPraiseNames());
                beans.get(i).setAllReplyCount(bean.getAllReplyCount());
                beans.get(i).setChild(bean.getChild());
                beans.get(i).setLaunch(bean.getLaunch());
                break;
            }
        }
    }

    /**
     * 清空缓存的展示数据
     */
    public void setDatasToNull(){
        allDatas = null;
        interactDatas = null;
        myDatas = null;
        resDatas = null;
        attDatas = null;
        impDatas = null;
    }


    /**
     * 设置加载更多页码
     * @param count = 页码
     */
    private void setLoadMorePager(int count,int index){
        switch (index){
            case 0:
                loadMorePager0 = count;
                break;
            case 1:
                loadMorePager1 = count;
                break;
            case 2:
                loadMorePager2 = count;
                break;
            case 5:
                loadMorePager3 = count;
                break;
            case 4:
                loadMorePager4 = count;
                break;
            case 3:
                loadMorePager5 = count;
                break;
        }
    }
    /**
     * 设置是否有下一页标记（如果没有下一页，隐藏加载更多）
     * @param count 页码的标记
     */
    private void setStatus(String count,int index){
        switch (index){
            case 0:
                status0 = count;
                if (status0.equals("1")){
                    allreadsListView.setPullLoadEnable(false);
                    allreadsListView.setDividerHeight(0);
                }else{
                    allreadsListView.setPullLoadEnable(true);
                    allreadsListView.setDividerHeight(1);
                }
                break;
            case 1:
                status1 = count;
                if (status1.equals("1")){
                    interactreadsTtListView.setPullLoadEnable(false);
                    interactreadsTtListView.setDividerHeight(0);
                }else{
                    interactreadsTtListView.setPullLoadEnable(true);
                    interactreadsTtListView.setDividerHeight(1);
                }
                break;
            case 2:
                status2 = count;
                if (status2.equals("1")){
                    myTreadsListView.setPullLoadEnable(false);
                    myTreadsListView.setDividerHeight(0);
                }else{
                    myTreadsListView.setPullLoadEnable(true);
                    myTreadsListView.setDividerHeight(1);
                }
                break;
            case 5:
                status3 = count;
                if (status3.equals("1")){
                    resourcesTreadsListView.setPullLoadEnable(false);
                    resourcesTreadsListView.setDividerHeight(0);
                }else{
                    resourcesTreadsListView.setPullLoadEnable(true);
                    resourcesTreadsListView.setDividerHeight(1);
                }
                break;
            case 4:
                status4 = count;
                if (status4.equals("1")){
                    attendsTreadsListView.setPullLoadEnable(false);
                    attendsTreadsListView.setDividerHeight(0);
                }else{
                    attendsTreadsListView.setPullLoadEnable(true);
                    attendsTreadsListView.setDividerHeight(1);
                }
                break;
            case 3:
                status5 = count;
                if (status5.equals("1")){
                    impTreadsListView.setPullLoadEnable(false);
                    impTreadsListView.setDividerHeight(0);
                }else{
                    impTreadsListView.setPullLoadEnable(true);
                    impTreadsListView.setDividerHeight(1);
                }
                break;
        }
    }
    /**
     * 获取是否有下一页标记
     */
    private String getStatus(int index){
        switch (index){
            case 0:
                return status0 ;
            case 1:
                return status1 ;
            case 2:
                return status2 ;
            case 5:
                return status3 ;
            case 4:
                return status4 ;
            case 3:
                return status5 ;
        }
        return "1";
    }
    /**
     * 获取加载更多页码
     */
    private int getLoadMorePager(){

        switch (isSelectIndex){
            case 0:
                return loadMorePager0;
            case 1:
                return loadMorePager1;
            case 2:
                return loadMorePager2;
            case 5:
                return loadMorePager3;
            case 4:
                return loadMorePager4;
            case 3:
                return loadMorePager5;
        }
        return 1;
    }

    /**
     * 将当前展示的数据存放到对应的缓存中
     * @param datas = 缓存的数据
     */
    private void setDatasToNotNull(List<Treads.DataListBean> datas){
        switch (isSelectIndex){
            case 0:
                allDatas = datas;
                break;
            case 1:
                interactDatas = datas;
                break;
            case 2:
                myDatas = datas;
                break;
            case 5:
                resDatas = datas;
                break;
            case 4:
                attDatas = datas;
                break;
            case 3:
                impDatas = datas;
                break;
        }
    }

    //    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void setReplyName(){
        setTreadsAdapterNotify(isSelectIndex);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        dialog = new ECProgressDialog(context);
        dialog.setPressText("正在加载数据");
        if (getView() == null){
            return;
        }
        linear = (LinearLayout) getView().findViewById(R.id.rl_bottom);
        replyET = (EditText) getView().findViewById(R.id.et_reply);
//        sendBtn = (Button) getView().findViewById(R.id.btn_send);
        initGridView();
        initViewPager();
    }

    public void resetTreadsDatas(String id, String replyMes, final int position, String newId, String userName, List<File> files) {
        switch (isSelectIndex){
            case 0:
                toListDatas(allDatas,position,id,replyMes,newId,userName);
                break;
            case 1:
                toListDatas(interactDatas,position,id,replyMes,newId,userName);
                break;
            case 2:
                toListDatas(myDatas,position,id,replyMes,newId,userName);
                break;
            case 5:
                toListDatas(resDatas,position,id,replyMes,newId,userName);
                break;
            case 4:
                toListDatas(attDatas,position,id,replyMes,newId,userName);
                break;
            case 3:
                toListDatas(impDatas,position,id,replyMes,newId,userName);
                break;
        }

        if (files != null){
            replyMap = new HashMap<>();
            replyMap.put("commucationId",new ParameterValue(id));
            replyMap.putAll(ECApplication.getInstance().getLoginUrlMap());
            switch (isSelectIndex){
                case 0:
                    treadsListViewAdapter0.initReplyDatas("HomeFragment",replyMap,position);
                    break;
                case 1:
                    treadsListViewAdapter1.initReplyDatas("HomeFragment",replyMap,position);
                    break;
                case 2:
                    treadsListViewAdapter2.initReplyDatas("HomeFragment",replyMap,position);
                    break;
                case 3:
                    treadsListViewAdapter3.initReplyDatas("HomeFragment",replyMap,position);
                    break;
                case 4:
                    treadsListViewAdapter4.initReplyDatas("HomeFragment",replyMap,position);
                    break;
                case 5:
                    treadsListViewAdapter5.initReplyDatas("HomeFragment",replyMap,position);
                    break;
            }
        }
    }
    HashMap<String,ParameterValue> replyMap;

    private void setAllReplyCount(){
        switch (isSelectIndex){
            case 0:
                allDatas.get(position).setAllReplyCount(allDatas.get(position).getAllReplyCount()+1);
                upDateTreads(allDatas.get(position));
                break;
            case 1:
                interactDatas.get(position).setAllReplyCount(interactDatas.get(position).getAllReplyCount()+1);
                upDateTreads(interactDatas.get(position));
                break;
            case 2:
                myDatas.get(position).setAllReplyCount(myDatas.get(position).getAllReplyCount()+1);
                upDateTreads(myDatas.get(position));
                break;
            case 5:
                resDatas.get(position).setAllReplyCount(resDatas.get(position).getAllReplyCount()+1);
                upDateTreads(resDatas.get(position));
                break;
            case 4:
                attDatas.get(position).setAllReplyCount(attDatas.get(position).getAllReplyCount()+1);
                upDateTreads(attDatas.get(position));
                break;
            case 3:
                impDatas.get(position).setAllReplyCount(impDatas.get(position).getAllReplyCount()+1);
                upDateTreads(impDatas.get(position));
                break;
        }

    }

    private void toListDatas(List<Treads.DataListBean> beans, int position,String id,String replyMes,String newId,String userName){
        if (beans.get(position).getId().equals(id)){
            Treads.DataListBean bean = new Treads.DataListBean();
            bean.setUserName(userName);
            bean.setContent(replyMes);
            bean.setId(newId);
            bean.setCreateTime("今天  "+DateUtil.getCurrTimeSecondString());
            bean.setChild(new ArrayList<Treads.DataListBean>());
            bean.setCanDelete("yes");
            setAllReplyCount();
            beans.get(position).getChild().add(bean);
        }else{
            listDatas(beans.get(position).getChild(),position,id,replyMes,newId,userName);
        }
    }

    private void listDatas(List<Treads.DataListBean> beans, int position,String id,String replyMes,String newId,String userName){
        Log.w("listDatas",position+"");
        for (int i = 0; i < beans.size(); i++) {
            LogUtil.w(beans.get(i).getContent());
            if (beans.get(i).getChild() != null && beans.get(i).getChild().size()>0){
                listDatas(beans.get(i).getChild(),i,id,replyMes,newId,userName);
            }
            if (beans.get(i).getId().equals(id)){
                Treads.DataListBean bean = new Treads.DataListBean();
                bean.setUserName(userName);
                bean.setContent(replyMes);
                bean.setChild(new ArrayList<Treads.DataListBean>());
                bean.setId(newId);
                bean.setCreateTime("今天  "+DateUtil.getCurrTimeSecondString());
                bean.setCanDelete("yes");
                setAllReplyCount();
                beans.get(i).getChild().add(bean);
                return;
            }
        }
    }


    /**
     * 初始化gridView
     * 每次点击事件即重新加载资源（原有的搜索条件清空）。
     */
    private void initGridView(){
        gridDatas = new ArrayList<>();
        gridDatas.add("全部动态");
        gridDatas.add("互动交流");
        gridDatas.add("我的动态");
        gridDatas.add("重要通知");
        gridDatas.add("我参与的");
        gridDatas.add("资源动态");
    }
    public PagerSlidingTabStrip mPagerSlidingTabStrip;
    private void initView() {
        if (getView() == null){
            return;
        }
        mPagerSlidingTabStrip = (PagerSlidingTabStrip)getView().findViewById(R.id.tabs);
        vp.setOffscreenPageLimit(1);
        homeViewPagerAdapter = new HomeViewPagerAdapter(viewPagerListDatas,gridDatas);
        vp.setAdapter(homeViewPagerAdapter);
        mPagerSlidingTabStrip.setShouldExpand(true);
        mPagerSlidingTabStrip.setViewPager(vp,null,null);
        mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#4cbbda"));
        mPagerSlidingTabStrip.setTabPaddingLeftRight(10);


        mPagerSlidingTabStrip.setOnTabClickListener(new PagerSlidingTabStrip.OnTabClickListener() {
            @Override
            public void onTabClick(View tab, int position) {
                isClickSelect = true;
                onEmptyClick(position);
            }
        });


        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isClickSelect){
                    isClickSelect = false;
                    return;
                }
                isSelectIndex = position;
                //判断当前是否有数据，如果有数据，直接展示，如果没数据，重新加载
                switch (position){
                    case 0:
                        if (allDatas != null&&allDatas.size()>0){
                            treadsListViewAdapter0.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId,position);
                            getSwitch();
                        }
                        break;
                    case 1:
                        if (interactDatas != null&&interactDatas.size()>0){
                            treadsListViewAdapter1.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId,position);
                            getSwitch();
                        }
                        break;
                    case 2:
                        if (myDatas != null&&myDatas.size()>0){
                            treadsListViewAdapter2.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId,position);
                            getSwitch();
                        }
                        break;
                    case 5:
                        if (resDatas != null&&resDatas.size()>0){
                            treadsListViewAdapter5.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId,position);
                            getSwitch();
                        }
                        break;
                    case 4:
                        if (attDatas != null&&attDatas.size()>0){
                            treadsListViewAdapter4.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId,position);
                            getSwitch();
                        }
                        break;
                    case 3:
                        if (impDatas != null&&impDatas.size()>0){
                            treadsListViewAdapter3.notifyDataSetChanged();
                        }else{
                            initDatas(startDate,endDate,name,eclassId,position);
                            getSwitch();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private boolean isClickSelect = false;

    public void onEmptyClick(int i){
        isSelectIndex = i;
        initTreadsDatas(i);//点击展示资源（重新加载）
    }

    /**
     * 初始化HomeViewPager
     * 滑动事件：
     *      每次滑动判断即将要展示的页是否有数据，如果有数据，表示搜索条件没有修改，继续展示原有数据。
     *      如果即将要展示的数据不存在，证明搜索的条件已经发生改变，需要重新加载数据。
     */
    private View impTreadsView;
    private void initViewPager (){
        if (getView() == null){
            return;
        }
        vp = (ViewPager) getView().findViewById(R.id.fragment_home_viewpager);

        //实例化布局重要通知
        impTreadsView = View.inflate(context,R.layout.fragment_impnotice,null);
        allTreadsView = View.inflate(context,R.layout.fragment_home_viewpager_alltreads,null);
        interacttreadsTreadsView = View.inflate(context,R.layout.fragment_home_viewpager_interacttreads,null);
        myTreadsView = View.inflate(context,R.layout.fragment_home_viewpager_mytreads,null);
        resourcesTreadsView = View.inflate(context,R.layout.fragment_home_viewpager_resourcestreads,null);
        attendsTreadsView = View.inflate(context,R.layout.fragment_home_viewpager_attendstreads,null);
//        impTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_impnotice,null);
//        allTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_alltreads,null);
//        interacttreadsTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_interacttreads,null);
//        myTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_mytreads,null);
//        resourcesTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_resourcestreads,null);
//        attendsTreadsView = LayoutInflater.from(context).inflate(R.layout.fragment_home_viewpager_attendstreads,null);
        //构造viewPager数据源
        viewPagerListDatas = new ArrayList<>();
        viewPagerListDatas.add(allTreadsView);
        viewPagerListDatas.add(interacttreadsTreadsView);
        viewPagerListDatas.add(myTreadsView);
        viewPagerListDatas.add(impTreadsView);
        viewPagerListDatas.add(attendsTreadsView);
        viewPagerListDatas.add(resourcesTreadsView);


        allreadsListView = (XListView) allTreadsView.findViewById(R.id.fragment_home_viewpager_alltreads_listview);
        impTreadsListView = (XListView) impTreadsView.findViewById(R.id.fragment_home_viewpager_important_listview);
        interactreadsTtListView = (XListView) interacttreadsTreadsView.findViewById(R.id.fragment_home_viewpager_interacttreads_listview);
        myTreadsListView = (XListView) myTreadsView.findViewById(R.id.fragment_home_viewpager_myTreads_listview);
        attendsTreadsListView = (XListView) attendsTreadsView.findViewById(R.id.fragment_home_viewpager_attendsTreads_listview);
        resourcesTreadsListView = (XListView) resourcesTreadsView.findViewById(R.id.fragment_home_viewpager_resourcesTreads_listview);

        initView();
        //第一次加载默认为加载全部
        initTreadsDatas(0);
    }

    /**
     * 根据当前展示的页，刷新相对应页的数据（应用给viewPager,MainActivity）
     * 判断语句：防止第一次加载出来之后直接滑动而出现空指针异常
     */
    public void getSwitch(){
        switch (isSelectIndex){
            case 0:
                initXListView(allreadsListView,0);
                initXListViewDatas(allreadsListView,0);
                break;
            case 1:
                initXListView(interactreadsTtListView,1);
                initXListViewDatas(interactreadsTtListView,1);
                break;
            case 2:
                initXListView(myTreadsListView,2);
                initXListViewDatas(myTreadsListView,2);
                break;
            case 5:
                initXListView(resourcesTreadsListView,5);
                initXListViewDatas(resourcesTreadsListView,5);
                break;
            case 4:
                initXListView(attendsTreadsListView,4);
                initXListViewDatas(attendsTreadsListView,4);
                break;
            case 3:
                initXListView(impTreadsListView,3);
                initXListViewDatas(impTreadsListView,3);
                break;
        }
    }

    /**
     * 分页显示活动内容
     * @param positon 正在加载的页码
     * 第一次默认加载调用；
     * 每次gridItem点击事件调用（清空搜索条件，重新下载数据）
     */
    private void initTreadsDatas(int positon){
        switch (positon){
            case 0://加载全部
                if (viewPagerListTreadsDatas == null){
                    viewPagerListTreadsDatas = new ArrayList<>();
                    treadsListViewAdapter0 = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                }
                initXListView(allreadsListView,0);
                initDatas(null,null,null,null,0);
                initXListViewDatas(allreadsListView,0);
                setLVonTouch(allreadsListView);
                break;
            case 1://加载互动交流
                treadsListViewAdapter1 = new TreadsListViewAdapter(interactDatas,context,HomeFragment.this);
                initXListView(interactreadsTtListView,1);
                initDatas(null,null,null,null,1);
                initXListViewDatas(interactreadsTtListView,1);
                setLVonTouch(interactreadsTtListView);
                break;
            case 2://加载我的动态
                treadsListViewAdapter2 = new TreadsListViewAdapter(myDatas,context,HomeFragment.this);
                initXListView(myTreadsListView,2);
                initDatas(null,null,null,null,2);
                initXListViewDatas(myTreadsListView,2);
                setLVonTouch(myTreadsListView);
                break;
            case 5://加载资源动态
                treadsListViewAdapter5 = new TreadsListViewAdapter(resDatas,context,HomeFragment.this);
                initXListView(resourcesTreadsListView,5);
                initDatas(null,null,null,null,5);
                initXListViewDatas(resourcesTreadsListView,5);
                setLVonTouch(resourcesTreadsListView);
                break;
            case 4://加载我参与的
                treadsListViewAdapter4 = new TreadsListViewAdapter(attDatas,context,HomeFragment.this);
                initXListView(attendsTreadsListView,4);
                initDatas(null,null,null,null,4);
                initXListViewDatas(attendsTreadsListView,4);
                setLVonTouch(attendsTreadsListView);
                break;
            case 3://加载重要通知
                treadsListViewAdapter3 = new TreadsListViewAdapter(impDatas,context,HomeFragment.this);
                initXListView(impTreadsListView,3);
                initDatas(null,null,null,null,3);
                initXListViewDatas(impTreadsListView,3);
                setLVonTouch(impTreadsListView);
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
        initDatas(startDate,endDate,name,eclassId,isSelectIndex);
    }
    /**
     * 根据传入的条件查询配置当前的搜索条件
     * @param startDate = 开始时间
     * @param endDate = 结束时间
     * @param name = 查询名称
     * @param eclassId = 班级ID
     */
    public void initDatas(String startDate,String endDate,String name,String eclassId,int index){

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
        if (name != null){
            hashMap.put("name",new ParameterValue(name));
        }
        this.name = name;
        //班级ID
        if (eclassId != null){
            hashMap.put("eclassId",new ParameterValue(eclassId));
        }
        this.eclassId = eclassId;

        hashMap.put("startDate",new ParameterValue(this.startDate));
        hashMap.put("endDate",new ParameterValue(this.endDate));
        switch (index){
            case 0:
                hashMap.put("tabsClick",new ParameterValue("allcommucation"));
                break;
            case 1:
                hashMap.put("tabsClick",new ParameterValue("commucation"));
                break;
            case 2:
                hashMap.put("tabsClick",new ParameterValue("mycommucation"));
                break;
            case 5:
                hashMap.put("tabsClick",new ParameterValue("resourcecommucation"));
                break;
            case 4:
                hashMap.put("tabsClick",new ParameterValue("reply"));
                break;
            case 3:
                hashMap.put("tabsClick",new ParameterValue("important"));
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
        setLoadMorePager(1,index);
    }

    /**
     * 根据当前的加载条件来加载数据
     * @param listView 根据当前条件刷新的listView
     */
    public void initXListViewDatas(final XListView listView,final int index){

        if(listView != null){
            Log.w("","");
        }
        dialog.show();
        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
        ProgressThreadWrap p = new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (treads != null) {
                                setStatus(treads.getStatus(),index);
                                viewPagerListTreadsDatas = treads.getDataList();
                                switch (index){
                                    case 0:
                                        allDatas = viewPagerListTreadsDatas;
                                        break;
                                    case 1:
                                        interactDatas = viewPagerListTreadsDatas;
                                        break;
                                    case 2:
                                        myDatas = viewPagerListTreadsDatas;
                                        break;
                                    case 3:
                                        impDatas = viewPagerListTreadsDatas;
                                        break;
                                    case 4:
                                        attDatas = viewPagerListTreadsDatas;
                                        break;
                                    case 5:
                                        resDatas = viewPagerListTreadsDatas;
                                        break;
                                }
                                setDatasToNotNull(viewPagerListTreadsDatas);
                                switch (index){
                                    case 0:
                                        treadsListViewAdapter0 = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                                        allreadsListView.setAdapter(treadsListViewAdapter0);
                                        allreadsListView.setEmptyView(allTreadsView.findViewById(R.id.emptyall));
                                        break;
                                    case 1:
                                        treadsListViewAdapter1 = new TreadsListViewAdapter(interactDatas,context,HomeFragment.this);
                                        interactreadsTtListView.setAdapter(treadsListViewAdapter1);
                                        interactreadsTtListView.setEmptyView(interacttreadsTreadsView.findViewById(R.id.emptyinter));
                                        break;
                                    case 2:
                                        treadsListViewAdapter2 = new TreadsListViewAdapter(myDatas,context,HomeFragment.this);
                                        myTreadsListView.setAdapter(treadsListViewAdapter2);
                                        myTreadsListView.setEmptyView(myTreadsView.findViewById(R.id.emptymy));
                                        break;
                                    case 5:
                                        treadsListViewAdapter5 = new TreadsListViewAdapter(resDatas,context,HomeFragment.this);
                                        resourcesTreadsListView.setAdapter(treadsListViewAdapter5);
                                        resourcesTreadsListView.setEmptyView(resourcesTreadsView.findViewById(R.id.emptyres));
                                        break;
                                    case 4:
                                        treadsListViewAdapter4 = new TreadsListViewAdapter(attDatas,context,HomeFragment.this);
                                        attendsTreadsListView.setAdapter(treadsListViewAdapter4);
                                        attendsTreadsListView.setEmptyView(attendsTreadsView.findViewById(R.id.emptyatt));
                                        break;
                                    case 3:
                                        treadsListViewAdapter3 = new TreadsListViewAdapter(impDatas,context,HomeFragment.this);
                                        impTreadsListView.setAdapter(treadsListViewAdapter3);
                                        impTreadsListView.setEmptyView(impTreadsView.findViewById(R.id.emptyimp));
                                        break;
                                }
                                ProgressUtil.hide();
                                if (dialog.isShowing()){
                                    dialog.dismiss();
                                }
                            } else {
                                ToastUtil.showMessage("请检查网络");
                                ProgressUtil.hide();
                                if (dialog.isShowing()){
                                    dialog.dismiss();
                                }
                            }
                        }
                    }, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        });
        p.start();
    }


    /**
     * 隐藏输入框
     */
    public void hideEdit(){
        linear.setVisibility(View.GONE);
    }

    /**
     * 当触摸外部时，输入框隐藏
     * @param lv  触摸的lv
     */
    private void setLVonTouch(XListView lv){
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (InputTools.KeyBoard(replyET)){
                    hideEdit();
                }
                return false;
            }
        });
    }
    public int start = 0;
    public int refreshCnt = 0;

    /**
     * 初始化下拉刷新，加载更多
     * @param listView 初始化的listView
     */
    public void initXListView(final XListView listView,final int loadIndex){
        final Handler mHandler = new Handler();
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                start = ++refreshCnt;
                ProgressUtil.hide();
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                switch (loadIndex){
                    case 0:
                        initDatasForRefrush(startDate,endDate,name,eclassId,allreadsListView,loadIndex);
                        break;
                    case 1:
                        initDatasForRefrush(startDate,endDate,name,eclassId,interactreadsTtListView,loadIndex);
                        break;
                    case 2:
                        initDatasForRefrush(startDate,endDate,name,eclassId,myTreadsListView,loadIndex);
                        break;
                    case 5:
                        initDatasForRefrush(startDate,endDate,name,eclassId,resourcesTreadsListView,loadIndex);
                        break;
                    case 4:
                        initDatasForRefrush(startDate,endDate,name,eclassId,attendsTreadsListView,loadIndex);
                        break;
                    case 3:
                        initDatasForRefrush(startDate,endDate,name,eclassId,impTreadsListView,loadIndex);
                        break;
                }

            }


            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onDatasChanged(listView,loadIndex);
                    }
                }, 1);
            }
        });

        listView.setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (visibleItemCount+firstVisibleItem == totalItemCount-1&&getStatus(loadIndex).equals("0")){
                    listView.startLoadMore();
                }
            }
        });
    }

    /**
     * 供给MainActivity发布成功动态后，清空所有搜索条件来加载数据
     */
    public void refurseTreadsAdapter(){

        initDatas(null,null,null,null,isSelectIndex);
        switch (isSelectIndex){
            case 0:
                initXListViewDatas(allreadsListView,0);
                break;
            case 1:
                initXListViewDatas(interactreadsTtListView,1);
                break;
            case 2:
                initXListViewDatas(myTreadsListView,2);
                break;
            case 5:
                initXListViewDatas(resourcesTreadsListView,3);
                break;
            case 4:
                initXListViewDatas(attendsTreadsListView,4);
                break;
            case 3:
                initXListViewDatas(impTreadsListView,5);
                break;
        }
    }


    private static boolean isLoadMoring0 = false;
    private static boolean isLoadMoring1 = false;
    private static boolean isLoadMoring2 = false;
    private static boolean isLoadMoring3 = false;
    private static boolean isLoadMoring4 = false;
    private static boolean isLoadMoring5 = false;
    private static boolean isRefshing0 = false;
    private static boolean isRefshing1 = false;
    private static boolean isRefshing2 = false;
    private static boolean isRefshing3 = false;
    private static boolean isRefshing4 = false;
    private static boolean isRefshing5 = false;
    private void setIsRefshing(boolean b,int index) {
        switch (index) {
            case 0:
                isRefshing0 = b;
                break;
            case 1:
                isRefshing1 = b;
                break;
            case 2:
                isRefshing2 = b;
                break;
            case 5:
                isRefshing3 = b;
                break;
            case 4:
                isRefshing4 = b;
                break;
            case 3:
                isRefshing5 = b;
                break;
        }
    }
    //
    private void setIsLoadMoring(int index,boolean b){
        switch (index){
            case 0:
                isLoadMoring0 = b;
                break;
            case 1:
                isLoadMoring1 = b;
                break;
            case 2:
                isLoadMoring2 = b;
                break;
            case 5:
                isLoadMoring3 = b;
                break;
            case 4:
                isLoadMoring4 = b;
                break;
            case 3:
                isLoadMoring5 = b;
                break;
        }
    }
    private boolean getIsLoadMoring(int index){
        switch (index){
            case 0:
                return isLoadMoring0;
            case 1:
                return isLoadMoring1;
            case 2:
                return isLoadMoring2;
            case 5:
                return isLoadMoring3;
            case 4:
                return isLoadMoring4;
            case 3:
                return isLoadMoring5;
        }
        return false;
    }
    private boolean getIsRefshing(int index){
        switch (index){
            case 0:
                return isRefshing0;
            case 1:
                return isRefshing1;
            case 2:
                return isRefshing2;
            case 5:
                return isRefshing3;
            case 4:
                return isRefshing4;
            case 3:
                return isRefshing5;
        }
        return false;
    }

    /**
     * 加载更多时调用的方法
     * @param listView 加载更多时需要刷新的对象
     */
    private void onDatasChanged(final XListView listView,final int loadIndex){
        if (getIsLoadMoring(loadIndex)){
            return;
        }
        setIsLoadMoring(loadIndex,true);
        hashMap.put("startDate",new ParameterValue(startDate));
        hashMap.put("endDate",new ParameterValue(endDate));
        if (name != null){
            hashMap.put("name",new ParameterValue(name));
        }
        if (eclassId != null){
            hashMap.put("eclassId",new ParameterValue(eclassId));
        }
        switch (loadIndex){
            case 0:
                hashMap.put("tabsClick",new ParameterValue("allcommucation"));
                break;
            case 1:
                hashMap.put("tabsClick",new ParameterValue("commucation"));
                break;
            case 2:
                hashMap.put("tabsClick",new ParameterValue("mycommucation"));
                break;
            case 5:
                hashMap.put("tabsClick",new ParameterValue("resourcecommucation"));
                break;
            case 4:
                hashMap.put("tabsClick",new ParameterValue("reply"));
                break;
            case 3:
                hashMap.put("tabsClick",new ParameterValue("important"));
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
        hashMap.put("pageNo",new ParameterValue(getLoadMorePager()+1+""));
        if (getStatus(loadIndex).equals("1")){
            return;
        }
        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
        ProgressThreadWrap d  = new ProgressThreadWrap(context,new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            setStatus(treads.getStatus(),loadIndex);
                            setLoadMorePager(getLoadMorePager()+1,loadIndex);
                            List<Treads.DataListBean> list = treads.getDataList();
                            if (list != null&&list.size()>0){
                                switch (loadIndex){
                                    case 0:
                                        if (allDatas == null)
                                            allDatas = new ArrayList<>();
                                        allDatas.addAll(list);
                                        RelativeLayout lin = (RelativeLayout) allTreadsView.findViewById(R.id.linear_all);
                                        lin.removeView(allreadsListView);
                                        lin.addView(allreadsListView);
                                        treadsListViewAdapter0.notifyDataSetChanged();
                                        break;
                                    case 1:
                                        if (interactDatas == null)
                                            interactDatas = new ArrayList<>();
                                        interactDatas.addAll(list);
                                        RelativeLayout lin1 = (RelativeLayout) interacttreadsTreadsView.findViewById(R.id.linear_interact);
                                        lin1.removeView(interactreadsTtListView);
                                        lin1.addView(interactreadsTtListView);
                                        treadsListViewAdapter1.notifyDataSetChanged();
                                        break;
                                    case 2:
                                        if (myDatas == null)
                                            myDatas = new ArrayList<>();
                                        myDatas.addAll(list);
                                        RelativeLayout lin2 = (RelativeLayout) myTreadsView.findViewById(R.id.linear_my);
                                        lin2.removeView(myTreadsListView);
                                        lin2.addView(myTreadsListView);
                                        treadsListViewAdapter2.notifyDataSetChanged();
                                        break;
                                    case 5:
                                        if (resDatas == null)
                                            resDatas = new ArrayList<>();
                                        resDatas.addAll(list);
                                        RelativeLayout lin3 = (RelativeLayout) resourcesTreadsView.findViewById(R.id.linear_res);
                                        lin3.removeView(resourcesTreadsListView);
                                        lin3.addView(resourcesTreadsListView);
                                        treadsListViewAdapter5.notifyDataSetChanged();
                                        break;
                                    case 4:
                                        if (attDatas == null)
                                            attDatas = new ArrayList<>();
                                        attDatas.addAll(list);
                                        RelativeLayout lin4 = (RelativeLayout) attendsTreadsView.findViewById(R.id.linear_att);
                                        lin4.removeView(attendsTreadsListView);
                                        lin4.addView(attendsTreadsListView);
                                        treadsListViewAdapter4.notifyDataSetChanged();
                                        break;
                                    case 3:
                                        if (impDatas == null)
                                            impDatas = new ArrayList<>();
                                        impDatas.addAll(list);
                                        RelativeLayout lin5 = (RelativeLayout) impTreadsView.findViewById(R.id.linear_imp);
                                        lin5.removeView(impTreadsListView);
                                        lin5.addView(impTreadsListView);
                                        treadsListViewAdapter3.notifyDataSetChanged();
                                        break;

                                }
                                setIsLoadMoring(loadIndex,false);
                                onLoad(listView);
                            }else{
                                setIsLoadMoring(loadIndex,false);
                                onLoad(listView);
                            }
                        }
                    }, 5);

                } catch (IOException e) {
                    setIsLoadMoring(loadIndex,false);
                    onLoad(listView);
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        });
        d.start();
    }

    /**
     * 只有下拉刷新时使用的方法
     * @param startDate = "开始时间"
     * @param endDate 结束时间
     * @param name 查询名称
     * @param eclassId 班级ID
     * @param listView 列表对象
     */
    public void initDatasForRefrush(String startDate, String endDate, String name, String eclassId, final XListView listView, final int index){

        if (!NetUtils.isNetworkConnected()){
            ToastUtil.showMessage("当前网络不可用!");
            return;
        }

        if(listView != null){
            Log.w("","");
        }

        if (getIsRefshing(index)){
            return;
        }
        setIsRefshing(true,index);
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
        if (name != null){
            hashMap.put("name",new ParameterValue(name));
        }
        //班级ID
        if (eclassId != null){
            hashMap.put("eclassId",new ParameterValue(eclassId));
        }
        hashMap.put("startDate",new ParameterValue(this.startDate));
        hashMap.put("endDate",new ParameterValue(this.endDate));
        switch (index){
            case 0:
                hashMap.put("tabsClick",new ParameterValue("allcommucation"));
                break;
            case 1:
                hashMap.put("tabsClick",new ParameterValue("commucation"));
                break;
            case 2:
                hashMap.put("tabsClick",new ParameterValue("mycommucation"));
                break;
            case 5:
                hashMap.put("tabsClick",new ParameterValue("resourcecommucation"));
                break;
            case 4:
                hashMap.put("tabsClick",new ParameterValue("reply"));
                break;
            case 3:
                hashMap.put("tabsClick",new ParameterValue("important"));
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
        setLoadMorePager(1,index);
        ProgressThreadWrap p = new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                try {
                    String treadsJson = ZddcUtil.getAllTreads(hashMap);
                    final Treads treads = new Gson().fromJson(treadsJson,Treads.class);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            setStatus(treads.getStatus(),index);
                            setDatasToNotNull(viewPagerListTreadsDatas);
//                            if (treads != null) {
                            ProgressUtil.hide();
                            viewPagerListTreadsDatas = treads.getDataList();
                            if (viewPagerListTreadsDatas == null){
                                viewPagerListTreadsDatas = new ArrayList<>();
                            }
                            switch (index){
                                case 0:
                                    allDatas = viewPagerListTreadsDatas;
                                    treadsListViewAdapter0 = new TreadsListViewAdapter(allDatas,context,HomeFragment.this);
                                    allreadsListView.setAdapter(treadsListViewAdapter0);
                                    allreadsListView.setEmptyView(allTreadsView.findViewById(R.id.emptyall));
                                    onLoad(allreadsListView);
                                    break;
                                case 1:
                                    interactDatas = viewPagerListTreadsDatas;
                                    treadsListViewAdapter1 = new TreadsListViewAdapter(interactDatas,context,HomeFragment.this);
                                    interactreadsTtListView.setAdapter(treadsListViewAdapter1);
                                    interactreadsTtListView.setEmptyView(interacttreadsTreadsView.findViewById(R.id.emptyinter));
                                    onLoad(interactreadsTtListView);
                                    break;
                                case 2:
                                    myDatas = viewPagerListTreadsDatas;
                                    treadsListViewAdapter2 = new TreadsListViewAdapter(myDatas,context,HomeFragment.this);
                                    myTreadsListView.setAdapter(treadsListViewAdapter2);
                                    myTreadsListView.setEmptyView(myTreadsView.findViewById(R.id.emptymy));
                                    onLoad(myTreadsListView);
                                    break;
                                case 5:
                                    resDatas = viewPagerListTreadsDatas;
                                    treadsListViewAdapter5 = new TreadsListViewAdapter(resDatas,context,HomeFragment.this);
                                    resourcesTreadsListView.setAdapter(treadsListViewAdapter5);
                                    resourcesTreadsListView.setEmptyView(resourcesTreadsView.findViewById(R.id.emptyres));
                                    onLoad(resourcesTreadsListView);
                                    break;
                                case 4:
                                    attDatas = viewPagerListTreadsDatas;
                                    treadsListViewAdapter4 = new TreadsListViewAdapter(attDatas,context,HomeFragment.this);
                                    attendsTreadsListView.setAdapter(treadsListViewAdapter4);
                                    attendsTreadsListView.setEmptyView(attendsTreadsView.findViewById(R.id.emptyatt));
                                    onLoad(attendsTreadsListView);
                                    break;
                                case 3:
                                    impDatas = viewPagerListTreadsDatas;
                                    treadsListViewAdapter3 = new TreadsListViewAdapter(impDatas,context,HomeFragment.this);
                                    impTreadsListView.setAdapter(treadsListViewAdapter3);
                                    impTreadsListView.setEmptyView(impTreadsView.findViewById(R.id.emptyimp));
                                    onLoad(impTreadsListView);
                                    break;
                            }
                            setIsRefshing(false,index);
                        }
                    }, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        });
        p.start();
    }

}


