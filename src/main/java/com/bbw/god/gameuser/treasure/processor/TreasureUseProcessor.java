package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDSeeAward;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import lombok.Getter;

/**
 * 道具处理器
 * 
 * @author suhq
 * @date 2018年11月28日 下午4:56:13
 */
@Getter
public abstract class TreasureUseProcessor {
	protected TreasureEnum treasureEnum;// 法宝标识
	protected boolean isAutoBuy;// 是否自动购买

	/**
	 * 是否自己实现扣除逻辑
	 *
	 * @return
	 */
	public boolean isSelfToDeductTreasure(long uid) {
		return false;
	}

	/**
	 * 是否宝箱类
	 *
	 * @return
	 */
	public boolean isChestType() {
		return false;
	}

	/**
	 * 保底次数
	 *
	 * @return
	 */
	public Integer minGuaranteeNum() {
		return 0;
	}

	/**
	 * 生效前校验
	 *
	 * @param gu
	 * @param param
	 */
	public void check(GameUser gu, CPUseTreasure param) {
	}

	public int getNeedNum(GameUser gu, int useTimes, WayEnum way) {
		return useTimes;
	}

	/**
	 * 法宝生效
	 *
	 * @param gu
	 * @param param
	 * @param rd
	 */
	abstract public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd);

	/**
	 * 获取treasureId对应的处理器
	 * 
	 * @param treasureId
	 * @return
	 */
	public boolean isMatch(int treasureId) {
		return treasureEnum.getValue() == treasureId;
	}

	/**
	 * 查看奖励内容
	 *
	 * @param treasureId
	 * @return
	 */
	 public RDSeeAward seeAward(int treasureId) {
	 	return null;
	 }
}
