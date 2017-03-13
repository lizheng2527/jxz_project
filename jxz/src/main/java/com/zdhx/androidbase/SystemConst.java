package com.zdhx.androidbase;

public class SystemConst {
	
    /**应用服务器**/
//	public static String DEFAULT_SERVER = "http://192.168.1.115:9998/dc-independentlearning";  //测试
	public static String DEFAULT_SERVER = "http://117.117.217.19/dc";  //正式
	/**图片服务器**/
	public static final String DEFAULT_IMAGE_URL= "http://117.78.48.224:8000";

	/**
	 * 修改为测试地址
	 * @param test = “test”
	 */
    public static void setTextIp(String test){
		if (test != null&&!test.equals("")&&test.equals("test")){
			DEFAULT_SERVER = "http://192.168.1.115:9998/jxz";  //测试
		}else{
			DEFAULT_SERVER = "http://117.117.217.19/dc";//正式
		}
	}
    
	/**登陆url**/
	public static final String LOGIN_URL = ECApplication.getInstance().getAddress()+"/loginapi/login";

}
