package com.zdhx.androidbase.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zdhx.androidbase.util.ToastUtil;

/**
 * ListAdapter基类
 */
public abstract class ViewHolderListAdapter<T, H> extends BaseAdapter {


    private LayoutInflater mInflater;
    protected List<T> items = new ArrayList<T>();
    protected Context context;

    public ViewHolderListAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void addListData(List<T> items) {
        if (items == null || items.size() == 0)
            return;

        this.items.addAll(items);

    }

    public List<T> getListData() {
        return this.items;
    }

    public void clearListData() {
        this.items = new ArrayList<T>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final T item = (T) getItem(position);

        final H holder;
        if (convertView == null) {
            holder = getHolder();
            convertView = getConvertView(item, mInflater, position);
            findView(convertView, holder, item);
            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }


        refreshView(position, item, convertView, holder);

        return convertView;
    }

    /**
     * holder.xx=(xx) view .findViewById(R.id.xx);
     *
     * @param convertView
     * @param holder
     * @param item        根据不同item类型初始化不同控件
     */
    protected abstract void findView(View convertView, H holder, T item);

    /**
     * 初始化布局View
     *
     * @param item      根据不同item类型初始化不同布局View
     * @param mInflater
     * @param position
     * @return
     */
    protected abstract View getConvertView(T item, LayoutInflater mInflater, int position);

    /**
     * @return new ViewHolder
     */
    protected abstract H getHolder();

    /**
     * @param position
     * @param item
     * @param convertView
     * @param holder
     */
    protected abstract void refreshView(int position, T item, View convertView,
                                        H holder);

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static String getUnNullString(String s, String defalt) {
        return (s == null || TextUtils.isEmpty(s) || "null".equals(s)) ? defalt : s;
    }

    public Context getContext() {
        return context;
    }

    public void doToast(String string){
        ToastUtil.showMessage(string);
    }
}
