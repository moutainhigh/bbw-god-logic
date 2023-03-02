package com.bbw.common;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年8月28日 上午11:50:11
 */
public class StrUtil extends StringUtils {

	public static final String EMPTY_STRING = "";
	public static final String[] NULL_STRING = { "null", "NULL" , "undefined"};

	/**
	 * 是否为Blank字符到判断。 在StringUtils.isBlank基础上添加了字符串=null，NULL判定为true。
	 * StrUtils.isBlank(null) = true <br/>
	 *  StrUtils.isBlank(undefined) = true <br/>
	 * StrUtils.isBlank("") = true <br/>
	 * StrUtils.isBlank(" ") = true <br/>
	 * StrUtils.isBlank("null") = true <br/>
	 * StrUtils.isBlank("NULL") = true <br/>
	 * StrUtils.isBlank("bob") = false <br/>
	 * StrUtils.isBlank(" bob ") = false
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {

		if (StringUtils.isBlank(str)) {
			return true;
		}
		for (String nullStr : NULL_STRING) {
			if (str.equals(nullStr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是非null
	 * 
	 * @param term
	 * @return
	 */
	public static boolean isNotNull(String str) {
		return !isNull(str);
	}

	/**
	 * 获得int
	 * 
	 * @param val
	 * @return
	 */
	public static int getInt(String val) {
		return getInt(val, -1);
	}

	/**
	 * 获得int
	 * 
	 * @param val
	 * @return
	 */
	public static int getInt(Object val) {
		if (val == null)
			return -1;
		return getInt(val.toString(), -1);
	}

	/**
	 * 获得int
	 * 
	 * @param val
	 * @param def
	 * @return
	 */
	public static int getInt(Object val, int def) {
		try {
			return Integer.parseInt(val.toString().trim());
		} catch (Exception e) {
			return def;
		}
	}

	public static long getLong(Object val, int def) {
		try {
			return Long.parseLong(val.toString().trim());
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 不相等
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean notEquals(String str1, String str2) {
		return !StrUtil.equals(str1, str2);
	}

	/**
	 * 替换字符串
	 * 
	 * @param text
	 *            原始文本
	 * @param replaced
	 *            要替换内容
	 * @param replacement
	 *            替换内容
	 * @return
	 */
	public static String replace(String text, String replaced, String replacement) {
		StringBuffer ret = new StringBuffer();
		String temp = text;

		while (temp.indexOf(replaced) > -1) {
			ret.append(temp.substring(0, temp.indexOf(replaced)) + replacement);

			temp = temp.substring(temp.indexOf(replaced) + replaced.length());
		}

		ret.append(temp);

		return ret.toString();
	}

	/**
	 * 字符串替换 正反向
	 * 
	 * @param sIni
	 * @param sFrom
	 * @param sTo
	 * @param caseSensitiveSearch
	 * @return
	 */
	public static String replaceString(String sIni, String sFrom, String sTo, boolean caseSensitiveSearch) {
		int i = 0;
		String s = new String(sIni);
		StringBuffer result = new StringBuffer();

		if (caseSensitiveSearch) {
			i = s.indexOf(sFrom);
		} else {
			i = s.toLowerCase().indexOf(sFrom.toLowerCase());
		}

		while (i != -1) {
			result.append(s.substring(0, i));
			result.append(sTo);

			s = s.substring(i + sFrom.length());

			if (caseSensitiveSearch) {
				i = s.indexOf(sFrom);
			} else {
				i = s.toLowerCase().indexOf(sFrom.toLowerCase());
			}
		}

		result.append(s);

		return result.toString();
	}

	/**
	 * 如果为null返回默认值
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static String get(Object obj, String def) {
		if (obj == null)
			return def;
		return isNull(obj.toString()) ? def : obj.toString();
	}

	/**
	 * 是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDigit(String str) {
		if (isNull(str)) {
			return false;
		}
		return StringUtils.isNumeric(str);
	}

	/**
	 * 首字母转小写
	 * 
	 * @param s
	 * @return
	 */
	public static String toLowerCaseFirstChar(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	/**
	 * 截断字符串
	 * @param length
	 * @return
	 */
	public static String getStringMaxLength(String str, int length) {
		if (StrUtil.isNull(str)) {
			return str;
		}
		if (str.length() < length) {
			return str;
		}
		return str.substring(0, length);
	}

	/**
	 * 字符串转数组 ,错误字符会跳过
	 * @param str
	 * @return
	 */
	public static List<Integer> toList(String str,String regex){
		List<Integer> rd=new ArrayList<>();
		if (isBlank(str)){
			return rd;
		}
		String[] strs=str.split(regex);
		for (String s:strs){
			if (isBlank(s)){
				continue;
			}
			try{
				rd.add(Integer.parseInt(s.trim()));
			}catch (NumberFormatException e){
				continue;
			}
		}
		return rd;
	}

	/**
	 * 将  val  前面补0 到length长度
	 * @param length
	 * @param val
	 * @return
	 */
	public static String toNumStr(int length,int val){
		String str=String.valueOf(val);
		for (int i=0;i<length;i++){
			if (str.length()>=length){
				break;
			}
			str="0"+str;
		}
		return str;
	}

	/**
	 * 字符串转不重复的数组 ,错误字符会跳过
	 * @param str
	 * @return
	 */
	public static List<Integer> toNoRepeatList(String str,String regex){
		List<Integer> rd=new ArrayList<>();
		if (isBlank(str)){
			return rd;
		}
		String[] strs=str.split(regex);
		for (String s:strs){
			if (isBlank(s)){
				continue;
			}
			try{
				int parseInt = Integer.parseInt(s.trim());
				if (!rd.contains(parseInt)){
					rd.add(parseInt);
				}
			}catch (NumberFormatException e){
				continue;
			}
		}
		return rd;
	}

	/**
	 * 数组转字符串
	 * @param str
	 * @param seq1 分隔符1
	 * @param seq2 分隔符2
	 * @return
	 */
	public static String fromArray(String[][] str,String seq1,String seq2){
		String[] array=new String[str.length];
		for (int i = 0; i < str.length; i++) {
			array[i]=fromArray(str[i],seq1);
		}
		return fromArray(array,seq2);
	}

	/**
	 *
	 * @param str
	 * @param seq
	 * @return
	 */
	public static String fromArray(String[] str,String seq){
		StringBuffer sb = new StringBuffer();
		for (String s : str) {
			sb.append(s).append(seq);
		}
		String content = sb.toString();
		if (StrUtil.isBlank(seq)){
			return content;
		}
		return content.substring(0,content.length()-1);
	}


}
