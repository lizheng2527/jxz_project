package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.GridListBean;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.quantity.CheckActivity;
import com.zdhx.androidbase.ui.treadssearch.SearchWorkActivity;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;
import com.zdhx.androidbase.view.pagerslidingtab.PagerSlidingTabStrip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zdhx.androidbase.ui.MainActivity.act;
import static com.zdhx.androidbase.ui.MainActivity.showSelectBatchLinear;
import static com.zdhx.androidbase.ui.xlistview.XListViewUtils.onLoad;

/**
 * Created by lizheng on 2016/12/24.
 * 主页
 */

public class WorkSpaceFragment extends Fragment {

    public static int selectIndexTag;
    //workSpace加载的gridView
    private GridView gridView;
    private ArrayList<GridListBean> gridList;

    private Context context;

    public ECProgressDialog dialog;

    private WorkSpaceListViewAdapter workSpaceListViewAdapter0;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter1;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter2;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter3;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter4;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter5;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter6;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter7;
    private WorkSpaceListViewAdapter workSpaceListViewAdapter8;

    private static int loadMorePager0 = 1;
    private static int loadMorePager1 = 1;
    private static int loadMorePager2 = 1;
    private static int loadMorePager3 = 1;
    private static int loadMorePager4 = 1;
    private static int loadMorePager5 = 1;
    private static int loadMorePager6 = 1;
    private static int loadMorePager7 = 1;
    private static int loadMorePager8 = 1;

    /**
     * 获取加载更多页码
     * @param index
     * @return
     */
    private int getLoadMorePager(int index){
        switch (index){
            case 0:
                return loadMorePager0 ;
            case 1:
                return loadMorePager1 ;
            case 2:
                return loadMorePager2;
            case 3:
                return loadMorePager3;
            case 4:
                return loadMorePager4;
            case 5:
                return loadMorePager5 ;
            case 6:
                return loadMorePager6 ;
            case 7:
                return loadMorePager7;
            case 8:
                return loadMorePager8 ;
        }
        return 1;
    }

    /**
     * 设置加载更多页码
     * @param index
     * @param pageNumber
     */
    private void setLoadMorePager(int index,int pageNumber){
        switch (index){
            case 0:
                loadMorePager0 = pageNumber;
                break;
            case 1:
                loadMorePager1 = pageNumber;
                break;
            case 2:
                loadMorePager2 = pageNumber;
                break;
            case 3:
                loadMorePager3 = pageNumber;
                break;
            case 4:
                loadMorePager4 = pageNumber;
                break;
            case 5:
                loadMorePager5 = pageNumber;
                break;
            case 6:
                loadMorePager6 = pageNumber;
                break;
            case 7:
                loadMorePager7 = pageNumber;
                break;
            case 8:
                loadMorePager8 = pageNumber;
                break;
        }
    }

    private static HashMap<WorkSpaceDatasBean.DataListBean,String> batchSelectMap = new HashMap<>();

    /**
     * 当前是否是全选
     * @return
     */
    public boolean isSelectAll(){
        switch (isSelectPosition){
            case 0:
                if (allDatas != null && batchSelectMap.size()>0){
                    return allDatas.size() == batchSelectMap.size();
                }
                break;
            case 1:
                if (imgDatas != null && batchSelectMap.size()>0){
                    return imgDatas.size() == batchSelectMap.size();
                }
                break;
            case 3:
                if (videoDatas != null && batchSelectMap.size()>0){
                    return videoDatas.size() == batchSelectMap.size();
                }
                break;
            case 4:
                if (pptDatas != null && batchSelectMap.size()>0){
                    return pptDatas.size() == batchSelectMap.size();
                }
                break;
            case 5:
                if (wordDatas != null && batchSelectMap.size()>0){
                    return wordDatas.size() == batchSelectMap.size();
                }
                break;
            case 6:
                if (musicDatas != null && batchSelectMap.size()>0){
                    return musicDatas.size() == batchSelectMap.size();
                }
                break;
            case 7:
                if (excelDatas != null && batchSelectMap.size()>0){
                    return excelDatas.size() == batchSelectMap.size();
                }
                break;
            case 8:
                if (otherDatas != null && batchSelectMap.size()>0){
                    return otherDatas.size() == batchSelectMap.size();
                }
                break;
        }

        return false;
    }

    /**
     * 全选点击事件
     * @param isSelectAll
     */
    public void selectAll(boolean isSelectAll){
        if (isSelectAll){//全选
            batchSelectMap.clear();
            switch (isSelectPosition){
                case 0:
                    for (int i1 = 0; i1 < allDatas.size(); i1++) {
                        allDatas.get(i1).setSelect(true);
                        batchSelectMap.put(allDatas.get(i1),allDatas.get(i1).getId());
                    }
                    break;
                case 1:
                    for (int i1 = 0; i1 < imgDatas.size(); i1++) {
                        imgDatas.get(i1).setSelect(true);
                        batchSelectMap.put(imgDatas.get(i1),imgDatas.get(i1).getId());
                    }
                    break;
                case 3:
                    for (int i1 = 0; i1 < videoDatas.size(); i1++) {
                        videoDatas.get(i1).setSelect(true);
                        batchSelectMap.put(videoDatas.get(i1),videoDatas.get(i1).getId());
                    }
                    break;
                case 4:
                    for (int i1 = 0; i1 < pptDatas.size(); i1++) {
                        pptDatas.get(i1).setSelect(true);
                        batchSelectMap.put(pptDatas.get(i1),pptDatas.get(i1).getId());
                    }
                    break;
                case 5:
                    for (int i1 = 0; i1 < wordDatas.size(); i1++) {
                        wordDatas.get(i1).setSelect(true);
                        batchSelectMap.put(wordDatas.get(i1),wordDatas.get(i1).getId());
                    }
                    break;
                case 6:
                    for (int i1 = 0; i1 < musicDatas.size(); i1++) {
                        musicDatas.get(i1).setSelect(true);
                        batchSelectMap.put(musicDatas.get(i1),musicDatas.get(i1).getId());
                    }
                    break;
                case 7:
                    for (int i1 = 0; i1 < excelDatas.size(); i1++) {
                        excelDatas.get(i1).setSelect(true);
                        batchSelectMap.put(excelDatas.get(i1),excelDatas.get(i1).getId());
                    }
                    break;
                case 8:
                    for (int i1 = 0; i1 < otherDatas.size(); i1++) {
                        otherDatas.get(i1).setSelect(true);
                        batchSelectMap.put(otherDatas.get(i1),otherDatas.get(i1).getId());
                    }
                    break;
            }
        }else{//取消全选
            if (batchSelectMap != null){
                for (WorkSpaceDatasBean.DataListBean in : batchSelectMap.keySet()) {
                    in.setSelect(false);
                }
                batchSelectMap.clear();
            }
        }
        selectNotifyAdapter(isSelectPosition);
    }

    public HashMap<WorkSpaceDatasBean.DataListBean,String> getBatchSelectMap(){
        return batchSelectMap;
    }

