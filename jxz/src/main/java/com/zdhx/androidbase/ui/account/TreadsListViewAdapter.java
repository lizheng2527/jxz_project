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
import com.zdhx.androidbase.util.DensityUtil;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RoundCornerImageView;
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
        vh.threadUserHead = (RoundCornerImageView) view.findViewById(R.id.fragment_home_viewpager_listview_item_userhead);
        String userHeadStr = list.get(i).getImagePath();
        if (userHeadStr != null&&!userHeadStr.equals("")){
            if (userHeadStr.contains("man")){
                vh.threadUserHead.setImageResource(R.drawable.man);
            }
            if (userHeadStr.contains("women")){
                vh.threadUserHead.setImageResource(R.drawable.women);
            }
            if (userHeadStr.contains("boy")){
                vh.threadUserHead.setImageResource(R.drawable.boy);
            }
            if (userHeadStr.contains("girl")){
                vh.threadUserHead.setImageResource(R.drawable.girl);
            }
        }


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
        vh.launchOrRetract = (TextView) view.findViewById(R.id.launchorretract);
        if (!list.get(i).getLaunch()){
            vh.launchOrRetract.setText("展开");
        }else{
            vh.launchOrRetract.setText("收起");
        }
        int replyCounts = list.get(i).getChild().size();
        vh.replyContent.removeAllViews();
        if (replyCounts>2){
            if (list.get(i).getLaunch()){
                for (int j = 0; j < replyCounts; j++) {
                    String name = list.get(i).getChild().get(j).getUserName()+":";
                    String content = list.get(i).getChild().get(j).getContent();
                    addReplyBody(vh,name,content,j,null,list.get(i).getChild(),i);
                    if (list.get(i).getChild().get(j).getChild() != null){
                        if (list.get(i).getChild().get(j).getChild().size()>0){
                            getChildList(vh,list.get(i).getChild().get(j).getChild(),name,i);
                        }
                    }
                }
            }else{
                for (int j = 0; j < 2; j++) {
                    String name = list.get(i).getChild().get(j).getUserName()+":";
                    String content = list.get(i).getChild().get(j).getContent();
                    addReplyBody(vh,name,content,j,null,list.get(i).getChild(),i);
                    if (list.get(i).getChild().get(j).getChild() != null){
                        if (list.get(i).getChild().get(j).getChild().size()>0){
                            getChildList(vh,list.get(i).getChild().get(j).getChild(),name,i);
                        }
                    }
                }
            }
        }else{//小于两条，遍历所有
            for (int j = 0; j < replyCounts; j++) {
                String name = list.get(i).getChild().get(j).getUserName()+":";
                String content = list.get(i).getChild().get(j).getContent();
                addReplyBody(vh,name,content,j,null,list.get(i).getChild(),i);
                if (list.get(i).getChild().get(j).getChild() != null){
                    if (list.get(i).getChild().get(j).getChild().size()>0){
                        getChildList(vh,list.get(i).getChild().get(j).getChild(),name,i);
                    }
                }
            }
        }


        vh.replyCount.setText(list.get(i).getAllReplyCount()+"");


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
            if (replyCounts >2){
                vh.launchOrRetract.setVisibility(View.VISIBLE);
            }else {
                vh.launchOrRetract.setVisibility(View.GONE);
            }
        }

