package com.zdhx.androidbase;

public class SystemConst {
	
//	public static String DEFAULT_SERVER = "http://117.117.217.19/dc";//二毛地址
	public static String DEFAULT_SERVER = "http://www.zdhx-edu.com/zddc-cs";//测试地址
//	public static String DEFAULT_SERVER = "http://58.132.20.19:8088/dc";//陈经纶测试
//	public static String DEFAULT_SERVER = "http://58.132.20.15:8088/dc";//陈经纶正式

//	http://192.168.1.233:14000/zddc-wisdomclass-lz/wcs/
	//判断是否有云课堂模块
	public static boolean isHasYKT = false;
	//二毛调用统计接口
	public static boolean doAccess = false;
	//根据学校不同显示的启动页不同，启动页的id
	public static int welcomeId;
	//登录页显示的内容判断标记
	public static String loginLogoBack = "test";
	/**
	 * 修改为测试地址
	 * @param test = “test”
	 */
    public static String setTextIp(String test){

		if (test != null&&!test.equals("")&&test.equals("test")||"http://www.zdhx-edu.com/zddc-cs/".equals(test)){//测试地址
			isHasYKT = true;
			doAccess = false;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "test";
			return "http://www.zdhx-edu.com/zddc-cs/";

		}else if(test != null && test.equals("9998")){//刚刚地址
			ECApplication.getInstance().saveValue("address","http://192.168.1.115:9998/jxz/");
			isHasYKT = true;
			doAccess = false;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "test";
			return "http://192.168.1.115:9998/jxz/";

		}else if (test != null && test.equals("cjltest") || "http://58.132.20.19:8088/dc".equals(test)){
			isHasYKT = false;
			doAccess = false;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "cjl";
			return "http://58.132.20.19:8088/dc";

		}else if (test != null && test.equals("cjl") || "http://58.132.20.15:8088/dc".equals(test)){
			isHasYKT = false;
			doAccess = false;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "cjl";
			return "http://58.132.20.15:8088/dc";
		}
		else if (test == null||test.equals("")||"http://117.117.217.19/dc".equals(test) || test.equals("em")){
			isHasYKT = false;
			doAccess = true;
			welcomeId = R.drawable.startpager;
			loginLogoBack = "em";
			return "http://117.117.217.19/dc";
		}else{
			isHasYKT = true;
			doAccess = true;
			welcomeId = R.drawable.welcome_cjl;
			loginLogoBack = "test";
			return test;
		}
	}
}
