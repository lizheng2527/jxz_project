package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ImageUrlBean;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.Treads;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.Tools;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lizheng on 2016/12/26.
 */

public class TreadsListViewAdapter extends BaseAdapter {

    private List<Treads.DataListBean> list;

    private ArrayList<Bitmap> bitmaps;

    private Activity context;

    private LayoutInflater inflater;

    private HashMap<String, ParameterValue> map ;

    private ImageLoader loader;

    private Handler handler;

    private HomeFragment fragment;


    public TreadsListViewAdapter(List<Treads.DataListBean> list, Activity context, HomeFragment fragment) {

        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        map = new HashMap<>();
        if (bitmaps !=null){
            bitmaps.clear();
        }else{
            bitmaps = new ArrayList<>();
        }
        loader = new ImageLoader(context);
        handler = new Handler();
        this.fragment = fragment;
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
            view = View.inflate(context, R.layout.fragment_home_viewpager_listview_item,null);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }
        vh.delete = (ImageView) view.findViewById(R.id.fragment_home_viewpager_listview_item_delete);
        if (list.get(i).getCanDelete().equals("no")){
            vh.delete.setVisibility(View.INVISIBLE);
        }else{
            vh.delete.setVisibility(View.VISIBLE);
        }
        vh.threadUserHead = (SimpleDraweeView) view.findViewById(R.id.fragment_home_viewpager_listview_item_userhead);
        String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getImagePath(),ECApplication.getInstance().getLoginUrlMap());
//        loader.DisplayImage(url,vh.threadUserHead,false);
        Uri threadFileHeadUri = Uri.parse(url);
        vh.threadUserHead.setImageURI(threadFileHeadUri);

        vh.threadUserName = (TextView) view.findViewById(R.id.fragment_home_viewpager_listview_item_username);
        vh.threadUserName.setText(list.get(i).getUserName());

        vh.createTime = (TextView) view.findViewById(R.id.fragment_home_viewpager_listview_item_createtime);
        vh.createTime.setText(list.get(i).getCreateTime());
//         内容*****************************************************************************
        vh.content = (TextView) view.findViewById(R.id.content);
        String text = list.get(i).getContent();
        if (text == null || text.length()<1){
            vh.content.setVisibility(View.GONE);
        }else{
            vh.content.setVisibility(View.VISIBLE);
            vh.content.setText(list.get(i).getContent());
        }

//          回复*****************************************************************************
        vh.replyRel = (RelativeLayout) view.findViewById(R.id.replyRel);
        vh.replyCount = (TextView) view.findViewById(R.id.replyCount);
        vh.replyContent = (LinearLayout) view.findViewById(R.id.fragment_home_viewpager_listview_item_context);
        int replyCounts = list.get(i).getChild().size();

        vh.replyContent.removeAllViews();
        if (replyCounts>0){
            for (int j = 0; j < replyCounts; j++) {
                String name = list.get(i).getChild().get(j).getUserName()+":";
                String content = list.get(i).getChild().get(j).getContent();
                addReplyBody(vh,name,content);
            }
        }
        vh.replyCount.setText(replyCounts+"");


//      点赞*********************************************************************
        vh.praiseQuantity = (TextView) view.findViewById(R.id.praiseQuantity);
        vh.linearLayout11 = (LinearLayout) view.findViewById(R.id.LinearLayout11);
        vh.thumbIV = (ImageView) view.findViewById(R.id.thumbIV);
        vh.thumbsupTV = (TextView) view.findViewById(R.id.thumbsupTV);
        vh.praiseRel = (RelativeLayout) view.findViewById(R.id.praise);
        vh.praiseImage = (ImageView) view.findViewById(R.id.thumbsupBT1);
        List<String> praiseNames = list.get(i).getPraiseNames();
        int praiseCounts = praiseNames.size();
        if (praiseNames!= null&&praiseCounts>0){
//            if (praiseNames.contains(ECApplication.getInstance().getCurrentUser().getName())){
            vh.praiseImage.setImageResource(R.drawable.button_praise_click);
            vh.linearLayout11.setVisibility(View.VISIBLE);
            vh.thumbIV.setVisibility(View.VISIBLE);
            vh.thumbsupTV.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < praiseNames.size(); j++) {
                if (j == praiseNames.size()-1){
                    sb.append(praiseNames.get(j));
                }else{
                    sb.append(praiseNames.get(j)+"、");
                }
            }
            vh.thumbsupTV.setText(sb.toString());
