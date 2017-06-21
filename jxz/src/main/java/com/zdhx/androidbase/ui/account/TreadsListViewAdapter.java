package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
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
import com.google.gson.Gson;
import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.EmojiConstant;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ImageUrlBean;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.Treads;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.downloadui.NumberProgressBar;
import com.zdhx.androidbase.ui.introducetreads.IntroduceTreadsActivity;
import com.zdhx.androidbase.ui.plugin.FileExplorerActivity;
import com.zdhx.androidbase.ui.plugin.FileUtils;
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
import com.zdhx.androidbase.view.SimpleDraweeViewCompressed.FrescoUtil;
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

public class TreadsListViewAdapter extends BaseAdapter {

    private TreadsListViewAdapter adapter;

    private static int downCount = 0;

    private List<Treads.DataListBean> list;

    private ArrayList<Bitmap> bitmaps;

    private Activity context;

    private LayoutInflater inflater;

    private HashMap<String, ParameterValue> map ;

    private ImageLoader loader;

    private Handler handler;

    private HomeFragment fragment;

    TreadsListViewAdapter(List<Treads.DataListBean> list, Activity context, HomeFragment fragment) {
        adapter = this;
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

    /**
     * 字符串替换Emoji表情
     * @param text
     * @return
     */
    private String getEmojyStr(String text){
        String cs = text.toString();
        if (cs.contains("[em_")) {
            for (int i = 1; i < 76; i++) {
                cs = cs.replaceAll("\\["+"em_"+i+"\\]","\\<img src='" + EmojiConstant.emojiIds[i-1] +"'\\/\\>");
                if (!cs.contains("[em_")) {
                    break;
                }
            }
        }
        return cs;
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
        vh.delete = (LinearLayout) view.findViewById(R.id.fragment_home_viewpager_listview_item_delete);
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
        if (list.get(i).getUserName().contains(ECApplication.getInstance().getCurrentUser().getName())){
            vh.threadUserName.setTextColor(Color.parseColor("#4cbbda"));
        }else{
            vh.threadUserName.setTextColor(Color.parseColor("#576b95"));
        }

        vh.createTime = (TextView) view.findViewById(R.id.fragment_home_viewpager_listview_item_createtime);
        vh.createTime.setText(list.get(i).getCreateTime().trim());
//         treads内容*****************************************************************************
        vh.content = (TextView) view.findViewById(R.id.content);
        String text = list.get(i).getContent();
        if (text == null || text.length()<1){
            vh.content.setVisibility(View.GONE);
        }else{
            vh.content.setVisibility(View.VISIBLE);
//            vh.content.setText(list.get(i).getContent());
            String nowText = list.get(i).getContent();
            if (nowText.contains("[em_")){
                String html = getEmojyStr(nowText);
                LogUtil.w(html);
                CharSequence charSequence = Html.fromHtml(html,
                        new Html.ImageGetter() {

                            @Override
                            public Drawable getDrawable(String source) {
                                Drawable drawable = context.getResources().getDrawable(
                                        Integer.parseInt(source));
                                // 设置drawable的大小。设置为实际大小
                                drawable.setBounds(0, 0,
                                        DensityUtil.dip2px(20f),
                                        DensityUtil.dip2px(20f));
                                return drawable;
                            }
                        }, null);
                vh.content.setText(charSequence);
            }else{
                vh.content.setText(nowText);
            }

        }

//          回复*****************************************************************************
        vh.replyRel = (RelativeLayout) view.findViewById(R.id.replyRel);
        vh.replyCount = (TextView) view.findViewById(R.id.replyCount);
        vh.replyContent = (LinearLayout) view.findViewById(R.id.fragment_home_viewpager_listview_item_context);
        vh.launchOrRetract = (TextView) view.findViewById(R.id.launchorretract);
        if (!list.get(i).getLaunch()){
            vh.launchOrRetract.setText("查看回复");
        }else{
            vh.launchOrRetract.setText("收起");
        }
        int replyCounts = list.get(i).getChild().size();
        vh.replyContent.removeAllViews();
        int allReplyCount = list.get(i).getAllReplyCount();
        if (allReplyCount>0){
//        if (replyCounts>2){
//            if (list.get(i).getLaunch()){
            if (replyCounts>1){
                vh.launchOrRetract.setPressed(true);
                vh.launchOrRetract.setClickable(false);
            }
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
//            }else{
//                for (int j = 0; j < 2; j++) {
//                    String name = list.get(i).getChild().get(j).getUserName()+":";
//                    String content = list.get(i).getChild().get(j).getContent();
//                    addReplyBody(vh,name,content,j,null,list.get(i).getChild(),i);
//                    if (list.get(i).getChild().get(j).getChild() != null){
//                        if (list.get(i).getChild().get(j).getChild().size()>0){
//                            getChildList(vh,list.get(i).getChild().get(j).getChild(),name,i);
//                        }
//                    }
//                }
//            }
//        }else{//小于两条，遍历所有
//            for (int j = 0; j < replyCounts; j++) {
//                String name = list.get(i).getChild().get(j).getUserName()+":";
//                String content = list.get(i).getChild().get(j).getContent();
//                addReplyBody(vh,name,content,j,null,list.get(i).getChild(),i);
//                if (list.get(i).getChild().get(j).getChild() != null){
//                    if (list.get(i).getChild().get(j).getChild().size()>0){
//                        getChildList(vh,list.get(i).getChild().get(j).getChild(),name,i);
//                    }
//                }
//            }
//        }


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

        vh.praiseImage.setImageResource(R.drawable.button_praise_nor);
        vh.praiseRel.setClickable(true);


        if (praiseNames!= null&&praiseCounts>0){
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
                if (praiseNames.get(j).equals(ECApplication.getInstance().getCurrentUser().getName())){
                    vh.praiseImage.setImageResource(R.drawable.button_praise_click);
                    vh.praiseRel.setClickable(false);
                }
            }
            vh.thumbsupTV.setText(sb.toString());
        }else{
            vh.thumbIV.setVisibility(View.GONE);
            vh.thumbsupTV.setVisibility(View.GONE);
        }

        vh.praiseQuantity.setText(praiseCounts+"");
//        判断是否显示回复界面视图*******************************************************************************************************
        vh.linearwhat = (LinearLayout) view.findViewById(R.id.linearwhat);
        if (praiseCounts==0&&allReplyCount == 0){
            vh.linearwhat.setVisibility(View.GONE);
        }else{
            vh.linearwhat.setVisibility(View.VISIBLE);
            if (allReplyCount >1&&!list.get(i).getLaunch()){
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
//        vh.treadsFileDown = (ImageView) view.findViewById(R.id.treadsFileDown);


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
//                    //如果是视频，文件颜色为蓝色
//                    if (lastName.contains(Constant.ATTACHMENT_3GP)
//                            ||lastName.contains(Constant.ATTACHMENT_AVI)
//                            ||lastName.contains(Constant.ATTACHMENT_MP4)
//                            ||lastName.contains(Constant.ATTACHMENT_FLV)){
//                        vh.fileName.setTextColor(Color.parseColor("#4cbbda"));
//                    }else{
//                        vh.fileName.setTextColor(Color.parseColor("#222222"));
//                    }

                    //存在该文件，文件名字显示为蓝色
                    if (ECApplication.getInstance().isExistFile(lastName)){
                        vh.fileName.setTextColor(Color.parseColor("#4cbbda"));
//                        vh.treadsFileDown.setVisibility(View.GONE);
                    }else{
//                        vh.treadsFileDown.setVisibility(View.VISIBLE);
                        vh.fileName.setTextColor(Color.parseColor("#222222"));
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
                            String simpleImageUrl = ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress() + arrs.get(0).getImg(), ECApplication.getInstance().getLoginUrlMap());
                            FrescoUtil.load(Uri.parse(simpleImageUrl),vh.simpleImage,DensityUtil.dip2px(150),DensityUtil.dip2px(150));
//                            Uri uri = Uri.parse(simpleImageUrl);
//                            vh.simpleImage.setImageURI(uri);
                        }
                    } else {//Grid展示
                        vh.simpleImage.setVisibility(View.GONE);
                        vh.muchImages.setVisibility(View.VISIBLE);
                        ArrayList<ImageUrlBean> urls = new ArrayList<>();
                        for (int j = 0; j < arrs.size(); j++) {
                            if (arrs.get(j).getImg() == null || arrs.get(j).getImg().equals("")) {
                                LogUtil.e("TreadsListAdapter_muchImagesUrl为空");
                            } else {
                                ImageUrlBean bean = new ImageUrlBean();
                                bean.setName(arrs.get(j).getFileName());
                                bean.setShowUrl(ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress() + arrs.get(j).getImg(), ECApplication.getInstance().getLoginUrlMap()));
                                bean.setDownLoadUrl(ZddcUtil.getUrlFirstAnd(ECApplication.getInstance().getAddress() + arrs.get(j).getDownUrl(), ECApplication.getInstance().getLoginUrlMap()));
                                urls.add(bean);
                            }
                        }
                        if (urls.size() > 0) {
                            ImageGirdAdapter adapter = new ImageGirdAdapter((Activity) context, urls, fragment,i,this.adapter,list.get(i).getId());
                            vh.muchImages.setAdapter(adapter);
                            Tools.setGridViewHeightBasedOnChildren(vh.muchImages);

                        }
                    }
                }
            }
        }

        vh.progressBar = (NumberProgressBar) view.findViewById(R.id.amd_progressBar);
        vh.progressIndexLinear = (LinearLayout) view.findViewById(R.id.down_progress_linear);
        vh.cancelDownLoad = (TextView) view.findViewById(R.id.cancelDownLoad);
        //如果当前正在下载，显示进度条
        if (list.get(i).getLoading()){
            vh.progressIndexLinear.setVisibility(View.VISIBLE);
        }else{
            vh.progressIndexLinear.setVisibility(View.GONE);
        }

        //下载数量
        vh.downCountTV = (TextView) view.findViewById(R.id.treads_down_tv);
        vh.downCountRel = (RelativeLayout) view.findViewById(R.id.treads_down_rel);
        //浏览数量
        vh.browseCountRel = (RelativeLayout) view.findViewById(R.id.treads_browse_rel);
        vh.browseCountTV = (TextView) view.findViewById(R.id.treads_browse_tv);

        //是否显示上传或者下载的数量
        if (list.get(i).getDownId() == null||list.get(i).getDownId().equals("")){
            vh.downCountRel.setVisibility(View.GONE);
            vh.browseCountRel.setVisibility(View.GONE);
        }else{

            vh.downCountRel.setVisibility(View.VISIBLE);
            vh.browseCountRel.setVisibility(View.VISIBLE);
            if (list.get(i).getAttachment().getIconList() !=null){
                //      外部链接隐藏下载与预览*******************************************************************************************************
                if (list.get(i).getAttachment().getIconList().size() > 0&&list.get(i).getAttachment().getIconList().get(0).getAddress() != null){
                    vh.downCountRel.setVisibility(View.GONE);
                    vh.browseCountRel.setVisibility(View.GONE);
                }else{
                    vh.downCountRel.setVisibility(View.VISIBLE);
                    vh.browseCountRel.setVisibility(View.VISIBLE);
                }
            }

            //如果没有下载量，默认设置为0
            if (list.get(i).getDown() == null||list.get(i).getDown().equals("")){
                vh.downCountTV.setText(0+"");
            }else{
                vh.downCountTV.setText(list.get(i).getDown());
            }
            //如果没有浏览量，默认为0
            if (list.get(i).getBrowse() == null||list.get(i).getBrowse().equals("")){
                vh.browseCountTV.setText(0+"");
            }else{
                vh.browseCountTV.setText(list.get(i).getBrowse());
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
    private void addOnClick(final ViewHolder vh, final int position){
        //下载量查看
        vh.downCountRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getDown() == null||list.get(position).getDown().equals("0")||list.get(position).getDown().equals("")){

                }else{
                    MainActivity.map.put("resouceId",list.get(position).getDownId());
                    MainActivity.map.put("type","2");
                    MainActivity.map.put("pageNo","1");
                    fragment.startActivity(new Intent(context, OperateRecordActicity.class));
                }
            }
        });
        //预览量查询
        vh.browseCountRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getBrowse() == null||list.get(position).getBrowse().equals("0")||list.get(position).getBrowse().equals("")){

                }else{
                    MainActivity.map.put("resouceId",list.get(position).getDownId());
                    MainActivity.map.put("type","1");
                    MainActivity.map.put("pageNo","1");
                    fragment.startActivity(new Intent(context, OperateRecordActicity.class));
                }
            }
        });

        //点击查看附件内容
        vh.l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = list.get(position).getAttachment().getIconList().get(0).getFileName().toLowerCase();
                String lastName = FileUtils.getStringEndWith(fileName);
                if (lastName == null ||lastName.equals("")){
                    ToastUtil.showMessage("文件格式有误");
                    return;
                }
                LogUtil.w(lastName);
                String name = list.get(position).getAttachment().getIconList().get(0).getFileName();
                String downUrl = list.get(position).getAttachment().getIconList().get(0).getDownUrl();
                String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+downUrl,ECApplication.getInstance().getLoginUrlMap());
                //视频播放
                if (lastName.equals(Constant.ATTACHMENT_3GP)
                        ||lastName.equals(Constant.ATTACHMENT_AVI)
                        ||lastName.equals(Constant.ATTACHMENT_MPG)
                        ||lastName.equals(Constant.ATTACHMENT_MOV)
                        ||lastName.equals(Constant.ATTACHMENT_MP3)
                        ||lastName.equals(Constant.ATTACHMENT_MP4)
                        ||lastName.equals(Constant.ATTACHMENT_SWF)
                        ||lastName.equals(Constant.ATTACHMENT_FLV)){

                    if (IntentUtil.isIntentAvailable(context,IntentUtil.getVideoFileIntent(url))){
                        fragment.getActivity().startActivity(IntentUtil.getVideoFileIntent(url));
                        doPreview(position);
                    }else{
                        ToastUtil.showMessage("当前手机不支持打开该文件");
                    }
                    return;
                }
                //此时应该是已经下载到本地浏览
                if (ECApplication.getInstance().isExistFile(name)){
                    File file = new File(ECApplication.getInstance().getDownloadJxzDir(),name);
                    if (file.exists()){
                        //word浏览
                        if(lastName.equals(Constant.ATTACHMENT_DOC)||lastName.equals(Constant.ATTACHMENT_DOCX)){
                            if (IntentUtil.isIntentAvailable(context,IntentUtil.getWordFileIntent(file.getAbsolutePath()))){
                                fragment.startActivity(IntentUtil.getWordFileIntent(file.getAbsolutePath()));
                            }else{
                                ToastUtil.showMessage("当前手机不支持打开该文件");
                            }
                        }
                        //ppt浏览
                        else if (lastName.equals(Constant.ATTACHMENT_PPT)||lastName.equals(Constant.ATTACHMENT_PPTX)){

                            if (IntentUtil.isIntentAvailable(context,IntentUtil.getPptFileIntent(file.getAbsolutePath()))){
                                fragment.startActivity(IntentUtil.getPptFileIntent(file.getAbsolutePath()));
                            }else{
                                ToastUtil.showMessage("当前手机不支持打开该文件");
                            }
                        }
                        //EXCEL浏览
                        else if (lastName.equals(Constant.ATTACHMENT_XLS)||lastName.equals(Constant.ATTACHMENT_XLSX)){

                            if (IntentUtil.isIntentAvailable(context,IntentUtil.getExcelFileIntent(file.getAbsolutePath()))){
                                fragment.startActivity(IntentUtil.getExcelFileIntent(file.getAbsolutePath()));
                            }else{
                                ToastUtil.showMessage("当前手机不支持打开该文件");
                            }
                        }
                        else if (lastName.equals(Constant.ATTACHMENT_PDF)){

                            if (IntentUtil.isIntentAvailable(context,IntentUtil.getPdfFileIntent(file.getAbsolutePath()))){
                                fragment.startActivity(IntentUtil.getPdfFileIntent(file.getAbsolutePath()));
                            }else{
                                ToastUtil.showMessage("当前手机不支持打开该文件");
                            }
                        }
                        else{
                            //跳转到指定位置目录
                            MainActivity.map.put("parentFile",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                            MainActivity.map.put("subFile",new File(ECApplication.getInstance().getDownloadJxzDir()));
                            Intent intent = new Intent(context,FileExplorerActivity.class);
                            intent.putExtra("key_title","查看附件");
                            MainActivity.map.put("isIntentCode","openFile");
                            context.startActivity(intent);
                        }
                    }else{
                        ToastUtil.showMessage("当前文件已被删除..");
                    }
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
                        DownloadAsyncTask d = (DownloadAsyncTask) MainActivity.map.get("downLoadFile"+position);
                        d.cancel(true);
                    }
                }).show();

            }
        });

        vh.l.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String name = list.get(position).getAttachment().getIconList().get(0).getFileName();
                if (ECApplication.getInstance().isExistFile(name)){
                    ToastUtil.showMessage("已存在该文件");
                    return true;
                }
                if (list.get(position).getLoading()){
                    return true;
                }
                ECAlertDialog.buildAlert(context, "是否后台下载该附件？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(position).getAttachment().getIconList().get(0).getDownUrl(),ECApplication.getInstance().getLoginUrlMap());
                        final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
                            @Override
                            public void predownload() {
                                if (downCount==3){
                                    ToastUtil.showMessage("连续下载文件过多...");
                                    return ;
                                }
                                list.get(position).setLoading(true);
                                downCount = downCount+1;
                                vh.progressIndexLinear.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void downloading(int progress, int position) {
                                LogUtil.w("下载进度："+progress);
                                vh.progressBar.setProgress(progress);
                            }

                            @Override
                            public void downloaded(File file1, final int position) {
                                if (file1 != null){
                                    File file = new File(file1.getParent(),name);
                                    file1.renameTo(file);
                                    ToastUtil.showMessage(file.getName()+"下载成功！");
                                    downCount = downCount-1;
                                    list.get(position).setLoading(false);
                                    vh.progressIndexLinear.setVisibility(View.GONE);
                                    vh.fileName.setTextColor(Color.parseColor("#4cbbda"));
                                    ECApplication.getInstance().addJxzFiles(file.getName(),file);
                                    if (!list.get(position).getUserName().contains(ECApplication.getInstance().getCurrentUser().getName())){
                                        if (list.get(position).getDown().equals("")){
                                            list.get(position).setDown(1+"");
                                        }else{
                                            list.get(position).setDown(Integer.parseInt(list.get(position).getDown())+1+"");
                                        }
                                    }
                                    fragment.upDateTreads(list.get(position));
                                    notifyDataSetChanged();
                                    if (!list.get(position).getUserName().contains(ECApplication.getInstance().getCurrentUser().getName())){
                                        //互动交流下载完附件，工作平台下载量加1（自己上传的不加下载量）
                                        new ProgressThreadWrap(context, new RunnableWrap() {
                                            @Override
                                            public void run() {
                                                String resouceId = list.get(position).getDownId();
                                                if (resouceId != null&&resouceId.length()>0){
                                                    String userId = ECApplication.getInstance().getCurrentUser().getId();
                                                    String type = "2";
                                                    HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                                                    map.put("resouceId",new ParameterValue(resouceId));
                                                    map.put("userId",new ParameterValue(userId));
                                                    map.put("type",new ParameterValue(type));
                                                    map.putAll(ECApplication.getInstance().getLoginUrlMap());
                                                    try {
                                                        ZddcUtil.doPreviewOrDown(map);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }).start();

                                    }
                                }else{
                                    ToastUtil.showMessage("文件存在未知错误");
                                }
                            }
                            @Override
                            public void canceled(int position) {
                                File file = new File(ECApplication.getInstance().getDownloadJxzDir(),"jxz_downFile");
                                if (file.exists()){
                                    file.delete();
                                }
                                list.get(position).setLoading(false);
                                downCount = downCount-1;
                                notifyDataSetChanged();
                            }
                        }, context);
                        downloadAsyncTask.execute(url, "aaa", position+"","jxz_downFile");
                        MainActivity.map.put("downLoadFile"+position,downloadAsyncTask);
                    }
                }).show();
                return true;
            }
        });

        //回复点击事件
        vh.replyRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.map.put("introduceReply","introduceReply");
                MainActivity.map.put("introduceReplyFragment",fragment);
                MainActivity.map.put("introduceReplyCommuncationId",list.get(position).getId());
                HomeFragment.position = position;
                fragment.startActivity(new Intent(context, IntroduceTreadsActivity.class));
