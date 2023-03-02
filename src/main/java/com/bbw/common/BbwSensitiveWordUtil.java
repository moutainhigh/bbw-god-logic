package com.bbw.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 竹风敏感词处理工具 - DFA算法实现
 *
 * @author sam
 * @since 2017/9/4
 */
public class BbwSensitiveWordUtil {
  public static final String PATH_DIC_WFC = "/META-INF/dic/wfc.dic";
  private static HashMap<String, String> wfc_map = null;
  private static HashMap<String, Integer> firstword_map = new HashMap<String, Integer>();
  /**
   * 敏感词匹配规则,最小匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国]人
   */
  public static final int MinMatchTYpe = 1;
  /**
   * 敏感词匹配规则,最大匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国人]
   */
  public static final int MaxMatchType = 2;
  /**
   * 敏感词集合
   */
  public static HashMap<Object, Object> sensitiveWordMap;

  public synchronized static Set<String> getWfcMap() {
    if (wfc_map == null) {
      wfc_map = getWordMap(PATH_DIC_WFC, "");
    }
    return wfc_map.keySet();
  }

  public static HashMap<String, String> getWordMap(String dicPath, String replace) {
    InputStream is = null;
    HashMap<String, String> wordMap = new HashMap<String, String>();
    try {
      // is = new FileInputStream(dicPath);
      is = KeyWordFilter.class.getResourceAsStream(dicPath);
      if (is == null) {
        System.out.println(dicPath + " not found!!!");
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord = null;
      HashMap<String, Integer> firstword = getFirstwordMap();

      String ss = null;
      if (dicPath.equals(PATH_DIC_WFC)) {

        do {
          theWord = br.readLine();
          if (theWord != null && !"".equals(theWord.trim())) {
            ss = theWord.trim();
            if (firstword.containsKey(ss.toCharArray()[0] + "") && firstword.get(ss.toCharArray()[0] + "") > ss.length()) {
              // do nothing
            } else {
              firstword.put(theWord.trim().toCharArray()[0] + "", ss.length());
            }
            wordMap.put(theWord.trim(), replace);
          }
        } while (theWord != null);

      }
    } catch (IOException ioe) {
      if (is == null) {
        System.err.println(dicPath + " not found!!!");
      } else {
        System.err.println(dicPath + " loading exception.");
      }
      ioe.printStackTrace();

    } finally {
      try {
        if (is != null) {
          is.close();
          is = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return wordMap;
  }

  public static synchronized HashMap<String, Integer> getFirstwordMap() {
    if (firstword_map == null) {
      firstword_map = new HashMap<String, Integer>();
    }
    return firstword_map;
  }

  /**
   * 初始化敏感词库，构建DFA算法模型
   */
  public static synchronized void init() {
    initSensitiveWordMap();
  }

  /**
   * 初始化敏感词库，构建DFA算法模型
   */
  private static void initSensitiveWordMap() {
    if (sensitiveWordMap != null) {
      return;
    }
    Set<String> sensitiveWordSet = getWfcMap();
    //初始化敏感词容器，减少扩容操作
    sensitiveWordMap = new HashMap<Object, Object>(sensitiveWordSet.size());
    String key;
    Map<Object, Object> nowMap;
    Map<Object, Object> newWorMap;
    //迭代sensitiveWordSet
    Iterator<String> iterator = sensitiveWordSet.iterator();
    while (iterator.hasNext()) {
      //关键字
      key = iterator.next();
      nowMap = sensitiveWordMap;
      for (int i = 0; i < key.length(); i++) {
        //转换成char型
        char keyChar = key.charAt(i);
        //库中获取关键字
        Object wordMap = nowMap.get(keyChar);
        //如果存在该key，直接赋值，用于下一个循环获取
        if (wordMap != null) {
          nowMap = (Map<Object, Object>) wordMap;
        } else {
          //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
          newWorMap = new HashMap<>();
          //不是最后一个
          newWorMap.put("isEnd", "0");
          nowMap.put(keyChar, newWorMap);
          nowMap = newWorMap;
        }

        if (i == key.length() - 1) {
          //最后一个
          nowMap.put("isEnd", "1");
        }
      }
    }
  }

  /**
   * 判断文字是否包含敏感字符
   *
   * @param txt       文字
   * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
   * @return 若包含返回true，否则返回false
   */
  private static boolean contains(String txt, int matchType) {
    init();
    boolean flag = false;
    txt = replaceSpecialStr(txt);
    for (int i = 0; i < txt.length(); i++) {
      int matchFlag = checkSensitiveWord(txt, i, matchType); //判断是否包含敏感字符
      if (matchFlag > 0) { //大于0存在，返回true
        flag = true;
      }
    }
    return flag;
  }

  /**
   * 批量检验文字，返回校验通过的文字集合
   *
   * @param strs
   * @return
   */
  public static List<String> getValidStr(List<String> strs) {
    init();
    List<String> res = new ArrayList<String>();
    for (String txt : strs) {
      if (contains(txt, MaxMatchType)) {
        continue;
      }
      res.add(txt);
    }
    return res;
  }

  /**
   * 判断文字是否包含敏感字符
   *
   * @param txt 文字
   * @return 若包含返回true，否则返回false
   */
  public static boolean contains(String txt) {
    return contains(txt, MaxMatchType);
  }

  /**
   * 获取文字中的敏感词
   *
   * @param txt       文字
   * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
   * @return
   */
  private static Set<String> getSensitiveWord(String txt, int matchType) {
    Set<String> sensitiveWordList = new HashSet<>();

    for (int i = 0; i < txt.length(); i++) {
      //判断是否包含敏感字符
      int length = checkSensitiveWord(txt, i, matchType);
      if (length > 0) {//存在,加入list中
        sensitiveWordList.add(txt.substring(i, i + length));
        i = i + length - 1;//减1的原因，是因为for会自增
      }
    }

    return sensitiveWordList;
  }

  /**
   * 获取文字中的敏感词
   *
   * @param txt 文字
   * @return
   */
  public static Set<String> getSensitiveWord(String txt) {
    return getSensitiveWord(txt, MaxMatchType);
  }

  /**
   * 替换敏感字字符
   *
   * @param txt         文本
   * @param replaceChar 替换的字符，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符：*， 替换结果：我爱***
   * @param matchType   敏感词匹配规则
   * @return
   */
  private static String replaceSensitiveWord(String txt, char replaceChar, int matchType) {
    init();
    String resultTxt = txt;
    //获取所有的敏感词
    Set<String> set = getSensitiveWord(txt, matchType);
    Iterator<String> iterator = set.iterator();
    String word;
    String replaceString;
    while (iterator.hasNext()) {
      word = iterator.next();
      replaceString = getReplaceChars(replaceChar, word.length());
      resultTxt = StrUtil.replace(resultTxt, word, replaceString);

    }

    return resultTxt;
  }

  /**
   * 替换敏感字字符
   *
   * @param txt 文本
   * @return 替换的字符，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符：*， 替换结果：我爱***
   */
  public static String replaceSensitiveWord(String txt) {
    init();
    return replaceSensitiveWord(txt, '*', MaxMatchType);
  }

//	/**
//	 * 替换敏感字字符
//	 *
//	 * @param txt        文本
//	 * @param replaceStr 替换的字符串，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符串：[屏蔽]，替换结果：我爱[屏蔽]
//	 * @param matchType  敏感词匹配规则
//	 * @return
//	 */
//	private static String replaceSensitiveWord(String txt, String replaceStr, int matchType) {
//		init();
//		String resultTxt = txt;
//		//获取所有的敏感词
//		Set<String> set = getSensitiveWord(txt, matchType);
//		Iterator<String> iterator = set.iterator();
//		String word;
//		while (iterator.hasNext()) {
//			word = iterator.next();
//			resultTxt = resultTxt.replaceAll(word, replaceStr);
//		}
//
//		return resultTxt;
//	}

//	/**
//	 * 替换敏感字字符
//	 *
//	 * @param txt        文本
//	 * @param replaceStr 替换的字符串，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符串：[屏蔽]，替换结果：我爱[屏蔽]
//	 * @return
//	 */
//	private static String replaceSensitiveWord(String txt, String replaceStr) {
//		return replaceSensitiveWord(txt, replaceStr, MaxMatchType);
//	}

  /**
   * 获取替换字符串
   *
   * @param replaceChar
   * @param length
   * @return
   */
  private static String getReplaceChars(char replaceChar, int length) {
    String resultReplace = String.valueOf(replaceChar);
    for (int i = 1; i < length; i++) {
      resultReplace += replaceChar;
    }

    return resultReplace;
  }

  /**
   * 检查文字中是否包含敏感字符，检查规则如下：<br>
   *
   * @param txt
   * @param beginIndex
   * @param matchType
   * @return 如果存在，则返回敏感词字符的长度，不存在返回0
   */
  private static int checkSensitiveWord(String txt, int beginIndex, int matchType) {
    //敏感词结束标识位：用于敏感词只有1位的情况
    boolean flag = false;
    //匹配标识数默认为0
    int matchFlag = 0;
    char word;
    Map<Object, Object> nowMap = sensitiveWordMap;
    for (int i = beginIndex; i < txt.length(); i++) {
      word = txt.charAt(i);
      if (existSpecialStr(word)) {
        matchFlag++;
        continue;
      }
      //获取指定key
      nowMap = (Map<Object, Object>) nowMap.get(word);
      if (nowMap != null) {//存在，则判断是否为最后一个
        //找到相应key，匹配标识+1
        matchFlag++;
        //如果为最后一个匹配规则,结束循环，返回匹配标识数
        if ("1".equals(nowMap.get("isEnd"))) {
          //结束标志位为true
          flag = true;
          //最小规则，直接返回,最大规则还需继续查找
          if (MinMatchTYpe == matchType) {
            break;
          }
        }
      } else {//不存在，直接返回
        break;
      }
    }
    if (!flag) {//长度必须大于等于1，为词
      matchFlag = 0;
    }
    return matchFlag;
  }

  /**
   * 替换特殊字符
   *
   * @param str
   * @return
   */
  private static String replaceSpecialStr(String str) {
    String regEx = "[`~!@#$%^&*()+_=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    str = str.replaceAll(" ", "");
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(str);
    return m.replaceAll("").trim();
  }

  /**
   * 是特殊字符
   *
   * @param str
   * @return
   */
  private static boolean existSpecialStr(char str) {
    if (" ".indexOf(str) > -1) {
      return true;
    }
    String regEx = "[`~!@#$%^&*()+_=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    return regEx.indexOf(str) > -1;
  }
}
