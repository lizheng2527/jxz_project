package com.zdhx.androidbase.ui.treadstree;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.treelistview.utils.Node;
import com.zdhx.androidbase.ui.treelistview.utils.adapter.TreeListViewAdapter;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.StringUtil;

import java.util.List;


public class SimpleTreeListViewAdapterForSelect<T> extends TreeListViewAdapter<T> {

	private Context context;

	public SimpleTreeListViewAdapterForSelect(ListView tree, Context context,
											  List<T> datas, int defaultExpandLevel)
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
				holder.checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			holder.checkBox1.setEnabled(true);
			holder.checkBox1.setVisibility(View.VISIBLE);
			if(node.isLeaf()){
				holder.textView.setTextColor(context.getResources().getColor(R.color.main_bg));
			}else{
				holder.textView.setTextColor(context.getResources().getColor(R.color.normal_text_color));
			}

			if(WorkTreeActivity.positionMap.get(node.getContactId()) != null){
				holder.checkBox1.setChecked(true);
			}else{
				holder.checkBox1.setChecked(false);
			}

			if(!node.isLeaf()){
				if(WorkTreeActivity.parentPositionMap.get(node.getId())!=null){
					holder.checkBox1.setChecked(true);
				}else{
					holder.checkBox1.setChecked(false);
				}
			}

			holder.textView.setText(node.getName());
			if (node.getIcon() == -1) {
				holder.imageView.setVisibility(View.INVISIBLE);
				if("123456".equals(node.getContactId())){
					holder.checkBox1.setVisibility(View.INVISIBLE);
				}else{
					holder.checkBox1.setVisibility(View.VISIBLE);
				}
			}else{
				holder.imageView.setVisibility(View.VISIBLE);
				holder.imageView.setImageResource(node.getIcon());
			}
			holder.headimgIV.setVisibility(View.GONE);
			addListener(holder, node,view);

		return view;
	}

	private class ViewHolder{
		ImageView imageView;
		ImageView headimgIV;
		TextView textView;
		CheckBox checkBox1;
	}
	private void addListener(final ViewHolder holder, final Node node,View view) {

		holder.checkBox1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(node.isLeaf()){
					if(WorkTreeActivity.positionMap.get(node.getContactId()) == null){
						WorkTreeActivity.positionMap.put(node.getContactId(), "true");
						contentAll(node);
					}else{
						WorkTreeActivity.positionMap.remove(node.getContactId());
						removeUperNode(node);
					}
				}else{
					if(WorkTreeActivity.parentPositionMap.get(node.getId()) == null){
						WorkTreeActivity.parentPositionMap.put(node.getId(), "true");
						addChildNode(node);
						contentAll(node);
					}else{
						WorkTreeActivity.parentPositionMap.remove(node.getId());
						removeChildNode(node);
						removeUperNode(node);
					}
				}
				notifyDataSetChanged();

			}
		});
	}

	/**
	 * 选中该组下所有节点
	 * @param node
	 */
	public void addChildNode(Node node){
		if(node.getChildren().size()!=0){
			for (int i = 0; i < node.getChildren().size(); i++) {
				if (node.getChildren().get(i).getChildren().size()== 0){
					if(StringUtil.isNotBlank(node.getChildren().get(i).getContactId())){
						if(!"123456".equals(node.getChildren().get(i).getContactId())){
							WorkTreeActivity.positionMap.put(node.getChildren().get(i).getContactId(), "true");
						}
					}
				}
				else{
					WorkTreeActivity.parentPositionMap.put(node.getChildren().get(i).getId(), "true");
					addChildNode(node.getChildren().get(i));//循环下一层级
					LogUtil.e("循环:"+i);
				}
			}
		}
//		************************************************************************************************
	}

	/**
	 * 移除该组下所有节点
	 * @param node
	 */
	public void removeChildNode(Node node){
		if(node.getChildren().size() != 0){
			for (int i = 0; i < node.getChildren().size(); i++) {
				if (node.getChildren().get(i).getChildren().size() == 0){
					if(StringUtil.isNotBlank(node.getChildren().get(i).getContactId())){
						WorkTreeActivity.positionMap.remove(node.getChildren().get(i).getContactId());
					}
				}
				else{
					WorkTreeActivity.parentPositionMap.remove(node.getChildren().get(i).getId());
					removeChildNode(node.getChildren().get(i));//循环下一层级
				}
			}
		}
	}

	/**
	 * 移除所有上级根节点
	 * @param node
	 */
	public void removeUperNode(Node node){
		if(node.getParent()!=null){
			WorkTreeActivity.parentPositionMap.remove(node.getParent().getId());
			removeUperNode(node.getParent());//循环上一层级
		}
	}

	/**
	 * 选中某个节点后，判断是否此层级已都选中，如果都选中则将上级跟节点选中，循环至顶
	 * @param node
	 */
	public void contentAll(Node node){
		if(node.getParent()==null){
			return;
		}
		for (int i = 0; i < node.getParent().getChildren().size(); i++) {

			if(!(WorkTreeActivity.positionMap.containsKey(node.getParent().getChildren().get(i).getContactId())||WorkTreeActivity.parentPositionMap.containsKey(node.getParent().getChildren().get(i).getId()))){
				return;
			}
		}
		WorkTreeActivity.parentPositionMap.put(node.getParent().getId(), "true");
		contentAll(node.getParent());//循环上一层级
	}
}
