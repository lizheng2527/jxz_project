package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.ImagePagerActivity;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.downloadui.NumberProgressBar;
import com.zdhx.androidbase.ui.plugin.FileExplorerActivity;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.treadssearch.UpFileActivity;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.SingleMediaScanner;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lizheng on 2016/12/26.
 */

public class TeachCourseAdapter extends BaseAdapter {

    private ArrayList<CourseWare> list;
    private Context context;
    private YKTFragment frag;
    public TeachCourseAdapter(ArrayList<CourseWare> list, Context context,YKTFragment frag) {
        this.list = list;
        this.context = context;
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
        ViewHolder vh = null;
        if (view == null){
            vh = new ViewHolder();
            view = View.inflate(context, R.layout.fragment_ykt_teachresouse_listview_item,null);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }
        vh.teachResouceTitle = (TextView) view.findViewById(R.id.teachResouceTitle);
        vh.teachResouceTitle.setText(list.get(i).getTitle());

        vh.teachResouceDateTime = (TextView) view.findViewById(R.id.teachResouceDateTime);
        vh.teachResouceDateTime.setText(list.get(i).getDateTime());

        vh.fileName = (TextView) view.findViewById(R.id.filename);
        vh.fileName.setText(list.get(i).getFileName());

        vh.fileHeadImg = (SimpleDraweeView) view.findViewById(R.id.filehead);
        setHeadImg(vh,i);


        vh.linearFile = (LinearLayout) view.findViewById(R.id.linearFile);

        vh.prePass = (ImageView) view.findViewById(R.id.prePass);
        vh.downFile = (ImageView) view.findViewById(R.id.downFile);
        vh.downRel = (RelativeLayout) view.findViewById(R.id.downRel);
        vh.passRel = (RelativeLayout) view.findViewById(R.id.passRel);
        if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
            vh.passRel.setVisibility(View.VISIBLE);
        }else{
            vh.passRel.setVisibility(View.GONE);
        }
        File file = new File(ECApplication.getInstance().getDownloadJxzDir(),list.get(i).getDateTime() + list.get(i).getFileName());
        if (file.exists()){
            vh.downFile.setImageResource(R.drawable.amd_list_item_open);
        }else{
            vh.downFile.setImageResource(R.drawable.download);
        }


        vh.progressIndexLinear = (LinearLayout) view.findViewById(R.id.down_progress_linear);
        vh.progressBar = (NumberProgressBar) view.findViewById(R.id.amd_progressBar);

