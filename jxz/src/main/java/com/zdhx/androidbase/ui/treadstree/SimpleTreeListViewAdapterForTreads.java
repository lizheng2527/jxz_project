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


public class SimpleTreeListViewAdapterForTreads<T> extends TreeListViewAdapter<T> {

	private Context context;
	private String className;
	public SimpleTreeListViewAdapterForTreads(ListView tree, Context context,
											  List<T> datas, int defaultExpandLevel, String className)
			throws IllegalAccessException, IllegalArgumentException {
		super(tree, context, datas, defaultExpandLevel);
		this.className = className;
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
//			if(TreadsTreeActivity.positionMap.get(node.getContactId()) != null){
//				holder.checkBox1.setChecked(true);
//			}else{
//				holder.checkBox1.setChecked(false);
//			}
		if(TreadsTreeActivity.positionMap.get(node.getContactId()) != null){
			holder.checkBox1.setChecked(true);
		}else{
			holder.checkBox1.setChecked(false);
		}

		if(!node.isLeaf()){
			if(TreadsTreeActivity.parentPositionMap.get(node.getId())!=null){
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
					if(TreadsTreeActivity.positionMap.get(node.getContactId()) == null){
						TreadsTreeActivity.positionMap.put(node.getContactId(), node.getName());
						contentAll(node);
					}else{
						TreadsTreeActivity.positionMap.remove(node.getContactId());
						removeUperNode(node);
					}
				}else{
					if(TreadsTreeActivity.parentPositionMap.get(node.getId()) == null){
						TreadsTreeActivity.parentPositionMap.put(node.getId(), node.getName());
						addChildNode(node);
						contentAll(node);
					}else{
						TreadsTreeActivity.parentPositionMap.remove(node.getId());
						removeChildNode(node);
						removeUperNode(node);
					}
				}
//				if (TreadsTreeActivity.positionMap.containsKey(node.getContactId())){
//					TreadsTreeActivity.positionMap.remove(node.getContactId());
//				}else{
//					TreadsTreeActivity.positionMap.clear();
//					TreadsTreeActivity.positionMap.put(node.getContactId(),"true");
//				}
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
							TreadsTreeActivity.positionMap.put(node.getChildren().get(i).getContactId(),node.getChildren().get(i).getName());
						}
					}
				}
				else{
					TreadsTreeActivity.parentPositionMap.put(node.getChildren().get(i).getId(), node.getChildren().get(i).getName());
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
						TreadsTreeActivity.positionMap.remove(node.getChildren().get(i).getContactId());
					}
				}
				else{
					TreadsTreeActivity.parentPositionMap.remove(node.getChildren().get(i).getId());
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
			TreadsTreeActivity.parentPositionMap.remove(node.getParent().getId());
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
			boolean b = (TreadsTreeActivity.positionMap.containsKey(node.getParent().getChildren().get(i).getContactId()));
			if(!(TreadsTreeActivity.positionMap.containsKey(node.getParent().getChildren().get(i).getContactId()))){

				return;
			}
		}
		TreadsTreeActivity.parentPositionMap.put(node.getParent().getId(), node.getParent().getName());
		contentAll(node.getParent());//循环上一层级
	}
}
