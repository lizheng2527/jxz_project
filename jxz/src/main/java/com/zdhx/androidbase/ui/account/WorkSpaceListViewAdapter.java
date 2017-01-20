package com.zdhx.androidbase.ui.account;

import android.content.Context;
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

import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.downloadui.NumberProgressBar;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.io.IOException;
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
        vh.fileHeadImg = (RoundCornerImageView) view.findViewById(R.id.fileHeadImg);
        String resourceStyle = list.get(i).getResourceStyle();
        if (resourceStyle != null){//有时候服务器会返回null（非正常数据）
            switch (resourceStyle){
                case "1":
                    vh.fileHeadImg.setImageResource(R.drawable.word);
                    break;
                case "2":
                    vh.fileHeadImg.setImageResource(R.drawable.ppt);
                    break;
                case "3":
                    vh.fileHeadImg.setImageResource(R.drawable.excel);
                    break;
                case "4":
                    String url = ZddcUtil.getUrlAnd(ECApplication.getInstance().getAddress()+ list.get(i).getIconUrl(),ECApplication.getInstance().getLoginUrlMap());
                    loader.DisplayImage(url, vh.fileHeadImg,false);
                    LogUtil.e(url);
                    break;
                case "5":
                    vh.fileHeadImg.setImageResource(R.drawable.music);
                    break;
                case "6":
                    vh.fileHeadImg.setImageResource(R.drawable.video);
                    break;
                case "7":
                    vh.fileHeadImg.setImageResource(R.drawable.zip);
                    break;
                case "8":
                    vh.fileHeadImg.setImageResource(R.drawable.other);
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
        vh.down.setText(list.get(i).getDown()+"");
//        删除-----------------------------------------------
        vh.deleteImg = (ImageView) view.findViewById(R.id.delete_nor);
        if (list.get(i).getCanDelete().equals("0")){
            vh.deleteImg.setVisibility(View.GONE);
        }else{
            vh.deleteImg.setVisibility(View.VISIBLE);
        }
//        下载-----------------------------------------------
        vh.downloadImg = (ImageView) view.findViewById(R.id.download);
        String name = list.get(i).getName();
        File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(idr,name);
        if (file.exists()){
            vh.downloadImg.setImageResource(R.drawable.amd_list_item_open);
        }else{
            vh.downloadImg.setImageResource(R.drawable.download);
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

    private void addClick(final ViewHolder vh, final int i) {
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

        vh.fileHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String style = list.get(i).getResourceStyle();
                if (style.equals("6")){
                    list.get(i).getDownUrl();
                    String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap());
                    LogUtil.e("视频播放地址:"+url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    frag.startActivity(intent);
                }
            }
        });
        vh.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                vh.downloadImg.setClickable(false);
                //TODO判断当前下载附件是否存在
                String name = list.get(i).getName();
                File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(idr,name);
                if (file.exists()){
                    String lastName = FileUtils.getExtensionName(file.getName());
                    try {
                        if(Constant.ATTACHMENT_DOC.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getWordFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_DOCX.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getWordFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PPT.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getPptFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PPTX.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getPptFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_XLS.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_XLSX.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getExcelFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PDF.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getPdfFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_TXT.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getTextFileIntent(file.getPath(), false));

                        }else if(Constant.ATTACHMENT_JPG.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getImageFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_PNG.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getImageFileIntent(file.getPath()));

                        }else if(Constant.ATTACHMENT_GIF.equals(lastName)){
                            frag.getActivity().startActivity(IntentUtil.getImageFileIntent(file.getPath()));

                        }else{
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
                    return;
                }
                final ECProgressDialog dialog = new ECProgressDialog(context);
                dialog.show();
                // 下载处理
                vh.progressBar.setVisibility(View.VISIBLE);
                final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
                    @Override
                    public void predownload() {

                    }

                    @Override
                    public void downloading(int progress, int position) {
                        dialog.dismiss();
                        vh.progressBar.setProgress(progress);
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


                    }

                    @Override
                    public void canceled(int position) {
                        vh.progressBar.setVisibility(View.INVISIBLE);

                    }
                }, context);
                downloadAsyncTask.execute(ZddcUtil.getUrlAnd(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()), "aaa", i + "", list.get(i).getName());
                LogUtil.e("下载资源地址"+ZddcUtil.getUrlAnd(ECApplication.getInstance().getAddress()+list.get(i).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()));
            }
        });
    }
    class ViewHolder{
        private RoundCornerImageView fileHeadImg;//文件头像
        private TextView name;
        private TextView size;
        private TextView userName;
        private TextView createTime;
        private TextView browse;
        private TextView down;
        private ImageView downloadImg,deleteImg,highQuantityImgS,highQuantityImgB;
        private LinearLayout downClick;
        private LinearLayout browseClick;
        private NumberProgressBar progressBar;
    }
}
