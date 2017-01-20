package com.zdhx.androidbase.ui.treelistview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.treelistview.utils.Node;
import com.zdhx.androidbase.ui.treelistview.utils.adapter.TreeListViewAdapter;

import java.util.List;


public class SimpleTreeListViewAdapter<T> extends TreeListViewAdapter<T> {
	
	private  Context context;

	public SimpleTreeListViewAdapter(ListView tree, Context context,List<T> datas, int defaultExpandLevel)
			throws IllegalAccessException, IllegalArgumentException {
		super(tree, context, datas, defaultExpandLevel);
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getConvertView(Node node, int arg0, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		if(view == null){
			//两个参数时，list_item.xml中的最外层设置无效。
			//false返回我们的list_item，true返回arg2（list_item加载到arg2中后）
			view = mInflater.inflate(R.layout.list_item1, arg2, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView1);
			holder.headimgIV = (ImageView) view.findViewById(R.id.headimgIV);
			holder.textView = (TextView) view.findViewById(R.id.textView1);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		if(node.isLeaf()){
			holder.textView.setTextColor(context.getResources().getColor(R.color.main_bg));
		}else{
			holder.textView.setTextColor(context.getResources().getColor(R.color.normal_text_color));
		}
		
		//加载头像
		holder.headimgIV.setVisibility(View.GONE);
		holder.textView.setText(node.getName());
		return view;
	}
	private class ViewHolder{
		ImageView imageView;
		ImageView headimgIV;
		TextView textView;
	}
}
