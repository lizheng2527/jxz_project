package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ImageUrlBean;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.lazyImageLoader.cache.ImageLoader;

import java.util.ArrayList;


public class ImageGirdAdapter extends BaseAdapter {
	private Activity context;
	private ArrayList<ImageUrlBean> list;
	private ImageLoader loader;
	private HomeFragment frag;
	private String[] urls;
	public ImageGirdAdapter(Activity context, ArrayList<ImageUrlBean> list, HomeFragment frag) {
		super();
		this.context = context;
		this.list = list;
		loader = new ImageLoader(context);
		this.frag = frag;
		urls = new String[list.size()];
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
			holder.imageGV = (ImageView) convertView.findViewById(R.id.imageGV);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		loader.DisplayImage(list.get(position).getShowUrl(),holder.imageGV,false);
//		LogUtil.e("duozhang:"+list.get(position));
		holder.imageGV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < list.size(); i++) {
					urls[i] = list.get(i).getDownLoadUrl();
				}
				Intent intent = new Intent(context, ImagePagerActivity.class);
				//图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra("images", urls);
				intent.putExtra("image_index",position);
				frag.startActivity(intent);
			}
		});
		return convertView;
	}
	
	class Holder{
		private ImageView imageGV;
	}


}
