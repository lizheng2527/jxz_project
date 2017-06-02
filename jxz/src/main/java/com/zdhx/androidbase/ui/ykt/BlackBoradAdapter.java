package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.ImagePagerActivity;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.downloadui.NumberProgressBar;
import com.zdhx.androidbase.ui.treadssearch.UpFileActivity;
import com.zdhx.androidbase.util.DensityUtil;
import com.zdhx.androidbase.util.SingleMediaScanner;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.SimpleDraweeViewCompressed.FrescoUtil;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class BlackBoradAdapter extends BaseAdapter {

    private ArrayList<BlackboardWrite> list;
    private Context context;
    public static Boolean isMuchSelect = false;
    public static HashMap<Integer,BlackboardWrite> isMuchSelectMap = new HashMap<>();

    BlackBoradAdapter(ArrayList<BlackboardWrite> list, Context context) {
        this.list = list;
        this.context = context;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null){
            vh = new ViewHolder();
            view = View.inflate(context, R.layout.fragment_blackborad_listview_item,null);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        vh.muchSelectBox = (CheckBox) view.findViewById(R.id.muchSelectBox);
        if (isMuchSelect){
            vh.muchSelectBox.setVisibility(View.VISIBLE);
            if (isMuchSelectMap.containsKey(i)){
                vh.muchSelectBox.setChecked(true);
            }else{
                vh.muchSelectBox.setChecked(false);
            }
        }else{
            vh.muchSelectBox.setVisibility(View.GONE);
        }
        vh.balckBoradImg = (SimpleDraweeView) view.findViewById(R.id.balckBoradImg);
//        vh.balckBoradImg.setImageResource(R.drawable.logo_wechat);
        String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getPreviewImage(), ECApplication.getInstance().getLoginUrlMap());
        FrescoUtil.load(Uri.parse(url),vh.balckBoradImg, DensityUtil.dip2px(50),DensityUtil.dip2px(50));

        vh.balckBoradTitle = (TextView) view.findViewById(R.id.balckBoradTitle);
        vh.balckBoradTitle.setText(list.get(i).getName());

        vh.balckBoradUserName = (TextView) view.findViewById(R.id.balckBoradUserName);
        vh.balckBoradUserName.setText(list.get(i).getTeacherName());

        vh.balckBoradDateTime = (TextView) view.findViewById(R.id.balckBoradDateTime);
        vh.balckBoradDateTime.setText(list.get(i).getDateTime());

        vh.previewImg = (ImageView) view.findViewById(R.id.btn_pre);
        vh.download = (ImageView) view.findViewById(R.id.download);
        File file = new File(ECApplication.getInstance().getDownloadJxzDir(),list.get(i).getDateTime()+list.get(i).getName());
        if (file.exists()){
            vh.download.setImageResource(R.drawable.amd_list_item_open);
            vh.previewImg.setVisibility(View.GONE);
        }else{
            vh.download.setImageResource(R.drawable.download);
            vh.previewImg.setVisibility(View.VISIBLE);
        }

        vh.progressIndexLinear = (LinearLayout) view.findViewById(R.id.down_progress_linear);
        vh.progressBar = (NumberProgressBar) view.findViewById(R.id.amd_progressBar);

        vh.good_nor = (ImageView) view.findViewById(R.id.good_nor);
        if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
            vh.good_nor.setVisibility(View.VISIBLE);
        }else{
            vh.good_nor.setVisibility(View.GONE);
        }
        vh.cancelDownLoad = (TextView) view.findViewById(R.id.cancelDownLoad);

        if (list.get(i).isLoading()){
            vh.progressIndexLinear.setVisibility(View.VISIBLE);
        }else{
            vh.progressIndexLinear.setVisibility(View.GONE);
        }

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
                    isMuchSelect = true;
                    MainActivity.showSelectBatchLinear(true);
                    notifyDataSetChanged();
                }
                return true;
            }
        });
        addClick(vh,i);
        return view;
    }

    private void addClick(final ViewHolder vh, final int i) {
        vh.previewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePagerActivity.class);
                intent.putExtra("images", new String[]{ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(), ECApplication.getInstance().getLoginUrlMap())});
                intent.putExtra("image_index",0);
                intent.putExtra("imgNames",new String[]{list.get(i).getName()});
                context.startActivity(intent);
            }
        });

        vh.good_nor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<BlackboardWrite> l = new ArrayList<>();
                l.add(list.get(i));
                MainActivity.map.put("BlackBorad",l);
                context.startActivity(new Intent(context,UpFileActivity.class));
            }
        });

        vh.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ECProgressDialog dialog = new ECProgressDialog(context);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setPressText("正在加载资源..");
                dialog.show();
                //TODO判断当前下载附件是否存在
                String name = list.get(i).getDateTime()+list.get(i).getName();
                File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dir = new File(idr+"/jxz");
                if (!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir,name);
                if (file.exists()){
                    dialog.dismiss();
                    Intent intent = new Intent(context, ImagePagerActivity.class);
                    intent.putExtra("images", new String[]{ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(), ECApplication.getInstance().getLoginUrlMap())});
                    intent.putExtra("image_index",0);
                    intent.putExtra("imgNames",new String[]{list.get(i).getName()});
                    context.startActivity(intent);
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
                                    vh.download.setClickable(false);
                                }

                                @Override
                                public void downloaded(File file1, int position) {
                                    vh.progressIndexLinear.setVisibility(View.GONE);
                                    if (file1 != null){
                                        File file = new File(file1.getParent(),list.get(i).getDateTime()+list.get(i).getName());
                                        file1.renameTo(file);
                                        new SingleMediaScanner(context, file);
//                                    downCounts = downCounts -1;
                                        list.get(i).setLoading(false);
                                    }else{
                                        ToastUtil.showMessage("文件存在未知错误");
                                    }
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void canceled(int position) {
                                    File file = new File(ECApplication.getInstance().getDownloadJxzDir(),"jxz_ykt_blackborad");
                                    if (file.exists()){
                                        file.delete();
                                    }
                                    list.get(i).setLoading(false);
                                    notifyDataSetChanged();
                                }
                            }, context);
                            downloadAsyncTask.execute(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(),ECApplication.getInstance().getLoginUrlMap()), "aaa", i + "","jxz_workSpace_downFile");
                            MainActivity.map.put("jxz_ykt_blackborad"+i,downloadAsyncTask);
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
                        DownloadAsyncTask d = (DownloadAsyncTask) MainActivity.map.get("jxz_ykt_blackborad"+i);
                        if (d !=null){
                            d.cancel(true);
                        }
                    }
                }).show();

            }
        });


        //批量checkBox点击事件
        vh.muchSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuchSelectMap.containsKey(i)){
                    isMuchSelectMap.remove(i);
                }else{
                    isMuchSelectMap.put(i,list.get(i));
                }
                notifyDataSetChanged();
            }
        });
    }

    class ViewHolder{
        private SimpleDraweeView balckBoradImg;
        private TextView balckBoradTitle;
        private TextView balckBoradUserName;
        private TextView balckBoradDateTime;
        private ImageView previewImg;
        private ImageView download;

        private LinearLayout progressIndexLinear;
        private NumberProgressBar progressBar;

        private ImageView good_nor;

        private TextView cancelDownLoad;
        private CheckBox muchSelectBox;
    }
}
