package com.zdhx.androidbase.ui.treadstree;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TreeActivity extends BaseActivity{

	private Activity context;

	ArrayList<Group> groups;
	ExpandableListView listView;
	EListAdapter adapter;

	private ImageView back;

	private TextView commitBtn;

	//标记是否有选择的班级
	boolean tag = false;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_tree;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		commitBtn = (TextView) findViewById(R.id.activity_tree_commit);
		listView = (ExpandableListView) findViewById(R.id.activity_tree_listview);
		back = (ImageView) findViewById(R.id.activity_selectscro_goback);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		groups = new ArrayList<Group>();
		String jsonStr = "{'CommunityUsersResult':[{'CommunityUsersList':[{'fullname':'a111','userid':11,'username':'a1'}"
				+ ",{'fullname':'b222','userid':12,'username':'b2'}],'id':1,'title':'人事部'},{'CommunityUsersList':[{'fullname':"
				+ "'c333','userid':13,'username':'c3'},{'fullname':'d444','userid':14,'username':'d4'},{'fullname':'e555','userid':"
				+ "15,'username':'e5'}],'id':2,'title':'开发部'}]}";

		try {
			JSONObject CommunityUsersResultObj = new JSONObject(jsonStr);
			JSONArray groupList = CommunityUsersResultObj.getJSONArray("CommunityUsersResult");

			for (int i = 0; i < groupList.length(); i++) {
				JSONObject groupObj = (JSONObject) groupList.get(i);
				Group group = new Group(groupObj.getString("id"), groupObj.getString("title"));
				JSONArray childrenList = groupObj.getJSONArray("CommunityUsersList");

				for (int j = 0; j < childrenList.length(); j++) {
					JSONObject childObj = (JSONObject) childrenList.get(j);
					Child child = new Child(childObj.getString("userid"), childObj.getString("fullname"),
							childObj.getString("username"));
					group.addChildrenItem(child);
				}

				groups.add(group);
			}
		} catch (JSONException e) {
			Log.d("allenj", e.toString());
		}
		adapter = new EListAdapter(this, groups);
		listView.setAdapter(adapter);
		listView.setOnChildClickListener(adapter);

		commitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tag = initGroups(adapter.getGroups());
				if (!tag){
					doToast("当前没有选择！");
				}else{
					TreeActivity.this.finish();
				}
			}
		});
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public boolean initGroups(ArrayList<Group> g){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < g.size(); i++) {
			sb.append("id:"+g.get(i).getId()+"\n\r");
			sb.append("title:"+g.get(i).getTitle()+"\n");
			boolean b = g.get(i).getChecked();
			sb.append("check:"+b+"\n");
			ArrayList<Child> a = g.get(i).getChildren();
			for (int j = 0; j < a.size(); j++) {
				sb.append("childId:"+a.get(j).getUserid()+"\n");
				sb.append("fullname:"+a.get(j).getFullname()+"\n");
				sb.append("username:"+a.get(j).getUsername()+"\n");
				boolean b2 = a.get(j).getChecked();
				sb.append("check:"+b2+"\n");
				if (b2){
					tag = true;
				}
			}
		}
		LogUtil.w(sb.toString());
		if (!tag){
			return false;
		}else{
			MainActivity.map.put("treeDatas",g);
			return true;
		}
	}
}
