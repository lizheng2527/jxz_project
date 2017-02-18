package com.zdhx.androidbase.ui.treadssearch;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;

import java.io.File;
import java.util.ArrayList;

import static com.zdhx.androidbase.ui.introducetreads.IntroduceTreadsActivity.fileNames;

public class VideoShowActivity extends BaseActivity {
	private ListView lv;

	private TextView commit;

	private ImageView back;

	private ArrayList<String> paths;

	private ArrayList<UpFileBean> fileList;

	private MyAdapter adapter;

	private ArrayList<UpFileBean> celectList = new ArrayList<>();

	private String[] fileSmallHeadUrls;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_showvideo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		ProgressUtil.show(this,"正在加载...");
		lv = (ListView) findViewById(R.id.video_list);
		commit = (TextView) findViewById(R.id.video_commit);
		back = (ImageView) findViewById(R.id.video_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		final Handler handler = new Handler();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				paths = FileUtils.getSpecificTypeOfFile(VideoShowActivity.this,new String[]{".mp4",".avi",".flv",".rmvb",".3gp",".amv",".dmv"});
				if (paths != null &&paths.size() > 1){
					fileList = new ArrayList<>();
					for (int i = 0; i < paths.size(); i++) {
						File file = new File(paths.get(i));
						LogUtil.e("加载视频的路径："+file.getAbsolutePath());
						UpFileBean bean = new UpFileBean();
						bean.setFileSize("("+FileUtils.formatFileLength(file.length())+")");
						bean.setTitle(file.getName());
						bean.setUserName(ECApplication.getInstance().getCurrentUser().getName());
						bean.setAbsolutePath(paths.get(i));
						bean.setPath(paths.get(i));
						if (file.length() != 0){
							fileList.add(bean);
							if (!fileNames.contains(file.getName()))
								fileNames.add(file.getName());
						}
					}
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						adapter = new MyAdapter();
						if (fileList != null&&fileList.size()>0){
							lv.setAdapter(adapter);
						}
						ProgressUtil.hide();
					}
				},100);
			}
		}).start();


		commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFinish();
			}
		});


	}

	public void onFinish(){
		MainActivity.map.put("celectList",celectList);
		MainActivity.map.put("mVideoThumbLoader",mVideoThumbLoader);
		VideoShowActivity.this.finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private MyVideoThumbLoader mVideoThumbLoader;
	class MyAdapter extends BaseAdapter{

		public MyAdapter() {
			mVideoThumbLoader = MyVideoThumbLoader.getMyVideoThumbLoader();
		}

		@Override
		public int getCount() {
			return fileList.size();
		}

		@Override
		public Object getItem(int position) {
			return fileList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null){
				vh = new ViewHolder();
				convertView = View.inflate(VideoShowActivity.this,R.layout.upfile_video_item,null);
				vh.img = (ImageView) convertView.findViewById(R.id.upfile_list_img);
				vh.title = (TextView) convertView.findViewById(R.id.upfile_list_title);
				vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
				vh.checkBox = (CheckBox) convertView.findViewById(R.id.upfile_list_checkbox);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			String path = fileList.get(position).getPath();
			File file = new File(path);
			vh.img.setTag(path);//绑定imageview

			vh.img.setImageResource(R.drawable.video_head);
			mVideoThumbLoader.showThumbByAsynctack(path, vh.img);
			LogUtil.w(path);
			vh.title.setText(fileList.get(position).getTitle());
			vh.fileSize.setText(fileList.get(position).getFileSize());
			addClick(vh,position);
			return convertView;
		}

		private void addClick(ViewHolder vh, final int position){
			vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
						fileList.get(position).setIndex(6);
						celectList.add(fileList.get(position));
					}else{
						celectList.remove(fileList.get(position));
					}
				}
			});
		}
		class ViewHolder{
			private ImageView img;
			private TextView title;
			private TextView fileSize;
			private CheckBox checkBox;
		}

	}
}
