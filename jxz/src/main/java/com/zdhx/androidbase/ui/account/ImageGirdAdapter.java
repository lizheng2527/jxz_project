package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ImageUrlBean;
import com.zdhx.androidbase.util.DensityUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;
import com.zdhx.androidbase.view.SimpleDraweeViewCompressed.FrescoUtil;

import java.util.ArrayList;


public class ImageGirdAdapter extends BaseAdapter {
	private Activity context;
	private ArrayList<ImageUrlBean> list;
	private ImageLoader loader;
	private HomeFragment frag;
	private String[] urls;
	private String[] names;
	private int listPosition;
	private TreadsListViewAdapter adapter;
	public ImageGirdAdapter(Activity context, ArrayList<ImageUrlBean> list, HomeFragment frag,int position,TreadsListViewAdapter adapter) {
		super();
		this.context = context;
		this.list = list;
		loader = new ImageLoader(context);
		this.frag = frag;
		urls = new String[list.size()];
		names = new String[list.size()];
		this.listPosition = position;
		this.adapter = adapter;
	}
	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, R.layout.gv_item_image, null);
			holder.imageGV = (SimpleDraweeView) convertView.findViewById(R.id.imageGV);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		Uri uri = Uri.parse(list.get(position).getShowUrl());
		FrescoUtil.load(uri,holder.imageGV, DensityUtil.dip2px(30f), DensityUtil.dip2px(30f));
//		holder.imageGV.setImageURI(uri);
		holder.imageGV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < list.size(); i++) {
					urls[i] = list.get(i).getDownLoadUrl();
					names[i] = list.get(i).getName();
					LogUtil.w("传入用于下载的图片名称："+names[i]);
				}
				Intent intent = new Intent(context, ImagePagerActivity.class);
				intent.putExtra("images", urls);
				intent.putExtra("image_index",position);
				intent.putExtra("imgNames",names);
				frag.startActivity(intent);
			}
		});
		return convertView;
	}
	
	class Holder{
		private SimpleDraweeView imageGV;
	}


}
