package com.zdhx.androidbase.view.SimpleDraweeViewCompressed;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by lizheng on 2017/3/8.
 */

public class FrescoUtil {

    public static void load(Uri uri, SimpleDraweeView draweeView, int width, int height){
        ImageRequest request =
                ImageRequestBuilder.newBuilderWithSource(uri)
                        .setResizeOptions(new ResizeOptions(width,height))
                        //缩放,在解码前修改内存中的图片大小, 配合Downsampling可以处理所有图片,否则只能处理jpg,
//                         开启Downsampling:在初始化时设置
//                        .setDownsampleEnabled(true)
//                        .setProgressiveRenderingEnabled(true)//支持图片渐进式加载
                        .setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                        .build();

        PipelineDraweeController controller =
                (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(draweeView.getController())
                        .build();

        draweeView.setController(controller);
    }
}
