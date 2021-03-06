package com.photoselector.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.photoselector.controller.AlbumController;
import com.photoselector.model.AlbumModel;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity.OnLocalAlbumListener;
import com.photoselector.ui.PhotoSelectorActivity.OnLocalReccentListener;

import java.util.List;

@SuppressLint("HandlerLeak")
public class PhotoSelectorDomain {

	private AlbumController albumController;

	public PhotoSelectorDomain(Context context) {
		albumController = new AlbumController(context);
	}

	public void getReccent(final OnLocalReccentListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onPhotoLoaded((List<PhotoModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<PhotoModel> photos = albumController.getCurrent();
//				if (photos != null){
//					for (int i = 0; i < photos.size(); i++) {
//						File file = new File(photos.get(i).getOriginalPath());
//						Log.w("d","遍历出来的图片路径为："+photos.get(i).getOriginalPath()+"长度为"+file.length());
//						if (file.length() == 0){
//							photos.remove(i);
//							Log.w("d","长度为0，删除："+photos.get(i).getOriginalPath());
//						}
//					}
//					for (int i = 0; i < photos.size(); i++) {
//						File file = new File(photos.get(i).getOriginalPath());
//						Log.w("d","即将展示的结果："+photos.get(i).getOriginalPath()+"长度为"+file.length());
//					}
//				}
				Message msg = new Message();
				msg.obj = photos;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/** 获取相册列表 */
	public void updateAlbum(final OnLocalAlbumListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onAlbumLoaded((List<AlbumModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<AlbumModel> albums = albumController.getAlbums();
				Message msg = new Message();
				msg.obj = albums;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/** 获取单个相册下的所有照片信息 */
	public void getAlbum(final String name, final OnLocalReccentListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onPhotoLoaded((List<PhotoModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<PhotoModel> photos = albumController.getAlbum(name);
				Message msg = new Message();
				msg.obj = photos;
				handler.sendMessage(msg);
			}
		}).start();
	}

}
