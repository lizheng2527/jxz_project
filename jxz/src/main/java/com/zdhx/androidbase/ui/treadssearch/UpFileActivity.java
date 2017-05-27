package com.zdhx.androidbase.ui.treadssearch;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;
import com.zdhx.androidbase.Constant;
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
import com.zdhx.androidbase.ui.ykt.BlackboardWrite;
import com.zdhx.androidbase.ui.ykt.CourseWare;
import com.zdhx.androidbase.ui.ykt.WriteAnswerResults;
import com.zdhx.androidbase.util.FileUpLoadCallBack;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.StringUtil;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.MarqueeTextView;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.zdhx.androidbase.ui.MainActivity.map;
import static com.zdhx.androidbase.ui.plugin.FileUtils.getStringEndWith;

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
	private ListViewAdapterForYKT listViewAdapterForYKT;
	private AdapterForBlackBorad adapterForBlackBorad;
	private AdapterForTeachCourse adapterForTeachCourse;

	//选择的文件集合
	private List<PhotoModel> paths = new ArrayList<PhotoModel>();
	//选择的文件展示的标题集合
	private List<UpFileBean> upFileBeens = new ArrayList<UpFileBean>();

	private UpFileBean bean;

	private Handler handler = new Handler();

	private RadioButton radioButton1,radioButton2;

	private MyVideoThumbLoader mVideoThumbLoader;

	private ImageView backImg;

	private TextView upFileTree;
	//从章节选择树选取的对象
	private TreeBean treeBean;

	private Boolean isOpenTag = true;

	private HashMap<String,WriteAnswerResults> handWriteFilesMap;
	private BlackboardWrite blackBorad;
	private CourseWare courseWare;
	private ArrayList<WriteAnswerResults> listForYKT;
	private ArrayList<BlackboardWrite> listForBlackBorad;
	private ArrayList<CourseWare> listForTeachCourse;

	private TextView commit;

	private View seleteFile;


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
		seleteFile = findViewById(R.id.seleteFile);


		upFileTree.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				map.put("SearchWorkTreeCode","upFileTree");
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

		handWriteFilesMap = (HashMap<String, WriteAnswerResults>) map.get("HandWriteFilesMap");
		blackBorad = (BlackboardWrite) MainActivity.map.get("BlackBorad");
		courseWare = (CourseWare) MainActivity.map.get("TeachCourse");

		//上传手写笔记
		if (handWriteFilesMap != null&&handWriteFilesMap.size()>0){
			seleteFile.setVisibility(View.GONE);
			listForYKT = new ArrayList<>();
			MainActivity.map.remove("HandWriteFilesMap");
			Iterator<Map.Entry<String, WriteAnswerResults>> it = handWriteFilesMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, WriteAnswerResults> entry = it.next();
				listForYKT.add(entry.getValue());
			}
			listViewAdapterForYKT = new ListViewAdapterForYKT();
			lv.setAdapter(listViewAdapterForYKT);
			//上传板书记录
		}else if (blackBorad != null){
			seleteFile.setVisibility(View.GONE);
			MainActivity.map.remove("BlackBorad");
			listForBlackBorad = new ArrayList<>();
			listForBlackBorad.add(blackBorad);
			adapterForBlackBorad = new AdapterForBlackBorad();
			lv.setAdapter(adapterForBlackBorad);
			//上传课件
		}else if (courseWare != null){
			seleteFile.setVisibility(View.GONE);
			MainActivity.map.remove("TeachCourse");
			listForTeachCourse = new ArrayList<>();
			listForTeachCourse.add(courseWare);
			adapterForTeachCourse = new AdapterForTeachCourse();
			lv.setAdapter(adapterForTeachCourse);
		}
		//上传本地资源
		else{
			seleteFile.setVisibility(View.VISIBLE);
			listAdapter = new ListViewAdapter();
			lv.setAdapter(listAdapter);
		}
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
				final String isOpen;
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

				if (upFileBeens == null || upFileBeens.size() == 0){
					if (listForYKT == null ||listForYKT.size() == 0){
						if (listForBlackBorad == null || listForBlackBorad.size() == 0){
							if (listForTeachCourse == null || listForTeachCourse.size() == 0){
								doToast("请选择文件..");
								dialog.dismiss();
								return;
							}else{
								//上传课件
								new ProgressThreadWrap(context, new RunnableWrap() {
									@Override
									public void run() {
										ArrayList<Bean> beans = new ArrayList<Bean>();
										for (int i = 0; i < listForTeachCourse.size(); i++) {
											Bean bean = new Bean();
											bean.setId(listForTeachCourse.get(i).getAttachmentId());
											if (listForTeachCourse.get(i).getReName() != null){
												bean.setName(listForTeachCourse.get(i).getReName());
											}else{
												bean.setName(listForTeachCourse.get(i).getFileName());
											}
											beans.add(bean);
										}
										String json = new Gson().toJson(beans);
										Log.w("YKT" , json);
										map.put("uploadFilePaths",new ParameterValue(json));
										map.putAll(ECApplication.getInstance().getLoginUrlMap());
										try {
											final String isOver = ZddcUtil.saveChapterResourceForYKT(map);
											Log.w("YKT",isOver);
											handler.postDelayed(new Runnable() {
												@Override
												public void run() {
													dialog.dismiss();
													if(isOver.contains("ok")){
														ToastUtil.showMessage("上传完成");
														UpFileActivity.this.finish();
													}else{
														ToastUtil.showMessage("上传失败");
													}
												}
											},5);
										} catch (IOException e) {
											handler.postDelayed(new Runnable() {
												@Override
												public void run() {
													dialog.dismiss();
													ToastUtil.showMessage("上传失败!");

												}
											},5);
											e.printStackTrace();
										}
									}
								}).start();
							}
						}else{
							// 上传板书
							new ProgressThreadWrap(context, new RunnableWrap() {
								@Override
								public void run() {
									ArrayList<Bean> beans = new ArrayList<Bean>();
									for (int i = 0; i < listForBlackBorad.size(); i++) {
										Bean bean = new Bean();
										bean.setId(listForBlackBorad.get(i).getOriginalImageAttachmentId());
										if (listForBlackBorad.get(i).getReName() != null){
											bean.setName(listForBlackBorad.get(i).getReName()+".jpg");
										}else{
											bean.setName(listForBlackBorad.get(i).getName()+".jpg");
										}
										beans.add(bean);
									}
									String json = new Gson().toJson(beans);
									Log.w("YKT" , json);
									map.put("uploadFilePaths",new ParameterValue(json));
									map.putAll(ECApplication.getInstance().getLoginUrlMap());
									try {
										final String isOver = ZddcUtil.saveChapterResourceForYKT(map);
										Log.w("YKT",isOver);
										handler.postDelayed(new Runnable() {
											@Override
											public void run() {
												dialog.dismiss();
												if(isOver.contains("ok")){
													ToastUtil.showMessage("上传完成");
													UpFileActivity.this.finish();
												}else{
													ToastUtil.showMessage("上传失败");
												}
											}
										},5);
									} catch (IOException e) {
										handler.postDelayed(new Runnable() {
											@Override
											public void run() {
												dialog.dismiss();
												ToastUtil.showMessage("上传失败!");
											}
										},5);
										e.printStackTrace();
									}
								}
							}).start();
						}
					}else {
						//上传手写笔记
						new ProgressThreadWrap(context, new RunnableWrap() {
							@Override
							public void run() {
								ArrayList<Bean> beans = new ArrayList<Bean>();
								for (int i = 0; i < listForYKT.size(); i++) {
									Bean bean = new Bean();
									bean.setId(listForYKT.get(i).getOriginalImageAttachmentId());
									if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
										if (!StringUtil.isBlank(listForYKT.get(i).getReName())){
											bean.setName(listForYKT.get(i).getReName()+".jpg");
										}else{
											bean.setName(listForYKT.get(i).getQuestionName()+" "+listForYKT.get(i).getStudentName()+".jpg");
										}
									}else{
										if (!StringUtil.isBlank(listForYKT.get(i).getReName())){
											bean.setName(listForYKT.get(i).getReName()+".jpg");
										}else{
											if (!StringUtil.isBlank(listForYKT.get(i).getStudentName())){
												bean.setName(listForYKT.get(i).getTitle()+" "+listForYKT.get(i).getStudentName()+".jpg");
											}else{
												bean.setName(listForYKT.get(i).getTitle()+".jpg");
											}
										}
									}
									beans.add(bean);
								}
								String json = new Gson().toJson(beans);
								Log.w("YKT" , json);
								map.put("uploadFilePaths",new ParameterValue(json));
								map.putAll(ECApplication.getInstance().getLoginUrlMap());
								try {
									final String isOver = ZddcUtil.saveChapterResourceForYKT(map);
									Log.w("YKT",isOver);
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											if(isOver.contains("ok")){
												ToastUtil.showMessage("上传完成");
												UpFileActivity.this.finish();
											}else{
												ToastUtil.showMessage("上传失败");
											}
										}
									},5);
								} catch (IOException e) {
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											dialog.dismiss();
											ToastUtil.showMessage("上传失败!");
										}
									},5);
									e.printStackTrace();
								}
							}
						}).start();
					}
				} else {
					for (int i = 0; i < upFileBeens.size(); i++) {
						File file = new File(upFileBeens.get(i).getAbsolutePath());
						files.add(file);
					}
					new ProgressThreadWrap(context, new RunnableWrap() {
						@Override
						public void run() {
							try {
								ZddcUtil.saveChapterResource(files, ECApplication.getInstance().getLoginUrlMap(), map, new FileUpLoadCallBack() {
									@Override
									public void upLoadProgress(final int fileCount, final int currentIndex, int currentProgress, final int allProgress) {

										handler.postDelayed(new Runnable() {
											@Override
											public void run() {
												if(100 == allProgress) {
													dialog.setPressText("附件上传完成,正在发送");
												} else {
													dialog.setPressText("正在上传附件(" + (currentIndex + 1) + "/"+ fileCount + ") 总进度:" + allProgress + "%");
												}
											}

										},1);

									}
								});
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										MainActivity.map.put("UpFileActivityTag","true");
										if (ECApplication.getInstance().getAddress().equals("http://117.117.217.19/dc")){
											getHideWebView().loadUrl(ZddcUtil.doAccess(ECApplication.getInstance().getLoginUrlMap()));
										}
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
			}
		});
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	private void upDateFileName(final ListViewAdapter.ViewHolder vh,final int position) {
		buildAlert = ECAlertDialog.buildAlert(this,R.string.title, null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
				if (name == null||name.equals("")){
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
					if (name.equals("")){
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

	public void dealwithMediaScanIntentData(String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(new File(path));
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

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
			if (paths !=null){
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
			ArrayList<UpFileBean> list = (ArrayList<UpFileBean>) map.get("celectList");
			mVideoThumbLoader = (MyVideoThumbLoader) map.get("mVideoThumbLoader");
			map.clear();
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
			treeBean = (TreeBean) map.get("ScroTreeBean");
			if (treeBean != null){
				map.remove("ScroTreeBean");
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

	private class ListViewAdapter extends BaseAdapter{

		ListViewAdapter() {
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
	private class ListViewAdapterForYKT extends BaseAdapter{

		public ListViewAdapterForYKT() {
			if (listForYKT == null)
				listForYKT = new ArrayList<>();
		}

		@Override
		public int getCount() {
			return listForYKT.size();
		}

		@Override
		public Object getItem(int position) {
			return listForYKT.get(position);
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
				convertView = View.inflate(context,R.layout.upfile_listview_ykt_item,null);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			vh.img = (SimpleDraweeView) convertView.findViewById(R.id.upfile_list_img);
			vh.title = (MarqueeTextView) convertView.findViewById(R.id.upfile_list_title);
			vh.userName = (TextView) convertView.findViewById(R.id.upfile_list_username);
			vh.delete = (ImageView) convertView.findViewById(R.id.upfile_list_delete);
			vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
			vh.rename = (ImageView) convertView.findViewById(R.id.upfile_list_rename);
			vh.preview = (ImageView) convertView.findViewById(R.id.upfile_list_preview);
			if (listForYKT.get(position).isQuestion()){
				vh.img.setImageURI(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+listForYKT.get(position).getPreviewImageQuestion(),ECApplication.getInstance().getLoginUrlMap()));
			}else{
				vh.img.setImageURI(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+listForYKT.get(position).getPreviewImage(),ECApplication.getInstance().getLoginUrlMap()));
			}
			vh.preview.setVisibility(View.GONE);
			if (listForYKT.get(position).getReName() != null &&!listForYKT.get(position).getReName().equals("")){
				vh.title.setText(listForYKT.get(position).getReName());
			}else{
				if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
					if(listForYKT.get(position).getTitle() != null){
						vh.title.setText(listForYKT.get(position).getQuestionName()+""+listForYKT.get(position).getTitle());
					}else{
						vh.title.setText(listForYKT.get(position).getQuestionName()+""+listForYKT.get(position).getStudentName());
					}
				}else{
					if (listForYKT.get(position).getStudentName() == null){
						vh.title.setText(listForYKT.get(position).getTitle());
					}else{
						vh.title.setText(listForYKT.get(position).getTitle()+""+listForYKT.get(position).getStudentName());
					}
				}
			}
			vh.delete.setImageResource(R.drawable.icon_delete_address);
			vh.userName.setText(ECApplication.getInstance().getUserForYKT().getName());
			vh.fileSize.setVisibility(View.GONE);
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
							listForYKT.remove(position);
							listViewAdapterForYKT.notifyDataSetChanged();
						}
					}).show();

				}
			});
			vh.rename.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//修改名称
					upDateFileNameForYKT(vh,position);
				}
			});
		}
		class ViewHolder{
			private SimpleDraweeView img;
			private MarqueeTextView title;
			private TextView userName;
			private ImageView delete;
			private TextView fileSize;
			private ImageView rename;
			private ImageView preview;
		}

	}
	private class AdapterForBlackBorad extends BaseAdapter{

		private AdapterForBlackBorad() {
			if (listForBlackBorad == null)
				listForBlackBorad = new ArrayList<>();
		}

		@Override
		public int getCount() {
			return listForBlackBorad.size();
		}

		@Override
		public Object getItem(int position) {
			return listForBlackBorad.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolderForBlackBorad vh = null;
			if (convertView == null){
				vh = new ViewHolderForBlackBorad();
				convertView = View.inflate(context,R.layout.upfile_listview_ykt_item,null);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolderForBlackBorad) convertView.getTag();
			}
			vh.img = (SimpleDraweeView) convertView.findViewById(R.id.upfile_list_img);
			vh.img.setImageURI(ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+listForBlackBorad.get(position).getPreviewImage(),ECApplication.getInstance().getLoginUrlMap()));

			vh.title = (MarqueeTextView) convertView.findViewById(R.id.upfile_list_title);

			if (listForBlackBorad.get(position).getReName() != null &&!listForBlackBorad.get(position).getReName().equals("")){
				vh.title.setText(listForBlackBorad.get(position).getReName());
			}else{
				vh.title.setText(listForBlackBorad.get(position).getName());
			}

			vh.userName = (TextView) convertView.findViewById(R.id.upfile_list_username);
			vh.userName.setText(ECApplication.getInstance().getUserForYKT().getName());

			vh.delete = (ImageView) convertView.findViewById(R.id.upfile_list_delete);
			vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
			vh.rename = (ImageView) convertView.findViewById(R.id.upfile_list_rename);
			vh.preview = (ImageView) convertView.findViewById(R.id.upfile_list_preview);
			vh.preview.setVisibility(View.GONE);
			vh.delete.setImageResource(R.drawable.icon_delete_address);
			vh.fileSize.setVisibility(View.GONE);
			addClick(vh,position);
			return convertView;
		}

		private void addClick(final ViewHolderForBlackBorad vh, final int position){
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
							listForBlackBorad.remove(position);
							adapterForBlackBorad.notifyDataSetChanged();
						}
					}).show();

				}
			});
			vh.rename.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//修改名称
					upDateFileNameForYKT1(vh,position);
				}
			});
		}
		class ViewHolderForBlackBorad{
			private SimpleDraweeView img;
			private MarqueeTextView title;
			private TextView userName;
			private ImageView delete;
			private TextView fileSize;
			private ImageView rename;
			private ImageView preview;
		}

	}
	private class AdapterForTeachCourse extends BaseAdapter{

		AdapterForTeachCourse() {
			if (listForTeachCourse == null)
				listForTeachCourse = new ArrayList<>();
		}

		@Override
		public int getCount() {
			return listForTeachCourse.size();
		}

		@Override
		public Object getItem(int position) {
			return listForTeachCourse.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolderForTeachCourse vh = null;
			if (convertView == null){
				vh = new ViewHolderForTeachCourse();
				convertView = View.inflate(context,R.layout.upfile_listview_ykt_item,null);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolderForTeachCourse) convertView.getTag();
			}
			vh.img = (SimpleDraweeView) convertView.findViewById(R.id.upfile_list_img);
			setHeadImg(vh,position);

			vh.title = (TextView) convertView.findViewById(R.id.upfile_list_title);

			if (listForTeachCourse.get(position).getReName() != null &&!"".equals(listForTeachCourse.get(position).getReName())){
				vh.title.setText(listForTeachCourse.get(position).getReName());
			}else{
				vh.title.setText(listForTeachCourse.get(position).getFileName());
			}

			vh.userName = (TextView) convertView.findViewById(R.id.upfile_list_username);
			vh.userName.setText(ECApplication.getInstance().getUserForYKT().getName());

			vh.delete = (ImageView) convertView.findViewById(R.id.upfile_list_delete);
			vh.fileSize = (TextView) convertView.findViewById(R.id.upfile_list_size);
			vh.rename = (ImageView) convertView.findViewById(R.id.upfile_list_rename);
			vh.preview = (ImageView) convertView.findViewById(R.id.upfile_list_preview);
			vh.preview.setVisibility(View.GONE);
			vh.delete.setImageResource(R.drawable.icon_delete_address);
			vh.fileSize.setVisibility(View.GONE);
			addClick(vh,position);
			return convertView;
		}

		private void setHeadImg(final ViewHolderForTeachCourse vh, final int i){
			String lastName = getStringEndWith(listForTeachCourse.get(i).getFileName());
			if (lastName == null){
				vh.img.setImageResource(R.drawable.other);
				listForTeachCourse.get(i).setResourceStyle("8");
			}else{
				if(lastName.equals(Constant.ATTACHMENT_DOC)||lastName.equals(Constant.ATTACHMENT_DOCX)){
					vh.img.setImageResource(R.drawable.word);
					listForTeachCourse.get(i).setResourceStyle("1");
				}
				else if (lastName.equals(Constant.ATTACHMENT_3GP)
						||lastName.equals(Constant.ATTACHMENT_AVI)
						||lastName.equals(Constant.ATTACHMENT_MPG)
						||lastName.equals(Constant.ATTACHMENT_MOV)
						||lastName.equals(Constant.ATTACHMENT_MP3)
						||lastName.equals(Constant.ATTACHMENT_MP4)
						||lastName.equals(Constant.ATTACHMENT_SWF)
						||lastName.equals(Constant.ATTACHMENT_FLV)){
					vh.img.setImageResource(R.drawable.video);
					listForTeachCourse.get(i).setResourceStyle("6");
				}

				else if (lastName.equals(Constant.ATTACHMENT_PPT)||lastName.equals(Constant.ATTACHMENT_PPTX)){
					vh.img.setImageResource(R.drawable.ppt);
					listForTeachCourse.get(i).setResourceStyle("2");
				}

				else if (lastName.equals(Constant.ATTACHMENT_XLS)||lastName.equals(Constant.ATTACHMENT_XLSX)){
					vh.img.setImageResource(R.drawable.excel);
					listForTeachCourse.get(i).setResourceStyle("3");
				}

				else if (lastName.equals(Constant.ATTACHMENT_PDF)){
					vh.img.setImageResource(R.drawable.pdf_b);
					listForTeachCourse.get(i).setResourceStyle("8");
				}

				else if(lastName.equals(Constant.ATTACHMENT_RAR)){
					vh.img.setImageResource(R.drawable.zip);
					listForTeachCourse.get(i).setResourceStyle("7");
				}

				else{
					vh.img.setImageResource(R.drawable.other);
					listForTeachCourse.get(i).setResourceStyle("8");
				}
			}
		}

		private void addClick(final ViewHolderForTeachCourse vh, final int position){
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
							listForTeachCourse.remove(position);
							adapterForTeachCourse.notifyDataSetChanged();
						}
					}).show();

				}
			});
			vh.rename.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//修改名称
					upDateFileNameForYKT2(vh,position);
				}
			});
		}
		class ViewHolderForTeachCourse{
			private SimpleDraweeView img;
			private TextView title;
			private TextView userName;
			private ImageView delete;
			private TextView fileSize;
			private ImageView rename;
			private ImageView preview;
		}

	}
	private void upDateFileNameForYKT(final ListViewAdapterForYKT.ViewHolder vh,final int position) {

		buildAlert = ECAlertDialog.buildAlert(this,R.string.title, null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
				if (name == null||name.equals("")){
					ToastUtil.showMessage("不能为空!");
					return;
				}
				listForYKT.get(position).setReName(name);
				listViewAdapterForYKT.notifyDataSetChanged();
			}
		});
		buildAlert.setTitle("修改文件名称");
		buildAlert.setCanceledOnTouchOutside(false);
		buildAlert.setContentView(R.layout.config_dcaddress_dialog);
		String title = "";
		String server;
		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
			if (listForYKT.get(position).getReName() != null &&!listForYKT.get(position).getReName().equals("")){
				server = listForYKT.get(position).getReName();
			}else{
				title = listForYKT.get(position).getQuestionName()+listForYKT.get(position).getStudentName();
				server = title;
			}
		}else{
			if (listForYKT.get(position).getStudentName() == null){
				server = listForYKT.get(position).getTitle();
			}else{
				server = listForYKT.get(position).getTitle()+""+listForYKT.get(position).getStudentName();
			}
		}
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
	private void upDateFileNameForYKT1(final AdapterForBlackBorad.ViewHolderForBlackBorad vh, final int position) {

		buildAlert = ECAlertDialog.buildAlert(this,R.string.title, null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
				if (name == null||name.equals("")){
					ToastUtil.showMessage("不能为空!");
					return;
				}
				listForBlackBorad.get(position).setReName(name);
				adapterForBlackBorad.notifyDataSetChanged();
			}
		});
		buildAlert.setTitle("修改文件名称");
		buildAlert.setCanceledOnTouchOutside(false);
		buildAlert.setContentView(R.layout.config_dcaddress_dialog);
		String server;
		if (listForBlackBorad.get(position).getReName() != null &&!listForBlackBorad.get(position).getReName().equals("")){
			server = listForBlackBorad.get(position).getReName();
		}else{
			server = listForBlackBorad.get(position).getName();
		}
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
	private String lastName;
	private void upDateFileNameForYKT2(final AdapterForTeachCourse.ViewHolderForTeachCourse vh, final int position) {

//		final String finalLastName = lastName;
		lastName = "";
		buildAlert = ECAlertDialog.buildAlert(this,R.string.title, null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
				if (name == null||name.equals("")){
					ToastUtil.showMessage("不能为空!");
					return;
				}
				listForTeachCourse.get(position).setReName(name+ lastName);
				adapterForTeachCourse.notifyDataSetChanged();
			}
		});
		buildAlert.setTitle("修改文件名称");
		buildAlert.setCanceledOnTouchOutside(false);
		buildAlert.setContentView(R.layout.config_dcaddress_dialog);
		String server;
		if (listForTeachCourse.get(position).getReName() != null &&!listForTeachCourse.get(position).getReName().equals("")){
			server = listForTeachCourse.get(position).getReName();
		}else{
			server = listForTeachCourse.get(position).getFileName();
		}

		final EditText editText = (EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText));
		TextView delectTV = (TextView) buildAlert.getWindow().findViewById(R.id.delectTV);
		delectTV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editText.setText("");
			}
		});
		if (!"".equals(server)) {
			editText.setText(server);
			lastName = "."+FileUtils.getStringEndWith(server);
		}
		if(!buildAlert.isShowing()){
			buildAlert.show();
			editText.setSelection(editText.getText().length());
			editText.setSelectAllOnFocus(true);
		}
	}

	private class Bean{
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		private String name;
		private String id;
	}
}
