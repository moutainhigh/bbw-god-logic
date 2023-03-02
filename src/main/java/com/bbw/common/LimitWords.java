package com.bbw.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * 管理端工具
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年5月11日 下午3:50:35
 */
public class LimitWords {
	private static Logger logger = LoggerFactory.getLogger(LimitWords.class);
	private final static File wordfilter = new File(new ClassPathResource("wordfilter.txt").getPath());

	private static long lastModified = 0L;
	//wordfilter.txt的行数
	private static int line_number = 2393;
	//避免List扩容
	private static List<String> words = new ArrayList<String>(line_number);

	/**
	 * 是否是敏感词汇
	 * @param content
	 * @return
	 */
	public static boolean isLimit(String word) {
		if (null == word) {
			return false;
		}
		if (!wordfilter.exists()) {
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

		if (wordfilter.lastModified() > lastModified) {
			synchronized (LimitWords.class) {
				try {
					lastModified = wordfilter.lastModified();
					LineIterator lines = FileUtils.lineIterator(wordfilter, "utf-8");
					int count = 0;
					while (lines.hasNext()) {
						String line = lines.nextLine();
						if (StringUtils.isNotBlank(line)) {
							words.add(StringUtils.trim(line).toLowerCase());
							count++;
						}
					}
					logger.info(String.format("敏感词载入完成！共载入 %d 个。", count));
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}
