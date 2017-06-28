package com.zdhx.androidbase;

import com.zdhx.androidbase.util.NetUtils;
import com.zdhx.androidbase.util.ToastUtil;

public class SystemConst {
	
//	public static String DEFAULT_SERVER = "http://117.117.217.19/dc";//二毛地址
//	public static String DEFAULT_SERVER = "http://www.zdhx-edu.com/zddc-cs";//测试地址
	public static String DEFAULT_SERVER = "http://zddc.zdhx-edu.com:8889/dc";//？？？？？
//	public static String DEFAULT_SERVER = "http://58.132.20.19:8088/dc";//陈经纶测试
//	public static String DEFAULT_SERVER = "http://58.132.20.15:8088/dc";//陈经纶正式

	//判断是否有云课堂模块
	public static boolean isHasYKT = false;
	//二毛调用统计接口
	public static boolean doAccess = false;
	//根据学校不同显示的启动页不同，启动页的id
	public static int welcomeId;
	//登录页显示的内容判断标记
	public static String loginLogoBack = "test";
	//判断是否有德育评比模块
	public static boolean hasMorContant = false;

	/**
	 * 修改对应地址，并判断是否显示对应模块
	 * @param test = “test”
	 */
    public static String setTextIp(String test){

		if (!NetUtils.isNetworkConnected()){
			ToastUtil.showMessage("当前网络不可用..");
			return test;
		}

		if (test != null&&!test.equals("")&&test.equals("test")||"http://www.zdhx-edu.com/zddc-cs/".equals(test)){//测试地址
			isHasYKT = true;
			doAccess = false;
			welcomeId = R.drawable.welcome_qchdl;
			loginLogoBack = "test";
			hasMorContant = true;
			return "http://www.zdhx-edu.com/zddc-cs/";

		}else if(test != null && test.equals("9998")){//刚刚地址
			ECApplication.getInstance().saveValue("address","http://192.168.1.115:9998/jxz/");
			isHasYKT = true;
			doAccess = false;
			welcomeId = R.drawable.welcome_qchdl;
			loginLogoBack = "test";
			hasMorContant = true;
			return "http://192.168.1.115:9998/jxz/";

		}
		//陈经纶测试地址
		else if (test != null && test.equals("cjltest") || "http://58.132.20.19:8088/dc".equals(test)){
			isHasYKT = false;
			doAccess = false;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "cjl";
			hasMorContant = false;
			return "http://58.132.20.19:8088/dc";

		}
		//陈经纶正式地址
		else if (test != null && test.equals("cjl") || "http://58.132.20.15:8088/dc".equals(test)){
			isHasYKT = false;
			doAccess = false;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "cjl";
			hasMorContant = false;
			return "http://58.132.20.15:8088/dc";
		}
		//二毛正式地址
		else if (test == null||test.equals("")||"http://117.117.217.19/dc".equals(test) || test.equals("em")){
			isHasYKT = false;
			doAccess = true;
			welcomeId = R.drawable.startpager;
			loginLogoBack = "em";
			hasMorContant = true;
			return "http://117.117.217.19/dc";
		}
		//通用
		else if ("http://zddc.zdhx-edu.com:8889/dc".equals(test)||"8889".equals(test)){
			isHasYKT = true;
			doAccess = false;
			welcomeId = R.drawable.welcome_qchdl;
			loginLogoBack = "test";
			hasMorContant = false;
			return "http://zddc.zdhx-edu.com:8889/dc";
		}
		//输入地址
		else{
			isHasYKT = true;
			doAccess = true;
			welcomeId = R.drawable.welcome_qchdl;
			loginLogoBack = "test";
			return test;
		}
	}
}
