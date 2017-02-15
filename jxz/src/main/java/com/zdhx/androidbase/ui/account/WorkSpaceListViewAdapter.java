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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.downloadui.NumberProgressBar;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;
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
//        头像-----------------------------------------------
        vh.fileHeadImg = (SimpleDraweeView) view.findViewById(R.id.fileHeadImg);
        String resourceStyle = list.get(i).getResourceStyle();
        //        预览------------------------------------------------------
        vh.btnPreview = (ImageView) view.findViewById(R.id.btn_pre);
        if (resourceStyle != null){//有时候服务器会返回null（非正常数据）
            switch (resourceStyle){
                case "1":
                    vh.fileHeadImg.setImageResource(R.drawable.word);
                    vh.btnPreview.setVisibility(View.VISIBLE);
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
                    LogUtil.w("工作平台文件头像地址:"+url);
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
                case "100":
                    vh.fileHeadImg.setImageResource(R.drawable.icon_outlink);
                    vh.btnPreview.setVisibility(View.VISIBLE);
                    break;
            }
        }
        vh.progressBar = (NumberProgressBar) view.findViewById(R.id.amd_progressBar);
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
        vh.deleteImg = (ImageView) view.findViewById(R.id.delete_nor);
        if (list.get(i).getCanDelete().equals("0")){
            vh.deleteImg.setVisibility(View.GONE);
        }else{
            vh.deleteImg.setVisibility(View.VISIBLE);
        }

//        String getName = list.get(i).getName().toLowerCase();
        //图片显示预览
//        if (getName.contains(".jpg")||getName.contains(".jpeg")||getName.contains(".png")||getName.contains(".mp4")||getName.contains("avi")||getName.contains(".3gp")||getName.contains(".flv")){
//            vh.btnPreview.setVisibility(View.VISIBLE);
//        }else{
//            vh.btnPreview.setVisibility(View.GONE);
//        }
        //外部链接显示预览
//        if (list.get(i).getResourceStyle().equals("100")){
//            vh.btnPreview.setVisibility(View.VISIBLE);
//        }else{
//            vh.btnPreview.setVisibility(View.GONE);
//        }

//        下载-----------------------------------------------
        vh.downloadImg = (ImageView) view.findViewById(R.id.download);
        String name = list.get(i).getName();
        //如果是自己上传的，隐藏下载按钮
        if (list.get(i).getUserName().equals(ECApplication.getInstance().getCurrentUser().getName())){
            vh.downloadImg.setVisibility(View.GONE);
        }
        //外部链接隐藏下载按钮
        else if (list.get(i).getResourceStyle().equals("100")){
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
        //TODO
        if (file.exists()){
            if (!list.get(i).getResourceStyle().equals("7")&&!list.get(i).getResourceStyle().equals("8")){
                vh.btnPreview.setVisibility(View.GONE);
                if (!list.get(i).getResourceStyle().equals("7")&&!list.get(i).getResourceStyle().equals("8")&&!list.get(i).getResourceStyle().equals("100")){
                    vh.downloadImg.setImageResource(R.drawable.amd_list_item_open);
                }else{
                    vh.downloadImg.setVisibility(View.GONE);
                    vh.btnPreview.setVisibility(View.VISIBLE);
                }
            }else{
                vh.downloadImg.setVisibility(View.GONE);
            }
        }else{
            vh.downloadImg.setImageResource(R.drawable.download);
        }

        //判断当前是否正在下载任务，如果下载任务，进行标记，将其他的progressBar设置为隐藏
        if (downTag){
            if (i == showProBarPosition){
                vh.progressBar.setVisibility(View.VISIBLE);
            }else{
                vh.progressBar.setVisibility(View.INVISIBLE);
            }
        }else{
            vh.progressBar.setVisibility(View.INVISIBLE);
        }

        vh.highQuantityImgS = (ImageView) view.findViewById(R.id.good_nor);
        vh.highQuantityImgB = (ImageView) view.findViewById(R.id.quality_nor);
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
        addClick(vh,i);
        return view;
    }

    public boolean downTag = false;

    private void addClick(final ViewHolder vh, final int i) {
        vh.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName = list.get(i).getName().toLowerCase();
                File file = new File(list.get(i).getDownUrl());
                String lastName = FileUtils.getExtensionName(file.getName());
                LogUtil.w("lastName:"+lastName);
//                视频**********************************************************************
                if (list.get(i).getResourceStyle().equals("6")||list.get(i).getResourceStyle().equals("5")){

                    String downUrl = list.get(i).getDownUrl();
                    String videoUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+downUrl,ECApplication.getInstance().getLoginUrlMap());
                    frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(videoUrl));
                }
//                外部链接**********************************************************************
                else if(list.get(i).getResourceStyle().equals("100")){
                    Uri uri = Uri.parse(list.get(i).getDownUrl());
                    Intent it = new Intent(Intent.ACTION_VIEW,uri);
                    frag.startActivity(it);
                }
//                WORD**********************************************************************
                else if(list.get(i).getResourceStyle().equals("1")){
                    String wordUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap());
                    frag.getActivity().startActivity(IntentUtil.getWordFileIntent(wordUrl));
                }
//                ppt**********************************************************************
                else if(list.get(i).getResourceStyle().equals("2")){
                    String pptUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap());
                    frag.getActivity().startActivity(IntentUtil.getPptFileIntent(pptUrl));

