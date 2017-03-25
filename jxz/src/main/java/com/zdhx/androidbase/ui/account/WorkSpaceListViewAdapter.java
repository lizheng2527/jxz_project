package com.zdhx.androidbase.ui.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.downloadui.NumberProgressBar;
import com.zdhx.androidbase.ui.plugin.FileExplorerActivity;
import com.zdhx.androidbase.ui.quantity.PrePassActivity;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lizheng on 2016/12/26.
 */

public class WorkSpaceListViewAdapter extends BaseAdapter {


    private List<WorkSpaceDatasBean.DataListBean> list;

    private Context context;

    private LayoutInflater inflater;

    private ImageLoader loader;

    private WorkSpaceFragment frag;

    private HashMap<String,ParameterValue> map = new HashMap();

    private Handler handler;

    private XListView listView;

    private int showProBarPosition = -1;

    public WorkSpaceListViewAdapter(List<WorkSpaceDatasBean.DataListBean> list, Context context, WorkSpaceFragment frag, XListView ListView) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        loader = new ImageLoader(context);
        handler = new Handler();
        this.listView = ListView;
        this.frag = frag;
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
        View item = view ;
        ViewHolder vh = null;
        if (view == null){
            vh = new ViewHolder();
            view = View.inflate(context, R.layout.fragment_workspace_listview_item,null);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        vh.prePass = (ImageView) view.findViewById(R.id.pre_pass);
        vh.btnPreview = (ImageView) view.findViewById(R.id.btn_pre);
        vh.batchSelectBox = (CheckBox) view.findViewById(R.id.batchselectbox);
        vh.deleteImg = (ImageView) view.findViewById(R.id.delete_nor);
        vh.downloadImg = (ImageView) view.findViewById(R.id.download);
        vh.highQuantityImgS = (ImageView) view.findViewById(R.id.good_nor);
        vh.highQuantityImgB = (ImageView) view.findViewById(R.id.quality_nor);
//        头像-----------------------------------------------
        vh.fileHeadImg = (SimpleDraweeView) view.findViewById(R.id.fileHeadImg);
        String resourceStyle = list.get(i).getResourceStyle();
        //        预览------------------------------------------------------

        if (resourceStyle != null){//有时候服务器会返回null（非正常数据）
            switch (resourceStyle){
                case "1":
                    vh.fileHeadImg.setImageResource(R.drawable.word);
                    vh.btnPreview.setVisibility(View.GONE);
                    break;
                case "2":
                    vh.fileHeadImg.setImageResource(R.drawable.ppt);
                    vh.btnPreview.setVisibility(View.GONE);
                    break;
                case "3":
                    vh.fileHeadImg.setImageResource(R.drawable.excel);
                    vh.btnPreview.setVisibility(View.GONE);
                    break;
                case "4":
                    vh.btnPreview.setVisibility(View.VISIBLE);
                    String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+ list.get(i).getIconUrl(),ECApplication.getInstance().getLoginUrlMap());
                    Uri uri = Uri.parse(url);
                    vh.fileHeadImg.setImageURI(uri);
                    break;
                case "5":
                    vh.fileHeadImg.setImageResource(R.drawable.music);
                    vh.btnPreview.setVisibility(View.VISIBLE);
                    break;
                case "6":
                    vh.fileHeadImg.setImageResource(R.drawable.video);
                    vh.btnPreview.setVisibility(View.VISIBLE);
                    break;
                case "7":
                    vh.fileHeadImg.setImageResource(R.drawable.zip);
                    vh.btnPreview.setVisibility(View.GONE);
                    break;
                case "8":
                    vh.fileHeadImg.setImageResource(R.drawable.other);
                    vh.btnPreview.setVisibility(View.GONE);
                    break;
                case "0":
                    vh.fileHeadImg.setImageResource(R.drawable.other);
                    vh.btnPreview.setVisibility(View.GONE);
                    break;
                case "100":
                    vh.fileHeadImg.setImageResource(R.drawable.icon_outlink);
                    vh.btnPreview.setVisibility(View.VISIBLE);
                    break;
            }
        }
        vh.progressBar = (NumberProgressBar) view.findViewById(R.id.amd_progressBar);
        vh.progressIndexLinear = (LinearLayout) view.findViewById(R.id.down_progress_linear);
        vh.cancelDownLoad = (TextView) view.findViewById(R.id.cancelDownLoad);
//        标题-----------------------------------------------
        vh.name = (TextView) view.findViewById(R.id.fileTitle);
        vh.name.setText(list.get(i).getName());
//        文件大小-----------------------------------------------
        vh.size = (TextView) view.findViewById(R.id.fileSize);
        vh.size.setText("("+list.get(i).getSize()+")");
        if (list.get(i).getResourceStyle().equals("100")){
            vh.size.setVisibility(View.INVISIBLE);
        }else{
            vh.size.setVisibility(View.VISIBLE);
        }
//        上传者姓名-----------------------------------------------
        vh.userName = (TextView) view.findViewById(R.id.fileuserName);
        vh.userName.setText(list.get(i).getUserName());
//        创建时间-----------------------------------------------
        vh.createTime = (TextView) view.findViewById(R.id.createTime);
        vh.createTime.setText(list.get(i).getCreateTime());
//        浏览量-----------------------------------------------
        vh.browse = (TextView) view.findViewById(R.id.browse);
        vh.browse.setText(list.get(i).getBrowse()+"");

//        下载量-----------------------------------------------
        vh.down = (TextView) view.findViewById(R.id.down);
        vh.downText = (TextView) view.findViewById(R.id.downtext);
        vh.down.setText(list.get(i).getDown()+"");
        if (list.get(i).getResourceStyle().equals("100")){
            vh.down.setVisibility(View.INVISIBLE);
            vh.downText.setVisibility(View.INVISIBLE);
        }else{
            vh.down.setVisibility(View.VISIBLE);
            vh.downText.setVisibility(View.VISIBLE);
        }
//        删除-----------------------------------------------

