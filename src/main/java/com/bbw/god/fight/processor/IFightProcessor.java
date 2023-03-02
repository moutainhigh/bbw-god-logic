package com.bbw.god.fight.processor;

import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;

/**
 * 战斗接口
 * 
 * @author suhq
 * @date 2019年2月18日 上午11:42:12
 */
public interface IFightProcessor {

	/**
	 * 战斗类匹配
	 *
	 * @param fightTypeEnum
	 * @return
	 */
	boolean isMatch(FightTypeEnum fightTypeEnum);

	/**
	 * 获取途径
	 * @return
	 */
	WayEnum getWay();
	/**
	 * 获取对手信息
	 * @param uid
	 * @param oppId
	 * @param fightAgain
	 * @return
	 */
	default CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain){
		return null;
	}
	/**
	 * 战斗提交接口
	 * 
	 * @param guId
	 * @param param
	 * @return
	 */
	 RDFightResult submitFightResult(long guId, FightSubmitParam param);

	/**
	 * 获取神仙加成
	 * @param gu
	 * @return
	 */
	int getGodCopperRate(GameUser gu);
	/**
	 * 初始化战斗数据
	 * 
	 * @param gu
	 * @param param
	 */
	void settleBefore(GameUser gu, FightSubmitParam param);

	/**
	 * 失败处理
	 * 
	 * @param gu
	 * @param param
	 */
	void failure(GameUser gu, RDFightResult rd, FightSubmitParam param);
	/**
	 * 发放奖励
	 * 
	 * @param gu
	 * @param resultPart
	 * @param isFightAgain
	 * @param rd
	 */
	void handleAward(GameUser gu, RDFightResult rd,FightSubmitParam param);

}
