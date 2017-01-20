package com.zdhx.androidbase.ui.treadstree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.zdhx.androidbase.R;

import java.util.ArrayList;

public class EListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {  
      
    private Context context;  
    private ArrayList<Group> groups;

    public ArrayList<Group> getGroups(){

        return groups;
    }

    public EListAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;  
        this.groups = groups;  
    }  
   
    public Object getChild(int groupPosition, int childPosition) {  
        return groups.get(groupPosition).getChildItem(childPosition);  
    }  
   
    public long getChildId(int groupPosition, int childPosition) {  
        return childPosition;  
    }  
   
    public int getChildrenCount(int groupPosition) {  
        return groups.get(groupPosition).getChildrenCount();  
    }  
   
    public Object getGroup(int groupPosition) {  
        return groups.get(groupPosition);  
    }  
   
    public int getGroupCount() {  
        return groups.size();  
    }  
   
    public long getGroupId(int groupPosition) {  
        return groupPosition;  
    }  
   
    public boolean hasStableIds() {  
        return true;  
    }  
   
    public boolean isChildSelectable(int groupPosition, int childPosition) {  
        return true;  
    }  
   
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Group group = (Group) getGroup(groupPosition);  
   
        if (convertView == null) {  
            LayoutInflater infalInflater = LayoutInflater.from(context);

            convertView = infalInflater.inflate(R.layout.activity_tree_item, null);
        }  
   
        TextView tv = (TextView) convertView.findViewById(R.id.tree_tv);
        tv.setText(group.getTitle());  
   
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.tree_check);
        checkBox.setChecked(group.getChecked());  
   
        checkBox.setOnClickListener(new Group_CheckBox_Click(groupPosition));
   
        return convertView;  
    }  
   
    class Group_CheckBox_Click implements OnClickListener {
        private int groupPosition;  
   
        Group_CheckBox_Click(int groupPosition) {  
            this.groupPosition = groupPosition;  
        }  
   
        public void onClick(View v) {  
            groups.get(groupPosition).toggle();  
   
            int childrenCount = groups.get(groupPosition).getChildrenCount();
            boolean groupIsChecked = groups.get(groupPosition).getChecked();  
            for (int i = 0; i < childrenCount; i++)  
                groups.get(groupPosition).getChildItem(i).setChecked(groupIsChecked);  
            notifyDataSetChanged();
        }  
    }  
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Child child = groups.get(groupPosition).getChildItem(childPosition);  
   
        if (convertView == null) {  
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_tree_item_child, null);
        }  
   
        TextView tv = (TextView) convertView.findViewById(R.id.tree_tv_child);
        tv.setText(child.getFullname());  
   
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.tree_check_child);
        checkBox.setChecked(child.getChecked());  
   
        checkBox.setOnClickListener(new Child_CheckBox_Click(groupPosition, childPosition));
   
        return convertView;  
    }  
   
    class Child_CheckBox_Click implements OnClickListener {
        private int groupPosition;  
        private int childPosition;  
   
        Child_CheckBox_Click(int groupPosition, int childPosition) {  
            this.groupPosition = groupPosition;  
            this.childPosition = childPosition;  
        }  
   
        public void onClick(View v) {  
            handleClick(childPosition, groupPosition);  
        }  
    }  
      
    public void handleClick(int childPosition, int groupPosition) {  
        groups.get(groupPosition).getChildItem(childPosition).toggle();  
          
        int childrenCount = groups.get(groupPosition).getChildrenCount();
        boolean childrenAllIsChecked = true;  
        for (int i = 0; i < childrenCount; i++) {  
            if (!groups.get(groupPosition).getChildItem(i).getChecked())  
                childrenAllIsChecked = false;  
        }  
  
        groups.get(groupPosition).setChecked(childrenAllIsChecked);  
        notifyDataSetChanged();
    }  
  
    @Override  
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {  
        handleClick(childPosition, groupPosition);  
        return true;  
    }  
}