        if (list.get(i).getCanDelete().equals("0")){
            vh.deleteImg.setVisibility(View.GONE);
        }else{
            vh.deleteImg.setVisibility(View.VISIBLE);
        }

//        下载-----------------------------------------------

        String name = list.get(i).getName();
//        //如果是自己上传的，隐藏下载按钮
//        if (list.get(i).getUserName().equals(ECApplication.getInstance().getCurrentUser().getName())){
////            vh.downloadImg.setVisibility(View.GONE);
//        }
//        //外部链接隐藏下载按钮
//        else
        if (list.get(i).getResourceStyle().equals("100")){
            vh.downloadImg.setVisibility(View.GONE);
        }
        else{
            vh.downloadImg.setVisibility(View.VISIBLE);
        }

        File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(idr+"/jxz");
        if (!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,name);
        if (file.exists()){
//            if (!list.get(i).getResourceStyle().equals("7")&&!list.get(i).getResourceStyle().equals("8")){
            vh.btnPreview.setVisibility(View.GONE);
//                if (!list.get(i).getResourceStyle().equals("7")&&!list.get(i).getResourceStyle().equals("8")&&!list.get(i).getResourceStyle().equals("100")){
            vh.downloadImg.setImageResource(R.drawable.amd_list_item_open);
//                }else{
//                    vh.downloadImg.setVisibility(View.GONE);
//                    vh.btnPreview.setVisibility(View.VISIBLE);
//                }
//            }else{
//                vh.downloadImg.setVisibility(View.GONE);
//            }
        }else{
            vh.downloadImg.setImageResource(R.drawable.download);
        }

