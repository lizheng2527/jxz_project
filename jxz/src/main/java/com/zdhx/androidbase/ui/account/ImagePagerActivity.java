package com.zdhx.androidbase.ui.account; /*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.downloadui.DownloadAsyncTask;
import com.zdhx.androidbase.ui.photoview.PhotoView;
import com.zdhx.androidbase.ui.photoview.PhotoViewAttacher;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.viewpagerindicator.CirclePageIndicator;
import com.zdhx.androidbase.ui.viewpagerindicator.HackyViewPager;
import com.zdhx.androidbase.ui.viewpagerindicator.PageIndicator;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.SingleMediaScanner;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;
import com.zdhx.androidbase.util.myImageLoader.MyImageLoader;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends Activity {

	protected ImageLoader imageLoader;
	protected MyImageLoader loader;
	private static final String STATE_POSITION = "STATE_POSITION";

	private static final String IMAGES = "images";

	private static final String IMAGE_POSITION = "image_index";

	HackyViewPager pager;
	PageIndicator mIndicator;
	private String imgName ;
	private ECProgressDialog dialog;
	private String[] compressImageUrls;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader = null;
		loader = null;
	}

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		setContentView(R.layout.ac_image_pager);
		imageLoader = new ImageLoader(this);
		loader = new MyImageLoader();
		dialog = new ECProgressDialog(this);

		Bundle bundle = getIntent().getExtras();
		String[] imageUrls = bundle.getStringArray(IMAGES);
		compressImageUrls = imageUrls;
		String[] imgNames = new String[compressImageUrls.length];
		int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);
		imgNames = bundle.getStringArray("imgNames");
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		pager = (HackyViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls,this,imgNames));
		pager.setCurrentItem(pagerPosition);
		if (imageUrls.length<10){
			mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
			mIndicator.setViewPager(pager);
		}

	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private String[] imgNames;
		private LayoutInflater inflater;

		private Context mContext;

		private ECAlertDialog buildAlert;


		ImagePagerAdapter(String[] images,Context context,String[] imgNames) {
			this.images = images;
			this.mContext=context;
			this.imgNames = imgNames;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {

			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);

			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
			imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
				@Override
				public void onViewTap(View view, float x, float y) {
					onBackPressed();
				}
			});
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			TextView count = (TextView) imageLayout.findViewById(R.id.count);
			int selectCount = position;
			count.setText(selectCount+1+"/"+images.length);
			if (images[position].contains("http")){
				imageLoader.DisplayImage(images[position],imageView,false);
				imageView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						buildAlert = ECAlertDialog.buildAlert(mContext,R.string.title, null, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
								String lastStr = FileUtils.getStringEndWith(name);
								if (lastStr != null){
									lastStr = lastStr.toLowerCase();
								}
								if (!"".equals(lastStr)&&!"jpg".equals(lastStr)&&!"png".equals(lastStr)&&!"jpeg".equals(lastStr)){
									name = name +".jpg";
								}
								if (name == null||name.equals("")){
									ToastUtil.showMessage("输入名称不能为空!");
									return;
								}
								File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
								File dir = new File(idr+"/jxz");
								if (!dir.exists()){
									dir.mkdir();
								}
								File file = new File(dir,name);
								if (!file.exists()){
									final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
										@Override
										public void predownload() {
											TreadsListViewAdapter adapter = (TreadsListViewAdapter) MainActivity.map.get("adapter");
											if (adapter != null){
												int position1 = (int) MainActivity.map.get("11");
												adapter.doDown(position1);
												MainActivity.map.remove("treadsListPosition");
												MainActivity.map.remove("adapter");
											}
										}

										@Override
										public void downloading(int progress, int position) {

										}

										@Override
										public void downloaded(File file, int position) {
											new SingleMediaScanner(mContext, file);
											ToastUtil.showMessage(file.getName()+"下载成功！");
										}

										@Override
										public void canceled(int position) {
										}
									}, ImagePagerActivity.this);
									downloadAsyncTask.execute(images[position], "aaa", position + "",name);
									LogUtil.w(images[position]);
								}else{
									ToastUtil.showMessage("已存在该文件");
									return;
								}

							}
						});
						buildAlert.setTitle("图片下载");
						buildAlert.setCanceledOnTouchOutside(false);
						buildAlert.setContentView(R.layout.config_dcaddress_dialog);
						String server = imgNames[position];
						final EditText editText = (EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText));
						TextView delectTV = (TextView) buildAlert.getWindow().findViewById(R.id.delectTV);
						delectTV.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								editText.setText("");
							}
						});
						if (!server.equals("")) {
							editText.setText(server);
						}
						if(!buildAlert.isShowing()){
							buildAlert.show();
							editText.setSelection(editText.getText().length());
							editText.setSelectAllOnFocus(true);
						}




						/*ECAlertDialog.buildAlert(ImagePagerActivity.this, "是否下载该图片？", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String name = imgNames[position];
								File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
								File dir = new File(idr+"/jxz");
								if (!dir.exists()){
									dir.mkdir();
								}
								File file = new File(dir,name);
								if (!file.exists()){
									final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
										@Override
										public void predownload() {
											TreadsListViewAdapter adapter = (TreadsListViewAdapter) MainActivity.map.get("adapter");
											if (adapter != null){
												int position1 = (int) MainActivity.map.get("11");
												adapter.doDown(position1);
												MainActivity.map.remove("treadsListPosition");
												MainActivity.map.remove("adapter");
											}
										}

										@Override
										public void downloading(int progress, int position) {

										}

										@Override
										public void downloaded(File file, int position) {
											ToastUtil.showMessage(file.getName()+"下载成功！");
											LogUtil.w("下载成功的地址为："+file.getAbsolutePath());
										}

										@Override
										public void canceled(int position) {
										}
									}, ImagePagerActivity.this);
									downloadAsyncTask.execute(images[position], "aaa", position + "",name);
									LogUtil.w(images[position]);
								}else{
									ToastUtil.showMessage("已存在该文件");
									return;
								}
							}
						}).show();*/

						return true;
					}
				});
			}else{
				//加载本地图片
				BitmapFactory.Options newOpts = new BitmapFactory.Options();
				newOpts.inJustDecodeBounds = false;
				newOpts.inPurgeable = true;
				newOpts.inInputShareable = true;
				// Do not compress
				newOpts.inSampleSize = 1;
				newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap bm = BitmapFactory.decodeFile(images[position], newOpts);
				imageView.setImageBitmap(bm);
			}
			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		/**
		 * 复制单个文件
		 * @param oldPath String 原文件路径 如：c:/fqf.txt
		 * @param newPath String 复制后路径 如：f:/fqf.txt
		 * @return boolean
		 */
		public void copyFile(String oldPath, String newPath) {
			try {
				int bytesum = 0;
				int byteread = 0;
				File oldfile = new File(oldPath);
				if (oldfile.exists()) { //文件存在时
					InputStream inStream = new FileInputStream(oldPath); //读入原文件
					FileOutputStream fs = new FileOutputStream(newPath);
					byte[] buffer = new byte[1444];
					int length;
					while ( (byteread = inStream.read(buffer)) != -1) {
						bytesum += byteread; //字节数 文件大小
						System.out.println(bytesum);
						fs.write(buffer, 0, byteread);
					}
					inStream.close();
				}
			}
			catch (Exception e) {
				System.out.println("复制单个文件操作出错");
				e.printStackTrace();

			}

		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
}