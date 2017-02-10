package com.zdhx.androidbase.ui.treadssearch;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class MyVideoThumbLoader {
	private ImageView imgView;
	private String path;
	//创建cache
	private LruCache<String, Bitmap> lruCache;
	public static MyVideoThumbLoader myVideoThumbLoader;

	@SuppressLint("NewApi")
	public MyVideoThumbLoader(){
		int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取最大的运行内存
		int maxSize = maxMemory /4;//拿到缓存的内存大小
		lruCache = new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//这个方法会在每次存入缓存的时候调用
				return value.getByteCount();
			}
		};

	}

	public static MyVideoThumbLoader getMyVideoThumbLoader(){
		if (myVideoThumbLoader == null)
			myVideoThumbLoader = new MyVideoThumbLoader();
		return myVideoThumbLoader;
	}
	public void addVideoThumbToCache(String path,Bitmap bitmap){
		if(getVideoThumbToCache(path) == null){
			//当前地址没有缓存，就添加,并且路径和bitmap都不为空时
			if (path != null&&bitmap != null){
				lruCache.put(path, bitmap);
			}
		}
	}
	public Bitmap getVideoThumbToCache(String path){
		if (path != null){
			return lruCache.get(path);
		}

		return null;


	}
	public void showThumbByAsynctack(String path,ImageView imgview){


		if(getVideoThumbToCache(path) == null){
			//异步加载
			new MyBobAsynctack(imgview, path).execute(path);
		}else{
			imgview.setImageBitmap(getVideoThumbToCache(path));
		}

	}



	public class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
		private ImageView imgView;
		private String path;

		public MyBobAsynctack(ImageView imageView,String path) {
			this.imgView = imageView;
			this.path = path;
		}

		@Override
		protected Bitmap doInBackground(String... params) {//这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
			//这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
			Bitmap bitmap = createVideoThumbnail(params[0], 70, 50, MediaStore.Video.Thumbnails.MICRO_KIND);
//			//加入缓存中
			if(getVideoThumbToCache(params[0]) == null){
				addVideoThumbToCache(path, bitmap);
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imgView.getTag() != null){
				if(imgView.getTag().equals(path)){//通过 Tag可以绑定 图片地址和 imageView，这是解决Listview加载图片错位的解决办法之一
					if (bitmap != null)
						imgView.setImageBitmap(bitmap);
				}
			}
		}
	}

	private Bitmap createVideoThumbnail(String param, int width, int height, int microKind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(param, microKind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}