        //判断当前是否正在下载任务，如果下载任务，进行标记，将其他的progressBar设置为隐藏
        if (list.get(i).isLoading()){
            vh.progressIndexLinear.setVisibility(View.VISIBLE);
        }else{
            vh.progressIndexLinear.setVisibility(View.GONE);
        }


//        无预评权限也无终评权限-----------------------------------------------
        if (list.get(i).getTeacherpreselection().equals("0")&&list.get(i).getTeacherfinalselection().equals("0")){
            vh.highQuantityImgS.setVisibility(View.GONE);
            vh.highQuantityImgB.setVisibility(View.GONE);
        }
//        有预评权限无终评权限-----------------------------------------------
        if (list.get(i).getTeacherpreselection().equals("1")&&list.get(i).getTeacherfinalselection().equals("0")){
            switch (list.get(i).getHighQuantity()){
                case "0":
                    vh.highQuantityImgS.setVisibility(View.VISIBLE);
                    vh.highQuantityImgS.setImageResource(R.drawable.good_nor);
                    vh.highQuantityImgB.setVisibility(View.GONE);
                    break;
                case "1":
                    vh.highQuantityImgS.setVisibility(View.VISIBLE);
                    vh.highQuantityImgS.setImageResource(R.drawable.good_click);
                    vh.highQuantityImgB.setVisibility(View.GONE);
                    break;
                case "2":
                    vh.highQuantityImgS.setVisibility(View.GONE);
                    vh.highQuantityImgB.setVisibility(View.GONE);
                    break;
            }
        }
        //有终评权限，无预评权限
        if (list.get(i).getTeacherpreselection().equals("0")&&list.get(i).getTeacherfinalselection().equals("1")){
            switch (list.get(i).getHighQuantity()){
                case "0":
                    vh.highQuantityImgS.setVisibility(View.GONE);
                    vh.highQuantityImgB.setVisibility(View.GONE);
                    break;
                case "1":
                    vh.highQuantityImgS.setVisibility(View.GONE);
                    vh.highQuantityImgB.setVisibility(View.VISIBLE);
                    vh.highQuantityImgB.setImageResource(R.drawable.quality_nor);
                    break;
                case "2":
                    vh.highQuantityImgS.setVisibility(View.GONE);
                    vh.highQuantityImgB.setVisibility(View.VISIBLE);
                    vh.highQuantityImgB.setImageResource(R.drawable.quality_click);
                    break;
            }
        }
        //有终评权限，有预评权限
        if (list.get(i).getTeacherpreselection().equals("1")&&list.get(i).getTeacherfinalselection().equals("1")){
            switch (list.get(i).getHighQuantity()){
                case "0":
                    vh.highQuantityImgS.setVisibility(View.VISIBLE);
                    vh.highQuantityImgS.setImageResource(R.drawable.good_nor);
                    vh.highQuantityImgB.setVisibility(View.GONE);
                    break;
                case "1":
                    vh.highQuantityImgS.setVisibility(View.GONE);
                    vh.highQuantityImgB.setVisibility(View.VISIBLE);
                    vh.highQuantityImgB.setImageResource(R.drawable.quality_nor);
                    break;
                case "2":
                    vh.highQuantityImgS.setVisibility(View.GONE);
                    vh.highQuantityImgB.setVisibility(View.VISIBLE);
                    vh.highQuantityImgB.setImageResource(R.drawable.quality_click);
                    break;
            }
        }
        vh.downClick = (LinearLayout) view.findViewById(R.id.downClick);
        vh.browseClick = (LinearLayout) view.findViewById(R.id.browseClick);

        //如果是审核通过的，不显示点击审核图片(教师登录独有)
        if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
            if (list.get(i).getCheckStatus().equals("审核通过")){
                vh.prePass.setVisibility(View.GONE);
            }else{
                vh.highQuantityImgS.setVisibility(View.GONE);
                vh.highQuantityImgB.setVisibility(View.GONE);
                //待审核或审核失败的需要判断，如果是批量选择，隐藏审核图片点击事件
                if (WorkSpaceFragment.isBatchSelect){
                    vh.prePass.setVisibility(View.GONE);
                }else{
                    vh.prePass.setVisibility(View.VISIBLE);
                }
            }
            //判断当前是否是批量选择，如果是批量选择，显示checkBox。
            if (WorkSpaceFragment.isBatchSelect&&!list.get(i).getCheckStatus().equals("审核通过")&&MainActivity.selectBatchLinearIsShowing){
                vh.batchSelectBox.setVisibility(View.VISIBLE);
                vh.prePass.setVisibility(View.GONE);
            }else{
                vh.batchSelectBox.setVisibility(View.GONE);
            }
            vh.batchSelectBox.setChecked(list.get(i).isSelect());
        }else{
            vh.prePass.setVisibility(View.GONE);
            vh.highQuantityImgS.setVisibility(View.GONE);
            vh.highQuantityImgB.setVisibility(View.GONE);
        }
        addClick(vh,i);
        return view;
    }

    private void addClick(final ViewHolder vh, final int i) {
        //批量审核checkBox点击事件
        vh.batchSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(i).isSelect()){
                    list.get(i).setSelect(false);
                    frag.getBatchSelectMap().remove(list.get(i));
                }else{
                    list.get(i).setSelect(true);
                    frag.getBatchSelectMap().put(list.get(i),list.get(i).getId());
                }
                MainActivity.setAllSelectText(frag.isSelectAll());
            }
        });
        //审核
        vh.prePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PrePassActivity.class);
                MainActivity.map.put("ids",list.get(i).getId());
                MainActivity.map.put("WorkSpaceFragment",frag);
                frag.startActivity(intent);
            }
        });
        //预览按钮
        vh.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resourceStyle = list.get(i).getResourceStyle();
                String showUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap());
