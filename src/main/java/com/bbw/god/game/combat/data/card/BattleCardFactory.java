package com.bbw.god.game.combat.data.card;

import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.gameuser.card.UserCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 战斗卡牌实例工厂类
 *
 * @author: suhq
 * @date: 2022/8/26 4:24 下午
 */
public class BattleCardFactory {
	/**
	 * 构建实例
	 *
	 * @param userCard
	 * @return
	 */
	public static BattleCard buildCard(UserCard userCard) {
		BattleCard instance = buildCard(userCard.gainCard(), userCard.getLevel(), userCard.getHierarchy());
		updateCardSkills(instance, userCard);
		return instance;
	}

	/**
	 * 构建实例
	 *
	 * @param cardId
	 * @param lv
	 * @param hv
	 * @return
	 */
	public static BattleCard buildCard(int cardId, int lv, int hv) {
		CfgCardEntity cardById = CardTool.getCardById(cardId);
		BattleCard instance = buildCard(cardById, lv, hv);
		return instance;
	}

	/**
	 * 构建实例
	 *
	 * @param cfgCard
	 * @param lv
	 * @param hv
	 * @return
	 */
	public static BattleCard buildCard(CfgCardEntity cfgCard, int lv, int hv) {
		BattleCard instance = new BattleCard();
		instance.setId(-1000);
		Integer isUseSkillScroll = cfgCard.getPerfect() != null ? cfgCard.getPerfect() : 0;
		instance.setImgId(cfgCard.getId());
		instance.setStars(cfgCard.getStar());
		instance.setName(cfgCard.getName());
		instance.setType(TypeEnum.fromValue(cfgCard.getType()));
		instance.setLv(lv);
		instance.setHv(hv);
		if (null != cfgCard.getGroup()) {
			instance.setGroupId(cfgCard.getGroup());
		}
		int initAtk = CombatInitService.getAtk(cfgCard.getAttack(), instance.getLv(), instance.getHv());
		int initHp = CombatInitService.getHp(cfgCard.getHp(), instance.getLv(), instance.getHv());
		instance.setInitAtk(initAtk);
		instance.setInitHp(initHp);
		instance.setRoundAtk(initAtk);
		instance.setRoundHp(initHp);
		instance.setAtk(initAtk);
		instance.setHp(initHp);
		Integer[] skillIds = {cfgCard.getZeroSkill(), cfgCard.getFiveSkill(), cfgCard.getTenSkill()};
		for (int i = 0; i < skillIds.length; i++) {
			Integer skillId = skillIds[i];
			if (null == skillId || 0 == skillId) {
				continue;
			}
			if (instance.getLv() < i * 5) {
				continue;
			}
			Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillId);
			if (!csOp.isPresent()) {
				continue;
			}
			CombatInitService.battleCardAddSKill(instance, skillId);
		}
		instance.setIsUseSkillScroll(isUseSkillScroll);
		return instance;
	}

	/**
	 * 将userCard的技能更新到battleCard
	 *
	 * @param battleCard
	 * @param userCard
	 */
	public static void updateCardSkills(BattleCard battleCard, UserCard userCard) {
		if (null == userCard) {
			return;
		}
		List<Integer> skillIds = userCard.gainActivedSkills();
		List<BattleSkill> battleSkills = new ArrayList<>();
		for (Integer skillId : skillIds) {
			Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillId);
			if (!csOp.isPresent()) {
				continue;
			}
			BattleSkill skill = BattleSkill.instanceBornSkill(csOp.get());
			battleSkills.add(skill);
		}
		battleCard.setSkills(battleSkills);
		battleCard.setIsUseSkillScroll(userCard.ifUseSkillScroll() ? 1 : 0);
	}
}
