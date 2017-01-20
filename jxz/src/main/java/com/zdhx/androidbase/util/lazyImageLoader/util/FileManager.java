package com.zdhx.androidbase.util.lazyImageLoader.util;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "Hzth_IM_File/image/";
		} else {
			return CommonUtil.getRootFilePath() + "Hzth_IM_File/image";
		}
	}
}
