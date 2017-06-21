package com.zdhx.androidbase.ui.introducetreads;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;
import com.zdhx.androidbase.Constant;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.SystemConst;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.User;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.HomeFragment;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.introducetreads.compressImg.PictureUtil;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.plugin.SendFile;
import com.zdhx.androidbase.ui.treadssearch.MyVideoThumbLoader;
import com.zdhx.androidbase.ui.treadssearch.UpFileBean;
import com.zdhx.androidbase.ui.treadssearch.VideoShowSimpleActivity;
import com.zdhx.androidbase.util.FileUpLoadCallBack;
import com.zdhx.androidbase.util.InputTools;
import com.zdhx.androidbase.util.IntentUtil;
import com.zdhx.androidbase.util.PhoneShareUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RoundCornerImageView;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.Tools;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.out;

public class IntroduceTreadsActivity extends BaseActivity{

	public RadioGroup impNoticeGroup;

	private int isImportant = 0;

	public RadioButton impNoticeRadioBtn1;
	public RadioButton impNoticeRadioBtn2;
	//显示“附件”文字对象
	private TextView fujianTitle;
	//显示“图片”文字对象
	private TextView imgTitle;
	//图片选择按钮
	public Button imageBtn;
	//文件选择按钮
	public Button fileBtn;
	//用来展示选择的附件列表
	private GridView fileGv;
	//点击选择附件（跳转到自定义文件管理器）
	private Button fujianBT;
	//用来展示文件与图片选择视图
	private PopupWindow mPop;
	//展示选择的附件集合
	public List<File> sendFiles = new ArrayList<>();

	public static final int MAX_IMG_COUNT = 9; // 选择图数量上限
	//用户选择的图片展示对象（动态）
	public GridView circleGV;
	//图片展示集合（动态）
	private ArrayList<PhotoModel> gridList;
	//上下文
	private Activity context;
	//展示动态图片的适配器
	private ImageGVAdapter adapter;
	//动态发送的文字对象
	private EditText circleET;
	//发送按钮对象
	public Button sendNoticeBT;
	//展示选择的文件集合
	private List<SendFile> files = new ArrayList<>();
	//动态图片压缩后的存储集合
	private List<Bitmap> nowBmp = new ArrayList<>();
	//附件数量显示对象
	private TextView fujianCountTV;
	//选择附件的视图（sdcard）
	public View view;
	//附件列表适配器
	private FileGVAdapter adapterGV = new FileGVAdapter();

	private final int VIDEOCODE = 22;

	public Button backBtn;

	public ListView circleLV;

	private TextView videoCountTV;

	private HashMap<String,ParameterValue> map;

	public TextView titleTV;

	private String introduceReply = null;

	private HomeFragment fragment;
	private String introduceReplyToCommuncationId;
	public String introduceReplyToUserName;

	public static ArrayList<String> fileNames = new ArrayList<>();
	@Override
	protected int getLayoutId() {
		return R.layout.activity_sendnewcircle;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}