    /**
     * 点击取消批量的刷新
     */
    public void notifyForSelect(){
        selectNotifyAdapter(isSelectPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workspace, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        dialog = new ECProgressDialog(context,"正在加载数据");
        initGridView();
        initListView();

    }
    /**
     * 初始化listView数据源及展示
     */
    public static boolean isBatchSelect = false;

    /**
     * 初次加载
     */
    private void initListView(){
        //初次加载
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            //教师端加载
            ListViewDatas(0,null,"","9",null,null,null,null,"2","","0","1");
        }else{
            ListViewDatas(0,null,"","9",null,null,null,null,"2","","0","1");
        }
    }

    private ViewPager vp;
    private View workAllView;
    private View workImgView;
    private View workVideoView;
    private View workPPTView;
    private View workWordView;
    private View workMusicView;
    private View workExcelView;
    private View workRarView;
    private View workOtherView;

    private XListView allXLV;
    private XListView imgXLV;
    private XListView videoXLV;
    private XListView pptXLV;
    private XListView wordXLV;
    private XListView musicXLV;
    private XListView excelXLV;
    private XListView rarXLV;
    private XListView otherXLV;

    private ArrayList<WorkSpaceDatasBean.DataListBean> allDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> imgDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> videoDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> pptDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> wordDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> musicDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> excelDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> rarDatas = new ArrayList<>();
    private ArrayList<WorkSpaceDatasBean.DataListBean> otherDatas = new ArrayList<>();

