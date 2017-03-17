package com.zdhx.androidbase.ui.treadssearch;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.ImagePagerActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.introducetreads.compressImg.PictureUtil;
import com.zdhx.androidbase.ui.plugin.FileExplorerActivity;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.treadstree.ScroTreeActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpFileActivity extends BaseActivity {

	private ECAlertDialog buildAlert = null;

	//上传图片请求码
	private final int IMAGEBTNCODE = 4;
	//上传文件请求码
	private final int FILEBTNCODE = 5;
	//上传视频请求码
	private final int VIDEOBTNCODE = 6;
	//选择章节树的请求码
	private final int SCROTREECODE = 7;

	//图片选择按钮
	private Button imageBtn;
	//文件选择按钮
	private Button fileBtn;

	private Button videoBtn;

	private ListView lv;

	private Context context;

	private ListViewAdapter listAdapter;

	//选择的文件集合
	private List<PhotoModel> paths = new ArrayList<PhotoModel>();
	//选择的文件展示的标题集合
	private List<UpFileBean> upFileBeens = new ArrayList<UpFileBean>();

	private UpFileBean bean;

	private Handler handler = new Handler();

	private RadioGroup radioGroup;

	private RadioButton radioButton1,radioButton2;

	private MyVideoThumbLoader mVideoThumbLoader;

	private ImageView backImg;

	private TextView upFileTree;
	//从章节选择树选取的对象
	private TreeBean treeBean;

	private Boolean isOpenTag = true;

	private TextView commit;
	//当修改名称时，判定是否存在该名字
	public static ArrayList<String> imgForRename = new ArrayList<>();
	public static ArrayList<String> videoForRename = new ArrayList<>();
	public static ArrayList<String> fileForRename = new ArrayList<>();

	@Override
	protected int getLayoutId() {
		return R.layout.activity_upfile;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		backImg = (ImageView) findViewById(R.id.search_treads_back);
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		imageBtn = (Button) findViewById(R.id.imageBT);
		fileBtn = (Button) findViewById(R.id.fileBT);
		videoBtn = (Button) findViewById(R.id.viedoBT);
		lv = (ListView) findViewById(R.id.upfile_list);
		commit = (TextView) findViewById(R.id.commit);
		upFileTree = (TextView) findViewById(R.id.upfiletree);

		upFileTree.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.map.put("SearchWorkTreeCode","upFileTree");
				startActivityForResult(new Intent(context, ScroTreeActivity.class),SCROTREECODE);
			}
		});
		radioButton1 = (RadioButton) findViewById(R.id.radiobutton1);
		radioButton2 = (RadioButton) findViewById(R.id.radiobutton2);
		if (!ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			radioButton1.setVisibility(View.INVISIBLE);
			radioButton2.setVisibility(View.INVISIBLE);
		}
		radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					isOpenTag = true;
				}
			}
		});
		radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					isOpenTag = false;
				}
			}
		});


		listAdapter = new ListViewAdapter();
		lv.setAdapter(listAdapter);
		imageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,PhotoSelectorActivity.class);
				intent.putExtra("canSelectCount", 4);
				startActivityForResult(intent, IMAGEBTNCODE);
			}
		});
		fileBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(context, FileExplorerActivity.class), FILEBTNCODE);//遍历内存空间
			}
		});

		videoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(context, VideoShowActivity.class), VIDEOBTNCODE);//遍历内存空间展示视频
			}
		});

		commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ECProgressDialog dialog = new ECProgressDialog(context,"正在上传..");
				dialog.show();
				final HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
				final List<File> files = new ArrayList<File>();
				String isOpen;
				if (treeBean ==null){
					doToast("请选择章节..");
					dialog.dismiss();
					return ;
				}
				String chapterId = treeBean.getId();
				map.put("chapterId",new ParameterValue(chapterId));
				String userId = ECApplication.getInstance().getCurrentUser().getId();
				map.put("userId",new ParameterValue(userId));
				String upLoadType = "1";
				map.put("upLoadType",new ParameterValue(upLoadType));
				if (isOpenTag){//公开
					isOpen = "0";
				}else{//不公开
					isOpen = "1";
				}
				map.put("isOpen",new ParameterValue(isOpen));
				if (upFileBeens.size() == 0){
					doToast("请选择文件..");
					dialog.dismiss();
					return;
				}
				for (int i = 0; i < upFileBeens.size(); i++) {
					File file = new File(upFileBeens.get(i).getAbsolutePath());
					files.add(file);
				}
				new ProgressThreadWrap(context, new RunnableWrap() {
					@Override
					public void run() {
						try {
							ZddcUtil.saveChapterResource(files,ECApplication.getInstance().getLoginUrlMap(),map);
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									dialog.dismiss();
									MainActivity.map.put("UpFileActivityTag","true");
									getHideWebView().loadUrl(ZddcUtil.doAccess(ECApplication.getInstance().getLoginUrlMap()));
									UpFileActivity.this.finish();
								}
							},5);
						} catch (IOException e) {
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									dialog.dismiss();
								}
							},5);
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	//
	private void upDateFileName(final ListViewAdapter.ViewHolder vh,final int position) {
		buildAlert = ECAlertDialog.buildAlert(this,R.string.title, null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
				if (name == null&&name.equals("")){
					doToast("标题不能为空！");
					return;
				}else{
					//选中要修改的文件地址
					String selectUrl = upFileBeens.get(position).getAbsolutePath();
					Bitmap bitmap = null;
					if (upFileBeens.get(position).getIndex() == IMAGEBTNCODE){
						bitmap = upFileBeens.get(position).getBitmap();
					}
					File file = new File(selectUrl);
					LogUtil.w("选中文件路径："+file.getAbsolutePath());
					String oldPath = file.getPath();
					String lastStr = FileUtils.getExtensionName(file.getName());
					if (name != null){
						String newFileUrl = file.getAbsolutePath().replace(file.getName(),name)+"."+lastStr;
						File newFile = new File(newFileUrl);
						boolean isSuccess = file.renameTo(newFile);
						if (isSuccess){
							UpFileBean newBean = new UpFileBean();
							newBean.setFileSize(FileUtils.formatFileLength(newFile.length()));
							newBean.setIndex(upFileBeens.get(position).getIndex());
							newBean.setTitle(newFile.getName());
							newBean.setUserName(ECApplication.getInstance().getCurrentUser().getName());
							newBean.setPath(oldPath);
							newBean.setAbsolutePath(newFile.getAbsolutePath());
							if (upFileBeens.get(position).getIndex() == IMAGEBTNCODE){
								if (bitmap != null)
									newBean.setBitmap(bitmap);
							}
							upFileBeens.remove(position);
							upFileBeens.add(newBean);
							dealwithMediaScanIntentData(newFile.getAbsolutePath());
							listAdapter.notifyDataSetChanged();
						}else{
							doToast("修改失败");
							return;
						}
					}else{
						doToast("请输入新名称..");
						return;
					}
					LogUtil.w("选中文件的后缀："+lastStr);
				}
			}
		});
		buildAlert.setTitle("修改文件名称");
		buildAlert.setCanceledOnTouchOutside(false);
		buildAlert.setContentView(R.layout.config_dcaddress_dialog);
		String server = upFileBeens.get(position).getTitle();
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
	}

	public void dealwithMediaScanIntentData(String path)
	{
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(new File(path));
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	//	private List<File> sendFiles = new ArrayList<File>();
//	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IMAGEBTNCODE&&data !=null){
			paths = PhotoSelectorActivity.selected;
			if (paths != null&&paths.size()>0)
				Compress(paths);

		}
		if (requestCode == FILEBTNCODE&&data !=null){

			String path = data.getStringExtra("choosed_file_path");
			File file = new File(path);
			if (paths !=null||paths.size()>0){
				bean = new UpFileBean();
				bean.setFileSize("("+FileUtils.formatFileLength(file.length())+")");
				bean.setIndex(FILEBTNCODE);
				bean.setTitle(file.getName());
				bean.setUserName(ECApplication.getInstance().getCurrentUser().getName());
				bean.setPath(file.getPath());
				bean.setAbsolutePath(file.getAbsolutePath());
				upFileBeens.add(bean);
			}
			listAdapter.notifyDataSetChanged();
		}
		if (requestCode == VIDEOBTNCODE){

			ArrayList<UpFileBean> list = (ArrayList<UpFileBean>) MainActivity.map.get("celectList");
			mVideoThumbLoader = (MyVideoThumbLoader) MainActivity.map.get("mVideoThumbLoader");
			MainActivity.map.clear();
			if (list == null){
				return;
			}
			if (mVideoThumbLoader == null){
				return;
			}
			upFileBeens.addAll(list);
			listAdapter.notifyDataSetChanged();
		}

		if (requestCode == SCROTREECODE){
			treeBean = (TreeBean) MainActivity.map.get("ScroTreeBean");
			if (treeBean != null){
				MainActivity.map.remove("ScroTreeBean");
				String lable = treeBean.getLabel();
				upFileTree.setText(lable);
			}
		}

	}

	/**
	 * 压缩图片并存放到即将展示的集合中
	 */
	public void Compress(List<PhotoModel> paths) {
		try {
			for (int i = 0; i < paths.size(); i++) {
				if (paths.get(i) != null) {
					File f = new File(paths.get(i).getOriginalPath());
					File fs = new File(PictureUtil.getAlbumDir(), "small_"
							+ f.getName());
					Bitmap bm = PictureUtil.getSmallBitmap(paths.get(i)
							.getOriginalPath());
					if (bm != null) {
						FileOutputStream fos = new FileOutputStream(fs);
						bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
						bean = new UpFileBean();
						bean.setBitmap(bm);
						bean.setFileSize("("+FileUtils.formatFileLength(fs.length())+")");
						bean.setIndex(IMAGEBTNCODE);
						bean.setTitle(fs.getName());
						bean.setUserName(ECApplication.getInstance().getCurrentUser().getName());
						bean.setPath(fs.getPath());
						bean.setAbsolutePath(fs.getAbsolutePath());
						upFileBeens.add(bean);
					}
				}
			}
			listAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null){
				vh = new ViewHolder();
				convertView = View.inflate(context,R.layout.upfile_listview_item,null);
				vh.img = (RoundCornerImageView) convertView.findViewById(R.id.upfile_list_img);
				vh.title = (TextView) convertView.findViewById(R.id.upfile_list_title);
				vh.userName = (TextView) convertView.findViewById(R.id.upfile_list_username);
				vh.delete = (ImageView) convertView.findViewById(R.id.upfile_list_delete);
				vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
				vh.rename = (ImageView) convertView.findViewById(R.id.upfile_list_rename);
				vh.preview = (ImageView) convertView.findViewById(R.id.upfile_list_preview);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			switch(upFileBeens.get(position).getIndex()){
				case IMAGEBTNCODE:
					vh.img.setImageBitmap(upFileBeens.get(position).getBitmap());
					vh.img.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//点击头像查看图片
							if (upFileBeens.get(position).getIndex() == IMAGEBTNCODE){
								Intent intent = new Intent(context, ImagePagerActivity.class);
								intent.putExtra("images", new String[]{upFileBeens.get(position).getAbsolutePath()});
								intent.putExtra("image_index",0);
								startActivity(intent);
							}
						}
					});
					break;
				case FILEBTNCODE:
					vh.img.setImageResource(R.drawable.icon_file);
					break;
				case VIDEOBTNCODE:
					if (mVideoThumbLoader !=null){
						Bitmap b = mVideoThumbLoader.getVideoThumbToCache(upFileBeens.get(position).getPath());
						if (b == null){
							mVideoThumbLoader.showThumbByAsynctack(upFileBeens.get(position).getAbsolutePath(), vh.img);
						}else{
							vh.img.setImageBitmap(b);
						}
					}
					vh.img.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//点击查看视频
							startActivity(IntentUtil.getVideoFileIntent(upFileBeens.get(position).getAbsolutePath()));
						}
					});
					break;
			}
			vh.preview.setVisibility(View.GONE);
			vh.title.setText(upFileBeens.get(position).getTitle());
			vh.delete.setImageResource(R.drawable.icon_delete_address);
			vh.userName.setText(upFileBeens.get(position).getUserName());
			vh.fileSize.setText(upFileBeens.get(position).getFileSize());
			addClick(vh,position);
			return convertView;
		}

		private void addClick(final ViewHolder vh, final int position){
			vh.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ECAlertDialog.buildAlert(context, "是否删除本条信息？", "取消", "确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							upFileBeens.remove(position);
							listAdapter.notifyDataSetChanged();
						}
					}).show();

				}
			});
			vh.rename.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					upDateFileName(vh,position);
				}
			});

		}
		class ViewHolder{
			private RoundCornerImageView img;
			private TextView title;
			private TextView userName;
			private ImageView delete;
			private TextView fileSize;
			private ImageView rename;
			private ImageView preview;
		}
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图
	 * 此方法有两点好处：
	 *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	 *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	 *        用这个工具生成的图像不会被拉伸。
	 * @param imagePath 图像的路径
	 * @param width 指定输出图像的宽度
	 * @param height 指定输出图像的高度
	 * @return 生成的缩略图
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

}
