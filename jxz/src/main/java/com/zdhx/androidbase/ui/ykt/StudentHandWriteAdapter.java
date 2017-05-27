package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.util.DensityUtil;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.SimpleDraweeViewCompressed.FrescoUtil;

import java.util.ArrayList;

/**
 * Created by lizheng on 2016/12/26.
 */

public class StudentHandWriteAdapter extends BaseAdapter {

    private ArrayList<StudentWriteAnswerResults> list;
    private Context context;
    public StudentHandWriteAdapter(ArrayList<StudentWriteAnswerResults> list, Context context) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (view == null){
            vh = new ViewHolder();
            view = View.inflate(context, R.layout.fragment_handwrite_viewpager_listview_item,null);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }
        vh.userHeadImg = (RoundCornerImageView) view.findViewById(R.id.handwriteheadimg);
        vh.handWritesimpleImage = (SimpleDraweeView) view.findViewById(R.id.handWritesimpleImage);
//        vh.handWritesimpleImage.setImageResource(R.drawable.question_image_default);
        vh.handWriteTitle = (TextView) view.findViewById(R.id.handwriteheadname);
        vh.dateTime = (TextView) view.findViewById(R.id.handwritedatetime);

        if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
            if (list.get(i).getSex().equals("男")){
                vh.userHeadImg.setImageResource(R.drawable.man);
            }else{
                vh.userHeadImg.setImageResource(R.drawable.women);
            }
        }
        if (ECApplication.getInstance().getUserForYKT().getType().equals("0")){
            if (list.get(i).getSex().equals("男")){
                vh.userHeadImg.setImageResource(R.drawable.boy);
            }else{
                vh.userHeadImg.setImageResource(R.drawable.girl);
            }
        }
        if (list.get(i).getPreviewImage() != null){
            String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getPreviewImage(), ECApplication.getInstance().getLoginUrlMap());
            FrescoUtil.load(Uri.parse(url),vh.handWritesimpleImage, DensityUtil.dip2px(150),DensityUtil.dip2px(150));
        }else{
            if (list.get(i).getAnswers() != null&&list.get(i).getAnswers().size()>0){
                String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getAnswers().get(0).getPreviewImage(), ECApplication.getInstance().getLoginUrlMap());
                FrescoUtil.load(Uri.parse(url),vh.handWritesimpleImage, DensityUtil.dip2px(150),DensityUtil.dip2px(150));
            }else{
                vh.handWritesimpleImage.setImageResource(R.drawable.question_image_default);
            }
        }
        vh.handWriteTitle.setText(list.get(i).getTitle());
        vh.dateTime.setText(list.get(i).getStartTime());
        addClick(vh,i);
        return view;
    }

    private void addClick(ViewHolder vh, final int i) {
        vh.handWritesimpleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<WriteAnswerResults> beans = new ArrayList<WriteAnswerResults>();
                if (list.get(i).getPreviewImage() != null){
                    WriteAnswerResults bean = new WriteAnswerResults();
                    bean.setPreviewImageAttachmentId(list.get(i).getPreviewImageAttachmentId());
                    bean.setOriginalImageAttachmentId(list.get(i).getOriginalImageAttachmentId());
                    bean.setPreviewImageQuestion(list.get(i).getPreviewImage());
                    bean.setOriginalImageQuestion(list.get(i).getOriginalImage());
                    bean.setStudentId(ECApplication.getInstance().getUserForYKT().getId());
                    bean.setTeacherName(list.get(i).getTeacherName());
                    bean.setQuestionName(list.get(i).getQuestionName());
                    bean.setTitle(list.get(i).getTitle());
                    bean.setQuestion(true);
                    beans.add(bean);
                }
                if (list.get(i).getAnswers() != null&&list.get(i).getAnswers().size()>0){
                    WriteAnswerResults bean = new WriteAnswerResults();
                    for (int i1 = 0; i1 < list.get(i).getAnswers().size(); i1++) {
                        bean.setPreviewImageAttachmentId(list.get(i).getAnswers().get(i1).getPreviewImageAttachmentId());
                        bean.setOriginalImageAttachmentId(list.get(i).getAnswers().get(i1).getOriginalImageAttachmentId());
                        bean.setPreviewImageQuestion(list.get(i).getPreviewImage());
                        bean.setPreviewImage(list.get(i).getAnswers().get(i1).getPreviewImage());
                        bean.setOriginalImage(list.get(i).getAnswers().get(i1).getOriginalImage());
                        bean.setOriginalImageQuestion(list.get(i).getOriginalImage());
                        bean.setStudentId(ECApplication.getInstance().getUserForYKT().getId());
                        bean.setStudentName(ECApplication.getInstance().getUserForYKT().getName());
                        bean.setTeacherName(list.get(i).getTeacherName());
                        bean.setQuestionName(list.get(i).getQuestionName());
                        bean.setTitle(list.get(i).getTitle());
                        beans.add(bean);
                    }
                }
                MainActivity.map.put("handWriteAnswer",beans);
                context.startActivity(new Intent(context,HandWriteSelectActivity.class));
            }
        });
    }

    class ViewHolder{

        private ImageView userHeadImg;
        private SimpleDraweeView handWritesimpleImage;
        private TextView handWriteTitle;
        private TextView dateTime;
    }
}
