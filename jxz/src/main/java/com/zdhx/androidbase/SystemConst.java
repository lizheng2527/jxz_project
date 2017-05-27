package com.zdhx.androidbase;

public class SystemConst {
	
//	public static String DEFAULT_SERVER = "http://117.117.217.19/dc";//二毛地址
	public static String DEFAULT_SERVER = "http://www.zdhx-edu.com/zddc-cs/";//测试地址
	public static String YKT_SERVER = "http://www.zdhx-edu.com/zddc-cs/";
//	public static String YKT_SERVER = "http://192.168.1.233:14000/zddc-wisdomclass-lz/wcs/";

//	http://192.168.1.233:14000/zddc-wisdomclass-lz/wcs/


	/**
	 * 修改为测试地址
	 * @param test = “test”
	 */
    public static void setTextIp(String test){
		if (test != null&&!test.equals("")&&test.equals("test")){//测试地址
			ECApplication.getInstance().saveValue("address","http://www.zdhx-edu.com/zddc-cs/");
		}else if(test != null && test.equals("9998")){//刚刚地址
			ECApplication.getInstance().saveValue("address","http://192.168.1.115:9998/jxz/");
		}else{
			ECApplication.getInstance().saveValue("address","http://117.117.217.19/dc");//二毛正式
		}
	}
}