//                excel**********************************************************************
                }else if(list.get(i).getResourceStyle().equals("3")){
                    String excelUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap());
                    frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(excelUrl));
                }
                //图片
                else if (list.get(i).getResourceStyle().equals("4")){
                    Intent intent = new Intent(context, ImagePagerActivity.class);
                    intent.putExtra("images", new String[]{ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap())});
                    intent.putExtra("image_index",0);
                    frag.startActivity(intent);
                }
//                //音乐
//                else if (list.get(i).getResourceStyle().equals("5")){
//                    File audioFile = new File(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()));
//                    Uri uri = Uri.parse(audioFile.getAbsolutePath());
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.setAction(Intent.ACTION_DEFAULT);
////        intent.addCategory(Intent.CATEGORY_APP_MUSIC);
//                    intent.setDataAndType(uri, "audio/*");
//                    frag.startActivity(intent);
//                }
                else{
                    frag.getActivity().startActivity(IntentUtil.getAllFileIntent(file.getPath()));
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
                frag.startAct(list.get(i).getId(),"2","1");
            }
        });
        vh.browseClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag.startAct(list.get(i).getId(),"1","1");
            }
        });

        vh.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECAlertDialog.buildAlert(context, "是否删除本条信息？", "确定", "取消", new DialogInterface.OnClickListener() {
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
                                    }
                                },5);

                            }
                        }).start();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                dialog.setPressText("正在加载资源..");
                dialog.show();
                LogUtil.w("该展示dialog了");
                //TODO判断当前下载附件是否存在
                String name = list.get(i).getName();
                File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dir = new File(idr+"/jxz");
                if (!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir,name);
                if (file.exists()){
                    String lastName = FileUtils.getExtensionName(file.getName());
                    try {
                        if(Constant.ATTACHMENT_DOC.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getWordFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_DOCX.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getWordFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PPT.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getPptFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PPTX.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getPptFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_XLS.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_XLSX.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PDF.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getPdfFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_TXT.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getTextFileIntent(file.getPath(), false));

                            //展示所有已经下载过的图片
                        }else if(Constant.ATTACHMENT_JPG.equals(lastName)|Constant.ATTACHMENT_PNG.equals(lastName)){
                            File[] files = dir.listFiles();
                            ArrayList<String> paths = new ArrayList<String>();
                            int selectCount = 0;
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getAbsolutePath().contains(".jpg")||files[i].getAbsolutePath().contains(".png")||files[i].getAbsolutePath().contains(".jpeg")||files[i].getAbsolutePath().contains(".JPG")){
                                    paths.add(files[i].getAbsolutePath());
                                }
                            }
                            String[] urls = new String[paths.size()];
                            for (int i1 = 0; i1 < paths.size(); i1++) {
                                urls[i1] = paths.get(i1);
                                if (paths.get(i1).equals(file.getAbsolutePath())||paths.get(i1).equals(file.getPath())){
                                    selectCount = i1;
                                }
                                LogUtil.e("第"+i1+"张图片！，名字为："+name);
                            }
                            Intent intent = new Intent(context, ImagePagerActivity.class);
                            intent.putExtra("images", urls);
                            for (int i1 = 0; i1 < urls.length; i1++) {
                                LogUtil.e("获取到的图片路径："+urls[i1]);
                            }
                            intent.putExtra("image_index",selectCount);
                            MainActivity.map.put("selectCountForPager",name);
                            dialog.dismiss();
                            frag.startActivity(intent);
                        }
                        else if(Constant.ATTACHMENT_GIF.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getImageFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_3GP.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(file.getPath()));
                        }
                        else if(Constant.ATTACHMENT_AVI.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_MP4.equals(lastName)){
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getVideoFileIntent(file.getPath()));

                        }else{
                            dialog.dismiss();
                            frag.getActivity().startActivity(IntentUtil.getAllFileIntent(file.getPath()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showMessage("设备中未安装相应的查看程序");
                    }
                    return;
                }
                if (list.get(i).getUserId().equals(ECApplication.getInstance().getCurrentUser().getId())){
                    ToastUtil.showMessage("下载失败.");
                    dialog.dismiss();
                    return;
                }

                if (downTag){
                    ToastUtil.showMessage("正在执行下载任务...");
                    return;
                }
                // 下载处理
                vh.progressBar.setVisibility(View.VISIBLE);
                final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
                    @Override
                    public void predownload() {
                        showProBarPosition = i;
                        downTag = true;
                    }

                    @Override
                    public void downloading(int progress, int position) {
                        dialog.dismiss();
                        vh.progressBar.setProgress(progress);
                        vh.downloadImg.setClickable(false);
                    }

                    @Override
                    public void downloaded(File file, int position) {
                        vh.progressBar.setVisibility(View.INVISIBLE);
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
                                            if (!list.get(i).getResourceStyle().equals("7")&&!list.get(i).getResourceStyle().equals("8")){
                                                vh.downloadImg.setImageResource(R.drawable.amd_list_item_open);
                                            }else{
                                                vh.downloadImg.setVisibility(View.GONE);
                                            }
                                            list.get(i).setDown(list.get(i).getDown()+1);
                                            notifyDataSetChanged();
                                            downTag = false;
                                        }
                                    },5);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void canceled(int position) {
                        vh.progressBar.setVisibility(View.INVISIBLE);
                    }
                }, context);
                downloadAsyncTask.execute(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()), "aaa", i + "", list.get(i).getName());
                LogUtil.e("下载资源地址"+ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()));
            }
        });
    }

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
    }
}
