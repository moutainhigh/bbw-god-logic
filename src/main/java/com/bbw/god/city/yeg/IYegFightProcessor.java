package com.bbw.god.city.yeg;

import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.exaward.YeGExawardEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDCommon;

import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月24日 上午10:01:27 类说明 野怪
 */
public interface IYegFightProcessor {
	default boolean isMatch(YeGuaiEnum type) {
		if (type == null) {
			return false;
		}
		return type.equals(getYeGEnum());
	}

	public boolean open(long uid);

	public YeGuaiEnum getYeGEnum();// 实际野怪归属
	
	public RDFightsInfo getFightsInfo(GameUser gu, int type);

	public RDCommon openBox(RDFightEndInfo fightEndInfo, long guId);

	public void sendBoxAward(RDFightEndInfo fightEndInfo, GameUser gu, RDCommon rd);

	public YeGExawardEnum getAdditionGoal();

	/**
	 * 获取宝箱内的随机奖励
	 * @param gu
	 * @return
	 */
	public List<Award> getRandomBoxAwards(long uid);


}