        if (list.get(i).isLoading()){
            vh.progressIndexLinear.setVisibility(View.VISIBLE);
        }else{
            vh.progressIndexLinear.setVisibility(View.GONE);
        }
        vh.userHeadImg = (RoundCornerImageView) view.findViewById(R.id.treachResouseHeadImg);
        vh.teacherName = (TextView) view.findViewById(R.id.teacherName);
        if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
            vh.teacherName.setVisibility(View.GONE);
        }else{
            vh.teacherName.setVisibility(View.VISIBLE);
            vh.teacherName.setText(list.get(i).getTeacherName());
        }
        if (list.get(i).getSex().equals("男")){
            vh.userHeadImg.setImageResource(R.drawable.man);
        }else{
            vh.userHeadImg.setImageResource(R.drawable.women);
        }

        vh.cancelDownLoad = (TextView) view.findViewById(R.id.cancelDownLoad);
        addClick(vh,i);
        return view;
    }

    /**
     * 设置课件显示附件头像
     * @param vh
     * @param i
     */
    private void setHeadImg(final ViewHolder vh, final int i){
        String lastName = FileUtils.getStringEndWith(list.get(i).getFileName());
        if (lastName == null){
            vh.fileHeadImg.setImageResource(R.drawable.other);
            list.get(i).setResourceStyle("8");
        }else{
            if(lastName.equals(Constant.ATTACHMENT_DOC)||lastName.equals(Constant.ATTACHMENT_DOCX)){
                vh.fileHeadImg.setImageResource(R.drawable.word);
                list.get(i).setResourceStyle("1");
            }
            else if (lastName.equals(Constant.ATTACHMENT_3GP)
                    ||lastName.equals(Constant.ATTACHMENT_AVI)
                    ||lastName.equals(Constant.ATTACHMENT_MPG)
                    ||lastName.equals(Constant.ATTACHMENT_MOV)
                    ||lastName.equals(Constant.ATTACHMENT_MP3)
                    ||lastName.equals(Constant.ATTACHMENT_MP4)
                    ||lastName.equals(Constant.ATTACHMENT_SWF)
                    ||lastName.equals(Constant.ATTACHMENT_FLV)){
                vh.fileHeadImg.setImageResource(R.drawable.video);
                list.get(i).setResourceStyle("6");
            }

            else if (lastName.equals(Constant.ATTACHMENT_PPT)||lastName.equals(Constant.ATTACHMENT_PPTX)){
                vh.fileHeadImg.setImageResource(R.drawable.ppt);
                list.get(i).setResourceStyle("2");
            }

            else if (lastName.equals(Constant.ATTACHMENT_XLS)||lastName.equals(Constant.ATTACHMENT_XLSX)){
                vh.fileHeadImg.setImageResource(R.drawable.excel);
                list.get(i).setResourceStyle("3");
            }

            else if (lastName.equals(Constant.ATTACHMENT_PDF)){
                vh.fileHeadImg.setImageResource(R.drawable.pdf_b);
                list.get(i).setResourceStyle("8");
            }

            else if(lastName.equals(Constant.ATTACHMENT_RAR)){
                vh.fileHeadImg.setImageResource(R.drawable.zip);
                list.get(i).setResourceStyle("7");
            }

            else{
                vh.fileHeadImg.setImageResource(R.drawable.other);
                list.get(i).setResourceStyle("8");
            }
        }
    }

    private void addClick(final ViewHolder vh, final int i) {
        vh.passRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.map.put("TeachCourse",list.get(i));
                context.startActivity(new Intent(context,UpFileActivity.class));
            }
        });

        vh.downRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ECProgressDialog dialog = new ECProgressDialog(context);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setPressText("正在加载资源..");
                dialog.show();
                //TODO判断当前下载附件是否存在
                String name = list.get(i).getDateTime() + list.get(i).getFileName();
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
                }else{
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
                                    list.get(i).setLoading(true);
                                }

                                @Override
                                public void downloading(int progress, int position) {
                                    dialog.dismiss();
                                    vh.progressBar.setProgress(progress);
                                    vh.downFile.setClickable(false);
                                }

                                @Override
                                public void downloaded(File file1, int position) {
                                    vh.progressIndexLinear.setVisibility(View.GONE);
                                    if (file1 != null){
                                        File file = new File(file1.getParent(),list.get(i).getDateTime() + list.get(i).getFileName());
                                        file1.renameTo(file);
                                        new SingleMediaScanner(context, file);
                                    }else{
                                        ToastUtil.showMessage("文件存在未知错误");
                                    }
                                    list.get(i).setLoading(false);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void canceled(int position) {
                                    File file = new File(ECApplication.getInstance().getDownloadJxzDir(),"jxz_ykt_downFile");
                                    if (file.exists()){
                                        file.delete();
                                    }
                                    list.get(i).setLoading(false);
                                    notifyDataSetChanged();
                                }
                            }, context);
                            downloadAsyncTask.execute(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getUrl(),ECApplication.getInstance().getLoginUrlMap()), "aaa", i + "","jxz_ykt_downFile");
                            MainActivity.map.put("jxz_ykt_downFile"+i,downloadAsyncTask);
                        }
                    }).show();
                }
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
                        DownloadAsyncTask d = (DownloadAsyncTask) MainActivity.map.get("jxz_ykt_downFile"+i);
                        if (d !=null){
                            d.cancel(true);
                        }
                    }
                }).show();

            }
        });
    }

    class ViewHolder{
        private TextView teachResouceTitle;
        private TextView teachResouceDateTime;

        private TextView fileName;
        private LinearLayout linearFile;
        private SimpleDraweeView fileHeadImg;

        private ImageView prePass;

        private ImageView downFile;

        private LinearLayout progressIndexLinear;

        private NumberProgressBar progressBar;

        private RoundCornerImageView userHeadImg;

        private TextView teacherName;

        private RelativeLayout downRel;

        private RelativeLayout passRel;

        private TextView cancelDownLoad;
    }
}
