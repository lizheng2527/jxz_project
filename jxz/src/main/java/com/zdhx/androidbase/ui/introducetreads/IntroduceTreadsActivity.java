package com.zdhx.androidbase.ui.introducetreads;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;
import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.introducetreads.compressImg.PictureUtil;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.plugin.SendFile;
import com.zdhx.androidbase.ui.treadssearch.MyVideoThumbLoader;
import com.zdhx.androidbase.ui.treadssearch.UpFileBean;
import com.zdhx.androidbase.ui.treadssearch.VideoShowActivity;
import com.zdhx.androidbase.util.InputTools;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.Tools;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntroduceTreadsActivity extends BaseActivity{
	//显示“附件”文字对象
	private TextView fujianTitle;
	//显示“图片”文字对象
	private TextView imgTitle;
	//图片选择按钮
	private Button imageBtn;
	//文件选择按钮
	private Button fileBtn;
	//用来展示选择的附件列表
	private GridView fileGv;
	//点击选择附件（跳转到自定义文件管理器）
	private Button fujianBT;
	//用来展示文件与图片选择视图
	private PopupWindow mPop;
	//展示选择的附件集合
	private List<File> sendFiles = new ArrayList<File>();

	public static final int MAX_IMG_COUNT = 9; // 选择图数量上限
	//用户选择的图片展示对象（动态）
	private GridView circleGV;
	//图片展示集合（动态）
	private ArrayList<PhotoModel> gridList;
	//上下文
	private Activity context;
	//展示动态图片的适配器
	private ImageGVAdapter adapter;
	//动态发送的文字对象
	private EditText circleET;
	//发送按钮对象
	private Button sendNoticeBT;
	//展示选择的文件集合
	private List<SendFile> files = new ArrayList<SendFile>();
	//动态图片压缩后的存储集合
	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();
	//附件数量显示对象
	private TextView fujianCountTV;
	//跳转到系统文件管理器
	private final int FILECODE = 4564;
	//选择附件的视图（sdcard）
	private View view;
	//附件列表适配器
	private FileGVAdapter adapterGV = new FileGVAdapter();

	private final int VIDEOCODE = 22;

	private Button backBtn;

	private ListView circleLV;

	private TextView videoCountTV;

	private HashMap<String,ParameterValue> map;
	@Override
	protected int getLayoutId() {
		return R.layout.activity_sendnewcircle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		map = new HashMap<>();
		circleGV = (GridView)findViewById(R.id.circleGV);
		circleET = (EditText) findViewById(R.id.circleET);
		fujianBT = (Button) findViewById(R.id.fujianBT);
		imgTitle = (TextView) findViewById(R.id.img_tv);
		fujianCountTV = (TextView) findViewById(R.id.fujianCountTV);
		sendNoticeBT = (Button) findViewById(R.id.sendNoticeBT);
		fileGv = (GridView) findViewById(R.id.fileGv);
		fujianTitle = (TextView) findViewById(R.id.file_tv);
		backBtn = (Button) findViewById(R.id.button1);
		circleLV = (ListView) findViewById(R.id.circleLV);
		fileGv.setAdapter(adapterGV);
		gridList = new ArrayList<PhotoModel>();
		gridList.add(null);//动态集合中“null"代表是“加号”图片
		adapter = new ImageGVAdapter();
		circleGV.setAdapter(adapter);
		videoCountTV = (TextView) findViewById(R.id.videocount_tv);
		videoCountTV.setVisibility(View.GONE);
		listViewAdapter = new ListViewAdapter();
		circleLV.setAdapter(listViewAdapter);
		initPopMenu();
		//添加附件
		fujianBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mPop.isShowing()) {
					InputTools.HideKeyboard(v);
					mPop.setFocusable(false);
					mPop.setOutsideTouchable(true);
					mPop.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
					fujianBT.setSelected(true);
				} else {
					mPop.dismiss();
				}
			}
		});
		//回退
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputTools.HideKeyboard(circleET);
				onBackPressed();
			}
		});

		sendNoticeBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputTools.HideKeyboard(circleET);
				commitTreads();
			}
		});
	}
	int type = 0 ;
	ArrayList<File> imgList = null;
	ArrayList<File> videoList = null;
	Handler handler = new Handler();
	public void commitTreads(){
		ProgressUtil.show(context,"正在上传");
		map.clear();
		String content = circleET.getText().toString();//发表的内容
		if (gridList != null &&gridList.size()>1){
			imgList = new ArrayList();
			type = 2;
			if (gridList.size()<9){
				for (int i = 0; i < gridList.size()-1; i++) {
					File file = new File(gridList.get(i).getOriginalPath());
					imgList.add(file);
				}
			}else{
				for (int i = 0; i < gridList.size(); i++) {
					File file = new File(gridList.get(i).getOriginalPath());
					imgList.add(file);
				}
			}
		}
		if (upFileBeens != null &&upFileBeens.size() != 0){
			videoList = new ArrayList<>();
			for (int i = 0; i < upFileBeens.size(); i++) {
				videoList.add(new File(upFileBeens.get(i).getAbsolutePath()));
			}
			type = 1;
		}

		if (type == 0){//无附件
			if (content == null||content.length() == 0){
				doToast("无上传内容");
				ProgressUtil.hide();
				return;
			}else{
				map.put("content",new ParameterValue(content));
			}
		}else{//有附件
			if (!content.equals("")){
				map.put("content",new ParameterValue(content));
			}
		}

		sendNoticeBT.setTextColor(Color.parseColor("#a0a0a0"));
		new Thread(){
			@Override
			public void run() {
				try {
					sleep(100);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							sendNoticeBT.setTextColor(Color.parseColor("#ffffff"));
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				map.put("type",new ParameterValue(type+""));
				map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentUser().getId()));
				switch (type){
					case 0:
						try {
							map.putAll(ECApplication.getInstance().getLoginUrlMap());
							ZddcUtil.saveCommucationWithoutFile(map);
						} catch (IOException e) {
							e.printStackTrace();
							ProgressUtil.hide();
							doToast("网络异常0！");
						}
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								doToast("上传成功！");
								ProgressUtil.hide();
								MainActivity.map.put("IntroduceTreadsIsTrue","true");
								IntroduceTreadsActivity.this.finish();
							}
						},10);
						break;
					case 1:
						try {
							ZddcUtil.saveCommucation(videoList,ECApplication.getInstance().getLoginUrlMap(),map);
						} catch (IOException e) {
							e.printStackTrace();
							ProgressUtil.hide();
							doToast("网络异常1！");
						}
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								doToast("上传成功！");
								MainActivity.map.put("IntroduceTreadsIsTrue","true");
								ProgressUtil.hide();
								IntroduceTreadsActivity.this.finish();
							}
						},10);
						break;
					case 2:
						try {
							ZddcUtil.saveCommucation(imgList,ECApplication.getInstance().getLoginUrlMap(),map);
						} catch (IOException e) {
							e.printStackTrace();
							ProgressUtil.hide();
							doToast("网络异常2！");
						}
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								doToast("上传成功！");
								MainActivity.map.put("IntroduceTreadsIsTrue","true");
								ProgressUtil.hide();
								IntroduceTreadsActivity.this.finish();
							}
						},10);
						break;
				}
			}

		}).start();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 初始化上传附件的视图（文件、图片）
	 */
	private void initPopMenu() {
		view = context.getLayoutInflater().inflate(
				R.layout.pop_sendnotice_fujian, null);
		if (mPop == null) {
			mPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, true);
		}
		mPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		if (mPop.isShowing()) {
			mPop.dismiss();
		}
		mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				fujianBT.setSelected(false);
			}
		});

		imageBtn = (Button) view.findViewById(R.id.imageBT);
		imageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,PhotoSelectorActivity.class);
				intent.putExtra("canSelectCount", 4);
				startActivityForResult(intent, 0);
			}
		});

		fileBtn = (Button) view.findViewById(R.id.fileBT);
		fileBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context,VideoShowActivity.class), VIDEOCODE);//遍历内存空间
			}
		});
	}
	private MyVideoThumbLoader mVideoThumbLoader;

	private ListViewAdapter listViewAdapter;
	//选择的文件展示的标题集合
	private List<UpFileBean> upFileBeens = new ArrayList<UpFileBean>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 1) {
//			if (data != null) {
//				classTV.setText("发送到：" + data.getStringExtra("className"));
//				departmentId = data.getStringExtra("classId");
//			}
//		}
//		********从视频管理器回来**********************************************************************************************

		if(VIDEOCODE == requestCode) {
			ArrayList<UpFileBean> list = (ArrayList<UpFileBean>) MainActivity.map.get("celectList");
			mVideoThumbLoader = (MyVideoThumbLoader) MainActivity.map.get("mVideoThumbLoader");
			if (list == null){
				return;
			}
			if (mVideoThumbLoader == null){
				return;
			}
			upFileBeens.clear();
			upFileBeens.addAll(list);
			if (upFileBeens.size()>0){
				videoCountTV.setVisibility(View.VISIBLE);
				gridList.clear();
				adapter.notifyDataSetChanged();
				checkSend();
			}
			listViewAdapter.notifyDataSetChanged();
			mPop.dismiss();
			return ;
		}
//		********从相册管理器结束**********************************************************************************************
		/**
		 * 从系统相册回来
		 */
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
						.getSerializable("photos");
				for (int i = 0; i < gridList.size(); i++) {
					if (gridList.get(i) == null) {
						gridList.remove(i);
					}
				}
				for (PhotoModel photoModel : photos) {
					gridList.add(photoModel);
				}
				if (gridList.size() < MAX_IMG_COUNT) {
					gridList.add(null);
				}
				if (gridList == null || gridList.isEmpty())
					return;
				checkSend();
				Compress();
				adapter.notifyDataSetChanged();
				upFileBeens.clear();
				listViewAdapter.notifyDataSetChanged();
				videoCountTV.setVisibility(View.GONE);
			}
		}
	}



	/**
	 * 压缩图片并存放到即将展示的集合中
	 */
	public void Compress() {
		nowBmp.clear();
		sendFiles.clear();
		try {
			for (int i = 0; i < gridList.size(); i++) {
				if (gridList.get(i) != null) {
					File f = new File(gridList.get(i).getOriginalPath());
					File fs = new File(PictureUtil.getAlbumDir(), "small_"
							+ f.getName());
					Bitmap bm = PictureUtil.getSmallBitmap(gridList.get(i)
							.getOriginalPath());
					if (bm != null) {
						FileOutputStream fos = new FileOutputStream(fs);
						bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
						nowBmp.add(bm);
						sendFiles.add(fs);
					}
				} else {
					nowBmp.add(null);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据发动态时，是否选择了图片来判断显示“图片”文字及点击状态
	 */
	public void checkSend() {
		if (gridList.size() > 1
				|| circleET.getEditableText().toString().trim().length() != 0) {
//			sendNoticeBT.setClickable(true);
			imgTitle.setVisibility(View.VISIBLE);
//			sendNoticeBT.setSelected(false);
		} else {
//			sendNoticeBT.setSelected(true);
//			sendNoticeBT.setClickable(false);
			imgTitle.setVisibility(View.GONE);
		}
	}

	/**
	 * 从动态集合、附件集合中移除一个对象
	 * @param position
	 */
	public void remove(int position) {
		gridList.remove(position);
		nowBmp.remove(position);
		sendFiles.remove(position);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adapter != null) {
			adapter = null;
		}
		gridList = null;
		if (adapterGV != null){
			adapterGV = null;
		}
//		if ((bitmap != null) && (!bitmap.isRecycled())) {
//			bitmap.recycle();
//		}
		System.gc();
	}
	/**
	 * 判断文件是否没有添加
	 * @param path
	 * @return
	 */
	private boolean isFileNotHas(String path){
		for (SendFile f : files) {
			if(f.getKind() == SendFile.FILE) {
				if (f.getFile().getAbsolutePath().equals(path)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获取添加附件的数量
	 */
	private void getFileListSize(){
		if(files.size() == 0) {
			fujianCountTV.setVisibility(View.GONE);
			fujianTitle.setVisibility(View.GONE);
		} else {
			fujianCountTV.setVisibility(View.VISIBLE);
			fujianCountTV.setText(files.size()+"");
			fujianTitle.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 展示附件的适配器
	 */
	class FileGVAdapter extends BaseAdapter {

		public FileGVAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return files.size();
		}

		@Override
		public SendFile getItem(int position) {
			return files.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.gv_item_file,null);
				holder.imageGV = (ImageView) convertView.findViewById(R.id.imageGV);
				holder.fileKindIV = (ImageView) convertView.findViewById(R.id.fileKindIV);
				holder.delBT = (ImageView) convertView.findViewById(R.id.delBT);
				holder.fileLay = (LinearLayout) convertView.findViewById(R.id.fileLay);
				holder.imageLay = (RelativeLayout) convertView.findViewById(R.id.imageLay);
				holder.fileNameTV = (TextView) convertView.findViewById(R.id.fileNameTV);
				holder.fileSizeTV = (TextView) convertView.findViewById(R.id.fileSizeTV);
				holder.imageSizeTV = (TextView) convertView.findViewById(R.id.imageSizeTV);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (SendFile.FILE == getItem(position).getKind()) {
				if(Constant.ATTACHMENT_JPG.equals(FileUtils.getExtensionName(getItem(position).getFile().getAbsolutePath())) ||
						Constant.ATTACHMENT_PNG.equals(FileUtils.getExtensionName(getItem(position).getFile().getAbsolutePath())) ||
						Constant.ATTACHMENT_JPEG.equals(FileUtils.getExtensionName(getItem(position).getFile().getAbsolutePath()))||
						Constant.ATTACHMENT_GIF.equals(FileUtils.getExtensionName(getItem(position).getFile().getAbsolutePath()))) {
					holder.fileLay.setVisibility(View.GONE);
					holder.imageLay.setVisibility(View.VISIBLE);
					holder.imageGV.setImageBitmap(loadImageFromLocal(getItem(position).getFile().getAbsolutePath()));
					holder.imageSizeTV.setText(FileUtils.formatFileLength(getItem(position).getFile().length()));
					System.out.println(getItem(position).getFile().getAbsolutePath());
				} else {
					holder.fileLay.setVisibility(View.VISIBLE);
					holder.imageLay.setVisibility(View.GONE);
					holder.fileNameTV.setText(FileUtils.getFilename(getItem(position).getFile().getAbsolutePath()));
					holder.fileSizeTV.setText(FileUtils.formatFileLength(getItem(position).getFile().length()));
					holder.fileKindIV.setImageResource(FileUtils.getFileIcon(FileUtils.getFilename(getItem(position).getFile().getAbsolutePath())));
				}
			} else {
				holder.fileLay.setVisibility(View.VISIBLE);
				holder.imageLay.setVisibility(View.GONE);
				holder.fileNameTV.setText(getItem(position).getAttachment().getName());
				holder.fileSizeTV.setText("原文附件");
				holder.fileKindIV.setImageResource(FileUtils.getFileIcon(getItem(position).getAttachment().getName()));
			}
			addListener(holder, position);
			return convertView;
		}

		private void addListener(Holder holder, final int position) {
			holder.delBT.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					files.remove(position);
					adapter.notifyDataSetChanged();
					getFileListSize();
					Tools.setGridViewHeightBasedOnChildren2(fileGv);
				}
			});
		}

		class Holder {
			private ImageView imageGV,fileKindIV;
			private ImageView delBT;
			private TextView fileNameTV,fileSizeTV,imageSizeTV;
			private LinearLayout fileLay;
			private RelativeLayout imageLay;
		}

		/**
		 * 从本地加载图片
		 *
		 * @param path
		 * @return
		 */
		public Bitmap loadImageFromLocal(String path) {
			return BitmapFactory.decodeFile(path);
		}
	}
	/**
	 * 展示动态图片的适配器
	 */
	class ImageGVAdapter extends BaseAdapter {

		public ImageGVAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return gridList.size();
		}

		@Override
		public Object getItem(int i) {
			return gridList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			Holder holder = null;
			if (view == null) {
				holder = new Holder();
				view = View.inflate(context, R.layout.gv_item_image,
						null);
				holder.imageGV = (ImageView) view
						.findViewById(R.id.imageGV);
				holder.delBT = (ImageView) view.findViewById(R.id.delBT);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			if (gridList.get(position) == null) {
				holder.imageGV.setImageResource(R.drawable.btn_add_pic);
				holder.delBT.setVisibility(View.INVISIBLE);
			} else {
				if (gridList.get(position) != null) {
					holder.imageGV.setImageBitmap(nowBmp.get(position));
					holder.delBT.setVisibility(View.VISIBLE);
				}
			}
			addListener(holder, position);
			return view;
		}

		/**
		 * 添加增加图片和删除图片的点击事件
		 * @param holder
		 * @param position
		 */
		private void addListener(Holder holder, final int position) {

			holder.delBT.setOnClickListener(new OnClickListener() {//删除图片的点击事件

				@Override
				public void onClick(View arg0) {
					if (position == gridList.size() - 1) {

					} else {
						remove(position);
						for (int i = 0; i < gridList.size(); i++) {
							if (gridList.get(i) == null) {
								gridList.remove(i);
							}
						}
						gridList.add(null);
						checkSend();
						adapter.notifyDataSetChanged();
					}
				}
			});
			holder.imageGV.setOnClickListener(new OnClickListener() {//添加文件的点击事件

				@Override
				public void onClick(View view) {

					if (position == gridList.size() - 1) {
						if (gridList.get(gridList.size() - 1) == null) {
							Intent intent = new Intent(context,
									PhotoSelectorActivity.class);
							intent.putExtra("canSelectCount", MAX_IMG_COUNT + 1
									- gridList.size());
							startActivityForResult(intent, 0);
						} else {
							remove(position);
							for (int i = 0; i < gridList.size(); i++) {
								if (gridList.get(i) == null) {
									gridList.remove(i);
								}
							}
							gridList.add(null);
							adapter.notifyDataSetChanged();
						}
					} else {
						List<PhotoModel> nowPhoto = new ArrayList<PhotoModel>();
						nowPhoto.add(gridList.get(position));
						Bundle bundle = new Bundle();
						bundle.putSerializable("photos",
								(Serializable) nowPhoto);
						CommonUtils.launchActivity(context,
								PhotoPreviewActivity.class, bundle);
					}
				}
			});
		}
		class Holder {
			private ImageView imageGV;
			private ImageView delBT;
		}
	}

	class ListViewAdapter extends BaseAdapter{

		public ListViewAdapter() {
			if (upFileBeens == null)
				upFileBeens = new ArrayList<>();
		}

		@Override
		public int getCount() {
			return upFileBeens.size();
		}

		@Override
		public Object getItem(int position) {
			return upFileBeens.get(position);
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
				convertView = View.inflate(context,R.layout.upfile_listview_item,null);
				vh.img = (RoundCornerImageView) convertView.findViewById(R.id.upfile_list_img);
				vh.title = (TextView) convertView.findViewById(R.id.upfile_list_title);
				vh.userName = (TextView) convertView.findViewById(R.id.upfile_list_username);
				vh.delete = (ImageView) convertView.findViewById(R.id.upfile_list_delete);
				vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			if (mVideoThumbLoader !=null){
				Bitmap b = mVideoThumbLoader.getVideoThumbToCache(upFileBeens.get(position).getPath());
				vh.img.setImageBitmap(b);
			}
			vh.title.setText(upFileBeens.get(position).getTitle());
			vh.delete.setImageResource(R.drawable.icon_delete_address);
			vh.userName.setText(ECApplication.getInstance().getCurrentUser().getDisplayName());
			vh.fileSize.setText(upFileBeens.get(position).getFileSize());
			addClick(vh,position);
			return convertView;
		}

		private void addClick(ViewHolder vh, final int position){
			vh.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					upFileBeens.remove(position);
					listViewAdapter.notifyDataSetChanged();
					if (upFileBeens.size() == 0){
						//TODO
						videoCountTV.setVisibility(View.GONE);
					}
				}
			});

		}

		class ViewHolder{
			private RoundCornerImageView img;
			private TextView title;
			private TextView userName;
			private ImageView delete;
			private TextView fileSize;
		}
	}
}
