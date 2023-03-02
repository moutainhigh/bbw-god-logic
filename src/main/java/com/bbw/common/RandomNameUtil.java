package com.bbw.common;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgRandomName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月11日 上午10:56:20
 * 类说明 生成随机名字
 */
public class RandomNameUtil {
	/**
	 * 生成SensitiveWordUtil 检测通过的名字
	 * @param size
	 * @return
	 */
	public static List<String> getRandomName(int size) {
		CfgRandomName names = Cfg.I.getUniqueConfig(CfgRandomName.class);
		List<String> surNamelist = names.getSurNames();// 百家姓+现存的复姓
		List<String> wordList = names.getWords();// 单字
		List<String> resList = new ArrayList<String>();
		do {
			int need = size - resList.size();
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < need; i++) {
				String name = "";
				name = PowerRandom.getRandomFromList(surNamelist).trim();
				name += PowerRandom.getRandomFromList(wordList).trim();
				name = name.replaceAll(" ", "");// 避免出现空格
				list.add(name);
			}
			resList.addAll(BbwSensitiveWordUtil.getValidStr(list));
		} while (resList.size() < size);
		return resList;
	}
}
