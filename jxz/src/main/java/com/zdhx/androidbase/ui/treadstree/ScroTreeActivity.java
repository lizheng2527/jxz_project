package com.zdhx.androidbase.ui.treadstree;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.ScroTreeBean;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean1;
import com.zdhx.androidbase.ui.treelistview.utils.Node;
import com.zdhx.androidbase.ui.treelistview.utils.adapter.TreeListViewAdapter;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ZddcUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScroTreeActivity extends BaseActivity{
	private ImageView back;
	private Handler handler = new Handler();
	private SimpleTreeListViewAdapterForScro adapter ;

	private List<TreeBean> list = new ArrayList<>();//每次加载树的集合
	public static List<TreeBean> listForWork = new ArrayList<>();//存储工作平台的树集合
	public static List<TreeBean> listForScro = new ArrayList<>();//存储积分的树集合
	private TreeBean treeBean;

	private ListView lv;

	private Context context;

	private TextView commit;

	public static HashMap<String,String> positionMap = new HashMap<>();
	public static HashMap<String,String> positionMapForSearchWork = new HashMap<>();
	public static HashMap<String,String> positionMapForUpFile = new HashMap<>();

	private static boolean firstTag = false;

	private TextView title;

	private String className;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_scro_tree;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		back = (ImageView) findViewById(R.id.activity_scrotree_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		context = this;
		ProgressUtil.show(context,"正在加载数据");
		lv = (ListView) findViewById(R.id.activity_scrotree_lv);
		list = new ArrayList<>();
		title = (TextView) findViewById(R.id.title_scrotree);
		final String SearchWorkTreeCode = (String) MainActivity.map.get("SearchWorkTreeCode");
		if (SearchWorkTreeCode != null && SearchWorkTreeCode.equals("SearchWorkTreeCode")){
			title.setText("教材选择");
			className = "SearchWorkActivity";
		}else if (SearchWorkTreeCode != null && SearchWorkTreeCode.equals("upFileTree")){
			title.setText("章节选择");
			className = "UpFileActivity";
		}else{
			title.setText("班级选择");
			className = "SelectScroActivity";
			if (!MainActivity.ScroSearchTag){//选择的班级被清空
				positionMap.clear();
				positionMap.put("20130418090523475737274383301974","true");
			}
		}
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {

				String json = "";
				if (SearchWorkTreeCode != null && SearchWorkTreeCode.equals("SearchWorkTreeCode")){
					MainActivity.map.remove("SearchWorkTreeCode");
					HashMap<String,ParameterValue> urlMap = new HashMap<String, ParameterValue>();
					urlMap.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentUser().getId()));
					urlMap.putAll(ECApplication.getInstance().getLoginUrlMap());
					List<TreeBean1> beans = new ArrayList<>();
					try {
						json = ZddcUtil.buildChapterTree(urlMap);
					} catch (IOException e) {
						e.printStackTrace();
					}
					beans = new Gson().fromJson(json, new TypeToken<List<TreeBean1>>(){}.getType());
					for (int i = 0; i < beans.size(); i++) {
						treeBean = new TreeBean();
						treeBean.setId(beans.get(i).getId());
						treeBean.setLabel(beans.get(i).getName());
						treeBean.setpId(beans.get(i).getParentId());
						treeBean.setContactId(beans.get(i).getId());
						treeBean.setLeafSize(beans.get(i).getChild().size()+"");
						treeBean.setType(beans.get(i).getType());
						list.add(treeBean);
						for (int i1 = 0; i1 < beans.get(i).getChild().size(); i1++) {
							getChildBeans(beans.get(i).getChild().get(i1));
						}
					}
					if (list != null){
						listForWork = list;
					}
					if (!firstTag){
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId().equals("20130424113427190508721241586190")){
								positionMapForSearchWork.put(list.get(i).getContactId(),"true");
								firstTag = true;
							}
						}
					}
				}else if (SearchWorkTreeCode != null && SearchWorkTreeCode.equals("upFileTree")){
					MainActivity.map.remove("SearchWorkTreeCode");
					List<TreeBean1> beans = new ArrayList<>();
					try {
						json = ZddcUtil.buildChapterTree(ECApplication.getInstance().getLoginUrlMap());
					} catch (IOException e) {
						e.printStackTrace();
					}
					beans = new Gson().fromJson(json, new TypeToken<List<TreeBean1>>(){}.getType());
					for (int i = 0; i < beans.size(); i++) {
						treeBean = new TreeBean();
						treeBean.setId(beans.get(i).getId());
						treeBean.setLabel(beans.get(i).getName());
						treeBean.setpId(beans.get(i).getParentId());
						treeBean.setContactId(beans.get(i).getId());
						treeBean.setLeafSize(beans.get(i).getChild().size()+"");
						treeBean.setType(beans.get(i).getType());
						list.add(treeBean);
						for (int i1 = 0; i1 < beans.get(i).getChild().size(); i1++) {
							getChildBeans(beans.get(i).getChild().get(i1));
						}
					}
				} else {
					try {
						json = ZddcUtil.getEclassJsonTree(ECApplication.getInstance().getLoginUrlMap());
						ScroTreeBean bean = new Gson().fromJson(json,ScroTreeBean.class);
						for (int i = 0; i < bean.getEclassList().size(); i++) {
							treeBean = new TreeBean();
							treeBean.setId(bean.getEclassList().get(i).getId());
							treeBean.setpId(bean.getEclassList().get(i).getParentId());
							treeBean.setContactId(bean.getEclassList().get(i).getId());
							treeBean.setType(bean.getEclassList().get(i).getType());
							treeBean.setLabel(bean.getEclassList().get(i).getName());
							list.add(treeBean);
						}
						for (int i = 0; i < bean.getGradeList().size(); i++) {
							treeBean = new TreeBean();
							treeBean.setId(bean.getGradeList().get(i).getId());
							treeBean.setpId(bean.getGradeList().get(i).getParentId());
							treeBean.setContactId(bean.getGradeList().get(i).getId());
							treeBean.setType(bean.getGradeList().get(i).getType());
							treeBean.setLabel(bean.getGradeList().get(i).getName());
							list.add(treeBean);
						}

						for (int i = 0; i < bean.getSchoolList().size(); i++) {
							treeBean = new TreeBean();
							treeBean.setId(bean.getSchoolList().get(i).getId());
							treeBean.setpId(bean.getSchoolList().get(i).getParentId());
							treeBean.setContactId(bean.getSchoolList().get(i).getId());
							treeBean.setType(bean.getSchoolList().get(i).getType());
							treeBean.setLabel(bean.getSchoolList().get(i).getName());
							list.add(treeBean);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (list != null)
						listForScro = list;
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {

						try {
							adapter = new SimpleTreeListViewAdapterForScro<TreeBean>(lv,context,list,0,className);
							lv.setAdapter(adapter);
							ProgressUtil.hide();
							adapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
								@Override
								public void onClick(Node node, int arg0) {
									if (className.equals("SearchWorkActivity")){
										if (node.isLeaf()) {
											if (positionMapForSearchWork.containsKey(node.getContactId())) {
												positionMapForSearchWork.remove(node.getContactId());
											} else {
												positionMapForSearchWork.clear();
												positionMapForSearchWork.put(node.getContactId(), "true");
											}
											adapter.notifyDataSetChanged();
										}
									}else if (className.equals("UpFileActivity")){
										if (node.isLeaf()) {
											if (positionMapForUpFile.containsKey(node.getContactId())) {
												positionMapForUpFile.remove(node.getContactId());
											} else {
												positionMapForUpFile.clear();
												positionMapForUpFile.put(node.getContactId(), "true");
											}
											adapter.notifyDataSetChanged();
										}
									}else{
										if (node.isLeaf()) {
											if (positionMap.containsKey(node.getContactId())) {
												positionMap.remove(node.getContactId());
											} else {
												positionMap.clear();
												positionMap.put(node.getContactId(), "true");
											}
											adapter.notifyDataSetChanged();
										}
									}
								}
							});
							ProgressUtil.hide();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				},5);
			}
		}).start();

		commit = (TextView) findViewById(R.id.activity_scrotree_commit);
		commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (className.equals("SearchWorkActivity")){
					for(String elt:positionMapForSearchWork.keySet()){
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getContactId().equals(elt)){
								MainActivity.map.put("ScroTreeBean",list.get(i));
								ScroTreeActivity.this.finish();
							}
						}
					}
				}else if (className.equals("UpFileActivity")){
					for(String elt:positionMapForUpFile.keySet()){
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getContactId().equals(elt)){
								MainActivity.map.put("ScroTreeBean",list.get(i));
								ScroTreeActivity.this.finish();
							}
						}
					}
				}else{
					for(String elt:positionMap.keySet()){
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getContactId().equals(elt)){
								MainActivity.map.put("ScroTreeBean",list.get(i));
								ScroTreeActivity.this.finish();
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	private void getChildBeans(TreeBean1.ChildBean bean){
		treeBean = new TreeBean();
		treeBean.setId(bean.getId());
		treeBean.setLabel(bean.getName());
		treeBean.setpId(bean.getParentId());
		treeBean.setContactId(bean.getId());
		treeBean.setType(bean.getType());
		treeBean.setLeafSize(bean.getChild().size()+"");
		list.add(treeBean);
		if(bean.getChild().size()!=0) {
			for (int i = 0; i < bean.getChild().size(); i++) {
				getChildBeans(bean.getChild().get(i));
			}
		}
	}
}
