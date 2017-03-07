package com.zdhx.androidbase.ui.treadssearch;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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
import java.util.HashMap;

import static com.zdhx.androidbase.ui.introducetreads.IntroduceTreadsActivity.fileNames;

public class VideoShowSimpleActivity extends BaseActivity {
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
				paths = FileUtils.getSpecificTypeOfFile(VideoShowSimpleActivity.this,new String[]{".mp4",".avi",".flv",".rmvb",".3gp",".amv",".dmv"});
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
						bean.setCheckTag(false);
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
							lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									if (celectList.contains(fileList.get(position))){
										celectList.clear();
									}else{
										celectList.clear();
										celectList.add(fileList.get(position));
									}
									adapter.notifyDataSetChanged();
								}
							});
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
		VideoShowSimpleActivity.this.finish();
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
				convertView = View.inflate(VideoShowSimpleActivity.this,R.layout.upfile_video_simple_item,null);
				vh.img = (ImageView) convertView.findViewById(R.id.upfile_list_img);
				vh.title = (TextView) convertView.findViewById(R.id.upfile_list_title);
				vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
				vh.checkBox = (RadioButton) convertView.findViewById(R.id.upfile_list_checkbox);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			String path = fileList.get(position).getPath();
			vh.img.setTag(path);//绑定imageview
			vh.img.setImageResource(R.drawable.video_head);
			mVideoThumbLoader.showThumbByAsynctack(path, vh.img);
			LogUtil.w(path);
			vh.title.setText(fileList.get(position).getTitle());
			vh.fileSize.setText(fileList.get(position).getFileSize());
			if (celectList.contains(fileList.get(position))){
				vh.checkBox.setChecked(true);
			}else{
				vh.checkBox.setChecked(false);
			}
			addClick(vh,position);
			return convertView;
		}

		private void addClick(ViewHolder vh, final int position){
			vh.checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (celectList.contains(fileList.get(position))){
						celectList.clear();
					}else{
						celectList.clear();
						celectList.add(fileList.get(position));
					}
					notifyDataSetChanged();
				}
			});
		}
		class ViewHolder{
			private ImageView img;
			private TextView title;
			private TextView fileSize;
			private RadioButton checkBox;
		}

	}

	private HashMap<UpFileBean,String> map = new HashMap<>();
}
