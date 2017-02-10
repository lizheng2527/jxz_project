package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.GridListBean;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.quantity.CheckActivity;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zdhx.androidbase.ui.xlistview.XListViewUtils.onLoad;

/**
 * Created by lizheng on 2016/12/24.
 * 主页
 */

public class WorkSpaceFragment extends Fragment {

    private String statusForLoadMore = "0";

    private boolean clickOrRefrush = false;

    private static int gridSelectionIndex = 0;

    public static int selectIndexTag = 0;
    //workSpace加载的gridView
    private GridView gridView;
    private ArrayList gridList;

    private WorkSpaceGridAdapter workSpaceGridAdapter;

    private Context context;

    private XListView lv;

    private ArrayList<WorkSpaceDatasBean.DataListBean> list = new ArrayList<>();

    public ECProgressDialog dialog;

    private WorkSpaceListViewAdapter workSpaceListViewAdapter;

    private static int loadMorePager = 1;
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
    private void initListView(){
        lv = (XListView) getView().findViewById(R.id.fragment_workspace_listview);
        //初次加载
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            ListViewDatas(0,null,"","9",null,null,null,null,"","","0","1");
        }
        initXListView(lv,workSpaceListViewAdapter);
    }

    /**
     * 初始化GridView布局
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
        workSpaceGridAdapter = new WorkSpaceGridAdapter(gridList,context,false);
        initGridViewWidth();
        gridView.setAdapter(workSpaceGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (dialog != null){
                    dialog.show();
                }
                clickOrRefrush = false;
                workSpaceReFreshDatas(i,1);
                WorkSpaceGridAdapter.index = i;
                workSpaceGridAdapter.notifyDataSetChanged();
                gridSelectionIndex = i;
            }
        });
    }

    /**
     * 根据条目刷新
     * @param i
     */
    public void workSpaceReFreshDatas(int i,int refreshOrSelect){
        if (refreshOrSelect == 1){//gridView点击时执行，此时同步当前的选择时间
            switch (i){
                case 0:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"9",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 1:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"4",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 2:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"6",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 3:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"2",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 4:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"1",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 5:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"5",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 6:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"3",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
//                case 7:
//                    ListViewDatas(selectIndexTag,name,highqualityValue,"0",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
//                    break;
                case 7:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"7",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
                case 8:
                    ListViewDatas(selectIndexTag,name,highqualityValue,"8",clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,"1");
                    break;
            }
        }else if (refreshOrSelect == 0){//点击刷新默认参数配置
            clickOrRefrush = true;
            switch (i){
                case 0:
                    ListViewDatas(selectIndexTag,null,"","9",null,null,null,null,"","","0","1");
                    break;
                case 1:
                    ListViewDatas(selectIndexTag,null,"","4",null,null,null,null,"","","0","1");
                    break;
                case 2:
                    ListViewDatas(selectIndexTag,null,"","6",null,null,null,null,"","","0","1");
                    break;
                case 3:
                    ListViewDatas(selectIndexTag,null,"","2",null,null,null,null,"","","0","1");
                    break;
                case 4:
                    ListViewDatas(selectIndexTag,null,"","1",null,null,null,null,"","","0","1");
                    break;
                case 5:
                    ListViewDatas(selectIndexTag,null,"","5",null,null,null,null,"","","0","1");
                    break;
                case 6:
                    ListViewDatas(selectIndexTag,null,"","3",null,null,null,null,"","","0","1");
                    break;
//                case 7:
//                    ListViewDatas(selectIndexTag,null,"0","3","20130424113427190508721241586190",null,null,null,"","","0","1");
//                    break;
                case 7:
                    ListViewDatas(selectIndexTag,null,"","7",null,null,null,null,"","","0","1");
                    break;
                case 8:
                    ListViewDatas(selectIndexTag,null,"","8",null,null,null,null,"","","0","1");
                    break;
            }
            workSpaceGridAdapter.index = i;
            loadMorePager = 1;
        }

    }

    public void onActReFresh(String name,String clickId,String type,String eclassIds){
        if (dialog != null){
            dialog.show();
        }
        this.name = name;
        this.clickId = clickId;
        this.type = type;
        this.eclassIds = eclassIds;
        switch(workSpaceGridAdapter.index){
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
        ListViewDatas(selectIndexTag,name,"",resourceStyle,clickId,type,null,eclassIds,"","","0","1");
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
     *
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
    public void ListViewDatas(int i,String name,String highqualityValue,String resourceStyle,String clickId,String type,String studentName,String eclassIds,String status,String studentUploadType,String radioValue,String pageNo){


        if (clickOrRefrush){
            this.name = null;
            this.highqualityValue = null;
            this.resourceStyle = null;
            this.clickId = null;
            this.type = null;
            this.studentUploadType = null;
            this.eclassIds = null;
            this.status = null;
            this.studentUploadType = null;
            this.pageNo = "1";
        }
        //设置主页标题栏中选择的搜索条件
        hashMap.clear();

        this.studentName = studentName;
        this.eclassIds = eclassIds;
        this.status = status;
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
            this.clickId = clickId;
        }
        //点击的树的节点类型  学科/教材/单元/次级单元    subject/teachingMaterial/unit/chapterSubject-chapterUnit
        if (type == null){
        }else{
            hashMap.put("type",new ParameterValue(type));
            this.type = type;
        }

        hashMap.put("pageNo",new ParameterValue(pageNo));
        this.pageNo = pageNo;

        //查询时的搜索文字
        if (name == null){

        }else{
            hashMap.put("name",new ParameterValue(name));
        }
        this.name = name;

        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
        //TODO
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String json = ZddcUtil.getResourceList(hashMap);
                    final WorkSpaceDatasBean bean = new Gson().fromJson(json,WorkSpaceDatasBean.class);
                    statusForLoadMore = bean.getStatus();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                            workSpaceListViewAdapter = new WorkSpaceListViewAdapter(list,context,WorkSpaceFragment.this,lv);
                            lv.setAdapter(workSpaceListViewAdapter);
                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                            loadMorePager = 1;
                            onLoad(lv);
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
     * 加载gridView的宽度
     */
    private void initGridViewWidth(){
        int size = gridList.size();
        int length = 80;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(0); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数
    }

    public void startAct(String resouceId,String type,String pageNo){
        MainActivity.map.put("resouceId",resouceId);
        MainActivity.map.put("type",type);
        MainActivity.map.put("pageNo",pageNo);
        startActivity(new Intent(context, OperateRecordActicity.class));

    }
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
    public void initXListView(final XListView listView, final BaseAdapter adapter){
        listView.setPullLoadEnable(true);
        final Handler mHandler = new Handler();
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start = ++refreshCnt;
                        workSpaceReFreshDatas(workSpaceGridAdapter.index,0);
                    }
                }, 1);
            }

            @Override
            public void onLoadMore() {
                if (isLoadMoring){
                    return;
                }
                isLoadMoring = true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch(workSpaceGridAdapter.index){
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
                        if (statusForLoadMore.equals("1")){
                            ToastUtil.showMessage("已无更多.");
                            onLoad(listView);
                            return;
                        }
                        ListViewDatasForOnLoadMore(workSpaceGridAdapter.index,name,highqualityValue,resourceStyle,clickId,type,studentName,eclassIds,status,studentUploadType,radioValue,loadMorePager+1+"");
                    }
                }, 5);
            }
        });
    }


    public void ListViewDatasForOnLoadMore(int i,String name,String highqualityValue,String resourceStyle,String clickId,String type,String studentName,String eclassIds,String status,String studentUploadType,String radioValue,String pageNo){

        //设置主页标题栏中选择的搜索条件
        hashMap.clear();

        this.studentName = studentName;
        this.eclassIds = eclassIds;
        this.status = status;
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
        //TODO
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String json = ZddcUtil.getResourceList(hashMap);
                    final WorkSpaceDatasBean bean = new Gson().fromJson(json,WorkSpaceDatasBean.class);
                    statusForLoadMore = bean.getStatus();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<WorkSpaceDatasBean.DataListBean> l = (ArrayList<WorkSpaceDatasBean.DataListBean>) bean.getDataList();
                            list.addAll(l);
                            if (l != null || l.size() != 0){
                                loadMorePager++;
                            }
                            workSpaceListViewAdapter = new WorkSpaceListViewAdapter(list,context,WorkSpaceFragment.this,lv);
                            workSpaceListViewAdapter.notifyDataSetChanged();
                            isLoadMoring = false;
                            onLoad(lv);
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
//        gridView.setSelection(gridSelectionIndex);
        workSpaceListViewAdapter = new WorkSpaceListViewAdapter(list,context,WorkSpaceFragment.this,lv);
        workSpaceListViewAdapter.notifyDataSetChanged();
        ToastUtil.showMessage("刷新完毕");
    }
}
