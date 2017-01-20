package com.zdhx.androidbase.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zdhx.androidbase.ECApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class PreferenceUtil {


	private PreferenceUtil() {
	}

	private static Context context = ECApplication.getInstance().getApplicationContext();

	public static String generateKey(String type, String id) {
		return type.concat("_" + id);
	}

	public static String[] parseKey(String key) {
		return key.split("_");
	}
	
	public static <T extends Serializable> boolean save(T entity, String key) {
		if (entity == null) {
			return false;
		}
		String prefFileName = entity.getClass().getName();
		SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
		SharedPreferences.Editor et = sp.edit();
		String json = GsonUtil.ser(entity);
		et.putString(key, json);
		return et.commit();
	}

	public static <T extends Serializable> List<T> findAll(Class<T> clazz) {
		String prefFileName = clazz.getName();
		SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
		Map<String, String> values = (Map<String, String>) sp.getAll();
		List<T> results = new ArrayList<T>();

		if (values == null || values.isEmpty())
			return results;

		Collection<String> colles = values.values();

		for (String json : colles) {
			results.add(GsonUtil.deser(json, clazz));
		}
		return results;
	}

	public static <T extends Serializable> T find(String key, Class<T> clazz) {
		String prefFileName = clazz.getName();
		SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
		String json = sp.getString(key, null);
		if (json == null)
			return null;
		return GsonUtil.deser(json, clazz);
	}

	public static <T extends Serializable> void delete(String key, Class<T> clazz) {
		String prefFileName = clazz.getName();
		SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
		if (sp.contains(key)) {
			sp.edit().remove(key).commit();
		}
	}

	public static <T extends Serializable> void deleteAll(Class<T> clazz) {
		String prefFileName = clazz.getName();
		SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
		sp.edit().clear().commit();
	}

}
