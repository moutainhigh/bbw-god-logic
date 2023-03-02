package com.bbw.god.game.combat.skill.service;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

/**
 * 卡牌装备技能服务类
 *
 * @author: suhq
 * @date: 2022/9/29 11:53 上午
 */
@Service
public class CardEquipmentSkillService {

	/**
	 * 是否要执行
	 *
	 * @param battleCard
	 * @param skillId
	 * @return
	 */
	public boolean isToPerform(BattleCard battleCard, int skillId) {
		BattleSkill performSkill = battleCard.getSkill(skillId).orElse(null);
		if (null == performSkill) {
			return false;
		}
		int rate = (int) (performSkill.getInitPerformProbability() * 100);
		if (PowerRandom.hitProbability(rate)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取技能加成
	 *
	 * @param battleCard
	 * @param skillId
	 * @return
	 */
	public double getExtraSkillRate(BattleCard battleCard, int skillId) {
		BattleSkill performSkill = battleCard.getSkill(skillId).orElse(null);
		if (null == performSkill) {
			return 0.0;
		}
		return performSkill.getInitExtraRate();
	}
}