//                音频、视频**********************************************************************
                if (resourceStyle.equals("6")||resourceStyle.equals("5")){

//                    frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(showUrl));
                    if (IntentUtil.isIntentAvailable(context,IntentUtil.getVideoFileIntent(showUrl))){
                        frag.startActivity(IntentUtil.getVideoFileIntent(showUrl));
                    }else{
                        ToastUtil.showMessage("当前手机不支持打开该文件");
                    }
                }
//                外部链接**********************************************************************
                else if(resourceStyle.equals("100")){
                    Uri uri = Uri.parse(list.get(i).getDownUrl());
                    Intent it = new Intent(Intent.ACTION_VIEW,uri);
                    frag.startActivity(it);
                }
//                WORD**********************************************************************
                else if(resourceStyle.equals("1")){
//                    frag.getActivity().startActivity(IntentUtil.getWordFileIntent(showUrl));
                    return;
                }
//                ppt**********************************************************************
                else if(resourceStyle.equals("2")){
//                    frag.getActivity().startActivity(IntentUtil.getPptFileIntent(showUrl));
                    return;

//                excel**********************************************************************
                }else if(resourceStyle.equals("3")){
//                    frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(showUrl));
                    return;
                }
                //图片
                else if (resourceStyle.equals("4")){
                    Intent intent = new Intent(context, ImagePagerActivity.class);
                    intent.putExtra("images", new String[]{showUrl});
                    intent.putExtra("image_index",0);
                    frag.startActivity(intent);
                }
                //不确定格式
                else{
                    ToastUtil.showMessage("不可预览");
                    return;
//                    frag.getActivity().startActivity(IntentUtil.getAllFileIntent(showUrl));
                }
                new ProgressThreadWrap(context, new RunnableWrap() {
                    @Override
                    public void run() {
                        if (list.get(i).getUserName().equals(ECApplication.getInstance().getCurrentUser().getName())){
                            return;
                        }
                        String resouceId = list.get(i).getId();
                        String userId = ECApplication.getInstance().getCurrentUser().getId();
                        String type = "1";
                        HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                        map.put("resouceId",new ParameterValue(resouceId));
                        map.put("userId",new ParameterValue(userId));
                        map.put("type",new ParameterValue(type));
                        map.putAll(ECApplication.getInstance().getLoginUrlMap());
                        try {
                            ZddcUtil.doPreviewOrDown(map);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    list.get(i).setBrowse(list.get(i).getBrowse()+1);
                                    notifyDataSetChanged();
                                }
                            },5);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        vh.downClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(i).getDown() != 0)
                    frag.startAct(list.get(i).getId(),"2","1");
            }
        });
        vh.browseClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(i).getBrowse() != 0)
                    frag.startAct(list.get(i).getId(),"1","1");
            }
        });

        vh.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECAlertDialog.buildAlert(context, "是否删除本条信息？", "取消", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressUtil.show(context,"正在删除");
                        map.clear();
                        map.put("ids",new ParameterValue(list.get(i).getId()));
                        map.putAll(ECApplication.getInstance().getLoginUrlMap());
                        new ProgressThreadWrap(context, new RunnableWrap() {
                            @Override
                            public void run() {
                                try {
                                    ZddcUtil.deleteChapterResource(map);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        list.remove(i);
                                        ProgressUtil.hide();
                                        notifyDataSetChanged();
                                        for (int i = 0; i < 9; i++) {
                                            if (i == WorkSpaceFragment.isSelectPosition){
                                                frag.setDataChanged(i,false);
                                            }else{
                                                frag.setDataChanged(i,true);
                                            }
                                        }
                                    }
                                },5);

                            }
                        }).start();
                    }
                }).show();


            }
        });

        vh.highQuantityImgS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag.startCheckAct(list.get(i).getId(),"1",i,frag,list.get(i).getHighQuantity(),list.get(i).getTeacherfinalselection());
            }
        });
        vh.highQuantityImgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag.startCheckAct(list.get(i).getId(),"2",i,frag,list.get(i).getHighQuantity(),list.get(i).getTeacherfinalselection());
            }
        });

        vh.downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ECProgressDialog dialog = new ECProgressDialog(context);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setPressText("正在加载资源..");
                dialog.show();
                //TODO判断当前下载附件是否存在
                String name = list.get(i).getName();
                File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dir = new File(idr+"/jxz");
                if (!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir,name);
                if (file.exists()){
                    String resourceStyle = list.get(i).getResourceStyle();
                    dialog.dismiss();
                    try {
                        switch (resourceStyle){
                            case "1":
                                if (IntentUtil.isIntentAvailable(context,IntentUtil.getWordFileIntent(file.getAbsolutePath()))){
                                    frag.getActivity().startActivity(IntentUtil.getWordFileIntent(file.getAbsolutePath()));
                                }else{
                                    ToastUtil.showMessage("当前手机不支持打开该文件");
                                }
                                break;
                            case "2":

                                if (IntentUtil.isIntentAvailable(context,IntentUtil.getPptFileIntent(file.getAbsolutePath()))){
                                    frag.getActivity().startActivity(IntentUtil.getPptFileIntent(file.getAbsolutePath()));
                                }else{
                                    ToastUtil.showMessage("当前手机不支持打开该文件");
                                }
                                break;
                            case "3":

                                if (IntentUtil.isIntentAvailable(context,IntentUtil.getExcelFileIntent(file.getAbsolutePath()))){
                                    frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(file.getAbsolutePath()));
                                }else{
                                    ToastUtil.showMessage("当前手机不支持打开该文件");
                                }
                                break;
                            case "4":
                                File[] files = dir.listFiles();
                                ArrayList<String> paths = new ArrayList<String>();
                                int selectCount = 0;
                                for (int i = 0; i < files.length; i++) {
                                    String path = files[i].getAbsolutePath().toLowerCase();
                                    if (path.contains(".jpg")||path.contains(".png")||path.contains(".jpeg")){
                                        paths.add(files[i].getAbsolutePath());
                                    }
                                }
                                String[] urls = new String[paths.size()];
                                for (int i1 = 0; i1 < paths.size(); i1++) {
                                    urls[i1] = paths.get(i1);
                                    if (paths.get(i1).equals(file.getAbsolutePath())||paths.get(i1).equals(file.getPath())){
                                        selectCount = i1;
                                    }
                                }
                                Intent intent = new Intent(context, ImagePagerActivity.class);
                                intent.putExtra("images", urls);
                                intent.putExtra("image_index",selectCount);
                                MainActivity.map.put("selectCountForPager",name);
                                dialog.dismiss();
                                frag.startActivity(intent);
                                break;
                            case "5":

                                if (IntentUtil.isIntentAvailable(context,IntentUtil.getVideoFileIntent(file.getAbsolutePath()))){
                                    frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(file.getAbsolutePath()));
                                }else{
                                    ToastUtil.showMessage("当前手机不支持打开该文件");
                                }
                                break;
                            case "6":
                                if (IntentUtil.isIntentAvailable(context,IntentUtil.getVideoFileIntent(file.getAbsolutePath()))){
                                    frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(file.getAbsolutePath()));
                                }else{
                                    ToastUtil.showMessage("当前手机不支持打开该文件");
                                }
                                break;
                            case "7":
                                //跳转到指定位置目录
                                MainActivity.map.put("parentFile",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                MainActivity.map.put("subFile",new File(ECApplication.getInstance().getDownloadJxzDir()));
                                MainActivity.map.put("isIntentCode","openFile");
                                Intent intent1 = new Intent(context,FileExplorerActivity.class);
                                intent1.putExtra("key_title",context.getResources().getString(R.string.search_file_title));
                                context.startActivity(intent1);
                                break;
                            case "8":
                                //跳转到指定位置目录
                                MainActivity.map.put("parentFile",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                MainActivity.map.put("subFile",new File(ECApplication.getInstance().getDownloadJxzDir()));
                                MainActivity.map.put("isIntentCode","openFile");
                                Intent intent2 = new Intent(context,FileExplorerActivity.class);
                                intent2.putExtra("key_title",context.getResources().getString(R.string.search_file_title));
                                context.startActivity(intent2);
                                break;
                            case "0":
                                //跳转到指定位置目录
                                MainActivity.map.put("parentFile",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                MainActivity.map.put("subFile",new File(ECApplication.getInstance().getDownloadJxzDir()));
                                MainActivity.map.put("isIntentCode","openFile");
                                Intent intent3 = new Intent(context,FileExplorerActivity.class);
                                intent3.putExtra("key_title",context.getResources().getString(R.string.search_file_title));
                                context.startActivity(intent3);
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showMessage("设备中未安装相应的查看程序");
                    }
                    return;
                }
//                if (list.get(i).getUserId().equals(ECApplication.getInstance().getCurrentUser().getId())){
//                    ToastUtil.showMessage("下载失败.");
//                    dialog.dismiss();
//                    return;
//                }

//                if (downTag){
//                    ToastUtil.showMessage("正在执行下载任务...");
//                    dialog.dismiss();
//                    return;
//                }
                ECAlertDialog.buildAlert(context, "是否下载本条信息？", "取消", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        vh.progressIndexLinear.setVisibility(View.VISIBLE);
                        final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
                            @Override
                            public void predownload() {
                                showProBarPosition = i;
                                if (downCounts>3){
                                    ToastUtil.showMessage("当前下载队列过多..");
                                    return;
                                }
                                list.get(i).setLoading(true);
                                downCounts = downCounts +1;
                            }

                            @Override
                            public void downloading(int progress, int position) {
                                dialog.dismiss();
                                vh.progressBar.setProgress(progress);
                                vh.downloadImg.setClickable(false);
                            }

                            @Override
                            public void downloaded(File file1, int position) {
                                vh.progressIndexLinear.setVisibility(View.GONE);
                                if (file1 != null){
                                    File file = new File(file1.getParent(),list.get(i).getName());
                                    file1.renameTo(file);
                                    downCounts = downCounts -1;
                                    list.get(i).setLoading(false);
                                    new ProgressThreadWrap(context, new RunnableWrap() {
                                        @Override
                                        public void run() {
                                            String resouceId = list.get(i).getId();
                                            String userId = ECApplication.getInstance().getCurrentUser().getId();
                                            String type = "2";
                                            HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                                            map.put("resouceId",new ParameterValue(resouceId));
                                            map.put("userId",new ParameterValue(userId));
                                            map.put("type",new ParameterValue(type));
                                            map.putAll(ECApplication.getInstance().getLoginUrlMap());
                                            try {
                                                ZddcUtil.doPreviewOrDown(map);
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        vh.downloadImg.setImageResource(R.drawable.amd_list_item_open);
                                                        list.get(i).setDown(list.get(i).getDown()+1);
                                                        notifyDataSetChanged();
                                                    }
                                                },5);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }else{
                                    ToastUtil.showMessage("文件存在未知错误");
                                }
                            }

                            @Override
                            public void canceled(int position) {
                                File file = new File(ECApplication.getInstance().getDownloadJxzDir(),"jxz_workSpace_downFile");
                                if (file.exists()){
                                    file.delete();
                                }
                                list.get(i).setLoading(false);
                                notifyDataSetChanged();
                            }
                        }, context);
                        downloadAsyncTask.execute(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()), "aaa", i + "","jxz_workSpace_downFile");
                        MainActivity.map.put("jxz_workSpace_downFile"+i,downloadAsyncTask);
                    }
                }).show();
                // 下载处理

            }
        });
        vh.cancelDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECAlertDialog.buildAlert(context, "是否取消本条下载？", "取消", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        DownloadAsyncTask d = (DownloadAsyncTask) MainActivity.map.get("jxz_workSpace_downFile"+i);
                        d.cancel(true);
                    }
                }).show();

            }
        });
    }


    private static int downCounts;

    class ViewHolder{
        private SimpleDraweeView fileHeadImg;//文件头像
        private TextView name;
        private TextView size;
        private TextView userName;
        private TextView createTime;
        private TextView browse;
        private TextView down;
        private ImageView downloadImg,deleteImg,highQuantityImgS,highQuantityImgB,btnPreview;
        private LinearLayout downClick;
        private LinearLayout browseClick;
        private NumberProgressBar progressBar;
        private TextView downText;
        private ImageView prePass;
        private CheckBox batchSelectBox;
        //下载进度条及按钮
        private LinearLayout progressIndexLinear;
        private TextView cancelDownLoad;
    }
}
