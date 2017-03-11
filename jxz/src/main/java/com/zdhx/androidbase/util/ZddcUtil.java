package com.zdhx.androidbase.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.SystemConst;
import com.zdhx.androidbase.entity.ParameterValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * DC工具类
 * @author Li.Xin 2014-3-31 上午9:36:35
 */
@SuppressLint("DefaultLocale")
public class ZddcUtil {

	public static boolean login(String url, String userName, String password, String dataSourceName) {
		boolean result = false;
		String baseUrl = checkUrl(url);
		try {
			setDataSource(baseUrl, dataSourceName);
			result = ajaxCheckUser(baseUrl, userName, password);
			if (result) {
				securityCheck(baseUrl, userName, password);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	private static boolean ajaxCheckUser(String baseUrl, String userName, String password) throws Exception {
		String loginUrl = baseUrl + "/bd/welcome!ajaxValidationUser.action";
		HttpURLConnection conn = (HttpURLConnection) (new URL(loginUrl).openConnection());
		conn.setDoOutput(true);
		conn.setDoInput(true);
		// POST必须大写
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		// 仅对当前请求自动重定向
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// 连接
		conn.connect();
		String content = "loginName=" + URLEncoder.encode(userName.toString().trim(), "utf-8");
		content += "&password=" + URLEncoder.encode(password.toString().trim(), "utf-8");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		out.flush();
		out.close();

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.out.println("异常响应编码：" + conn.getResponseCode());
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String resultTemp = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			resultTemp += temp + "\n";
		}
		reader.close();
		conn.disconnect();
		return resultTemp.contains("true");
	}

	private static void securityCheck(String baseUrl, String userName, String password) throws Exception {
		String loginUrl = baseUrl + "/j_spring_security_check";
		HttpURLConnection conn = (HttpURLConnection) (new URL(loginUrl).openConnection());
		conn.setDoOutput(true);
		conn.setDoInput(true);
		// POST必须大写
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		// 仅对当前请求自动重定向
		conn.setInstanceFollowRedirects(false);
		// header 设置编码
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// 链接
		conn.connect();
		String content = "j_username=" + URLEncoder.encode(userName.toString().trim(), "utf-8");
		content += "&j_password=" + URLEncoder.encode(password.toString().trim(), "utf-8");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		out.flush();
		out.close();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
			System.out.println("异常响应编码：" + conn.getResponseCode());
		}
		conn.disconnect();
	}

	private static void setDataSource(String baseUrl, String dataSourceName) throws Exception {
		String loginUrl = baseUrl + "/bd/welcome!ajaxSetUserType.action?dataSourceName=" + dataSourceName;
		HttpURLConnection conn = (HttpURLConnection) (new URL(loginUrl).openConnection());
		conn.setDoOutput(true);
		conn.setDoInput(true);
		// POST必须大写
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		// 仅对当前请求自动重定向
		conn.setInstanceFollowRedirects(true);
		// header 设置编码
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// 连接
		conn.connect();
		conn.disconnect();
	}

	public static String getUrlResponse(String url) throws IOException {
		return getUrlResponse(url, null);
	}

	public static String getUrlResponse(String url, Map<String, ParameterValue> map) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url)).openConnection());
		conn.setDoInput(true);
		conn.setDoOutput(true);
		// POST必须大写
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		// 仅对当前请求自动重定向
		conn.setInstanceFollowRedirects(true);
		// header 设置编码
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// 连接
		conn.connect();
		
		writeParameters(conn, map);
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			Log.v("error", "错误的相应代码：" + conn.getResponseCode());
			throw new IOException();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			result += temp + "\n";
		}
		reader.close();
		conn.disconnect();
		return result;
	}

	public static void writeParameters(HttpURLConnection conn, Map<String, ParameterValue> map) throws IOException {
		if (map == null) {
			return;
		}
		String content = "";
		Set<String> keySet = map.keySet();
		int i = 0;
		try {
			for (String key : keySet) {
				for (String val : map.get(key).getValues()) {
					content += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(val, "utf-8");
					i++;
				}
			}
		}catch (ConcurrentModificationException ex){
			ex.printStackTrace();
		}

		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		out.flush();
		out.close();
		Log.e("url", conn.getURL()+"?"+content);
	}

	public static String getUrl(String url, Map<String, ParameterValue> map) {
		if (url.contains("?")){
			url = url + "&";
		}else{
			url = url + "?";
		}
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(val, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return url;
	}
	public static String getUrlAnd(String url, Map<String, ParameterValue> map) {
		url = url + "&";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(val, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return url;
	}

	public static String getUrlFirstAnd(String url, Map<String, ParameterValue> map) {
		url = url + "&";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(val, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return url;
	}

	public static String getImgUrl(String url, Map<String, ParameterValue> map) {
		url = url + "&";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "="
							+ URLEncoder.encode(val, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		System.out.println(url);
		return url;
	}
	
	public static String getUrlWithDownLoad(String url, Map<String, ParameterValue> map) {
		url = url + "&";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(val, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return url;
	}
	public static String checkUrl(String url) {
		String result = url;
		if (url.startsWith("http://")) {
			url = url.replaceFirst("http://", "");
			if (url.contains("//")) {
				url = url.replaceAll("//", "/");
			}
			result = "http://" + url;
		} else {
			result = "http://" + url;
		}
		result = result.replaceAll("：", ":").replaceAll("：", ":").replaceAll(" ", "");
		return result;
	}

	public static String checkBaseParameters(String Parameter) {
		if (Parameter == null) {
			Parameter = "";
		}
		return Parameter;
	}

	public static String getSchoolInfoStr(String baseUrl) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/welcome!ajaxGetSchoolNames.action");
	}

	public static String uploadFileToServer(String baseUrl, File file) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(baseUrl) + "/component/attachment!saveUploadFile.action").openConnection());
		conn.setReadTimeout(1000000);
		conn.setConnectTimeout(1000000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Charset", "utf-8");
		conn.setRequestProperty("connection", "keep-alive");
		String boundary = UUID.randomUUID().toString();
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		// 链接
		conn.connect();
		FileInputStream in = null;
		DataOutputStream out = null;
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("--" + boundary + "\r\n");
		strBuffer.append("Content-Disposition: form-data; name=\"Filename\"\r\n\r\n");
		strBuffer.append(file.getName() + "\r\n");
		strBuffer.append("--" + boundary + "\r\n");
		strBuffer.append("Content-Disposition: form-data; name=filedata; filename=" + file.getName() + "\r\n");
		strBuffer.append("Content-Type: application/octet-stream\r\n");
		strBuffer.append("\r\n");
		try {
			in = new FileInputStream(file);
			out = new DataOutputStream(conn.getOutputStream());
			out.write(strBuffer.toString().getBytes());
			byte[] buffer = new byte[1024];
			int hasRead = -1;
			while ((hasRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, hasRead);
			}
			out.write("\r\n".getBytes());
			out.write(("--" + boundary + "--\r\n").getBytes());
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				// out.close();
			}
		}
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			Log.v("error", "错误的相应代码：" + conn.getResponseCode() + "," + checkUrl(baseUrl) + "/component/attachment!saveUploadFile.action");
			throw new IOException();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			result += temp;
		}
		reader.close();
		conn.disconnect();
		return result;
	}

	/**
	 * 提交表单带文件
	 * 
	 * @param url
	 * @param files
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public static String commitWithFiles(String url, List<File> files, Map<String, ParameterValue> map) throws IOException {
		Log.i("xx", "save:" + url);
		if (map == null) {
			map = new HashMap<String, ParameterValue>();
		}
		HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url)).openConnection());
		conn.setReadTimeout(1000000);
		conn.setConnectTimeout(1000000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(true);
		conn.setChunkedStreamingMode(10240);
		conn.setRequestProperty("Charset", "utf-8");
		conn.setRequestProperty("connection", "keep-alive");
		String boundary = UUID.randomUUID().toString();
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		// 连接
		conn.connect();
		FileInputStream in = null;
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		// 普通参数
		StringBuffer sb = new StringBuffer();
		// 文件名称
		for (File file : files) {
			sb.append("--" + boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"uploadFileNames\"\r\n\r\n");
			sb.append(file.getName() + "\r\n");
			sb.append("--" + boundary + "\r\n");
		}
		// 其他参数
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			for (String val : map.get(key).getValues()) {
				String name = key;
				sb.append("--" + boundary + "\r\n");
				sb.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
				sb.append(val + "\r\n");
				sb.append("--" + boundary + "\r\n");
			}
		}
		out.write(sb.toString().getBytes("utf-8"));
		// 文件
		for (File file : files) {
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("--" + boundary + "\r\n");
			strBuffer.append("Content-Disposition: form-data; name=uploadFiles; filename=" + file.getName() + "\r\n");
			strBuffer.append("Content-Type: application/octet-stream\r\n");
			strBuffer.append("\r\n");
			try {
				in = new FileInputStream(file);
				out.write(strBuffer.toString().getBytes());
				byte[] buffer = new byte[1024];
				int hasRead = -1;
				while ((hasRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, hasRead);
				}
				out.write("\r\n".getBytes());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new IOException();
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					// out.close();
				}
			}
		}
		out.write(("--" + boundary + "--\r\n").getBytes());
		out.flush();
		out.close();// 11.6 9:25添加
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.out.println(conn.getResponseCode());
			conn.disconnect();
			throw new IOException();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			result += temp;
		}
		reader.close();
		conn.disconnect();
		return result;
	}

	public static String commitWithFiles(String url, File file, Map<String, ParameterValue> map) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(file);
		return commitWithFiles(url, files, map);
	}
	

	public static Intent openFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		/* 取得扩展名 */
		String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
		if (end.equals("ppt")||end.equals("pptx")) {
			return getPptFileIntent(filePath);
		}else{
			return null;
		}
	}
	
	// Android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}
	/**
	 * 在新增和修改的时候要进行拼参数 ,调用此方法
	 * 
	 * @param map
	 * @return
	 */
	public static String getFileUpdateLoadParams(Map<String, ParameterValue> map) {
		String params = "";
		String value = "";
		int i = 0;
		for (Entry<String, ParameterValue> entry : map.entrySet()) {
			try {

				if (entry.getValue() == null || entry.getValue().getValues() == null) {
					value = "";
				} else {
					value = entry.getValue().getValues().get(0);
				}
				params += (i == 0 ? "?" : "&") + entry.getKey() + "=" + URLEncoder.encode(value, "utf-8");
				i++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return params;
	}

	/************************************ 数据接口start *************************************************/

	/**
	 * 发布工作平台资源
	 * subject 主题 content 内容 receiveUserId 接收人id 逗号分隔 attIds 原附件id 逗号分隔
	 *            sourceId 原通知id
	 */
	public static String saveChapterResource(List<File> files,Map<String, ParameterValue> loginMap,
										 Map<String, ParameterValue> map) throws IOException {
		return commitWithFiles(getUrl(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!saveChapterResource.action", loginMap),files, map);
	}


	/**
	 * 下载量+1
	 *
	 * @return
	 * @throws IOException
	 */
	public static String doPreviewOrDown(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!doPreviewOrDown.action", map);
	}
	/**
	 * 获取所有回复内容
	 *
	 * @return
	 * @throws IOException
	 */
	public static String initAllReply(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!initAllReply.action", map);
	}
	/**
	 * 是否有发布重要通知的权限
	 * （yes/no）
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getUserAuth(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!getUserAuth.action", map);
	}
	/**
	 * 推优
	 *
	 * @return
	 * @throws IOException
	 */
	public static String doQuantity(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!doQuantity.action", map);
	}
	/**
	 * 工作平台资源审核
	 *
	 * @return
	 * @throws IOException
	 */
	public static String doCheck(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!doCheck.action", map);
	}
	/**
	 * 删除一条工作平台资源
	 *
	 * @return
	 * @throws IOException
	 */
	public static String buildUploadChapterTree(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!buildUploadChapterTree.action", map);
	}
	/**
	 * 获取积分
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getRankList(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!getRankList.action", map);
	}
	/**
	 * 删除一条工作平台资源
	 *
	 * @return
	 * @throws IOException
	 */
	public static String deleteChapterResource(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!deleteChapterResource.action", map);
	}
	/**
	 * 操作记录
	 *
	 * @return
	 * @throws IOException
	 */
	public static String operateLog(Map<String, ParameterValue> map) throws IOException {

		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!operateLog.action", map);
	}
	/**
	 * 搜索活动显示班级的spinner
	 *
	 * @return
	 * @throws IOException
	 */
	public static String teacherEclassTree(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!teacherEclassTree.action", map);
	}
	/**
	 * 互动交流点赞
	 *
	 * @return
	 * @throws IOException
	 */
	public static String doPpraise(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!doPpraise.action", map);
	}
	/**
	 * 互动交流删除
	 *
	 * @return
	 * @throws IOException
	 */
	public static String delete(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!delete.action", map);
	}
	/**
	 * 互动交流回复
	 *
	 * @return
	 * @throws IOException
	 */
	public static String doReply(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!doReply.action", map);
	}

	/**
	 * 发布动态
	 * subject 主题 content 内容 receiveUserId 接收人id 逗号分隔 attIds 原附件id 逗号分隔
	 *            sourceId 原通知id
	 */
	public static String doReplyWithFiles(List<File> files,Map<String, ParameterValue> loginMap,
										 Map<String, ParameterValue> map) throws IOException {
		return commitWithFiles(getUrl(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!doReply.action", loginMap),files, map);
	}
	/**
	 * 默认获取的互动数据信息
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getAllTreads(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!communcationList.action", map);
	}
	/**
	 * 默认获取的工作平台信息
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getResourceList(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!getResourceList.action", map);
	}


	/**
	 * 获取章节树
	 *
	 * @return
	 * @throws IOException
	 */
	public static String buildChapterTree(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!buildChapterTree.action", map);
	}
	public static String buildChapterTreeStr = "/il/mobile!buildChapterTree.action";
	/**
	 * 获取班级树
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getEclassJsonTree(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!getEclassJsonTree.action", map);
	}
	public static String getEclassJsonTreeStr = "/il/mobile!getEclassJsonTree.action";

	/**
	 * 发布动态
	 * subject 主题 content 内容 receiveUserId 接收人id 逗号分隔 attIds 原附件id 逗号分隔
	 *            sourceId 原通知id
	 */
	public static String saveCommucation(List<File> files,Map<String, ParameterValue> loginMap,
										Map<String, ParameterValue> map) throws IOException {
		return commitWithFiles(getUrl(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!saveCommucation.action", loginMap),files, map);
	}
	/**
	 * 发布动态（不带附件）
	 * subject 主题 content 内容 receiveUserId 接收人id 逗号分隔 attIds 原附件id 逗号分隔
	 *            sourceId 原通知id
	 */
	public static String saveCommucationWithoutFile(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(SystemConst.DEFAULT_SERVER) + "/il/mobile!saveCommucation.action", map);
	}


	/**
	 * 获取用户详细信息
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getUserInfo(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(ECApplication.getInstance().getAddress()) + "/bd/user!getUserInfo.action", map);
	}

	/**
	 * 验证登陆
	 * @throws IOException
	 */
	public static String checkLogin(Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(ECApplication.getInstance().getAddress()) + "/bd/welcome!ajaxValidationUser.action", map);
	}
	
}
