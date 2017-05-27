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

public class HandWriteAdapter extends BaseAdapter {

    private ArrayList<HandWriteBean> list;
    private Context context;
    public HandWriteAdapter(ArrayList<HandWriteBean> list,Context context) {
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
        if (list.get(i).getPreviewQuestionImg() != null){
            String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getPreviewQuestionImg(), ECApplication.getInstance().getLoginUrlMap());
            FrescoUtil.load(Uri.parse(url),vh.handWritesimpleImage, DensityUtil.dip2px(150),DensityUtil.dip2px(150));
        }else{
            vh.handWritesimpleImage.setImageResource(R.drawable.question_image_default);
        }
        vh.handWriteTitle.setText(list.get(i).getQuestionName());
        vh.dateTime.setText(list.get(i).getDateTime());
        addClick(vh,i);
        return view;
    }

    private void addClick(ViewHolder vh, final int i) {
        vh.handWritesimpleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.map.put("handWriteAnswer",list.get(i).getQuestionId());
                MainActivity.map.put("handWriteAnswer",list.get(i));
//                MainActivity.map.put("originalImageAttachmentQuestionId",list.get(i).getOriginalImageAttachmentQuestionId());
//                MainActivity.map.put("questionName",list.get(i).getQuestionName());

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