//                HomeFragment.showEdit(list.get(position).getId(),position,null);
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
                                        fragment.upDateTreads(list.get(position));
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

                ECAlertDialog.buildAlert(context, "是否删除本条信息？", "取消", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
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
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressUtil.hide();
                                            fragment.removeForDeleteTreads(list.get(position));
                                            list.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    },6);
                                } catch (IOException e) {
                                    ToastUtil.showMessage("删除失败");
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                }).show();

            }
        });

        vh.simpleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePagerActivity.class);
                intent.putExtra("images", new String[]{ZddcUtil.getUrlAnd(ECApplication.getInstance().getAddress()+list.get(position).getAttachment().getIconList().get(0).getDownUrl(),ECApplication.getInstance().getLoginUrlMap())});
                intent.putExtra("image_index",0);
                intent.putExtra("imgNames",new String[]{list.get(position).getAttachment().getIconList().get(0).getFileName()});
                if (list.get(position).getUserName().contains(ECApplication.getInstance().getCurrentUser().getName())){
                    fragment.startActivity(intent);
                }else{
                    MainActivity.map.put("11",position);
                    MainActivity.map.put("adapter",adapter);
                    fragment.startActivity(intent);
                    //预览+1
                    doPreview(position);
                }
                MainActivity.map.put("treadsId",list.get(position).getId());
                MainActivity.map.put("isGraffiti","true");
                MainActivity.map.put("introduceReplyFragment",fragment);
            }
        });
        vh.launchOrRetract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.launchOrRetract.setPressed(true);
                vh.launchOrRetract.setClickable(false);
                vh.replyContent.addView(inflater.inflate(R.layout.view_progress,null));
                HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                map.put("commucationId",new ParameterValue(list.get(position).getId()));
                map.putAll(ECApplication.getInstance().getLoginUrlMap());
                initReplyDatas("TreadsListViewAdapter",map,position);
            }
        });
    }

    public void initReplyDatas(String from,final HashMap<String,ParameterValue> map,final int position){
        final ECProgressDialog dialog = new ECProgressDialog(context);
        dialog.setPressText("正在获取回复内容..");
        if (from.equals("HomeFragment")){
            dialog.show();
        }
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String allReply = ZddcUtil.initAllReply(map);
                    final Treads treads = new Gson().fromJson(allReply, Treads.class);
                    if (treads != null) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //更改回复内容
                                list.get(position).setChild(treads.getDataList());
                                list.get(position).setLaunch(true);
                                fragment.upDateTreads(list.get(position));
                                if (dialog.isShowing()){
                                    dialog.dismiss();
                                }
                                notifyDataSetChanged();
                            }
                        }, 10);
                    } else {
                        ToastUtil.showMessage("网络异常");
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doPreview(final int position) {
        if (list.get(position).getBrowse().equals("")){
            list.get(position).setBrowse(1+"");
        }else{
            list.get(position).setBrowse(Integer.parseInt(list.get(position).getBrowse())+1+"");
        }
        fragment.upDateTreads(list.get(position));
        notifyDataSetChanged();
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                String resouceId = list.get(position).getDownId();
                String userId = ECApplication.getInstance().getCurrentUser().getId();
                String type = "1";
                HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                map.put("resouceId",new ParameterValue(resouceId));
                map.put("userId",new ParameterValue(userId));
                map.put("type",new ParameterValue(type));
                map.putAll(ECApplication.getInstance().getLoginUrlMap());
                try {
                    ZddcUtil.doPreviewOrDown(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void doDown(final int position) {

        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                String resouceId = list.get(position).getDownId();
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
                            if (list.get(position).getDown().equals("")){
                                list.get(position).setDown(1+"");
                            }else{
                                list.get(position).setDown(Integer.parseInt(list.get(position).getDown())+1+"");
                            }
                            fragment.upDateTreads(list.get(position));
                            notifyDataSetChanged();
                        }
                    },10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        private LinearLayout delete;
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
        //收起展开
        private TextView launchOrRetract;

        private TextView address;

        private NumberProgressBar progressBar;

        private TextView downCountTV,browseCountTV;

        private RelativeLayout downCountRel,browseCountRel;
        //下载进度条及按钮
        private LinearLayout progressIndexLinear;

        private TextView cancelDownLoad;


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
        TextView replyDate = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,DensityUtil.dip2px(5),0,0);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(0, 5, 0, 0);
        String allStringOfComment = "";
        if (parentName== null){
            allStringOfComment = name + content.trim();
        }else{
            allStringOfComment = name + "@"+parentName+content.trim();
            name = name + "@"+parentName;
        }
        CharSequence charSequence = allStringOfComment;
        if (allStringOfComment.contains("[em_")){
            String html = getEmojyStr(allStringOfComment);
            charSequence = Html.fromHtml(html,
                    new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            Drawable drawable = context.getResources().getDrawable(
                                    Integer.parseInt(source));
                            // 设置drawable的大小。设置为实际大小
                            drawable.setBounds(0, 0,
                                    DensityUtil.dip2px(20f),
                                    DensityUtil.dip2px(20f));
                            return drawable;
                        }
                    }, null);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(charSequence);
        ForegroundColorSpan redSpanBlue = new ForegroundColorSpan(context.getResources().getColor(R.color.name_blue));
        ForegroundColorSpan redSpanTitleColor = new ForegroundColorSpan(context.getResources().getColor(R.color.title_color));
        int start = allStringOfComment.indexOf(name);
        int end = start + name.length();
        if (name.contains(ECApplication.getInstance().getCurrentUser().getName())){
            builder.setSpan(redSpanTitleColor, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            builder.setSpan(redSpanBlue, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }



        textView.setText(builder);
        textView.setTextColor(Color.parseColor("#555555"));
        textView.setTextSize(14);
        textView.setBackgroundResource(R.drawable.bg_button_light_gray_selector);
        textView.setTag(beans.get(position).getChild());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);



        LinearLayout.LayoutParams replyDateParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        replyDateParams.setMargins(0,DensityUtil.dip2px(2),0,0);
        replyDate.setLayoutParams(replyDateParams);
        replyDate.setTextColor(Color.parseColor("#777777"));
        replyDate.setTextSize(12);
        replyDate.setGravity(Gravity.END);
        replyDate.setText(beans.get(position).getCreateTime().trim());




// ----------2017/3/8 加入回复图片----------------------------------------------------------------------------------- 2017/3/8 加入回复图片
        LinearLayout.LayoutParams replyImageLinearOut = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layoutOut = new LinearLayout(context);
        layoutOut.setLayoutParams(replyImageLinearOut);
        layoutOut.setGravity(Gravity.LEFT);
        if (beans.get(position).getIconList() != null&&beans.get(position).getIconList().size()>0){
            ArrayList<ImageUrlBean> urls = new ArrayList<>();
            for (int i = 0; i < beans.get(position).getIconList().size(); i++) {
                ImageUrlBean bean = new ImageUrlBean();
                bean.setName(beans.get(position).getIconList().get(i).getFileName());
                bean.setShowUrl(ZddcUtil.getUrl(ECApplication.getInstance().getAddress() + beans.get(position).getIconList().get(i).getImg(), ECApplication.getInstance().getLoginUrlMap()));
                LogUtil.w(ZddcUtil.getUrl(ECApplication.getInstance().getAddress() + beans.get(position).getIconList().get(i).getImg(), ECApplication.getInstance().getLoginUrlMap()));
                bean.setDownLoadUrl(ZddcUtil.getUrl(ECApplication.getInstance().getAddress() + beans.get(position).getIconList().get(i).getDownUrl(), ECApplication.getInstance().getLoginUrlMap()));
                urls.add(bean);
            }
            LinearLayout.LayoutParams replyImageLinear = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.gridView_linear_width), LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(replyImageLinear);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
            GridView gridView = new GridView(context);
            gridView.setNumColumns(3);
            gridView.setVerticalSpacing(5);
            gridView.setHorizontalSpacing(5);
            layout.addView(gridView);
            if (urls.size() > 0) {
                ImageGirdAdapter adapter = new ImageGirdAdapter((Activity) context, urls, fragment,0,this.adapter,null);
                gridView.setAdapter(adapter);
                Tools.setGridViewHeightBasedOnChildren(gridView);
            }
            layoutOut.addView(layout);
        }

        TextView textViewLine = new TextView(context);
        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        textViewLine.setLayoutParams(layoutParamsLine);
        textViewLine.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
// ----------2017/3/8 加入回复图片结束----------------------------------------------------------------------------------- 2017/3/8 加入回复图片



        vh.replyContent.addView(textView);
        vh.replyContent.addView(layoutOut);
        vh.replyContent.addView(replyDate);
        vh.replyContent.addView(textViewLine);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beans.get(position).getCanDelete().equals("yes")){
                    ECAlertDialog.buildAlert(context, "是否删除本条回复内容？", "取消", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, new DialogInterface.OnClickListener() {
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
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ProgressUtil.hide();
                                                resetAllReplyCount(beans.get(position).getChild());
                                                list.get(index).setAllReplyCount(list.get(index).getAllReplyCount()-deleteCount);
                                                deleteCount = 1;
                                                beans.remove(position);
                                                fragment.upDateTreads(list.get(index));
                                                notifyDataSetChanged();
                                            }
                                        },6);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }).show();

                }else{
                    MainActivity.map.put("introduceReply","introduceReply");
                    MainActivity.map.put("introduceReplyFragment",fragment);
                    MainActivity.map.put("introduceReplyCommuncationId",list.get(index).getId());
                    MainActivity.map.put("introduceReplyToCommuncationId",beans.get(position).getId());
                    MainActivity.map.put("introduceReplyToUserName",beans.get(position).getUserName());
                    HomeFragment.position = index;
                    fragment.startActivity(new Intent(context, IntroduceTreadsActivity.class));
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