//            }
        }else{
            vh.praiseImage.setImageResource(R.drawable.button_praise_nor);
            vh.thumbIV.setVisibility(View.GONE);
            vh.thumbsupTV.setVisibility(View.GONE);
        }
        vh.praiseQuantity.setText(praiseCounts+"");
//        判断是否显示回复界面视图*******************************************************************************************************
        vh.linearwhat = (LinearLayout) view.findViewById(R.id.linearwhat);
        if (praiseCounts==0&&replyCounts == 0){
            vh.linearwhat.setVisibility(View.GONE);
        }else{
            vh.linearwhat.setVisibility(View.VISIBLE);
        }

//       附件*******************************************************************************************************

        vh.simpleImage = (SimpleDraweeView) view.findViewById(R.id.simpleImage);
        vh.muchImages = (GridView) view.findViewById(R.id.fragment_home_viewpager_listview_item_grid);
        vh.fileName = (TextView) view.findViewById(R.id.filename);
        vh.fileSize = (TextView) view.findViewById(R.id.filesize);
        vh.threadFileHead = (SimpleDraweeView) view.findViewById(R.id.filehead);
        vh.l = (LinearLayout) view.findViewById(R.id.lineargone);



        if (list.get(i).getAttachment().getIconList() ==null){//没有图片展示
            vh.simpleImage.setVisibility(View.GONE);
            vh.muchImages.setVisibility(View.GONE);
            vh.fileName.setVisibility(View.GONE);
            vh.fileSize.setVisibility(View.GONE);
            vh.threadFileHead.setVisibility(View.GONE);
            vh.l.setVisibility(View.GONE);
        }else{
            if (list.get(i).getAttachment().getType().equals("other")){//有附件展示(判断返回的加载头像地址是否为空)
                if(list.get(i).getAttachment().getIconList().get(0).getImg() == null||list.get(i).getAttachment().getIconList().get(0).getImg().equals("")){
                    vh.simpleImage.setVisibility(View.GONE);
                    vh.muchImages.setVisibility(View.GONE);
                    vh.fileName.setVisibility(View.GONE);
                    vh.fileSize.setVisibility(View.GONE);
                    vh.threadFileHead.setVisibility(View.GONE);
                    vh.l.setVisibility(View.GONE);
                }else{
                    vh.simpleImage.setVisibility(View.GONE);
                    vh.muchImages.setVisibility(View.GONE);
                    vh.fileName.setVisibility(View.VISIBLE);
                    vh.fileSize.setVisibility(View.VISIBLE);
                    vh.threadFileHead.setVisibility(View.VISIBLE);
                    vh.l.setVisibility(View.VISIBLE);
                    vh.fileName.setText(list.get(i).getAttachment().getIconList().get(0).getFileName());
                    vh.fileSize.setText(list.get(i).getAttachment().getIconList().get(0).getFileSize());
                    String threadFileHeadUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getAttachment().getIconList().get(0).getImg(),ECApplication.getInstance().getLoginUrlMap());
                    LogUtil.e("threadFileHeadUrl:"+threadFileHeadUrl);
//                    loader.DisplayImage(threadFileHeadUrl,vh.threadFileHead,false);
                    Uri uri = Uri.parse(threadFileHeadUrl);
                    vh.threadFileHead.setImageURI(uri);
                }
            }else{//展示图片
                vh.fileName.setVisibility(View.GONE);
                vh.fileSize.setVisibility(View.GONE);
                vh.threadFileHead.setVisibility(View.GONE);
                vh.l.setVisibility(View.GONE);
                ArrayList<Treads.DataListBean.AttachmentBean.IconListBean> arrs = (ArrayList<Treads.DataListBean.AttachmentBean.IconListBean>) list.get(i).getAttachment().getIconList();
                if (arrs.size() == 1){//单张展示
                    vh.muchImages.setVisibility(View.GONE);
                    vh.simpleImage.setVisibility(View.VISIBLE);
//                    String imgUrl = arrs.get(0).getImg();
                    if (arrs.get(0).getImg() == null||arrs.get(0).getImg().equals("")){
                        vh.simpleImage.setVisibility(View.GONE);
                        vh.muchImages.setVisibility(View.GONE);
                        vh.fileName.setVisibility(View.GONE);
                        vh.fileSize.setVisibility(View.GONE);
                        vh.threadFileHead.setVisibility(View.GONE);
                        vh.l.setVisibility(View.GONE);
                    }else{
                        vh.simpleImage.setImageResource(R.drawable.actionsheet_single_pressed);
                        String simpleImageUrl = ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress()+arrs.get(0).getImg(),ECApplication.getInstance().getLoginUrlMap());
                        LogUtil.e("simpleImageUrl:"+simpleImageUrl);
                        Uri uri = Uri.parse(simpleImageUrl);
                        vh.simpleImage.setImageURI(uri);
//                        loader.DisplayImage(simpleImageUrl,vh.simpleImage,false);
                    }
                }else{//Grid展示
                    vh.simpleImage.setVisibility(View.GONE);
                    vh.muchImages.setVisibility(View.VISIBLE);
                    ArrayList<ImageUrlBean> urls = new ArrayList<>();
                    for (int j = 0; j < arrs.size(); j++) {
                        if (arrs.get(j).getImg() == null||arrs.get(j).getImg().equals("")){

                        }else{
                            ImageUrlBean bean = new ImageUrlBean();
                            bean.setShowUrl(ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress()+arrs.get(j).getImg(),ECApplication.getInstance().getLoginUrlMap()));
                            bean.setDownLoadUrl(ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress()+arrs.get(j).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()));
                            urls.add(bean);
                        }
                    }
                    if (urls.size()>0){
                        ImageGirdAdapter adapter = new ImageGirdAdapter((Activity) context,urls,fragment);
                        vh.muchImages.setAdapter(adapter);
                        Tools.setGridViewHeightBasedOnChildren(vh.muchImages);
                    }
                }
            }
        }

        addOnClick(vh,i);
        return view;
    }
    HashMap<Integer,ImageGirdAdapter> adapterMap = new HashMap<>();
    private void addOnClick(final ViewHolder vh, final int position){
        vh.l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getAttachment().getIconList() != null&&list.get(position).getAttachment().getIconList().size()>0){

                    String downUrl = list.get(position).getAttachment().getIconList().get(0).getDownUrl();
                    String videoUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+downUrl,ECApplication.getInstance().getLoginUrlMap());
