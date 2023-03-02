package com.bbw.god.gameuser.treasure;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用法宝参数
 * 
 * @author suhq
 * @date 2019年4月2日 下午4:05:08
 */
@Data
public class CPUseTreasure {
	private Integer proId;// 使用的法宝ID
	private String count;// 七香车点数
	private String pos;// 乾坤图、山河社稷图、风火轮必传位置参数
	private String direction = "0";// 回马枪选择方向
	private String cardId;// 卡牌ID
	private String selectedGoods;// 玩家选择的物品id集合，至尊秘传宝箱必传参数,格式为 id,num;id,num;
	private Integer useTimes = 1;// 法宝使用次数，默认为1，例：魔王骰子十连抽时传10
	private Integer way = 0;
	private String bet;//投注 赛马

	private Integer useAll=0;//是否使用所有
	private Integer isGameFst; //是否是跨服封神台

	//难度类型
	private Integer difficulty;
	// 区域id
	private Integer regionId;
	/** 四圣挑战 类型 */
	private Integer challengeType;

	public static final String ID = "id";
	public static final String NUM = "num";
	

	public int gainCount() {
		return Integer.valueOf(count);
	}

	public int gainPosition() {
		return Integer.valueOf(pos);
	}

	public int gainDirection() {
		return Integer.valueOf(direction);
	}

	public List<Map<String, Integer>> gainSelectedGoods() {
		String[] split = selectedGoods.split(";");
		List<Map<String, Integer>> goods = new ArrayList<>();
		for (String selectedGood : split) {
			String[] str = selectedGood.split(",");
			int id = Integer.parseInt(str[0]);
			int num = Integer.parseInt(str[1]);
			Map<String, Integer> goodMap = new HashMap<>();
			goodMap.put(ID, id);
			goodMap.put(NUM, num);
			goods.add(goodMap);
		}
		return goods;
	}
}