	private String graffPath;
	private String replyId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		dialog = new ECProgressDialog(context);
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
		titleTV = (TextView) findViewById(R.id.editTypeTV);
		impNoticeGroup = (RadioGroup) findViewById(R.id.impNoticeGroup);
		videoCountTV = (TextView) findViewById(R.id.videocount_tv);
		if (ECApplication.getInstance().getUserAuth().equals("no")){
			impNoticeGroup.setVisibility(View.GONE);
		}else{
			impNoticeGroup.setVisibility(View.VISIBLE);
		}
		impNoticeRadioBtn1 = (RadioButton) findViewById(R.id.impNoticeRadioBtn1);
		impNoticeRadioBtn2 = (RadioButton) findViewById(R.id.impNoticeRadioBtn2);
		impNoticeRadioBtn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					isImportant = 0;
				}
			}
		});
		impNoticeRadioBtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					isImportant = 1;
				}
			}
		});
		fileGv.setAdapter(adapterGV);
		gridList = new ArrayList<>();
		gridList.add(null);//动态集合中“null"代表是“加号”图片
		Intent intent = getIntent();
		String path = PhoneShareUtil.shareFormOtherProg(intent,context);
		if (path != null&&!path.equals("")){
			String lastName = FileUtils.getStringEndWith(path);
			if (lastName != null){
				lastName = lastName.toLowerCase();
			}
			impNoticeGroup.setVisibility(View.GONE);
			User user = ECApplication.getInstance().getCurrentUser();
			if (user != null){
				circleET.setText("来自于分享内容");
				ECApplication.getInstance().setLoginUrlMap("sys_auto_authenticate", "true");
				ECApplication.getInstance().setLoginUrlMap("sys_username", user.getLoginName());
				ECApplication.getInstance().setLoginUrlMap("sys_password", user.getPassWord());
				if (lastName != null){
					if (lastName.equals("jpg")||lastName.equals("jpeg")||lastName.equals("png")||lastName.equals("bmp")){
						gridList.clear();
						gridList.add(new PhotoModel(path,true));
						gridList.add(null);
						Compress();
						videoCountTV.setVisibility(View.GONE);
					}else{
						File file = new File(path);
						UpFileBean newBean = new UpFileBean();
						upFileBeens.clear();
						newBean.setFileSize(FileUtils.formatFileLength(file.length()));
						newBean.setIndex(6);
						newBean.setTitle(file.getName());
						newBean.setUserName(ECApplication.getInstance().getCurrentUser().getName());
						newBean.setPath(file.getAbsolutePath());
						newBean.setAbsolutePath(file.getAbsolutePath());
						upFileBeens.add(newBean);
						gridList.clear();
						nowBmp.clear();
						sendFiles.clear();
						videoCountTV.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		adapter = new ImageGVAdapter();
		circleGV.setAdapter(adapter);
		videoCountTV.setVisibility(View.GONE);
		listViewAdapter = new ListViewAdapter();
		circleLV.setAdapter(listViewAdapter);
		introduceReply = (String) MainActivity.map.get("introduceReply");
		MainActivity.map.remove("introduceReply");
		communcationId = (String) MainActivity.map.get("introduceReplyCommuncationId");
		fragment = (HomeFragment) MainActivity.map.get("introduceReplyFragment");
		introduceReplyToCommuncationId = (String)MainActivity.map.get("introduceReplyToCommuncationId");
		introduceReplyToUserName = (String) MainActivity.map.get("introduceReplyToUserName");
//		introduceReplyPosition = (int) MainActivity.map.get("introduceReplyPosition");
		MainActivity.map.remove("introduceReplyCommuncationId");
		MainActivity.map.remove("introduceReplyToCommuncationId");
		MainActivity.map.remove("introduceReplyToUserName");
		MainActivity.map.remove("introduceReplyPosition");
		MainActivity.map.remove("introduceReplyFragment");
		initPopMenu();

		graffPath = (String) MainActivity.map.get("GrafftiActImgPath");
		replyId = (String) MainActivity.map.get("treadsId");

		if (introduceReply == null && graffPath == null && replyId == null){
			fujianBT.setVisibility(View.VISIBLE);
			titleTV.setText("新动态");
			circleET.setHint("这一刻的想法...");
		}
		else if (graffPath != null && replyId != null){
			graffPath = (String) MainActivity.map.remove("GrafftiActImgPath");
			replyId = (String) MainActivity.map.remove("treadsId");
			fujianBT.setVisibility(View.GONE);
			impNoticeGroup.setVisibility(View.GONE);
			titleTV.setText("交流回复");
			circleET.setHint("内容已标注...");
			gridList.clear();
			gridList.add(new PhotoModel(graffPath,true));
			gridList.add(null);
			Compress();
			videoCountTV.setVisibility(View.GONE);
			communcationId = replyId;
		}
		else{
			fujianBT.setVisibility(View.GONE);
			impNoticeGroup.setVisibility(View.GONE);
			titleTV.setText("交流回复");
			if (introduceReplyToUserName != null){
				circleET.setHint("想对"+introduceReplyToUserName+"说点...");
			}else{
				circleET.setHint("说点什么吧...");
			}
		}


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
	private ECProgressDialog dialog ;
	public void commitTreads(){
		dialog.show();
		map.clear();
		final String content = circleET.getText().toString();//发表的内容
		if (gridList != null &&gridList.size()>1){
			imgList = new ArrayList<>();
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
			if (content.length() == 0){
				doToast("无上传内容");
				if (dialog.isShowing()){
					dialog.dismiss();
				}
				return;
			}else{
				map.put("content",new ParameterValue(content));
			}
		}else{//有附件
			if (!content.equals("")){
				map.put("content",new ParameterValue(content));
			}
		}
		//正常发表
		if (introduceReply == null && graffPath == null && replyId == null){
			if (SystemConst.doAccess){
				getHideWebView().loadUrl(ZddcUtil.doAccess(ECApplication.getInstance().getLoginUrlMap()));
			}
			new ProgressThreadWrap(context, new RunnableWrap() {
				@Override
				public void run() {
					map.put("type",new ParameterValue(type+""));
					map.put("status",new ParameterValue(isImportant+""));
					map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentUser().getId()));
					switch (type){
						case 0:
							try {
								dialog.setPressText("正在发送..");
								map.putAll(ECApplication.getInstance().getLoginUrlMap());
								ZddcUtil.saveCommucationWithoutFile(map);
							} catch (IOException e) {
								e.printStackTrace();
								if (dialog.isShowing()){
									dialog.dismiss();
								}
								doToast("网络异常0！");
							}
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									doToast("上传成功！");
									if (dialog.isShowing()){
										dialog.dismiss();
									}
									MainActivity.map.put("IntroduceTreadsIsTrue","true");
									IntroduceTreadsActivity.this.finish();
								}
							},10);
							break;
						case 1:
							try {
								ZddcUtil.saveCommucation(videoList, ECApplication.getInstance().getLoginUrlMap(), map, new FileUpLoadCallBack() {
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
							} catch (IOException e) {
								e.printStackTrace();
								if (dialog.isShowing()){
									dialog.dismiss();
								}
								doToast("网络异常1！");
							}
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									doToast("上传成功！");
									MainActivity.map.put("IntroduceTreadsIsTrue","true");
									if (dialog.isShowing())
										dialog.dismiss();
									IntroduceTreadsActivity.this.finish();
								}
							},10);
							break;
						case 2:
							try {
								ZddcUtil.saveCommucation(imgList, ECApplication.getInstance().getLoginUrlMap(), map, new FileUpLoadCallBack() {
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
							} catch (IOException e) {
								e.printStackTrace();
								if (dialog.isShowing()){
									dialog.dismiss();
								}
								doToast("网络异常2！");
							}
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									doToast("上传成功！");
									MainActivity.map.put("IntroduceTreadsIsTrue","true");
									if (dialog.isShowing()){
										dialog.dismiss();
									}
									IntroduceTreadsActivity.this.finish();
								}
							},10);
							break;
					}
				}

			}).start();
		}else{
			new ProgressThreadWrap(context, new RunnableWrap() {
				@Override
				public void run() {
					map.put("content",new ParameterValue(content));
					if (introduceReplyToCommuncationId != null&&!"".equals(introduceReplyToCommuncationId)){
						map.put("communcationId",new ParameterValue(introduceReplyToCommuncationId));
					}else{
						map.put("communcationId",new ParameterValue(communcationId));
					}
					switch (type){
						//无附件回复
						case 0:
							map.put("uploadFiles",new ParameterValue(new ArrayList<File>()));
							map.put("uploadFileNames",new ParameterValue(""));
							map.putAll(ECApplication.getInstance().getLoginUrlMap());
							try {
								String json = ZddcUtil.doReply(map);
								JSONObject j = new JSONObject(json);
								final String newId = j.getString("id");
								final String userName = j.getString("userName");

								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (introduceReplyToCommuncationId != null&&!"".equals(introduceReplyToCommuncationId)){
											fragment.resetTreadsDatas(introduceReplyToCommuncationId,content, HomeFragment.position,newId,userName,null);
										}else{
											fragment.resetTreadsDatas(communcationId,content, HomeFragment.position,newId,userName,null);
										}
										fragment.setReplyName();
//										ProgressUtil.hide();
										if (dialog.isShowing()){
											dialog.dismiss();
										}
										IntroduceTreadsActivity.this.finish();
									}
								},5);

							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						//有图片
						case 2:
							map.putAll(ECApplication.getInstance().getLoginUrlMap());
							try {
								String json = ZddcUtil.doReplyWithFiles(imgList, ECApplication.getInstance().getLoginUrlMap(), map, new FileUpLoadCallBack() {
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
								JSONObject j = new JSONObject(json);
								final String newId = j.getString("id");
								final String userName = j.getString("userName");

								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										fragment.resetTreadsDatas(communcationId,content, HomeFragment.position,newId,userName,imgList);
										if (dialog.isShowing()){
											dialog.dismiss();
										}
										IntroduceTreadsActivity.this.finish();
									}
								},5);

							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
					}
				}
			}).start();
		}

	}
	private String communcationId = null;
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 初始化上传附件的视图（视频、图片）
	 */
	private void initPopMenu() {

		view = View.inflate(context,R.layout.pop_sendnotice_fujian,null);
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
				startActivityForResult(new Intent(context, VideoShowSimpleActivity.class), VIDEOCODE);//遍历内存空间
			}
		});
	}
	private MyVideoThumbLoader mVideoThumbLoader;

	private ListViewAdapter listViewAdapter;
	//选择的文件展示的标题集合
	private List<UpFileBean> upFileBeens = new ArrayList<>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

