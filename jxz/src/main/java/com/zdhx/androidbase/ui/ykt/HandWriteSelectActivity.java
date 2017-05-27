package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.ImagePagerActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treadssearch.UpFileActivity;
import com.zdhx.androidbase.ui.treadstree.RequestWithCacheGet;
import com.zdhx.androidbase.util.RequestWithCache;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;


public class HandWriteSelectActivity extends BaseActivity {

	private Context context;

	private TextView emptyView;

	private GridView gridView;

	private ArrayList<WriteAnswerResults> list;

	private String questionName;

	private HashMap<String,WriteAnswerResults> isSelectMap = new HashMap<>();

	private int phoneWidth;
	private int phoneHeight;

	private String id;
	private String originalImageAttachmentQuestionId;

	private TextView commitHandWrite;

	private ImageView backImg;

	private HandWriteBean bean;

	private ECProgressDialog dialog ;
	@Override
	protected int getLayoutId() {
		return R.layout.handwriteselection;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		mRequestWithCache = new RequestWithCacheGet(context);
		emptyView = (TextView) findViewById(R.id.ShowHandWriteEmpty);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		phoneWidth = metric.widthPixels;     // 屏幕宽度（像素）
		phoneHeight = metric.heightPixels;   // 屏幕高度（像素）
		gridView = (GridView) findViewById(R.id.handWriteImgShow);
		dialog = new ECProgressDialog(context,"正在获取...");
		dialog.show();
		backImg = (ImageView) findViewById(R.id.activity_selectHandWrite_goback);
		commitHandWrite = (TextView) findViewById(R.id.commitHandWrite);
		if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
			bean = (HandWriteBean) MainActivity.map.get("handWriteAnswer");
			if (bean != null){
				MainActivity.map.remove("handWriteAnswer");
				id = bean.getQuestionId();
				questionName = bean.getQuestionName();
				originalImageAttachmentQuestionId = bean.getOriginalImageAttachmentQuestionId();
				getQuestionAnswer();
			}
		}
		else{
			if (list != null){
				list.clear();
			}
			list = (ArrayList<WriteAnswerResults>) MainActivity.map.get("handWriteAnswer");
			if (list != null&&list.size()>0){
				getWriteAnswerForStudent();
			}
		}
		backImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		commitHandWrite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commit();
			}
		});


	}

	/**
	 * 学生调用获取手写笔记答案
	 */
	private void getWriteAnswerForStudent() {
		gridView.setAdapter(new ShowGridAdapter());
		gridView.setEmptyView(emptyView);
		dialog.dismiss();
	}

	private void commit() {
		if (isSelectMap.size() == 0){
			doToast("请选择笔记");
			return;
		}
		MainActivity.map.put("HandWriteFilesMap",isSelectMap);
		startActivity(new Intent(context, UpFileActivity.class));
		HandWriteSelectActivity.this.finish();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private RequestWithCacheGet mRequestWithCache;
	private void  getQuestionAnswer(){
		String json = null;
		HashMap<String, ParameterValue> map = new HashMap<>();
		map.putAll(ECApplication.getInstance().getLoginUrlMap());
		map.put("questionId",new ParameterValue(id));
		try {
			json = mRequestWithCache.getRseponse(ZddcUtil.getWriteAnswerResultsJsonWithQuestion(map), new RequestWithCacheGet.RequestListener() {

				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCache.NOT_OUTOFDATE)) {
						list = new Gson().fromJson(response, new TypeToken<List<WriteAnswerResults>>(){}.getType());
						ArrayList<WriteAnswerResults> listNow = new ArrayList<WriteAnswerResults>();
						if(bean != null&&bean.getPreviewQuestionImg() != null){
							WriteAnswerResults w = new WriteAnswerResults();
							w.setOriginalImageAttachmentId(bean.getOriginalImageAttachmentQuestionId());
							w.setOriginalImageQuestion(bean.getOriginaQuestionlImg());
							w.setPreviewImageQuestion(bean.getPreviewQuestionImg());
							w.setPreviewImage(bean.getPreviewQuestionImg());
							w.setOriginalImage(bean.getOriginaQuestionlImg());
							w.setStudentName("题目");
							w.setSelectde(false);
							w.setQuestion(true);
							w.setQuestionName(bean.getQuestionName());
							listNow.add(w);
							listNow.addAll(list);
							list.clear();
							list = listNow;
						}
						if (list == null||list.size() == 0){
							list = new ArrayList<WriteAnswerResults>();
						}
						dialog.dismiss();
						gridView.setAdapter(new ShowGridAdapter());
						gridView.setEmptyView(emptyView);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					dialog.dismiss();
				}
			});
		} catch (IOException e) {
			ToastUtil.showMessage("连接超时..");
			dialog.dismiss();
			e.printStackTrace();
		}

		if ((json != null && !json.equals(RequestWithCache.NO_DATA))) {
			list = new Gson().fromJson(json, new TypeToken<List<WriteAnswerResults>>(){}.getType());
			if (json != null && !json.equals(RequestWithCache.NOT_OUTOFDATE)) {
				list = new Gson().fromJson(json, new TypeToken<List<WriteAnswerResults>>(){}.getType());
				ArrayList<WriteAnswerResults> listNow = new ArrayList<WriteAnswerResults>();
				if(bean != null&&bean.getPreviewQuestionImg() != null){
					WriteAnswerResults w = new WriteAnswerResults();
					w.setOriginalImageAttachmentId(bean.getOriginalImageAttachmentQuestionId());
					w.setOriginalImageQuestion(bean.getOriginaQuestionlImg());
					w.setPreviewImageQuestion(bean.getPreviewQuestionImg());
					w.setPreviewImage(bean.getPreviewQuestionImg());
					w.setOriginalImage(bean.getOriginaQuestionlImg());
					w.setStudentName("题目");
					w.setSelectde(false);
					w.setQuestion(true);
					w.setQuestionName(bean.getQuestionName());
					listNow.add(w);
					listNow.addAll(list);
					list.clear();
					list = listNow;
				}
				if (list == null||list.size() == 0){
					list = new ArrayList<WriteAnswerResults>();
				}
				gridView.setAdapter(new ShowGridAdapter());
				gridView.setEmptyView(emptyView);
				dialog.dismiss();
			}
		}
	}

	public class ShowGridAdapter extends BaseAdapter {


		public ShowGridAdapter(){

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int i) {
			return list.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {
			ViewHolder vh = null;
			if (view == null){
				vh = new ViewHolder();
				view = View.inflate(context,R.layout.handwriteselection_griditem,null);
				view.setTag(vh);
			}else{
				vh = (ViewHolder) view.getTag();
			}
			vh.img = (SimpleDraweeView) view.findViewById(R.id.handWritesimpleImage);
//			vh.img.setImageResource(R.drawable.logo_wechat);
			vh.checkBox = (CheckBox) view.findViewById(R.id.handWriteCheckBox);
			vh.name = (TextView) view.findViewById(R.id.handwritename);

			if (list.get(i).isSelectde()){
				vh.checkBox.setChecked(true);
			}else{
				vh.checkBox.setChecked(false);
			}
			if (i == 0){
				if (list.get(i).getPreviewImageQuestion() != null && !list.get(i).getPreviewImageQuestion().equals("")){
					String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getPreviewImageQuestion(), ECApplication.getInstance().getLoginUrlMap());
					vh.img.setImageURI(url);
					vh.name.setText("题目");
				}else{
					String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getPreviewImage(), ECApplication.getInstance().getLoginUrlMap());
					vh.img.setImageURI(url);
					vh.name.setText(list.get(i).getStudentName());
				}
			}else{
				if (list.get(i).getPreviewImage() != null && !list.get(i).getPreviewImage().equals("")){
					String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getPreviewImage(), ECApplication.getInstance().getLoginUrlMap());
					vh.img.setImageURI(url);
				}
				vh.name.setText(list.get(i).getStudentName());
			}
			Log.w("YKT",list.get(i).isSelectde()+"");
			addClick(vh,i);
			return view;
		}

		private void addClick(ViewHolder vh, final int i) {
			vh.img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ImagePagerActivity.class);
					if (i == 0){
						if (list.get(i).getOriginalImageQuestion() != null && !list.get(i).getOriginalImageQuestion().equals("")){
							intent.putExtra("images", new String[]{ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImageQuestion(), ECApplication.getInstance().getLoginUrlMap())});
							intent.putExtra("image_index",0);
							intent.putExtra("imgNames",new String[]{"题目.jpg"});
						}else{
							intent.putExtra("images", new String[]{ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(), ECApplication.getInstance().getLoginUrlMap())});
							intent.putExtra("image_index",0);
							intent.putExtra("imgNames",new String[]{list.get(i).getStudentName()+".jpg"});
						}
					}else{
						intent.putExtra("images", new String[]{ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(), ECApplication.getInstance().getLoginUrlMap())});
						intent.putExtra("image_index",0);
						intent.putExtra("imgNames",new String[]{list.get(i).getStudentName()+".jpg"});
					}
					startActivity(intent);
				}
			});
			vh.checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url;
					if (i == 0){
						if (list.get(i).getOriginalImageQuestion() != null && !list.get(i).getOriginalImageQuestion().equals("")){
							url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImageQuestion(), ECApplication.getInstance().getLoginUrlMap());
						}else{
							url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(), ECApplication.getInstance().getLoginUrlMap());
						}
					}else{
						url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+list.get(i).getOriginalImage(), ECApplication.getInstance().getLoginUrlMap());
					}
					if (!isSelectMap.containsKey(url)){
						list.get(i).setSelectde(true);
						list.get(i).setQuestionName(questionName);
						isSelectMap.put(url,list.get(i));
					}else{
						list.get(i).setSelectde(false);
						isSelectMap.remove(url);
					}
					notifyDataSetChanged();
				}
			});
		}

		class ViewHolder{
			private SimpleDraweeView img;
			private CheckBox checkBox;
			private TextView name;
		}
	}
}



