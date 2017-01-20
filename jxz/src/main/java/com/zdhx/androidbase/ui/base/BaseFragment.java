package com.zdhx.androidbase.ui.base;

import android.support.v4.app.Fragment;
import android.view.View;

/**   
 * @Title: BaseFreament.java 
 * @Package com.xinyulong.seagood 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @date 2016-5-3 下午1:11:54 
 */
public abstract class BaseFragment extends Fragment {
	
	
	/**
     * 每个页面需要实现该方法返回一个该页面所对应的资源ID
     * @return 页面资源ID
     */
    protected abstract int getLayoutId();
    
    
    /**
     * 查找	View
     * @param paramInt
     * @return
     */
    public final View findViewById(int paramInt) {
        return getView().findViewById(paramInt);
    }

}
