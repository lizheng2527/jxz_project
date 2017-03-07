package com.zdhx.androidbase.ui.treadstree;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.ScroTreeBean;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.ui.treelistview.utils.Node;
import com.zdhx.androidbase.ui.treelistview.utils.adapter.TreeListViewAdapter;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.ZddcUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;


public class TreadsTreeActivity extends BaseActivity{
	private ImageView back;
	private Handler handler = new Handler();
	private SimpleTreeListViewAdapterForTreads adapter ;

	private List<TreeBean> list = new ArrayList<>();//每次加载树的集合
	private TreeBean treeBean;

	private ListView lv;

	private Context context;

	private TextView commit;

	public static HashMap<String,String> positionMap = new HashMap<>();
	public static HashMap<String,String> parentPositionMap = new HashMap<>();

	private static boolean firstTag = false;

	private TextView title;

	private String className;

	private RequestWithCacheGet cacheGet;

	private String json = "";

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
		cacheGet = new RequestWithCacheGet(context);
		ProgressUtil.show(context,"正在加载数据");
		lv = (ListView) findViewById(R.id.activity_scrotree_lv);
		list = new ArrayList<>();
		title = (TextView) findViewById(R.id.title_scrotree);
		try {
			adapter = new SimpleTreeListViewAdapterForTreads<TreeBean>(lv,context,list,0,className);
			lv.setAdapter(adapter);
			adapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
				@Override
				public void onClick(Node node, int arg0) {
					if (node.isLeaf()) {
						if (positionMap.containsKey(node.getContactId())) {
							positionMap.remove(node.getContactId());
							adapter.removeUperNode(node);
						} else {
							positionMap.put(node.getContactId(), node.getName());
							adapter.contentAll(node);
						}
						adapter.notifyDataSetChanged();
					}
				}
			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		commit = (TextView) findViewById(R.id.activity_scrotree_commit);
		commit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (positionMap.size() == 0){
					doToast("请选择班级");
					return ;
				}else{
					TreadsTreeActivity.this.finish();
				}
			}
		});

		initEClassDatas();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 初始化班级排名处树数据
	 * @param json
	 */
	public void initEClassTreeDatas(String json){
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

	}

	/**
	 * 展示已经加载好的树
	 */
	public void setListView(){
		try {
			adapter = new SimpleTreeListViewAdapterForTreads<TreeBean>(lv,context,list,0,className);
			lv.setAdapter(adapter);
			ProgressUtil.hide();
			adapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
				@Override
				public void onClick(Node node, int arg0) {
						if (node.isLeaf()) {
							if (positionMap.containsKey(node.getContactId())) {
								positionMap.remove(node.getContactId());
								adapter.removeUperNode(node);
							} else {
								positionMap.put(node.getContactId(), node.getName());
								adapter.contentAll(node);
							}
							adapter.notifyDataSetChanged();
						}
				}
			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 获取积分排名处树数据
	 */
	private void initEClassDatas(){
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				HashMap<String,ParameterValue> urlMap = new HashMap<String, ParameterValue>();
				urlMap.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentUser().getId()));
				urlMap.putAll(ECApplication.getInstance().getLoginUrlMap());
				String url = ZddcUtil.getUrl(ECApplication.getInstance().getAddress()+ZddcUtil.getEclassJsonTreeStr,urlMap);
				json = cacheGet.getRseponse(url, new RequestWithCacheGet.RequestListener() {
					@Override
					public void onResponse(String response) {
						System.out.println(response + "response");
						if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
							initEClassTreeDatas(response);
							setListView();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
				if ((json != null && !json.equals(RequestWithCacheGet.NO_DATA))) {
					initEClassTreeDatas(json);
					setListView();
				}
			}
		},500);

	}
}
