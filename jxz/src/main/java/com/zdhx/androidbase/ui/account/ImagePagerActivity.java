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
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.photoview.PhotoView;
import com.zdhx.androidbase.ui.viewpagerindicator.CirclePageIndicator;
import com.zdhx.androidbase.ui.viewpagerindicator.HackyViewPager;
import com.zdhx.androidbase.ui.viewpagerindicator.PageIndicator;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;
import com.zdhx.androidbase.util.myImageLoader.MyImageLoader;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


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

		int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);

		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		pager = (HackyViewPager) findViewById(R.id.pager);
//		if (imageUrls != null&&imageUrls.length>0&&!imageUrls[0].contains("http://")){
//			dialog.setPressText("正在加载..");
//			dialog.show();
//			compressBitmap(compressImageUrls,pagerPosition);
//		}else{
//			pager.setAdapter(new ImagePagerAdapter(imageUrls,this));
//			pager.setCurrentItem(pagerPosition);
//			if (imageUrls.length<10){
//				mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
//				mIndicator.setViewPager(pager);
//			}
//		}
		pager.setAdapter(new ImagePagerAdapter(imageUrls,this));
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

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;
		private Context mContext;


		ImagePagerAdapter(String[] images,Context context) {
			this.images = images;
			this.mContext=context;
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
		public Object instantiateItem(ViewGroup view, int position) {

			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);

			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
//			imageView.setImageResource(R.drawable.icon_img_loading);
//			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			if (images[position].contains("http")){
				imageLoader.DisplayImage(images[position],imageView,false);

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
	private ArrayList<String> list = new ArrayList<>();
	private int selectCountForPager;

	/**
	 * 数组多张压缩
	 * @param images
	 * @param pagerPosition
	 */
	private void compressBitmap(final String[] images,final int pagerPosition){
		selectCountForPager = pagerPosition;
		for (int jj = 0; jj < images.length; jj++) {
			final int i = jj;
			try {
				Thread.sleep(10);
				new Thread(){
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Luban.get(ImagePagerActivity.this)
										.load(new File(images[i])) //传人要压缩的图片
										.putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
										.setCompressListener(new OnCompressListener() { //设置回调

											@Override
											public void onStart() {
												// TODO 压缩开始前调用，可以在方法内启动 loading UI
												LogUtil.w("开始压缩"+i);
											}
											@Override
											public void onSuccess(File file) {
												// TODO 压缩成功后调用，返回压缩后的图片文件
												list.add(file.getAbsolutePath());
												LogUtil.w(file.getAbsolutePath()+"压缩成功,大小为："+file.length());
												String name = (String) MainActivity.map.get("selectCountForPager");

												if (list.size() == images.length){
													String[] strs = new String[list.size()];
													for (int i1 = 0; i1 < list.size(); i1++) {
														strs[i1] = list.get(i1);
														if (list.get(i1).contains(name)){
															selectCountForPager = i1;
															MainActivity.map.remove("selectCountForPager");
														}
													}
													pager.setAdapter(new ImagePagerAdapter(strs,ImagePagerActivity.this));
													pager.setCurrentItem(selectCountForPager);
													if (strs.length<10){
														mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
														mIndicator.setViewPager(pager);
													}
													dialog.dismiss();
												}
											}
											@Override
											public void onError(Throwable e) {
												// TODO 当压缩过去出现问题时调用
												LogUtil.w("压缩失败");
											}
										}).launch();//启动压缩
							}
						});

					}
				}.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}
	}
	/**
	 * 单张张压缩
	 * @param images
	 * @param pagerPosition
	 */
	private void compressBitmapSimple(final String[] images,final int pagerPosition){
		selectCountForPager = pagerPosition;
		for (int jj = 0; jj < images.length; jj++) {
			final int i = jj;
			File file = new File(images[jj]);
			if (file.length()/1024<300){
				LogUtil.w(file.getAbsolutePath()+"小于300kb不用压缩,当前大小为："+file.length()/1024+"kb");
				return;
			}
			try {
				Thread.sleep(10);
				new Thread(){
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Luban.get(ImagePagerActivity.this)
										.load(new File(images[i])) //传人要压缩的图片
										.putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
										.setCompressListener(new OnCompressListener() { //设置回调

											@Override
											public void onStart() {
												// TODO 压缩开始前调用，可以在方法内启动 loading UI
												LogUtil.w("开始压缩"+i);
											}
											@Override
											public void onSuccess(File file) {
												// TODO 压缩成功后调用，返回压缩后的图片文件
												list.add(file.getAbsolutePath());
												LogUtil.w(file.getAbsolutePath()+"压缩成功,大小为："+file.length());
												String name = (String) MainActivity.map.get("selectCountForPager");

												if (list.size() == images.length){
													String[] strs = new String[list.size()];
													for (int i1 = 0; i1 < list.size(); i1++) {
														strs[i1] = list.get(i1);
														if (list.get(i1).contains(name)){
															selectCountForPager = i1;
															MainActivity.map.remove("selectCountForPager");
														}
													}
													pager.setAdapter(new ImagePagerAdapter(strs,ImagePagerActivity.this));
													pager.setCurrentItem(selectCountForPager);
													if (strs.length<10){
														mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
														mIndicator.setViewPager(pager);
													}
													dialog.dismiss();
												}
											}
											@Override
											public void onError(Throwable e) {
												// TODO 当压缩过去出现问题时调用
												LogUtil.w("压缩失败");
											}
										}).launch();//启动压缩
							}
						});

					}
				}.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}
	}


}