//       附件*******************************************************************************************************
        vh.simpleImage = (SimpleDraweeView) view.findViewById(R.id.simpleImage);
        vh.muchImages = (GridView) view.findViewById(R.id.fragment_home_viewpager_listview_item_grid);
        vh.fileName = (TextView) view.findViewById(R.id.filename);
        vh.fileSize = (TextView) view.findViewById(R.id.filesize);
        vh.threadFileHead = (SimpleDraweeView) view.findViewById(R.id.filehead);
        vh.l = (LinearLayout) view.findViewById(R.id.lineargone);
        vh.address = (TextView) view.findViewById(R.id.outUrl);


        if (list.get(i).getAttachment().getIconList() ==null){//没有图片展示
            vh.simpleImage.setVisibility(View.GONE);
            vh.muchImages.setVisibility(View.GONE);
            vh.fileName.setVisibility(View.GONE);
            vh.fileSize.setVisibility(View.GONE);
            vh.threadFileHead.setVisibility(View.GONE);
            vh.l.setVisibility(View.GONE);
            vh.address.setVisibility(View.GONE);
            vh.fileName.setTextColor(Color.parseColor("#000000"));
        }else{

            //      外部链接*******************************************************************************************************
            if (list.get(i).getAttachment().getIconList().size() > 0&&list.get(i).getAttachment().getIconList().get(0).getAddress() != null){
                vh.address.setVisibility(View.VISIBLE);
                vh.address.setText(list.get(i).getAttachment().getIconList().get(0).getAddress());
            }else{
                vh.address.setVisibility(View.GONE);
            }
            if (list.get(i).getAttachment().getType().equals("other")){//有附件展示(判断返回的加载头像地址是否为空)

                if(list.get(i).getAttachment().getIconList().get(0).getImg() == null||list.get(i).getAttachment().getIconList().get(0).getImg().equals("")){
                    vh.simpleImage.setVisibility(View.GONE);
                    vh.muchImages.setVisibility(View.GONE);
                    vh.fileName.setVisibility(View.GONE);
                    vh.fileSize.setVisibility(View.GONE);
                    vh.threadFileHead.setVisibility(View.GONE);
                    vh.l.setVisibility(View.GONE);
                    vh.fileName.setTextColor(Color.parseColor("#000000"));
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
                    Uri uri = Uri.parse(threadFileHeadUrl);
                    vh.threadFileHead.setImageURI(uri);
                    String lastName = list.get(i).getAttachment().getIconList().get(0).getFileName();
                    if (lastName.contains(Constant.ATTACHMENT_3GP)||lastName.contains(Constant.ATTACHMENT_AVI)||lastName.contains(Constant.ATTACHMENT_MP4)||lastName.contains(Constant.ATTACHMENT_FLV)){
                        vh.fileName.setTextColor(Color.parseColor("#4cbbda"));
                    }else{
                        vh.fileName.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }else {//展示图片
                vh.fileName.setVisibility(View.GONE);
                vh.fileSize.setVisibility(View.GONE);
                vh.threadFileHead.setVisibility(View.GONE);
                vh.muchImages.setVisibility(View.GONE);
                vh.simpleImage.setVisibility(View.GONE);
                vh.l.setVisibility(View.GONE);
                ArrayList<Treads.DataListBean.AttachmentBean.IconListBean> arrs = (ArrayList<Treads.DataListBean.AttachmentBean.IconListBean>) list.get(i).getAttachment().getIconList();
                if (arrs != null && arrs.size() > 0) {
                    if (arrs.size() == 1) {//单张展示
                        vh.muchImages.setVisibility(View.GONE);
                        vh.simpleImage.setVisibility(View.VISIBLE);
                        if (arrs.get(0).getImg() == null || arrs.get(0).getImg().equals("")) {
                            vh.simpleImage.setVisibility(View.GONE);
                            vh.muchImages.setVisibility(View.GONE);
                            vh.fileName.setVisibility(View.GONE);
                            vh.fileSize.setVisibility(View.GONE);
                            vh.threadFileHead.setVisibility(View.GONE);
                            vh.l.setVisibility(View.GONE);
                        } else {
                            vh.simpleImage.setImageResource(R.drawable.actionsheet_single_pressed);
                            String simpleImageUrl = ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress() + arrs.get(0).getImg(), ECApplication.getInstance().getLoginUrlMap());
                            LogUtil.e("simpleImageUrl:" + simpleImageUrl);
                            Uri uri = Uri.parse(simpleImageUrl);
                            vh.simpleImage.setImageURI(uri);
                        }
                    } else {//Grid展示
                        vh.simpleImage.setVisibility(View.GONE);
                        vh.muchImages.setVisibility(View.VISIBLE);
                        ArrayList<ImageUrlBean> urls = new ArrayList<>();
                        for (int j = 0; j < arrs.size(); j++) {
                            if (arrs.get(j).getImg() == null || arrs.get(j).getImg().equals("")) {

                            } else {
                                ImageUrlBean bean = new ImageUrlBean();
                                bean.setName(arrs.get(j).getFileName());
                                LogUtil.w("传入的文件名："+arrs.get(j).getFileName());
                                bean.setShowUrl(ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress() + arrs.get(j).getImg(), ECApplication.getInstance().getLoginUrlMap()));
                                bean.setDownLoadUrl(ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress() + arrs.get(j).getDownUrl(), ECApplication.getInstance().getLoginUrlMap()));
                                urls.add(bean);
                            }
                        }
                        if (urls.size() > 0) {
                            ImageGirdAdapter adapter = new ImageGirdAdapter((Activity) context, urls, fragment);
                            vh.muchImages.setAdapter(adapter);
                            Tools.setGridViewHeightBasedOnChildren(vh.muchImages);
                        }
                    }
                }
            }
        }
        addOnClick(vh,i);
        return view;
    }

    private void getChildList(ViewHolder vh, List<Treads.DataListBean> beans,String parentName,int index){
        for (int i1 = 0; i1 < beans.size(); i1++) {
            String name = beans.get(i1).getUserName();
            String content = beans.get(i1).getContent();
            addReplyBody(vh,name,content,i1,parentName,beans,index);
            if (beans.get(i1).getChild() != null){
                if (beans.get(i1).getChild().size()>0){
                    getChildList(vh,beans.get(i1).getChild(),name,index);
                }
            }
        }
    }
    HashMap<Integer,ImageGirdAdapter> adapterMap = new HashMap<>();
    private void addOnClick(final ViewHolder vh, final int position){
        vh.l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastName = list.get(position).getAttachment().getIconList().get(0).getFileName();
                //视频播放
                if (lastName.contains(Constant.ATTACHMENT_3GP)||lastName.contains(Constant.ATTACHMENT_AVI)||lastName.contains(Constant.ATTACHMENT_MP4)||lastName.contains(Constant.ATTACHMENT_FLV)){
                    String downUrl = list.get(position).getAttachment().getIconList().get(0).getDownUrl();
                    String videoUrl = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+downUrl,ECApplication.getInstance().getLoginUrlMap());
                    fragment.getActivity().startActivity(IntentUtil.getVideoFileIntent(videoUrl));
                }

            }
        });
        //回复点击事件
        vh.replyRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getReplyCount()>1){
                    list.get(position).setLaunch(true);
                }
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
                intent.putExtra("imgNames",new String[]{list.get(position).getAttachment().getIconList().get(0).getFileName()});
                fragment.startActivity(intent);
            }
        });

        vh.launchOrRetract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int replyCounts = list.get(position).getChild().size();
                if (!list.get(position).getLaunch()){
                    for (int j = 2; j < replyCounts; j++) {
                        String name = list.get(position).getChild().get(j).getUserName()+":";
                        String content = list.get(position).getChild().get(j).getContent();
                        addReplyBody(vh,name,content,j,null,list.get(position).getChild(),position);
                        if (list.get(position).getChild()!=null && list.get(position).getChild().get(j).getChild() !=null){
                            if (list.get(position).getChild().get(j).getChild().size()>0){
                                getChildList(vh,list.get(position).getChild().get(j).getChild(),name,position);
                            }
                        }
                    }
                    list.get(position).setLaunch(true);
                    vh.launchOrRetract.setText("收起");
                }else{
                    vh.replyContent.removeAllViews();
                    //TODO
                    for (int j = 0; j < 2; j++) {
                        String name = list.get(position).getChild().get(j).getUserName()+":";
                        String content = list.get(position).getChild().get(j).getContent();
                        addReplyBody(vh,name,content,j,null,list.get(position).getChild(),position);
                        if (list.get(position).getChild()!=null && list.get(position).getChild().get(j).getChild() !=null) {
                            if (list.get(position).getChild().get(j).getChild().size() > 0) {
                                getChildList(vh, list.get(position).getChild().get(j).getChild(), name, position);
                            }
                        }
                    }
                    list.get(position).setLaunch(false);
                    vh.launchOrRetract.setText("展开");
                    fragment.setListViewSelection(position);
                }
            }
        });
    }


    HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
    class ViewHolder{
        /*** 展示动态的用户头像*/
        private RoundCornerImageView threadUserHead;
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

        private TextView launchOrRetract;

        private TextView address;


    }

    /**
     * 动态添加回复的textView
     * @param vh
     * @param name
     * @param content
     */
    public void addReplyBody(ViewHolder vh, String name, String content, final int position, final String parentName, final List<Treads.DataListBean> beans,final int index){
        vh.replyContent.setVisibility(View.VISIBLE);
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,DensityUtil.dip2px(5),0,0);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(0, 5, 0, 0);
        String allStringOfComment = "";
        if (parentName== null){
            allStringOfComment = name + content;
        }else{
            allStringOfComment = name + "@"+parentName+content;
            name = name + "@"+parentName;
        }
        textView.setTextColor(Color.parseColor("#555555"));
        textView.setTextSize(14);
        vh.replyContent.addView(textView);
        SpannableStringBuilder builder = new SpannableStringBuilder(allStringOfComment);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.name_blue));
        int start = allStringOfComment.indexOf(name);
        int end = start + name.length();
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
        textView.setTag(beans.get(position).getChild());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beans.get(position).getCanDelete().equals("yes")){
                    ECAlertDialog.buildAlert(context, "是否删除本条回复内容？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressUtil.show(context,"正在删除!");
                            hashMap.clear();
                            hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                            String communcationId = beans.get(position).getId();
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
                                            resetAllReplyCount(beans.get(position).getChild());
                                            beans.get(position).setAllReplyCount(beans.get(position).getAllReplyCount()-deleteCount);
                                            deleteCount = 1;
                                            beans.remove(position);
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

                }else{
                    fragment.setReplyName("指定回复");
                    HomeFragment.showEdit(beans.get(position).getId(),index);
                }
            }
        });
    }
    private int deleteCount = 1;
    private void resetAllReplyCount(List<Treads.DataListBean> beans) {
        deleteCount += beans.size();
        for (int i = 0; i < beans.size(); i++) {
            resetAllReplyCount(beans.get(i).getChild());

        }
    }

}
