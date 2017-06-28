package com.zdhx.androidbase.ui.moralevaluation;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.ToastUtil;

import java.io.File;
import java.util.List;

/**
 * 德育评比webView
 * Created by lizheng on 2017/6/12.
 */

public class MoralLevaluationActivity extends BaseActivity {

    private WebView webView;

    //顶部进度条
    private ProgressBar progressBar1;


    @Override
    protected int getLayoutId() {
        return R.layout.moralleva_webview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBarView().setVisibility(View.GONE);
        webView = (WebView) findViewById(R.id.webView_moral);

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);//启用或禁止WebView访问文件数据
        settings.setBlockNetworkImage(false);//是否显示网络图像  false 为显示

        //交互接口
        webView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void openTreeList() {
//                startActivityForResult(new Intent(context, WebAppSelectActivity.class), 111);
            }

            @JavascriptInterface
            public void toFinishActivity() {
                finish();
            }

            @JavascriptInterface
            public void showToast(String msg) {
                ToastUtil.showMessage(msg);
            }

        }, "nativeMobileDom");
        //相机相册
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new HelloWebViewClient());
        webView.loadUrl("http://www.zdhx-edu.com/moralm/");
    }

    private class MyWebChromeClient extends WebChromeClient{
        public void onProgressChanged(WebView view, int progress) {
            // 载入进度改变而触发
            if (progress == 100) {
                progressBar1.setVisibility(View.GONE);
            } else {
                progressBar1.setProgress(progress);
            }
            super.onProgressChanged(view, progress);
        }
        //扩展支持alert事件
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (result == null){
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("提示").setMessage(message).setPositiveButton("确定", null);
            builder.setCancelable(false);
            //  builder.setIcon(R.drawable.ic_launcher);
            AlertDialog dialog = builder.create();
            dialog.show();
            result.confirm();
            return true;
        }

        //扩展浏览器上传文件
        //3.0++版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg);
        }

        //3.0--版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg);
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android > 5.0
        @Override
        public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }
    }
    /**
     * 5.0以下
     * @param uploadMsg
     */
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
//        Intent intent = new Intent(MoralLevaluationActivity.this, PhotoSelectorActivity.class);
//        intent.putExtra("canSelectCount", 4);
//        startActivityForResult(intent, 2);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent xxx= createChooserIntent(createCameraIntent());
        xxx.putExtra(Intent.EXTRA_INTENT, i);
        startActivityForResult(xxx, FILECHOOSER_RESULTCODE);
    }


    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    private String mCameraFilePath;
    private Uri uri;

    /**
     * 5.0以上的
     * @param uploadMsg
     */
    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {


        Intent intent = new Intent(MoralLevaluationActivity.this, PhotoSelectorActivity.class);
        intent.putExtra("canSelectCount", 4);
        startActivityForResult(intent, 2);
        mUploadMessageForAndroid5 = uploadMsg;
//        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        contentSelectionIntent.setType("image/*");
//
//        // Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//
//        Intent xxx= createChooserIntent(createCameraIntent());
//        xxx.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//        startActivityForResult(xxx, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                File.separator + "browser-photos");
        cameraDataDir.mkdirs();
        mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                System.currentTimeMillis() + ".jpg";
        uri=  Uri.fromFile(new File(mCameraFilePath));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return cameraIntent;
    }

    private Intent createChooserIntent(Intent ... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择图片来源");
        return chooser;
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view,String url)  //加载结束
        {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {

            if (webView.getUrl().endsWith("#")) { //重定向URL 直接结束
                finish();
            } else {
                webView.goBack(); //goBack()表示返回WebView的上一页面
            }
            return true;
        }else{

            finish();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {

        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (intent != null && intent.getExtras() != null) {
                @SuppressWarnings("unchecked")
                List<PhotoModel> photos = (List<PhotoModel>) intent.getExtras()
                        .getSerializable("photos");
                if (photos == null){
                    mUploadMessageForAndroid5.onReceiveValue(null);
                    return;
                }
                Uri[] uris = new Uri[photos.size()];
                for (int i = 0; i < photos.size(); i++) {
                    uris[i] = Uri.fromFile(new File(photos.get(i).getOriginalPath()));
                }
                mUploadMessageForAndroid5.onReceiveValue(uris);
            }else{
                mUploadMessageForAndroid5.onReceiveValue(null);
            }
            mUploadMessageForAndroid5 = null;
        }else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }else{
            if (mUploadMessageForAndroid5 != null)
                mUploadMessageForAndroid5.onReceiveValue(null);
            if (mUploadMessage != null)
                mUploadMessage.onReceiveValue(null);
        }

        /*if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            //针对5.0的
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                //相册或者文件
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                //拍照
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{uri});
            }
            mUploadMessageForAndroid5 = null;*/

    }

}
