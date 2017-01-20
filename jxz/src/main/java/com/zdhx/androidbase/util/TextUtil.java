package com.zdhx.androidbase.util;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本内容处理工具类
 *
 * @author 吕绪文
 */
public final class TextUtil {
	private TextUtil() {
	}

	/**
	 * 是否是有效的邮箱地址
	 *
	 * @param address
	 * @return
	 */
	public static final boolean isEMailAddress(String address) {
		// String strPattern =
		// "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		// String strPattern =
		// "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w-]*[a-zA-Z0-9]\\.[a-zA-Z]{2,3}(\\.[a-zA-Z]{2})?$";
		Pattern pattern = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(address);
		return matcher.matches();
	}

	/**
	 * 是否是有效的移动电话
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(14[57])|(15[^4,\\D])|(17[0678])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 是否是有效的固定电话
	 *
	 * @param number
	 * @return
	 */
	public static boolean isTelephoneNumber(String number) {
		Pattern p = Pattern.compile("^(\\(*\\d{3,4}\\)*-)*\\d{6,8}$");
		Matcher m = p.matcher(number);
		return m.matches();
	}

	/**
	 * 需要转换的字符串
	 *
	 * @param text
	 * @return
	 */
	public static Spanned convertHtmlToText(String text) {
		return Html.fromHtml(text, null, null);
	}

	ImageGetter imgGetter = new Html.ImageGetter() {
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			Log.d("Image Path", source);
			URL url;
			try {
				url = new URL(source);
				drawable = Drawable.createFromStream(url.openStream(), "");
			} catch (Exception e) {
				return null;
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			return drawable;
		}
	};

	/**
	 * 给字符串添加颜色,返回SpannableString
	 *
	 * @param str
	 * @param color
	 * @return
	 */
	public static SpannableString getSpannableString(String str, int color) {
		SpannableString spannableString = new SpannableString(str);
		spannableString.setSpan(new ForegroundColorSpan(color), 0,
				str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}

	/**
	 * 转换合适单位
	 *
	 * @param fileSize
	 *            单位字节
	 *
	 * @return
	 */
	public static String FormetFileSize(long fileSize) {
		DecimalFormat df = new DecimalFormat("#0.00");
		String fileSizeString = "";
		if (fileSize < 1024) {
			fileSizeString = df.format((double) fileSize) + "字节";
		} else
		if (fileSize < 1024 * 1024) {
			fileSizeString = df.format((double) fileSize / (1024)) + "千字节";
		} else
		if (fileSize < 1024 * 1024 * 1024) {
			fileSizeString = df.format((double) fileSize
					/ (1024 * 1024))
					+ "兆字节";
		} else {
			fileSizeString = df.format((double) fileSize
					/ (1024 * 1024 * 1024))
					+ "吉字节";
		}
		return fileSizeString;
	}

	// 身份证号码验证：start
	/**
	 * 功能：身份证的有效验证
	 *
	 * @param IDStr
	 *            身份证号
	 * @return 有效：返回"" 无效：返回String信息
	 * @throws ParseException
	 */
	public static String idCardValidate(String IDStr) {
		String errorInfo = "";// 记录错误信息
		String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位。";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else
		if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
			return errorInfo;
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效。";
			return errorInfo;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(
					strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				errorInfo = "身份证生日不在有效范围。";
				return errorInfo;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "解析失败";
		} catch (ParseException e) {
			e.printStackTrace();
			return "解析失败";

		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无效";
			return errorInfo;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无效";
			return errorInfo;
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		Hashtable h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误。";
			return errorInfo;
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		System.out.println("Ai:" + Ai);
		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				return errorInfo;
			}
		} else {
			return "";
		}
		// =====================(end)=====================
		return "";
	}

	/**
	 * 功能：设置地区编码
	 *
	 * @return Hashtable 对象
	 */
	private static Hashtable<String, String> GetAreaCode() {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	/**
	 * 验证日期字符串是否是YYYY-MM-DD格式
	 *
	 * @param str
	 * @return
	 */
	public static boolean isDataFormat(String str) {
		boolean flag = false;
		// String
		// regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
		String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern pattern1 = Pattern.compile(regxStr);
		Matcher isNo = pattern1.matcher(str);
		if (isNo.matches()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 功能：判断字符串是否为数字
	 *
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将指定的字符串替换成※
	 *
	 * @param text
	 * @param start
	 * @param length
	 * @return
	 */
	public static String getSafePhoneText(String text, int start, int length) {
		if (text == null || "".equals(text) || text.length() < start + length) {
			return "";
		}
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < text.length(); i++) {
			if (i > start && i <= start + length) {
				sb.append("*");
			} else {
				sb.append(text.charAt(i));
			}
		}
		return sb.toString();

	}

	public static String getSafeEmailText(String text, int start) {
		if (text == null || "".equals(text)) {
			return "";
		}
		int index = text.lastIndexOf("@");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			if (i > start && i < index) {
				sb.append("*");
			} else {
				sb.append(text.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 将null转换成指定字符，不为null原字符串返回
	 * @param s
	 * @param defalt
	 * @return
	 */
	public static String getUnNullString(String s, String defalt) {
		return (s == null || TextUtils.isEmpty(s) || "null".equals(s)) ? defalt
				: s;
	}
}