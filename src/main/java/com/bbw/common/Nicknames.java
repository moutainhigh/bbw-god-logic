package com.bbw.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理端工具
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年5月11日 下午3:50:35
 */
public class Nicknames {
	private static Logger logger = LoggerFactory.getLogger(Nicknames.class);

	private static File ruleFile;

	private static long lastModified = 0L;
	private static int line_number = 538;
	//避免List扩容
	private static List<String> words = new ArrayList<String>(line_number);

	static {
		URL url = Nicknames.class.getClassLoader().getResource("config/game/nickname.txt");
		ruleFile = new File(toUTF8(url.getFile()));
		checkReload();
	}

	public static List<String> getNicknames() {
		if (words.isEmpty()) {
			checkReload();
		}
//		System.out.println("昵称数量:" + words.size());
		return words;
	}

	/**
	 * 是否是敏感词汇
	 * @param content
	 * @return
	 */
	public static boolean isLimit(String word) {
		if (null == word) {
			return false;
		}
		if (!ruleFile.exists()) {
			return false;
		}
		checkReload();
		for (String tmp_word : words) {
			if (word.indexOf(tmp_word) >= 0) {
				return true;
			}
		}
		return false;
	}

	private static void checkReload() {

		if (ruleFile.lastModified() > lastModified) {
			synchronized (LimitWords.class) {
				try {
					lastModified = ruleFile.lastModified();
					LineIterator lines = FileUtils.lineIterator(ruleFile, "utf-8");
					int count = 0;
					while (lines.hasNext()) {
						String line = lines.nextLine();
						if (StringUtils.isNotBlank(line)) {
							words.add(StringUtils.trim(line).toLowerCase());
							count++;
						}
					}
					logger.info(String.format("昵称载入完成！共载入 %d 个。", count));
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * utf8编码，以便支持中文文件名
	 *
	 * @param str
	 * @return
	 */
	private static String toUTF8(String str) {
		try {
			String utfStr = URLDecoder.decode(str, "UTF-8");
			return utfStr;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
}
