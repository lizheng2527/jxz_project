/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.zdhx.androidbase.ui.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;

import java.io.File;

/**
 * 文件浏览器
 * @author 容联•云通讯 Modify By Li.Xin @ 立思辰合众
 * @date 2014-12-29
 * @version 4.0
 */
public class FileExplorerActivity extends BaseActivity implements View.OnClickListener {

	/**设备内置目录*/
	private static final int DIR_ROOT = 0;
	/**外置存储卡*/
	private static final int DIR_SDCARD = 1;
	/**文件浏览器列表*/
	private ListView mFileListView;
	/**设备选项卡*/
	private TextView mRootTab;
	/**设备选项卡导航线*/
	private View mRootTabSelector;
	/**存储卡选项卡*/
	private TextView mSdcardTab;
	/**存储卡导航线*/
	private View mSdcardTabSelector;

	private String mFileExplorerRootTag;
	private String mFileExplorerSdcardTag;
	private FileListAdapter mAdapter;
	private File mRootFile;
	private File mSdcardFile;
	/**浏览文件目录类型*/
	private int mType = DIR_ROOT;

	final private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			File file = (File) mAdapter.getItem(position);
			if(!file.isFile()) {
				if(mType == DIR_ROOT) {
					// 当前为内置存储浏览模式
					mRootFile = file;
				} else {
					mSdcardFile = file;
				}

				if(file != mAdapter.getParentFile()) {
					mAdapter.setFiles(mAdapter.getCurrentFile(), file);
				} else {
					mAdapter.setFiles(mAdapter.getParentFile().getParentFile(), mAdapter.getParentFile());
				}

				mAdapter.notifyDataSetChanged();
				mFileListView.setSelection(0);
				return ;
			}
			String openFile = (String) MainActivity.map.get("isIntentCode");
			if (openFile == null||"".equals(openFile)){
				setResult(RESULT_OK, new Intent().putExtra("choosed_file_path", file.getAbsolutePath()));
			}else{
				//选择打开的文件
				MainActivity.map.remove("isIntentCode");
				String fileName = file.getName();
				String lastName = FileUtils.getStringEndWith(fileName);
				if (lastName == null||lastName.equals("")){
					finish();
					return;
				}
				//播放视频
				if (lastName.equals(Constant.ATTACHMENT_3GP)
						||lastName.equals(Constant.ATTACHMENT_AVI)
						||lastName.equals(Constant.ATTACHMENT_MP3)
						||lastName.equals(Constant.ATTACHMENT_MP4)
						||lastName.equals(Constant.ATTACHMENT_SWF)
						||lastName.equals(Constant.ATTACHMENT_FLV)){

					if (IntentUtil.isIntentAvailable(context,IntentUtil.getVideoFileIntent(file.getAbsolutePath()))){
						startActivity(IntentUtil.getVideoFileIntent(file.getAbsolutePath()));
					}
				}
				//word浏览
				else if(lastName.equals(Constant.ATTACHMENT_DOC)||lastName.equals(Constant.ATTACHMENT_DOCX)){
					if (IntentUtil.isIntentAvailable(context,IntentUtil.getWordFileIntent(file.getAbsolutePath()))){
						startActivity(IntentUtil.getWordFileIntent(file.getAbsolutePath()));
					}
				}

				//ppt浏览
				else if (lastName.equals(Constant.ATTACHMENT_PPT)||lastName.equals(Constant.ATTACHMENT_PPTX)){

					if (IntentUtil.isIntentAvailable(context,IntentUtil.getPptFileIntent(file.getAbsolutePath()))){
						startActivity(IntentUtil.getPptFileIntent(file.getAbsolutePath()));
					}
				}
				//EXCEL浏览
				else if (lastName.equals(Constant.ATTACHMENT_XLS)||lastName.equals(Constant.ATTACHMENT_XLSX)){

					if (IntentUtil.isIntentAvailable(context,IntentUtil.getExcelFileIntent(file.getAbsolutePath()))){
						startActivity(IntentUtil.getExcelFileIntent(file.getAbsolutePath()));
					}
				}
				//PDF浏览
				else if (lastName.equals(Constant.ATTACHMENT_PDF)){

					if (IntentUtil.isIntentAvailable(context,IntentUtil.getPdfFileIntent(file.getAbsolutePath()))){
						startActivity(IntentUtil.getPdfFileIntent(file.getAbsolutePath()));
					}
				}
				//安装apk
				else if (lastName.equals(Constant.ATTACHMENT_APK)){
					if (IntentUtil.isIntentAvailable(context,IntentUtil.getApkFileIntent(file.getAbsolutePath()))){
						startActivity(IntentUtil.getApkFileIntent(file.getAbsolutePath()));
					}
				}
				//打开所有自己选吧
				else {
					startActivity(IntentUtil.getAllFileIntent(file.getAbsolutePath()));
				}

			}
			finish();
		}
	};

	@Override
	protected int getLayoutId() {
		return R.layout.file_explorer;
	}

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setBackgroundResource(R.color.title_color);
		String title = getIntent().getStringExtra("key_title");
		if(TextUtils.isEmpty(title)) {
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1, "添加附件", this);

		} else {
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1, title, this);
		}
		context = this;
		initFileExplorer();
	}

	/**
	 * 初始化页面
	 */
	private void initFileExplorer() {
		mFileListView = (ListView) findViewById(R.id.file_explorer_list_lv);
		mRootTab = (TextView) findViewById(R.id.root_tab);
		mRootTabSelector = findViewById(R.id.root_tab_selector);
		mSdcardTab = (TextView) findViewById(R.id.sdcard_tab);
		mSdcardTabSelector = findViewById(R.id.sdcard_tab_selector);

		mFileExplorerRootTag = getString(R.string.plugin_file_explorer_root_tag);
		mFileExplorerSdcardTag = getString(R.string.plugin_file_explorer_sdcard_tag);

		File rootDirectory = Environment.getRootDirectory();
		if(!rootDirectory.canRead()) {
			File dataDirectory = Environment.getDataDirectory();
			if(dataDirectory.canRead()) {
				rootDirectory = dataDirectory;
				mFileExplorerRootTag = dataDirectory.getName();
			}
		}
		mRootFile = rootDirectory;

		File externalStorageFile = null;
		if(!FileUtils.checkExternalStorageCanWrite()) {
			File downloadCacheDirectory = Environment.getDownloadCacheDirectory();
			if(downloadCacheDirectory.canRead()) {
				externalStorageFile = downloadCacheDirectory;
				mFileExplorerSdcardTag = downloadCacheDirectory.getName();
			}
		} else {
			externalStorageFile = Environment.getExternalStorageDirectory();

		}
		mSdcardFile = externalStorageFile;

		setCurrentTabSelector(DIR_SDCARD);
		mAdapter = new FileListAdapter(this);
		mAdapter.setPath(externalStorageFile.getPath());
		mAdapter.setFiles(externalStorageFile.getParentFile(), externalStorageFile);
		mFileListView.setAdapter(mAdapter);
		mFileListView.setOnItemClickListener(mItemClickListener);
		mRootTab.setOnClickListener(new FileTabClickListener(DIR_ROOT , mRootFile));
		mSdcardTab.setOnClickListener(new FileTabClickListener(DIR_SDCARD , mSdcardFile));
	}

	/**
	 * 切换视图
	 * @param type
	 */
	private void setCurrentTabSelector(int type) {
		mType = type;
		if (type == DIR_SDCARD) {
			mSdcardTab.setTextColor(getResources().getColor(R.color.red_btn_color_normal));
			mRootTab.setTextColor(getResources().getColor(R.color.action_bar_tittle_color));
			mRootTabSelector.setVisibility(View.GONE);
			mSdcardTabSelector.setVisibility(View.VISIBLE);
			return;
		}
		mRootTab.setTextColor(getResources().getColor(R.color.red_btn_color_normal));
		mSdcardTab.setTextColor(getResources().getColor(R.color.action_bar_tittle_color));
		mRootTabSelector.setVisibility(View.VISIBLE);
		mSdcardTabSelector.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			default:
				break;
		}
	}


	private class FileTabClickListener implements View.OnClickListener {

		private int type;
		/**当前浏览文件夹*/
		private File mParentFile;
		/**扩展卡根目录*/
		private File mRootPath;
		public FileTabClickListener(int type , File f) {
			this.type = type;
			this.mRootPath = f;
		}

		@Override
		public void onClick(View v) {
			mParentFile = (this.type == DIR_SDCARD) ? mSdcardFile : mRootFile;
			setCurrentTabSelector(this.type);
			mAdapter.setPath(mRootPath.getPath());
			LogUtil.w(mRootPath.getPath());
			mAdapter.setFiles(mParentFile.getParentFile(), mParentFile);
			mAdapter.notifyDataSetInvalidated();
			mAdapter.notifyDataSetChanged();
			mFileListView.setSelection(0);
		}

	}
}