    /**
     * 修改本地对应数据调用的方法
     * @param bean
     * @param beans
     */
    private void upDataTreadsChild(WorkSpaceDatasBean.DataListBean bean,ArrayList<WorkSpaceDatasBean.DataListBean> beans){

        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getId().equals(bean.getId())){
                beans.get(i).setBrowse(bean.getBrowse());
                beans.get(i).setHighQuantity(bean.getHighQuantity());
                beans.get(i).setDown(bean.getDown());
                break;
            }
        }
    }

    private void removeBeansForDelete(ArrayList<WorkSpaceDatasBean.DataListBean> beans,WorkSpaceDatasBean.DataListBean bean){
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getId().equals(bean.getId())){
                beans.remove(i);
                break;
            }
        }
    }

    /**
     * 删除本地资源时调用的方法
     * @param bean
     */
    public void removeForDeleteTreads(WorkSpaceDatasBean.DataListBean bean){
        if (allDatas != null&&allDatas.size()>0&&isSelectPosition != 0){
            removeBeansForDelete(allDatas,bean);
        }
        if (imgDatas != null&&imgDatas.size()>0&&isSelectPosition != 1){
            removeBeansForDelete(imgDatas,bean);
        }
        if (videoDatas != null&&videoDatas.size()>0&&isSelectPosition != 2){
            removeBeansForDelete(videoDatas,bean);
        }
        if (pptDatas != null&&pptDatas.size()>0&&isSelectPosition != 3){
            removeBeansForDelete(pptDatas,bean);
        }
        if (wordDatas != null&&wordDatas.size()>0&&isSelectPosition != 4){
            removeBeansForDelete(wordDatas,bean);
        }
        if (musicDatas != null&&musicDatas.size()>0&&isSelectPosition != 5){
            removeBeansForDelete(musicDatas,bean);
        }
        if (excelDatas != null&&excelDatas.size()>0&&isSelectPosition != 6){
            removeBeansForDelete(excelDatas,bean);
        }
        if (rarDatas != null&&rarDatas.size()>0&&isSelectPosition != 7){
            removeBeansForDelete(rarDatas,bean);
        }
        if (otherDatas != null&&otherDatas.size()>0&&isSelectPosition != 8){
            removeBeansForDelete(otherDatas,bean);
        }
    }

    /**
     * 下载、浏览、推优修改对应数据（本地）
     * @param bean
     */
    public void upDateTreads(WorkSpaceDatasBean.DataListBean bean){
        if (allDatas != null&&allDatas.size()>0&&isSelectPosition != 0){
            upDataTreadsChild(bean,allDatas);
        }
        if (imgDatas != null&&imgDatas.size()>0&&isSelectPosition != 1){
            upDataTreadsChild(bean,imgDatas);
        }
        if (videoDatas != null&&videoDatas.size()>0&&isSelectPosition != 2){
            upDataTreadsChild(bean,videoDatas);
        }
        if (pptDatas != null&&pptDatas.size()>0&&isSelectPosition != 3){
            upDataTreadsChild(bean,pptDatas);
        }
        if (wordDatas != null&&wordDatas.size()>0&&isSelectPosition != 4){
            upDataTreadsChild(bean,wordDatas);
        }
        if (musicDatas != null&&musicDatas.size()>0&&isSelectPosition != 5){
            upDataTreadsChild(bean,musicDatas);
        }
        if (excelDatas != null&&excelDatas.size()>0&&isSelectPosition != 6){
            upDataTreadsChild(bean,excelDatas);
        }
        if (rarDatas != null&&rarDatas.size()>0&&isSelectPosition != 7){
            upDataTreadsChild(bean,rarDatas);
        }
        if (otherDatas != null&&otherDatas.size()>0&&isSelectPosition != 8){
            upDataTreadsChild(bean,otherDatas);
        }
    }



    private String status0 = "0";
    private String status1 = "0";
    private String status2 = "0";
    private String status3 = "0";
    private String status4 = "0";
    private String status5 = "0";
    private String status6 = "0";
    private String status7 = "0";
    private String status8 = "0";

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
            case 3:
                return status3 ;
            case 4:
                return status4 ;
            case 5:
                return status5 ;
            case 6:
                return status6 ;
            case 7:
                return status7 ;
            case 8:
                return status8 ;
        }
        return "1";
    }
    /**
     * 获取是否有下一页标记
     * 并且隐藏加载更多视图
     */
    private void setStatus(int index,String status){
        switch (index){
            case 0:
                status0 = status;
                if (status0.equals("1")){
                    allXLV.setPullLoadEnable(false);
                    allXLV.setDividerHeight(0);
                }else{
                    allXLV.setPullLoadEnable(true);
                    allXLV.setDividerHeight(1);
                }
                break;
            case 1:
                status1 = status;
                if (status1.equals("1")){
                    imgXLV.setPullLoadEnable(false);
                    imgXLV.setDividerHeight(0);
                }else{
                    imgXLV.setPullLoadEnable(true);
                    imgXLV.setDividerHeight(1);
                }
                break;
            case 2:
                status2 = status;
                if (status2.equals("1")){
                    videoXLV.setPullLoadEnable(false);
                    videoXLV.setDividerHeight(0);
                }else{
                    videoXLV.setPullLoadEnable(true);
                    videoXLV.setDividerHeight(1);
                }
                break;
            case 3:
                status3 = status;
                if (status3.equals("1")){
                    pptXLV.setPullLoadEnable(false);
                    pptXLV.setDividerHeight(0);
                }else{
                    pptXLV.setPullLoadEnable(true);
                    pptXLV.setDividerHeight(1);
                }
                break;
            case 4:
                status4 = status;
                if (status4.equals("1")){
                    wordXLV.setPullLoadEnable(false);
                    wordXLV.setDividerHeight(0);
                }else{
                    wordXLV.setPullLoadEnable(true);
                    wordXLV.setDividerHeight(1);
                }
                break;
            case 5:
                status5 = status;
                if (status5.equals("1")){
                    musicXLV.setPullLoadEnable(false);
                    musicXLV.setDividerHeight(0);
                }else{
                    musicXLV.setPullLoadEnable(true);
                    musicXLV.setDividerHeight(1);
                }
                break;
            case 6:
                status6 = status;
                if (status6.equals("1")){
                    excelXLV.setPullLoadEnable(false);
                    excelXLV.setDividerHeight(0);
                }else{
                    excelXLV.setPullLoadEnable(true);
                    excelXLV.setDividerHeight(1);
                }
                break;
            case 7:
                status7 = status;
                if (status7.equals("1")){
                    rarXLV.setPullLoadEnable(false);
                    rarXLV.setDividerHeight(0);
                }else{
                    rarXLV.setPullLoadEnable(true);
                    rarXLV.setDividerHeight(1);
                }
                break;
            case 8:
                status8 = status;
                if (status8.equals("1")){
                    otherXLV.setPullLoadEnable(false);
                    otherXLV.setDividerHeight(0);
                }else{
                    otherXLV.setPullLoadEnable(true);
                    otherXLV.setDividerHeight(1);
                }
                break;
//            case 9:
//                status9 = status;
        }
    }

    //标题动态数据
    private List<View> viewPagerListDatas;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private WorkViewPagerAdapter homeViewPagerAdapter;

    /**
     * 设置页面9个fragment，初始化视图
     */
    private void initGridView(){
        gridView = (GridView) getView().findViewById(R.id.fragment_workspace_grid);
        gridList = new ArrayList();
        gridList.add(new GridListBean("全部",0));
        gridList.add(new GridListBean("图片", R.drawable.img_s));
        gridList.add(new GridListBean("视频", R.drawable.video_s));
        gridList.add(new GridListBean("PPT", R.drawable.ppt_s));
        gridList.add(new GridListBean("Word", R.drawable.wrod_s));
        gridList.add(new GridListBean("音频", R.drawable.music_s));
        gridList.add(new GridListBean("Excel", R.drawable.excel_s));
//        gridList.add(new GridListBean("PDF", R.drawable.pdf_s));
        gridList.add(new GridListBean("压缩包", R.drawable.zip_s));
        gridList.add(new GridListBean("其他", R.drawable.other));


        vp = (ViewPager) getView().findViewById(R.id.fragment_home_viewpager);
        workAllView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_all,null);
        workImgView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_img,null);
        workVideoView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_video,null);
        workPPTView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_ppt,null);
        workWordView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_word,null);
        workMusicView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_music,null);
        workExcelView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_excel,null);
        workRarView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_rar,null);
        workOtherView = LayoutInflater.from(context).inflate(R.layout.fragment_work_viewpager_other,null);
        viewPagerListDatas = new ArrayList<>();
        viewPagerListDatas.add(workAllView);
        viewPagerListDatas.add(workImgView);
        viewPagerListDatas.add(workVideoView);
        viewPagerListDatas.add(workPPTView);
        viewPagerListDatas.add(workWordView);
        viewPagerListDatas.add(workMusicView);
        viewPagerListDatas.add(workExcelView);
        viewPagerListDatas.add(workRarView);
        viewPagerListDatas.add(workOtherView);

        allXLV = (XListView) workAllView.findViewById(R.id.fragment_work_viewpager_all_listview);
        initXListView(allXLV);
        allXLV.setEmptyView(workAllView.findViewById(R.id.allempty));

        imgXLV = (XListView) workImgView.findViewById(R.id.fragment_work_viewpager_img_listview);
        initXListView(imgXLV);
        imgXLV.setEmptyView(workImgView.findViewById(R.id.imgempty));

        videoXLV = (XListView) workVideoView.findViewById(R.id.fragment_work_viewpager_video_listview);
        initXListView(videoXLV);
        videoXLV.setEmptyView(workVideoView.findViewById(R.id.videoempty));

        pptXLV = (XListView) workPPTView.findViewById(R.id.fragment_work_viewpager_ppt_listview);
        initXListView(pptXLV);
        pptXLV.setEmptyView(workPPTView.findViewById(R.id.pptempty));

        wordXLV = (XListView) workWordView.findViewById(R.id.fragment_work_viewpager_word_listview);
        initXListView(wordXLV);
        wordXLV.setEmptyView(workWordView.findViewById(R.id.wordempty));

        musicXLV = (XListView) workMusicView.findViewById(R.id.fragment_work_viewpager_music_listview);
        initXListView(musicXLV);
        musicXLV.setEmptyView(workMusicView.findViewById(R.id.musicempty));

        excelXLV = (XListView) workExcelView.findViewById(R.id.fragment_work_viewpager_excel_listview);
        initXListView(excelXLV);
        excelXLV.setEmptyView(workExcelView.findViewById(R.id.excelempty));

        rarXLV = (XListView) workRarView.findViewById(R.id.fragment_work_viewpager_rar_listview);
        initXListView(rarXLV);
        rarXLV.setEmptyView(workRarView.findViewById(R.id.rarempty));

        otherXLV = (XListView) workOtherView.findViewById(R.id.fragment_work_viewpager_other_listview);
        initXListView(otherXLV);
        otherXLV.setEmptyView(workOtherView.findViewById(R.id.otherempty));


        mPagerSlidingTabStrip = (PagerSlidingTabStrip)getView().findViewById(R.id.tabs);



        vp.setOffscreenPageLimit(1);
        homeViewPagerAdapter = new WorkViewPagerAdapter(viewPagerListDatas,gridList);
        vp.setAdapter(homeViewPagerAdapter);
        mPagerSlidingTabStrip.setShouldExpand(true);
        mPagerSlidingTabStrip.setViewPager(vp,context,gridList);
        mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#4cbbda"));
        mPagerSlidingTabStrip.setTabPaddingLeftRight(10);

        //点击事件，此时需要重新加载数据刷新
        mPagerSlidingTabStrip.setOnTabClickListener(new PagerSlidingTabStrip.OnTabClickListener() {
            @Override
            public void onTabClick(View tab, int position) {
                dialog.show();
                //当前正在批量审核选择中，点击后取消显示，并清理存储的数据，标记滑动过程中其他页面需要刷新
                if (isBatchSelect){
                    act.onSelectCancel();
                    for (int i = 0; i < 9; i++) {
                        if (i == position){
                            setDataChanged(i,false);
                        }else{
                            setDataChanged(i,true);
                        }
                    }
                }else{
                    //标记当前再次滑动时，不需要刷新
                    setDataChanged(position,false);
                }
                workSpaceReFreshDatas(position,0);
                isSelectPosition = position;
                setLoadMorePager(position,1);
            }
        });


        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                isSelectPosition = position;
                MainActivity.act.onSelectCancel();
                switch (position){
                    case 0:
                        if (allDatas.size()<1||getDataChanged(0)){//当前加载无数据、条件改变，需要重新刷新
                            dialog.show();
                            workSpaceReFreshDatas(0,1);
                            setDataChanged(0,false);
                        }else{
                            workSpaceListViewAdapter0.notifyDataSetChanged();
                        }
                        break;
                    case 1:
                        if (imgDatas.size()<1||getDataChanged(1)){
                            dialog.show();
                            workSpaceReFreshDatas(1,1);
                            setDataChanged(1,false);
                        }else{
                            workSpaceListViewAdapter1.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        if (videoDatas.size()<1||getDataChanged(2)){
                            dialog.show();
                            workSpaceReFreshDatas(2,1);
                            setDataChanged(2,false);
                        }else{
                            workSpaceListViewAdapter2.notifyDataSetChanged();
                        }
                        break;
                    case 3:
                        if (pptDatas.size()<1||getDataChanged(3)){
                            dialog.show();
                            workSpaceReFreshDatas(3,1);
                            setDataChanged(3,false);
                        }else{
                            workSpaceListViewAdapter3.notifyDataSetChanged();
                        }
                        break;
                    case 4:
                        if (wordDatas.size()<1||getDataChanged(4)){
                            dialog.show();
                            workSpaceReFreshDatas(4,1);
                            setDataChanged(4,false);
                        }else{
                            workSpaceListViewAdapter4.notifyDataSetChanged();
                        }
                        break;
                    case 5:
                        if (musicDatas.size()<1||getDataChanged(5)){
                            dialog.show();
                            workSpaceReFreshDatas(5,1);
                            setDataChanged(5,false);
                        }else{
                            workSpaceListViewAdapter5.notifyDataSetChanged();
                        }
                        break;
                    case 6:
                        if (excelDatas.size()<1||getDataChanged(6)){
                            dialog.show();
                            workSpaceReFreshDatas(6,1);
                            setDataChanged(6,false);
                        }else{
                            workSpaceListViewAdapter6.notifyDataSetChanged();
                        }
                        break;
                    case 7:
                        if (rarDatas.size()<1||getDataChanged(7)){
                            dialog.show();
                            workSpaceReFreshDatas(7,1);
                            setDataChanged(7,false);
                        }else{
                            workSpaceListViewAdapter7.notifyDataSetChanged();
                        }
                        break;
                    case 8:
                        if (otherDatas.size()<1||getDataChanged(8)){
                            dialog.show();
                            workSpaceReFreshDatas(8,1);
                            setDataChanged(8,false);
                        }else{
                            workSpaceListViewAdapter8.notifyDataSetChanged();
                        }
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private boolean isDataChanged0 = false;
    private boolean isDataChanged1 = false;
    private boolean isDataChanged2 = false;
    private boolean isDataChanged3 = false;
    private boolean isDataChanged4 = false;
    private boolean isDataChanged5 = false;
    private boolean isDataChanged6 = false;
    private boolean isDataChanged7 = false;
    private boolean isDataChanged8 = false;

    /**
     * 滑动过程中根据之前设置的判断本次是否需要重新刷新
     * @param position
     * @return
     */
    public boolean getDataChanged(int position){
        switch (position){
            case 0:
                return isDataChanged0;
            case 1:
                return isDataChanged1;
            case 2:
                return isDataChanged2;
            case 3:
                return isDataChanged3;
            case 4:
                return isDataChanged4;
            case 5:
                return isDataChanged5;
            case 6:
                return isDataChanged6;
            case 7:
                return isDataChanged7;
            case 8:
                return isDataChanged8;
        }
        return true;
    }

    /**
     * 设置当滑动时是否重新加载数据
     * @param position
     * @param tag
     */
    public void setDataChanged(int position,boolean tag){
        switch (position){
            case 0:
                isDataChanged0 = tag;
                break;
            case 1:
                isDataChanged1 = tag;
                break;
            case 2:
                isDataChanged2 = tag;
                break;
            case 3:
                isDataChanged3 = tag;
                break;
            case 4:
                isDataChanged4 = tag;
                break;
            case 5:
                isDataChanged5 = tag;
                break;
            case 6:
                isDataChanged6 = tag;
                break;
            case 7:
                isDataChanged7 = tag;
                break;
            case 8:
                isDataChanged8 = tag;
                break;
        }
    }

    public void showDialogForPrePassAct(){
        if (dialog != null&&!dialog.isShowing()){
            dialog.show();
        }
    }

    /**
     * 点击刷新（refreshOrSelect == 0）
     * 下拉刷新（refreshOrSelect == 1）
     *
     * @param i
     */
    public void workSpaceReFreshDatas(int i,int refreshOrSelect){
        if (getIsRefrushing(i)){
            return;
        }
        setIsRefrushing(i,true);
        if (refreshOrSelect == 1){//下拉刷新，滑动刷新，此时同步当前的选择条件
            switch (i){
                case 0:
                    ListViewDatas(i,name,highqualityValue,"9",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 1:
                    ListViewDatas(i,name,highqualityValue,"4",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 2:
                    ListViewDatas(i,name,highqualityValue,"6",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 3:
                    ListViewDatas(i,name,highqualityValue,"2",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 4:
                    ListViewDatas(i,name,highqualityValue,"1",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 5:
                    ListViewDatas(i,name,highqualityValue,"5",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 6:
                    ListViewDatas(i,name,highqualityValue,"3",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
//                case 7:
//                    ListViewDatas(selectIndexTag,name,highqualityValue,"0",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
//                    break;
                case 7:
                    ListViewDatas(i,name,highqualityValue,"7",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 8:
                    ListViewDatas(i,name,highqualityValue,"8",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
            }
        }else if (refreshOrSelect == 0){//点击刷新，恢复默认条件刷新
            //点击刷新时如果此时是在进行批量审核，则修改回正常显示
            if (WorkSpaceFragment.isBatchSelect){
                showSelectBatchLinear(false);
                WorkSpaceFragment.isBatchSelect = false;
            }
            //刷新之后将搜索条件的对象清空
            SearchWorkActivity.setTreeBeanToNull();
            SearchWorkActivity.setTreeBeanForEclassToNull();
            switch (i){
                case 0:
                    ListViewDatas(i,null,"","9",null,null,null,null,"2","","0","1");
                    break;
                case 1:
                    ListViewDatas(i,null,"","4",null,null,null,null,"2","","0","1");
                    break;
                case 2:
                    ListViewDatas(i,null,"","6",null,null,null,null,"2","","0","1");
                    break;
                case 3:
                    ListViewDatas(i,null,"","2",null,null,null,null,"2","","0","1");
                    break;
                case 4:
                    ListViewDatas(i,null,"","1",null,null,null,null,"2","","0","1");
                    break;
                case 5:
                    ListViewDatas(i,null,"","5",null,null,null,null,"2","","0","1");
                    break;
                case 6:
                    ListViewDatas(i,null,"","3",null,null,null,null,"2","","0","1");
                    break;
//                case 7:
//                    ListViewDatas(i,null,"0","3","20130424113427190508721241586190",null,null,null,"","","0","1");
//                    break;
                case 7:
                    ListViewDatas(i,null,"","7",null,null,null,null,"2","","0","1");
                    break;
                case 8:
                    ListViewDatas(i,null,"","8",null,null,null,null,"2","","0","1");
                    break;
            }
            setLoadMorePager(i,1);
        }
    }


    /**
     * 搜索资源后调用刷新
     * @param name
     * @param clickId
     * @param type
     * @param eclassIds
     * @param status
     * @param highqualityValue
     */
    public void onActReFresh(String name,String clickId,String type,String eclassIds,String status,String highqualityValue){
        if (dialog != null){
            dialog.show();
        }
        this.name = name;
        this.clickId = clickId;
        this.type = type;
        this.status = status;
        this.eclassIds = eclassIds;
        this.highqualityValue = highqualityValue;
        switch(isSelectPosition){
            case 0:
                resourceStyle = "9";
                break;
            case 1:
                resourceStyle = "4";
                break;
            case 2:
                resourceStyle = "6";
                break;
            case 3:
                resourceStyle = "2";
                break;
            case 4:
                resourceStyle = "1";
                break;
            case 5:
                resourceStyle = "5";
                break;
            case 6:
                resourceStyle = "3";
                break;
//          case 7:
//              resourceStyle = "0";
//              break;
            case 7:
                resourceStyle = "7";
                break;
            case 8:
                resourceStyle = "8";
                break;
        }
        ListViewDatas(isSelectPosition,name,highqualityValue,resourceStyle,clickId,type,null,eclassIds,status,"","0","1");
    }

    private HashMap<String,ParameterValue> hashMap = new HashMap<>();

    private int i;
    private String name;
    private String highqualityValue;
    private String resourceStyle;
    private String clickId;
    private String type;
    private String studentName;
    private String eclassIds;
    private String status;
    private String studentUploadType;
    private String radioValue;
    private String pageNo;

    /**
     *对应加载数据
     * @param i
     * @param name
     * @param highqualityValue
     * @param resourceStyle
     * @param clickId
     * @param type
     * @param studentName
     * @param eclassIds
     * @param status
     * @param studentUploadType
     * @param radioValue
     * @param pageNo
     */
    public void ListViewDatas(final int i, String name, String highqualityValue, String resourceStyle, String clickId, String type, String studentName, String eclassIds, String status, String studentUploadType, String radioValue, String pageNo){

        //设置主页标题栏中选择的搜索条件
        hashMap.clear();
        this.studentName = studentName;
        this.eclassIds = eclassIds;
        this.status = status;
        SearchWorkActivity.status = this.status;
        SearchWorkActivity.oldStatus = this.status;
        SearchWorkActivity.highQuality = this.highqualityValue;
        SearchWorkActivity.oldHighQuality = this.highqualityValue;
        this.studentUploadType = studentUploadType;
        this.radioValue = radioValue;
        this.clickId = clickId;
        this.name = name;
        this.type = type;
        //教师登录
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            if (selectIndexTag == 0){//教师备课资源
                hashMap.put("tabType",new ParameterValue("teacher"));
                hashMap.put("radioValue",new ParameterValue(radioValue));
            }
            if(selectIndexTag == 1){//学生生成资源
                hashMap.put("tabType",new ParameterValue("student"));

                if (studentName !=null){//学生生成需要（学生姓名）
                    hashMap.put("studentName",new ParameterValue(studentName));
                }

                if (eclassIds !=null){
                    hashMap.put("eclassIds",new ParameterValue(eclassIds));
                }

                if (status !=null){//审核状态
                    hashMap.put("status",new ParameterValue(status));
                }

                if (studentUploadType !=null){//学生上传的类型
                    hashMap.put("studentUploadType",new ParameterValue(studentUploadType));
                }
            }
        }
        //学生登录
        if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
            if (selectIndexTag == 0){//教师引导
                hashMap.put("tabType",new ParameterValue("teacherGuid"));
                hashMap.put("radioValue",new ParameterValue(radioValue));
            }
            if (selectIndexTag == 1){//同伴互助
                hashMap.put("tabType",new ParameterValue("studentGuid"));
                hashMap.put("radioValue",new ParameterValue(radioValue));
            }
            if (selectIndexTag == 2){//学习成果
                hashMap.put("tabType",new ParameterValue("studyResult"));
                hashMap.put("radioValue",new ParameterValue(radioValue));
            }
        }

        if (highqualityValue == null||highqualityValue.equals("")){//每个选择都需要（提名/优质）
            hashMap.put("highqualityValue",new ParameterValue(""));
            highqualityValue = "";
        }else{
            hashMap.put("highqualityValue",new ParameterValue(highqualityValue));
        }
        this.highqualityValue = highqualityValue;
        //搜索格式类型    全部/word/ppt/excel/图片/音频/视频/压缩包/其他      0/1/2/3/4/5/6/7/8  都有
        hashMap.put("resourceStyle",new ParameterValue(resourceStyle));
        this.resourceStyle = resourceStyle;

        if (clickId != null){//点击的树的节点id
            hashMap.put("clickId",new ParameterValue(clickId));
        }
        //点击的树的节点类型  学科/教材/单元/次级单元    subject/teachingMaterial/unit/chapterSubject-chapterUnit
        if (type != null){
            hashMap.put("type",new ParameterValue(type));
        }
        hashMap.put("pageNo",new ParameterValue(pageNo));
        this.pageNo = pageNo;

        //查询时的搜索文字
        if (name != null){
            hashMap.put("name",new ParameterValue(name));
        }
        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String json = ZddcUtil.getResourceList(hashMap);
                    final WorkSpaceDatasBean bean = new Gson().fromJson(json,WorkSpaceDatasBean.class);
//                    statusForLoadMore = bean.getStatus();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setStatus(i,bean.getStatus());
                            switch (i){
                                case 0:
                                    allDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter0 = new WorkSpaceListViewAdapter(allDatas,context,WorkSpaceFragment.this,allXLV);
                                    allXLV.setAdapter(workSpaceListViewAdapter0);
                                    onLoad(allXLV);
                                    break;
                                case 1:
                                    imgDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter1 = new WorkSpaceListViewAdapter(imgDatas,context,WorkSpaceFragment.this,imgXLV);
                                    imgXLV.setAdapter(workSpaceListViewAdapter1);
                                    onLoad(imgXLV);
                                    break;
                                case 2:
                                    videoDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter2 = new WorkSpaceListViewAdapter(videoDatas,context,WorkSpaceFragment.this,videoXLV);
                                    videoXLV.setAdapter(workSpaceListViewAdapter2);
                                    onLoad(videoXLV);
                                    break;
                                case 3:
                                    pptDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter3 = new WorkSpaceListViewAdapter(pptDatas,context,WorkSpaceFragment.this,pptXLV);
                                    pptXLV.setAdapter(workSpaceListViewAdapter3);
                                    onLoad(pptXLV);
                                    break;
                                case 4:
                                    wordDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter4 = new WorkSpaceListViewAdapter(wordDatas,context,WorkSpaceFragment.this,wordXLV);
                                    wordXLV.setAdapter(workSpaceListViewAdapter4);
                                    onLoad(wordXLV);
                                    break;
                                case 5:
                                    musicDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter5 = new WorkSpaceListViewAdapter(musicDatas,context,WorkSpaceFragment.this,musicXLV);
                                    musicXLV.setAdapter(workSpaceListViewAdapter5);
                                    onLoad(musicXLV);
                                    break;
                                case 6:
                                    excelDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter6 = new WorkSpaceListViewAdapter(excelDatas,context,WorkSpaceFragment.this,excelXLV);
                                    excelXLV.setAdapter(workSpaceListViewAdapter6);
                                    onLoad(excelXLV);
                                    break;
                                case 7:
                                    rarDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter7 = new WorkSpaceListViewAdapter(rarDatas,context,WorkSpaceFragment.this,rarXLV);
                                    rarXLV.setAdapter(workSpaceListViewAdapter7);
                                    onLoad(rarXLV);
                                    break;
                                case 8:
                                    otherDatas = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                                    workSpaceListViewAdapter8 = new WorkSpaceListViewAdapter(otherDatas,context,WorkSpaceFragment.this,otherXLV);
                                    otherXLV.setAdapter(workSpaceListViewAdapter8);
                                    onLoad(otherXLV);
                                    break;
                            }
                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                            setIsRefrushing(i,false);
                        }
                    },5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private Handler handler = new Handler();

    /**
     * 浏览记录
     * @param resouceId
     * @param type
     * @param pageNo
     */
    public void startAct(String resouceId,String type,String pageNo){
        MainActivity.map.put("resouceId",resouceId);
        MainActivity.map.put("type",type);
        MainActivity.map.put("pageNo",pageNo);
        startActivity(new Intent(context, OperateRecordActicity.class));

    }

    /**
     * 推优
     * @param quantity_resouceId
     * @param quantity_type
     * @param position
     * @param fragment
     * @param highQuality
     * @param teacherFinalSelection
     */
    public void startCheckAct(String quantity_resouceId,String quantity_type,int position,WorkSpaceFragment fragment,String highQuality,String teacherFinalSelection){
        MainActivity.map.put("quantity_resouceId",quantity_resouceId);
        MainActivity.map.put("quantity_teacherFinalSelection",teacherFinalSelection);
        MainActivity.map.put("quantity_type",quantity_type);
        MainActivity.map.put("position",position);
        MainActivity.map.put("fragment",fragment);
        MainActivity.map.put("quantity_highQuality",highQuality);
        startActivity(new Intent(context, CheckActivity.class));

    }

    private int start;
    private int refreshCnt;
    private boolean isLoadMoring = false;
    private boolean isRefrushing0 = false;
    private boolean isRefrushing1 = false;
    private boolean isRefrushing2 = false;
    private boolean isRefrushing3 = false;
    private boolean isRefrushing4 = false;
    private boolean isRefrushing5 = false;
    private boolean isRefrushing6 = false;
    private boolean isRefrushing7 = false;
    private boolean isRefrushing8 = false;

    /**
     * 当前对应的listView正在刷新
     * @param position
     * @param tag
     */
    private void setIsRefrushing(int position,boolean tag){
        switch (position){
            case 0:
                isRefrushing0 = tag;
                break;
            case 1:
                isRefrushing1 = tag;
                break;
            case 2:
                isRefrushing2 = tag;
                break;
            case 3:
                isRefrushing3 = tag;
                break;
            case 4:
                isRefrushing4 = tag;
                break;
            case 5:
                isRefrushing5 = tag;
                break;
            case 6:
                isRefrushing6 = tag;
                break;
            case 7:
                isRefrushing7 = tag;
                break;
            case 8:
                isRefrushing8 = tag;
                break;
        }
    }

    private boolean isLoadMoring0 = false;
    private boolean isLoadMoring1 = false;
    private boolean isLoadMoring2 = false;
    private boolean isLoadMoring3 = false;
    private boolean isLoadMoring4 = false;
    private boolean isLoadMoring5 = false;
    private boolean isLoadMoring6 = false;
    private boolean isLoadMoring7 = false;
    private boolean isLoadMoring8 = false;

    /**
     * 是否正在加载更多..
     * @param position
     * @return
     */
    private boolean getIsLoadMoring(int position){
        switch (position){
            case 0:
                return isLoadMoring0;
            case 1:
                return isLoadMoring1;
            case 2:
                return isLoadMoring2;
            case 3:
                return isLoadMoring3;
            case 4:
                return isLoadMoring4;
            case 5:
                return isLoadMoring5;
            case 6:
                return isLoadMoring6;
            case 7:
                return isLoadMoring7;
            case 8:
                return isLoadMoring8;
        }
        return false;
    }

    /**
     * 设置当前正在加载
     * @param position
     * @param tag
     */
    private void setIsLoadMoring(int position,boolean tag){
        switch (position){
            case 0:
                isLoadMoring0 = tag;
                break;
            case 1:
                isLoadMoring1 = tag;
                break;
            case 2:
                isLoadMoring2 = tag;
                break;
            case 3:
                isLoadMoring3 = tag;
                break;
            case 4:
                isLoadMoring4 = tag;
                break;
            case 5:
                isLoadMoring5 = tag;
                break;
            case 6:
                isLoadMoring6 = tag;
                break;
            case 7:
                isLoadMoring7 = tag;
                break;
            case 8:
                isLoadMoring8 = tag;
                break;
        }
    }


    /**
     * 判断是否正在刷新中
     * @param position
     * @return
     */
    private boolean getIsRefrushing(int position){
        switch (position){
            case 0:
                return isRefrushing0;
            case 1:
                return isRefrushing1;
            case 2:
                return isRefrushing2;
            case 3:
                return isRefrushing3;
            case 4:
                return isRefrushing4;
            case 5:
                return isRefrushing5;
            case 6:
                return isRefrushing6;
            case 7:
                return isRefrushing7;
            case 8:
                return isRefrushing8;
        }
        return false;
    }
    public static int isSelectPosition;

    /**
     * 初始化XlistView
     * @param listView
     */
    public void initXListView(final XListView listView){
        listView.setPullLoadEnable(true);
        listView.setDividerHeight(1);
        listView.setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount+firstVisibleItem == totalItemCount&&getStatus(isSelectPosition).equals("0")&&totalItemCount>19){
                    listView.startLoadMore();
                }
            }
        });
        final Handler mHandler = new Handler();
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start = ++refreshCnt;
                        if (getIsRefrushing(isSelectPosition)){
                            return;
                        }
                        workSpaceReFreshDatas(isSelectPosition,1);
                    }
                }, 1);
            }

            @Override
            public void onLoadMore() {
                if (getStatus(isSelectPosition).equals("1")){
                    onLoad(listView);
                    return;
                }
                if (getIsLoadMoring(isSelectPosition)){
                    return;
                }
                setIsLoadMoring(isSelectPosition,true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch(isSelectPosition){
                            case 0:
                                resourceStyle = "9";
                                break;
                            case 1:
                                resourceStyle = "4";
                                break;
                            case 2:
                                resourceStyle = "6";
                                break;
                            case 3:
                                resourceStyle = "2";
                                break;
                            case 4:
                                resourceStyle = "1";
                                break;
                            case 5:
                                resourceStyle = "5";
                                break;
                            case 6:
                                resourceStyle = "3";
                                break;
//                            case 7:
//                                resourceStyle = "0";
//                                break;
                            case 7:
                                resourceStyle = "7";
                                break;
                            case 8:
                                resourceStyle = "8";
                                break;
                        }
                        ListViewDatasForOnLoadMore(isSelectPosition,name,highqualityValue,resourceStyle,clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,getLoadMorePager(isSelectPosition)+1+"");
                    }
                }, 5);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!SearchWorkActivity.status.equals("2")&&!WorkSpaceFragment.isBatchSelect){
                    WorkSpaceFragment.isBatchSelect = true;
                    showSelectBatchLinear(true);
                    batchSelectMap.clear();
                    selectNotifyAdapter(isSelectPosition);
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (WorkSpaceFragment.isBatchSelect){
                    switch (isSelectPosition){
                        case 0:
                            if (allDatas.get(position-1).isSelect()){
                                allDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(allDatas.get(position-1));
                            }else{
                                allDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(allDatas.get(position-1),allDatas.get(position-1).getId());
                            }
                            break;
                        case 1:
                            if (imgDatas.get(position-1).isSelect()){
                                imgDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(imgDatas.get(position-1));
                            }else{
                                imgDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(imgDatas.get(position-1),imgDatas.get(position-1).getId());
                            }
                            break;
                        case 2:
                            if (videoDatas.get(position-1).isSelect()){
                                videoDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(videoDatas.get(position-1));
                            }else{
                                videoDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(videoDatas.get(position-1),videoDatas.get(position-1).getId());
                            }
                            break;
                        case 3:
                            if (pptDatas.get(position-1).isSelect()){
                                pptDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(pptDatas.get(position-1));
                            }else{
                                pptDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(pptDatas.get(position-1),pptDatas.get(position-1).getId());
                            }
                            break;
                        case 4:
                            if (wordDatas.get(position-1).isSelect()){
                                wordDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(wordDatas.get(position-1));
                            }else{
                                wordDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(wordDatas.get(position-1),wordDatas.get(position-1).getId());
                            }
                            break;
                        case 5:
                            if (musicDatas.get(position-1).isSelect()){
                                musicDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(musicDatas.get(position-1));
                            }else{
                                musicDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(musicDatas.get(position-1),musicDatas.get(position-1).getId());
                            }
                            break;
                        case 6:
                            if (excelDatas.get(position-1).isSelect()){
                                excelDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(excelDatas.get(position-1));
                            }else{
                                excelDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(excelDatas.get(position-1),excelDatas.get(position-1).getId());
                            }
                            break;
                        case 7:
                            if (rarDatas.get(position-1).isSelect()){
                                rarDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(rarDatas.get(position-1));
                            }else{
                                rarDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(rarDatas.get(position-1),rarDatas.get(position-1).getId());
                            }
                            break;
                        case 8:
                            if (otherDatas.get(position-1).isSelect()){
                                otherDatas.get(position-1).setSelect(false);
                                batchSelectMap.remove(otherDatas.get(position-1));
                            }else{
                                otherDatas.get(position-1).setSelect(true);
                                batchSelectMap.put(otherDatas.get(position-1),otherDatas.get(position-1).getId());
                            }
                            break;
                    }

                    MainActivity.setAllSelectText(isSelectAll());
                    selectNotifyAdapter(isSelectPosition);
                }
            }
        });
    }

    /**
     * 根据当前显示刷新对应adapter
     * @param position
     */
    private void selectNotifyAdapter(int position){
        switch (position){
            case 0:
                if (workSpaceListViewAdapter0 != null)
                    workSpaceListViewAdapter0.notifyDataSetChanged();
                break;
            case 1:
                if (workSpaceListViewAdapter1 != null)
                    workSpaceListViewAdapter1.notifyDataSetChanged();
                break;
            case 2:
                if (workSpaceListViewAdapter2 != null)
                    workSpaceListViewAdapter2.notifyDataSetChanged();
                break;
            case 3:
                if (workSpaceListViewAdapter3 != null)
                    workSpaceListViewAdapter3.notifyDataSetChanged();
                break;
            case 4:
                if (workSpaceListViewAdapter4 != null)
                    workSpaceListViewAdapter4.notifyDataSetChanged();
                break;
            case 5:
                if (workSpaceListViewAdapter5 != null)
                    workSpaceListViewAdapter5.notifyDataSetChanged();
                break;
            case 6:
                if (workSpaceListViewAdapter6 != null)
                    workSpaceListViewAdapter6.notifyDataSetChanged();
                break;
            case 7:
                if (workSpaceListViewAdapter7 != null)
                    workSpaceListViewAdapter7.notifyDataSetChanged();
                break;
            case 8:
                if (workSpaceListViewAdapter8 != null)
                    workSpaceListViewAdapter8.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 加载更多调用方法
     * @param i
     * @param name
     * @param highqualityValue
     * @param resourceStyle
     * @param clickId
     * @param type
     * @param studentName
     * @param eclassIds
     * @param status
     * @param studentUploadType
     * @param radioValue
     * @param pageNo
     */
    public void ListViewDatasForOnLoadMore(final int i, String name, String highqualityValue, String resourceStyle, String clickId, String type, String studentName, String eclassIds, String status, String studentUploadType, String radioValue, String pageNo){

        //设置主页标题栏中选择的搜索条件
        hashMap.clear();

        this.studentName = studentName;
        this.eclassIds = eclassIds;
        this.status = status;
        SearchWorkActivity.status = this.status;
        SearchWorkActivity.oldStatus = this.status;
        this.studentUploadType = studentUploadType;
        this.radioValue = radioValue;
        if (selectIndexTag == 0){//教师备课资源
            hashMap.put("tabType",new ParameterValue("teacher"));
            hashMap.put("radioValue",new ParameterValue(radioValue));
        }

        if(selectIndexTag == 1){//学生生成资源
            hashMap.put("tabType",new ParameterValue("student"));

            if (studentName !=null){//学生生成需要（学生姓名）
                hashMap.put("studentName",new ParameterValue(studentName));
            }

            if (eclassIds !=null){
                hashMap.put("eclassIds",new ParameterValue(eclassIds));
            }

            if (status !=null){//审核状态
                hashMap.put("status",new ParameterValue(status));
            }else{
                hashMap.put("status",new ParameterValue("2"));
            }

            if (studentUploadType !=null){//学生上传的类型
                hashMap.put("studentUploadType",new ParameterValue(studentUploadType));
            }
        }


        if (highqualityValue == null){//每个选择都需要（提名/优质）
            hashMap.put("highqualityValue",new ParameterValue(""));
            highqualityValue = "";
        }else{
            hashMap.put("highqualityValue",new ParameterValue(highqualityValue));
        }
        this.highqualityValue = highqualityValue;
        //搜索格式类型    全部/word/ppt/excel/图片/音频/视频/压缩包/其他      0/1/2/3/4/5/6/7/8  都有
        hashMap.put("resourceStyle",new ParameterValue(resourceStyle));
        this.resourceStyle = resourceStyle;

        if (clickId == null){//点击的树的节点id

        }else{
            hashMap.put("clickId",new ParameterValue(clickId));
        }
        this.clickId = clickId;
        //点击的树的节点类型  学科/教材/单元/次级单元    subject/teachingMaterial/unit/chapterSubject-chapterUnit
        if (type == null){

        }else{
            hashMap.put("type",new ParameterValue(type));
        }
        this.type = type;

        hashMap.put("pageNo",new ParameterValue(pageNo));
        this.pageNo = pageNo;

        //查询时的搜索文字
        if (name == null){

        }else{
            hashMap.put("name",new ParameterValue(name));
        }
        this.name = name;

        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String json = ZddcUtil.getResourceList(hashMap);
                    final WorkSpaceDatasBean bean = new Gson().fromJson(json,WorkSpaceDatasBean.class);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setStatus(i,bean.getStatus());
                            List<WorkSpaceDatasBean.DataListBean> l = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                            switch (i){
                                case 0:
                                    allDatas.addAll(l);
                                    RelativeLayout lin = (RelativeLayout) workAllView.findViewById(R.id.linear_all);
                                    lin.removeAllViews();
                                    lin.addView(allXLV);
                                    workSpaceListViewAdapter0.notifyDataSetChanged();
                                    setLoadMorePager(0,getLoadMorePager(0)+1);
                                    onLoad(allXLV);
                                    break;
                                case 1:
                                    imgDatas.addAll(l);
                                    RelativeLayout lin1 = (RelativeLayout) workImgView.findViewById(R.id.linear_img);
                                    lin1.removeAllViews();
                                    lin1.addView(imgXLV);
                                    workSpaceListViewAdapter1.notifyDataSetChanged();
                                    setLoadMorePager(1,getLoadMorePager(1)+1);
                                    onLoad(imgXLV);
                                    break;
                                case 2:
                                    videoDatas.addAll(l);
                                    RelativeLayout lin2 = (RelativeLayout) workVideoView.findViewById(R.id.linear_video);
                                    lin2.removeAllViews();
                                    lin2.addView(videoXLV);
                                    workSpaceListViewAdapter2.notifyDataSetChanged();
                                    setLoadMorePager(2,getLoadMorePager(2)+1);
                                    onLoad(videoXLV);
                                    break;
                                case 3:
                                    pptDatas.addAll(l);
                                    RelativeLayout lin3 = (RelativeLayout) workPPTView.findViewById(R.id.linear_ppt);
                                    lin3.removeAllViews();
                                    lin3.addView(pptXLV);
                                    workSpaceListViewAdapter3.notifyDataSetChanged();
                                    setLoadMorePager(3,getLoadMorePager(3)+1);
                                    onLoad(pptXLV);
                                    break;
                                case 4:
                                    wordDatas.addAll(l);
                                    RelativeLayout lin4 = (RelativeLayout) workWordView.findViewById(R.id.linear_word);
                                    lin4.removeAllViews();
                                    lin4.addView(wordXLV);
                                    workSpaceListViewAdapter4.notifyDataSetChanged();
                                    setLoadMorePager(4,getLoadMorePager(4)+1);
                                    onLoad(wordXLV);
                                    break;
                                case 5:
                                    musicDatas.addAll(l);
                                    RelativeLayout lin5 = (RelativeLayout) workMusicView.findViewById(R.id.linear_music);
                                    lin5.removeAllViews();
                                    lin5.addView(musicXLV);
                                    workSpaceListViewAdapter5.notifyDataSetChanged();
                                    setLoadMorePager(5,getLoadMorePager(5)+1);
                                    onLoad(musicXLV);
                                    break;
                                case 6:
                                    excelDatas.addAll(l);
                                    RelativeLayout lin6 = (RelativeLayout) workExcelView.findViewById(R.id.linear_excel);
                                    lin6.removeAllViews();
                                    lin6.addView(excelXLV);
                                    workSpaceListViewAdapter6.notifyDataSetChanged();
                                    setLoadMorePager(6,getLoadMorePager(6)+1);
                                    onLoad(excelXLV);
                                    break;
                                case 7:
                                    rarDatas.addAll(l);
                                    RelativeLayout lin7 = (RelativeLayout) workRarView.findViewById(R.id.linear_rar);
                                    lin7.removeAllViews();
                                    lin7.addView(rarXLV);
                                    workSpaceListViewAdapter7.notifyDataSetChanged();
                                    setLoadMorePager(7,getLoadMorePager(7)+1);
                                    onLoad(rarXLV);
                                    break;
                                case 8:
                                    otherDatas.addAll(l);
                                    RelativeLayout lin8 = (RelativeLayout) workOtherView.findViewById(R.id.linear_other);
                                    lin8.removeAllViews();
                                    lin8.addView(otherXLV);
                                    workSpaceListViewAdapter8.notifyDataSetChanged();
                                    setLoadMorePager(8,getLoadMorePager(8)+1);
                                    onLoad(otherXLV);
                                    break;
                            }
                            setIsLoadMoring(i,false);
                        }
                    },5);
                } catch (IOException e) {
                    e.printStackTrace();
                    isLoadMoring = false;
                }
            }
        }).start();

    }

    /**
     *
     * @param position 推优的资源下标，用来搜索修改对应的信息
     * @param status    推优通过或者不通过
     */
    public void onCheckRefrush(int position,boolean status){
        ArrayList<WorkSpaceDatasBean.DataListBean> list = new ArrayList<>();
        switch (isSelectPosition){
            case 0:
                list = allDatas;
                break;
            case 1:
                list = imgDatas;
                break;
            case 2:
                list = videoDatas;
                break;
            case 3:
                list = pptDatas;
                break;
            case 4:
                list = wordDatas;
                break;
            case 5:
                list = musicDatas;
                break;
            case 6:
                list = excelDatas;
                break;
            case 7:
                list = rarDatas;
                break;
            case 8:
                list = otherDatas;
                break;
        }
        if (list.get(position).getHighQuantity().equals("0")){
            if (status){
                list.get(position).setHighQuantity("1");
            }else{
                return;
            }
        }else if (list.get(position).getHighQuantity().equals("1")){
            if (status){
                list.get(position).setHighQuantity("2");
            }else{
                list.get(position).setHighQuantity("0");
            }
        }else if (list.get(position).getHighQuantity().equals("2")) {
            if (status){
                return;
            }else{
                list.get(position).setHighQuantity("1");
            }
        }
        upDateTreads(list.get(position));
        switch (isSelectPosition){
            case 0:
                allDatas = list;
                break;
            case 1:
                imgDatas = list;
                break;
            case 2:
                videoDatas = list;
                break;
            case 3:
                pptDatas = list;
                break;
            case 4:
                wordDatas = list;
                break;
            case 5:
                musicDatas = list;
                break;
            case 6:
                excelDatas = list;
                break;
            case 7:
                rarDatas = list;
                break;
            case 8:
                otherDatas = list;
                break;
        }
        selectNotifyAdapter(isSelectPosition);
    }
}
