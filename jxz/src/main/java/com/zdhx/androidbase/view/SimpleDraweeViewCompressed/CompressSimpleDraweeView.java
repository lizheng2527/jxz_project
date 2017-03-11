package com.zdhx.androidbase.view.SimpleDraweeViewCompressed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.Postprocessor;

/**
 * Created by lizheng on 2017/3/8.
 */

public class CompressSimpleDraweeView extends SimpleDraweeView {

    private Context context;


    public CompressSimpleDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public CompressSimpleDraweeView(Context context) {
        super(context);
        this.context = context;
    }

    public CompressSimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CompressSimpleDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public CompressSimpleDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    Uri uri;
    SimpleDraweeView mSimpleDraweeView = new SimpleDraweeView(context);
    Postprocessor redMeshPostprocessor = new BasePostprocessor() {
        @Override
        public String getName() {
            return "redMeshPostprocessor";
        }

        @Override
        public void process(Bitmap bitmap) {
            for (int x = 0; x < bitmap.getWidth(); x+=2) {
                for (int y = 0; y < bitmap.getHeight(); y+=2) {
                    bitmap.setPixel(x, y, Color.RED);
                }
            }
        }
    };
}
