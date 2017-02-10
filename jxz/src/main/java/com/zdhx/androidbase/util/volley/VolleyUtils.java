package com.zdhx.androidbase.util.volley;

import android.util.Log;

import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.util.NetUtils;
import volley.AuthFailureError;
import volley.DefaultRetryPolicy;
import volley.NetworkError;
import volley.NoConnectionError;
import volley.Request;
import volley.RequestQueue;
import volley.Response;
import volley.ServerError;
import volley.TimeoutError;
import volley.VolleyError;
import volley.toolbox.StringRequest;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 网络请求工具类
 */
public class VolleyUtils {

	/**
	 * 请求接口
	 * 
	 * @param url
	 *            请求url
	 * @param listener
	 *            结果回调
	 * @param isShouldCache
	 *			是否缓存
	 */
	public static void requestService(String url, final Map<String, String> params, final ResultLinstener listener, boolean isShouldCache) {

		//无网络不进行请求
		if (!NetUtils.isNetworkConnected()){
			listener.onIOError();
			listener.onError();
			return;
		}

		listener.onSetTag(url);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i("Response", response);
				listener.onSuccess(response);
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				listener.onError();
				if (error instanceof NetworkError) {
					listener.onIOError();
				} else if (error instanceof ServerError) {
					listener.onServerError();
				} else if (error instanceof NoConnectionError) {
					listener.onNoConnectionError();
				} else if (error instanceof TimeoutError) {
					listener.onTimeOutError();
				}
			}
		}) {

			@Override
			protected String getParamsEncoding() {
				return "UTF-8";
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0, 1));
		// Add the request to the RequestQueue.
		ECApplication.addRequest(stringRequest, url,isShouldCache);

	}

    /**
     * 带文件post请求
     * @param url 请求url
     * @param params 其他请求参数
     * @param filePartName 文件参数名
     * @param files 文件List
     * @param listener 结果回调
	 * @param isShouldCache 是否缓存
     */
	public static void requestServiceWithFile(String url, final Map<String, String> params
            , String filePartName, List<File> files, final ResultLinstener listener, boolean isShouldCache) {
		//无网络不进行请求
		if (!NetUtils.isNetworkConnected()){
			listener.onIOError();
			listener.onError();
			return;
		}

		listener.onSetTag(url);
		MultipartRequest multipartRequest=new MultipartRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    listener.onError();
                    if (error instanceof NetworkError) {
                        listener.onIOError();
                    } else if (error instanceof ServerError) {
                        listener.onServerError();
                    } else if (error instanceof NoConnectionError) {
                        listener.onNoConnectionError();
                    } else if (error instanceof TimeoutError) {
                        listener.onTimeOutError();
                    }
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(response);
                Log.i("Response", response);
            }
        },filePartName,files,params){
            @Override
            protected String getParamsEncoding() {
                return "UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };


        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(3*60 * 1000, 0, 1));
		// Add the request to the RequestQueue.
        ECApplication.addRequest(multipartRequest, url,isShouldCache);

	}

    /**
     * 取消请求
     * @param tag
     */
	public static void cancelService(String tag) {
		ECApplication.getRequestQueue().cancelAll(tag);
	}

    /**
     * 取消请求
     */
	public static void cancelService() {
		ECApplication.getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return true;
			}
		});
	}

}
