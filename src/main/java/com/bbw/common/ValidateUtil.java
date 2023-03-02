package com.bbw.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：验证数据的工具类
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年8月27日 下午6:27:46
 */
public class ValidateUtil {
	public static final String HREF_URL_REGEX = "(http:|https:)//[^[A-Za-z0-9\\._\\?%&+\\-=/#!]]*";
	private final static String DEFAULT_URI_PATTERN = "([a-zA-Z0-9]{3,})";
	private final static String IP_ADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private final static Pattern IP_ADDRESS = Pattern.compile(IP_ADDRESS_PATTERN);
	/**
	 * 定义电话号码的正则表达式
	 * 匹配格式：
	 * 11位手机号码
	 * 3-4位区号，7-8位直播号码，1－4位分机号
	 * 如:12345678901、1234-12345678-1234	
	 */
	private static final String _PHONE_REGEX_PATTERN = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}-(\\d{1,4})$))";

	/**
	 * 是否为Blank字符到判断。
	 * 在StringUtils.isBlank基础上添加了字符串=null，NULL判定为true。
	 * StrUtils.isBlank(null)      = true
	 * <br/>StrUtils.isBlank("")        = true
	 * <br/>StrUtils.isBlank(" ")       = true
	 * <br/>StrUtils.isBlank("null")       = true
	 * <br/>StrUtils.isBlank("NULL")       = true
	 * <br/>StrUtils.isBlank("bob")     = false
	 * <br/>StrUtils.isBlank("  bob  ") = false
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		return StrUtil.isNull(str);
	}

	public static boolean isNotNull(String str) {
		return StrUtil.isNotNull(str);
	}

	/**
	 * 是否是正整数
	 * @param str
	 * @return
	 */
	public static boolean isIntegerUnsigned(String str) {
		if (str == null) {
			return false;
		}
		char ac[] = str.toCharArray();
		for (int i = 0; i < ac.length; i++)
			if (!Character.isDigit(ac[i])) {
				return false;
			}
		return true;
	}

	public static boolean isDigit(String str) {
		if (StrUtil.isNull(str)) {
			return false;
		}
		Boolean strResult = str.matches("-?[0-9]+.*[0-9]*");
		return strResult;
	}

	/**
	 * 是否符合时间格式
	 * @param term
	 * @param pattern
	 * @return
	 */
	public static boolean isDate(String term, String pattern) {
		if (term == null)
			return false;
		if (pattern == null)
			pattern = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			sdf.parse(term);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 是否是标准电话
	 * @param term
	 * @return
	 */
	public static boolean isPhone(String term) {
		return isRegex(term, _PHONE_REGEX_PATTERN);
	}

	/**
	 * 是否是null数组[本身为null,长度为0,内容为null]
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> boolean isNullArray(T[] t) {
		if (t == null || t.length < 1)
			return true;
		for (T tt : t) {
			if (tt != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否是null List [本身为null,isEmpty,所有值为null]
	 * @param t
	 * @return
	 */
	public static <T> boolean isNotNullList(List<T> t) {
		if (null == t || t.isEmpty()) {
			return false;
		}
		for (Object object : t) {
			if (object != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 正则判断[完全符合]
	 * @param term
	 * @param pattern
	 * @return
	 */
	public static boolean isRegex(String term, String pattern) {
		if (pattern == null)
			throw new IllegalArgumentException();
		if (term == null)
			return false;
		Pattern p = Pattern.compile(pattern, Pattern.CANON_EQ);
		Matcher matcher = p.matcher(term);
		return matcher.matches();
	}

	/**
	 * 判断是否是中文
	 * @param term
	 * @return
	 */
	public static boolean isChiness(String term) {
		if (null == term)
			return false;
		String pattern = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(pattern);
		char[] cs = term.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if (!p.matcher(String.valueOf(cs[i])).find())
				return false;
		}
		return true;
	}

	/**
	 * 判断是否是有效的uri
	 * @param uri
	 * @return
	 */
	public static boolean isValidUrl(String uri) {
		if (uri.indexOf("/") >= 0)
			return false;
		if (uri.indexOf(".") >= 0)
			return false;
		Pattern p = Pattern.compile(DEFAULT_URI_PATTERN);
		Matcher m = p.matcher(uri);
		return m.find();
	}

	/**
	 * 判断IP地址
	 * @param domain
	 * @return
	 */
	public static boolean isIPAddress(String domain) {
		if (domain == null)
			return false;
		if (domain.indexOf(".") <= 0)
			return false;
		Matcher m = IP_ADDRESS.matcher(domain);
		return m.find();
	}

	/**
	 * 判断常用图片扩展名
	 * @param filename
	 * @return
	 */
	public static boolean isImageExtension(String filename) {
		return isRegex(filename, "(.*)(jpg|png|gif)$");
	}

	/**
	 * 判断两个字符串是否相等
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isEquals(String str1, String str2) {
		if (str1 == str2)
			return true;
		if (str1 == null || str2 == null)
			return false;
		return str1.equals(str2);
	}

	/**
	 * 判断两个字符串是否相等,不区分大小写
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isEqualsIgnoreCase(String str1, String str2) {
		if (str1 == null || str2 == null)
			return false;
		return str1.equalsIgnoreCase(str2);
	}

	/**
	 * 判断是否整个字符串由英文组成
	 * @param word
	 * @return
	 */
	public static boolean isAllEnglishLetter(String word) {
		int len = word.length();
		int i = 0;

		while (i < len && ((word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') || (word.charAt(i) >= 'a' && word.charAt(i) <= 'z'))) {
			i++;
		}
		if (i < len)
			return false;

		return true;
	}

	/**
	 * 验证file是否比file2新
	 * @param file
	 * @param file2
	 * @return
	 */
	public static boolean isLatestModifyFile(File file, File file2) {

		if (file != null && file.exists()) {
			if (file2 == null || !file2.exists())
				return true;
			if (file.lastModified() > file2.lastModified())
				return true;
		}
		return false;
	}

	/**
	 * 判断当前系统是不是windows系统
	 * @return
	 */
	public static boolean isWindowsOS() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		return isWindowsOS;
	}
}
