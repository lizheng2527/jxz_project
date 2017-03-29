package com.zdhx.androidbase.ui.treadstree;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zdhx.androidbase.util.NetUtils;
import com.zdhx.androidbase.util.ToastUtil;

import volley.DefaultRetryPolicy;
import volley.RequestQueue;
import volley.Response;
import volley.toolbox.StringRequest;
import volley.toolbox.Volley;

/**
 * @author Li.Xin @ 立思辰合众 使用方法： RequestWithCache qCache = new
 *         RequestWithCache(context); String string =
 *         qCache.getRseponse("http://www.baidu.com", new RequestListener() {
 * @Override public void onResponse(String response) { method stub
 *
 *           System.out.println("second : " + response); } }, new
 *           ErrorListener() {
 * @Override public void onErrorResponse(VolleyError error) { Auto-generated
 *           method stub
 *
 *           } });
 *
 */
public class RequestWithCacheGet {
	private Context mContext;
	private RequestQueue mRequestQueue;
	public static final String NOT_OUTOFDATE = "notoutofdate";
	public static final String NO_DATA = "nodata";
	private static final String STR_SIZE = "size";
	public static final String SHAREDPREFERENCES_NAME = "josncatch";
	public static final String SHAREDPREFERENCES_KEY = "josncatchkey";
	public boolean isOpenCache = true;

	/**
	 * @param context
	 */
	public RequestWithCacheGet(Context context) {
		mContext = context;
		mRequestQueue = Volley.newRequestQueue(context);

	}

	/**
	 * @param url
	 * @param listener
	 *            当前 RequestWithCache 类中的 下载完成监听
	 *            返回字符串RequestWithCache.NOT_OUTOFDATE代表新数据与缓存数据一致 否则返回新数据
	 * @param errorListener
	 *            出错监听 导 volley 中的包
	 * @return 返回 已经缓存 过的数据 没有的话返回RequestWithCache.NOT_DATA
	 * @author 容联•云通讯 Modify By Li.Xin @ 立思辰合众 注意不要导错包
	 */
	public String getRseponse(final String url, final RequestListener listener, Response.ErrorListener errorListener) {
		Log.i("Url", url);
		if (!NetUtils.isNetworkConnected()) {
			ToastUtil.showMessage("网络连接不可用");
		}
		final SharedPreferences sharedPreferences = mContext
				.getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
		final SharedPreferences sharedPreferenceskey = mContext
				.getSharedPreferences(SHAREDPREFERENCES_KEY, 0);

		Response.Listener<String> netlistener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
//				Log.i("Response", response);
				int sizekey = sharedPreferenceskey.getInt(url + STR_SIZE, -1);
				if (sizekey != -1 && sizekey == response.getBytes().length+ response.hashCode()) {
					listener.onResponse(NOT_OUTOFDATE);
				} else {
					if (isOpenCache) {
						if (!response.contains("</html>")) {
							sharedPreferences.edit().putString(url, response).commit();
							// 采用hashcode + size比较 不记hashcode 碰撞 基本精确比较两个字符
							sharedPreferenceskey.edit().putInt(url + STR_SIZE,response.getBytes().length + response.hashCode()).commit();
						} else {
							response = "[]";
							ToastUtil.showMessage("数据异常，请检查wifi登录状态");
						}
					}
					listener.onResponse(new String(response));
				}
			}
		};
		mRequestQueue.add(new StringRequest(url, netlistener, errorListener).setRetryPolicy(new DefaultRetryPolicy(60*1000,0,1)));
		return sharedPreferences.getString(url, NO_DATA);// 返回文件读取的数据
	}

	/**
	 * 清除缓存 只是简单删掉本地文件中的数据
	 *
	 */
	public void cleraCache() {
		// 清楚缓存FIX ME
		mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0).edit().clear()
				.commit();
	}

	public void removeSimpleCache(String url,String json){
		mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0).edit().remove(url).commit();
		mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0).edit().putString(url,json).commit();
	}

	public interface RequestListener {
		/** Called when a response is received. */
		public void onResponse(String response);
	}

	public void isOpenCache(boolean isOpenCache) {
		this.isOpenCache = isOpenCache;
	}
}
