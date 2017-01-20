package com.zdhx.androidbase.util.volley;

import java.util.HashMap;
import java.util.Map;

public class Params {

    private static String addressId;

    /**
     * 登陆参数
     *
     * @param remember_me
     * @param password
     * @param username
     * @return
     */
    public static Map<String, String> getLoginParams(String remember_me, String username, String password) {
        Map<String, String> map = new HashMap<String, String>();
//        map.put("remember_me", remember_me);
        map.put("password", password);
        map.put("username", username);
        return map;
    }
}