//		********从视频管理器回来**********************************************************************************************

		if(VIDEOCODE == requestCode) {
			ArrayList<UpFileBean> list = (ArrayList<UpFileBean>) MainActivity.map.get("celectList");
			MainActivity.map.remove("celectList");
			mVideoThumbLoader = (MyVideoThumbLoader) MainActivity.map.get("mVideoThumbLoader");
			MainActivity.map.remove("mVideoThumbLoader");
			if (list == null){
				return;
			}
			if (mVideoThumbLoader == null){
				return;
			}
			//发送互动交流只能发送一个视频
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
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
						.getSerializable("photos");
				if (photos == null){
					return;
				}
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
		if (gridList.size() > 1 || circleET.getEditableText().toString().trim().length() != 0) {
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
	 * @param position = 删除的下标
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
	 * 获取添加附件的数量
	 */
	private void getFileListSize(){
		if(files.size() == 0) {
			fujianCountTV.setVisibility(View.GONE);
			fujianTitle.setVisibility(View.GONE);
		} else {
			fujianCountTV.setVisibility(View.VISIBLE);
			String text = files.size()+"";
			fujianCountTV.setText(text);
			fujianTitle.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 展示附件的适配器
	 */
	private class FileGVAdapter extends BaseAdapter {

		FileGVAdapter() {
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
			Holder holder ;
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
					out.println(getItem(position).getFile().getAbsolutePath());
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
		 * @param path = 路径
		 * @return 路径
		 */
		private Bitmap loadImageFromLocal(String path) {
			return BitmapFactory.decodeFile(path);
		}
	}
	/**
	 * 展示动态图片的适配器
	 */
	private class ImageGVAdapter extends BaseAdapter {

		ImageGVAdapter() {
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
			Holder holder ;
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
		 * @param holder = 点击事件封装的viewholder
		 * @param position = 下标
		 */
		private void addListener(Holder holder, final int position) {

			holder.delBT.setOnClickListener(new OnClickListener() {//删除图片的点击事件

				@Override
				public void onClick(View arg0) {
					if (position != gridList.size() - 1) {

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
							if (introduceReply == null){
								intent.putExtra("canSelectCount", MAX_IMG_COUNT + 1
										- gridList.size());
							}else{
								intent.putExtra("canSelectCount", 3 + 1
										- gridList.size());
							}
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
						List<PhotoModel> nowPhoto = new ArrayList<>();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
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
			if (mVideoThumbLoader !=null){
				Bitmap b = mVideoThumbLoader.getVideoThumbToCache(upFileBeens.get(position).getPath());
				if (b == null){
					mVideoThumbLoader.showThumbByAsynctack(upFileBeens.get(position).getAbsolutePath(), vh.img);
				}else{
					vh.img.setImageBitmap(b);
				}
			}else{
				MyVideoThumbLoader mVideoThumbLoader = MyVideoThumbLoader.getMyVideoThumbLoader();
				mVideoThumbLoader.showThumbByAsynctack(upFileBeens.get(position).getAbsolutePath(), vh.img);
			}
			vh.title.setText(upFileBeens.get(position).getTitle());
			vh.delete.setImageResource(R.drawable.icon_delete_address);
			vh.userName.setText(ECApplication.getInstance().getCurrentUser().getDisplayName());
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
							listViewAdapter.notifyDataSetChanged();
							if (upFileBeens.size() == 0){
								videoCountTV.setVisibility(View.GONE);
								gridList.add(null);
								adapter.notifyDataSetChanged();
							}
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

			vh.preview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(IntentUtil.getVideoFileIntent(upFileBeens.get(position).getAbsolutePath()));
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
	private ECAlertDialog buildAlert = null;
	private void upDateFileName(final ListViewAdapter.ViewHolder vh,final int position) {
		buildAlert = ECAlertDialog.buildAlert(this,R.string.title, null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (buildAlert.getWindow() == null){
					ToastUtil.showMessage("系统错误...");
					return;
				}
				final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
				if (name.equals("")){
					doToast("标题不能为空！");
				}else{
					//选中要修改的文件地址
					String selectUrl = upFileBeens.get(position).getAbsolutePath();
					File file = new File(selectUrl);
					String oldPath = file.getPath();
					String lastStr = FileUtils.getExtensionName(file.getName());
					String newFileUrl = file.getAbsolutePath().replace(file.getName(),name)+"."+lastStr;
					File newFile = new File(newFileUrl);
					if (fileNames.contains(newFile.getName())){
						doToast("该文件名已存在！");
						return;
					}
					boolean isSuccess = file.renameTo(newFile);
					if (isSuccess){
						UpFileBean newBean = new UpFileBean();
						newBean.setFileSize(FileUtils.formatFileLength(newFile.length()));
						newBean.setIndex(upFileBeens.get(position).getIndex());
						newBean.setTitle(newFile.getName());
						newBean.setUserName(ECApplication.getInstance().getCurrentUser().getName());
						newBean.setPath(oldPath);
						newBean.setAbsolutePath(newFile.getAbsolutePath());
						upFileBeens.remove(position);
						upFileBeens.add(newBean);
						dealwithMediaScanIntentData(newFile.getAbsolutePath());
						listViewAdapter.notifyDataSetChanged();
						MyVideoThumbLoader.getMyVideoThumbLoader().showThumbByAsynctack(newBean.getAbsolutePath(),vh.img);
					}else{
						doToast("修改失败");
					}
				}
			}
		});
		buildAlert.setTitle("修改文件名称");
		buildAlert.setCanceledOnTouchOutside(false);
		buildAlert.setContentView(R.layout.config_dcaddress_dialog);
		String server = upFileBeens.get(position).getTitle();
		if (buildAlert.getWindow() == null){
			ToastUtil.showMessage("系统错误...");
			return;
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

	public void dealwithMediaScanIntentData(String path)
	{
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(new File(path));
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}
}
