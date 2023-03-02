package com.bbw.god.game.wanxianzhen;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 万仙阵宝箱
 * @date 2020/6/4 9:49
 **/
@Data
public class CfgWanXianBox implements CfgInterface, Serializable {
	private static final long serialVersionUID = 6873136235113698267L;
	private String key;
	private List<BoxAward> boxAwards;
	private List<CardProbability> cardProbabilityList;

	@Data
	public static class BoxAward implements Serializable {
		private static final long serialVersionUID = 6438671724225100188L;
		private Integer treasureId;
		/**
		 * 10是常规赛，20是特色赛
		 */
		private Integer type;
		private List<Award> awards;
		private Integer probability;
	}

	@Data
	public static class CardProbability implements Serializable {
		private static final long serialVersionUID = -1657212854594528296L;
		private Integer cardId;
		/**
		 * 10是常规赛，20是特色赛
		 */
		private Integer type;
		private Integer probability;
	}

	/**
	 * 获取配置项到ID值
	 *
	 * @return
	 */
	@Override
	public Serializable getId() {
		return key;
	}

	/**
	 * 获取排序号
	 *
	 * @return
	 */
	@Override
	public int getSortId() {
		return 0;
	}
}
