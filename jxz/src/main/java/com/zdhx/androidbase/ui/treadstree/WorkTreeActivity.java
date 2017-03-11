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
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treadssearch.SearchWorkActivity;
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
import java.util.Map;

public class WorkTreeActivity extends BaseActivity{
	public static Map<String, String> positionMap = new HashMap<String, String>();  //选择的人
	public static Map<String, String> parentPositionMap = new HashMap<String, String>(); //选择的节点
	HashMap<String,ParameterValue> urlMap = new HashMap<String, ParameterValue>();
	private Handler handler = new Handler();

	private ListView lv;

	private ImageView back;

	private Context context;

	private List<TreeBean1> beans = new ArrayList<>();

	public static List<TreeBean> treeList = new ArrayList<>();

	private TreeBean treeBean;

	private TextView commit;

	private SimpleTreeListViewAdapterForSelect<TreeBean> simpleAdapter;
	@Override
	protected int getLayoutId() {
		return R.layout.activity_work_tree;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		back = (ImageView) findViewById(R.id.activity_selectscro_goback);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		context = this;
		lv = (ListView) findViewById(R.id.activity_tree_listview);
		commit = (TextView) findViewById(R.id.activity_tree_commit);
		commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WorkTreeActivity.this.finish();
			}
		});
		if (treeList.size() == 0){
			initTreeDatas();
		}else{
			try {
				simpleAdapter = new SimpleTreeListViewAdapterForSelect<TreeBean>(lv,context,treeList,0);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			lv.setAdapter(simpleAdapter);
			simpleAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
				@Override
				public void onClick(Node node, int arg0) {
					if (node.isLeaf()) {
						if (positionMap.containsKey(node.getContactId())) {
							positionMap.remove(node.getContactId());
							simpleAdapter.removeUperNode(node);
						} else {
							positionMap.put(node.getContactId(), "true");
							simpleAdapter.contentAll(node);
						}
						simpleAdapter.notifyDataSetChanged();
					}
				}
			});
			ProgressUtil.hide();
		}

	}

	private void initTreeDatas() {
		urlMap.clear();
		//	跳转到加载教材树
		if (SearchWorkActivity.eclassOrBooks.equals("books")){
			ProgressUtil.show(this,"正在加载教材列表..");
			new ProgressThreadWrap(this, new RunnableWrap() {
				@Override
				public void run() {
					urlMap.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentUser().getId()));
					urlMap.putAll(ECApplication.getInstance().getLoginUrlMap());
					try {
						String json = ZddcUtil.buildChapterTree(urlMap);
						beans = new Gson().fromJson(json, new TypeToken<List<TreeBean1>>(){}.getType());
						for (int i = 0; i < beans.size(); i++) {
							treeBean = new TreeBean();
							treeBean.setId(beans.get(i).getId());
							treeBean.setLabel(beans.get(i).getName());
							treeBean.setpId(beans.get(i).getParentId());
							treeBean.setContactId(beans.get(i).getId());
							treeBean.setType(beans.get(i).getType());
							treeList.add(treeBean);
						}

						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									simpleAdapter = new SimpleTreeListViewAdapterForSelect<TreeBean>(lv,context,treeList,0);
									lv.setAdapter(simpleAdapter);
									simpleAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
										@Override
										public void onClick(Node node, int arg0) {
											if (node.isLeaf()) {
												if (positionMap.containsKey(node.getContactId())) {
													positionMap.remove(node.getContactId());
													simpleAdapter.removeUperNode(node);
												} else {
													positionMap.put(node.getContactId(), "true");
													simpleAdapter.contentAll(node);
												}
												simpleAdapter.notifyDataSetChanged();
											}
										}
									});
									ProgressUtil.hide();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						},5);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
