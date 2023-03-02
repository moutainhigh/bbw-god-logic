package com.bbw.god.random.box;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 箱子、礼包的物品奖励
 *
 * @author suhq
 * @date 2019年4月10日 下午11:37:30
 */
@Data
public class BoxGood implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 奖励类型 */
	private Integer item;
	/**
	 * 物品描述
	 * 道具：道具名称、法宝、万能灵石
	 * 卡牌：卡牌名称、卡牌策略名称、一星、二星、三星、四星、五星
	 * 元素：元素名称、随机
	 */
	private String good;
	/** 数量 */
	private Integer num = 1;
	/** 概率 1表示1/10000 */
	private Integer prop;
	/** 星级概率组，如法宝 */
	private List<Integer> starProps;
}