//                    File file = new File(videoUrl);
                    String lastName = list.get(position).getAttachment().getIconList().get(0).getFileName();
                    if(lastName.contains(Constant.ATTACHMENT_3GP)){

                        fragment.getActivity().startActivity(IntentUtil.getVideoFileIntent(videoUrl));
                    }
                    else if(lastName.contains(Constant.ATTACHMENT_AVI)){

                        fragment.getActivity().startActivity(IntentUtil.getVideoFileIntent(videoUrl));

                    }else if(lastName.contains(Constant.ATTACHMENT_MP4)){

                        fragment.getActivity().startActivity(IntentUtil.getVideoFileIntent(videoUrl));

                    }
                }else{
                    return;
                }
            }
        });
        //回复点击事件
        vh.replyRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.showEdit(list.get(position).getId(),position);
            }
        });
        //点赞
        vh.praiseRel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProgressUtil.show(context,"正在加载..");
                map.clear();
                map.put("communcationId",new ParameterValue(list.get(position).getId()));
                map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentUser().getId()));
                map.putAll(ECApplication.getInstance().getLoginUrlMap());
                new ProgressThreadWrap(context, new RunnableWrap() {
                    @Override
                    public void run() {
                        try {
                            String status = ZddcUtil.doPpraise(map);
                            if (status.contains("own")){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressUtil.hide();
                                        ToastUtil.showMessage("不可以赞自己");
                                    }
                                },5);
                            }
                            if (status.contains("ok")){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vh.praiseImage.setImageResource(R.drawable.button_praise_click);
                                        list.get(position).getPraiseNames().add(ECApplication.getInstance().getCurrentUser().getName());
                                        notifyDataSetChanged();
                                        ProgressUtil.hide();
                                    }
                                },5);
                            }
                            if (status.contains("repraise")){
                                ProgressUtil.hide();
                                ToastUtil.showMessage("重复点赞..");
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

            }
        });

        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECAlertDialog.buildAlert(context, "是否删除本条信息？", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressUtil.show(context,"正在删除!");
                        hashMap.clear();
                        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                        String communcationId = list.get(position).getId();
                        hashMap.put("communcationId",new ParameterValue(communcationId));
                        new ProgressThreadWrap(context, new RunnableWrap() {
                            @Override
                            public void run() {

                                try {
                                    ZddcUtil.delete(hashMap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressUtil.hide();
                                        list.remove(position);
                                        notifyDataSetChanged();
                                    }
                                },6);
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

        vh.simpleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePagerActivity.class);
                //图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra("images", new String[]{ZddcUtil.getUrlAnd(ECApplication.getInstance().getAddress()+list.get(position).getAttachment().getIconList().get(0).getDownUrl(),ECApplication.getInstance().getLoginUrlMap())});
                LogUtil.e("单张："+ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(position).getAttachment().getIconList().get(0).getDownUrl(),ECApplication.getInstance().getLoginUrlMap()));
                intent.putExtra("image_index",0);
                fragment.startActivity(intent);
            }
        });
    }


    HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
    class ViewHolder{
        /*** 展示动态的用户头像*/
        private SimpleDraweeView threadUserHead;
        /*** 展示动态的用户名称*/
        private TextView threadUserName;
        /*** 展示动态的用户类型（教师/学生）*/
        private TextView threadUserType;
        /*** 删除*/
        private ImageView delete;
        /*** 展示动态的用户文字内容*/
        private TextView content;
        /*** 单张展示的图片*/
        private SimpleDraweeView simpleImage;
        /*** 多张展示的图片*/
        private GridView muchImages;
        /*** 展示动态的文件图片*/
        private SimpleDraweeView threadFileHead;
        /*** 展示动态的文件标题*/
        private TextView fileName;
        /*** 展示动态的文件大小*/
        private TextView fileSize;
        /*** 展示动态的发送时间*/
        private TextView createTime;
        /*** 展示动态的点赞图片*/
        private ImageView praiseImage;
        /*** 展示动态的点赞个数*/
        private TextView praiseQuantity;
        /*** 展示动态的评论图片*/
        private ImageView replyImage;
        /*** 展示动态的评论个数*/
        private TextView replyCount;
        //是否显示附件
        private LinearLayout l;
        private LinearLayout linearLayout11;
        private ImageView thumbIV;
        private LinearLayout replyContent;
        private TextView thumbsupTV;
        //点赞
        private RelativeLayout replyRel;
        private RelativeLayout praiseRel;
        //当没有回复和点赞情况此时他被隐藏
        public LinearLayout linearwhat;

    }

    /**
     * 动态添加回复的textView
     * @param vh
     * @param name
     * @param content
     */
    public void addReplyBody(ViewHolder vh,String name,String content){
        vh.replyContent.setVisibility(View.VISIBLE);
        TextView textView = new TextView(context);
        //这里的Textview的父layout是ListView，所以要用ListView.LayoutParams
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(0, 5, 0, 0);
        String allStringOfComment = name + content;
        textView.setTextColor(Color.parseColor("#555555"));
        textView.setTextSize(12);
        vh.replyContent.addView(textView);
        SpannableStringBuilder builder = new SpannableStringBuilder(allStringOfComment);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.name_blue));
        int start = allStringOfComment.indexOf(name);
        int end = start + name.length();
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
