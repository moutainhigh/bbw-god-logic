package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatCardTools;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 蜂袋3165：每回合，召唤1张与施法者等级阶数相同的蚊道人至随机阵位，召唤的蚊道人从场上离开时不会消失。
 * （1）召唤逻辑参考【幻术】，定向召唤蚊道人。
 * （2）召唤出的蚊道人允许进入坟场和返回手牌/卡组。
 *
 * @author: suhq
 * @date: 2021/11/19 2:00 下午
 */
@Service
public class BattleSkill3165 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.FENG_DAI.getValue();
	@Autowired
	private BattleCardService battleCardService;
	@Autowired
	private CombatRunesPerformService runesPerformService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos()));
		BattleCard battleCard = doSummonCard(ar, psp);
		if (null == battleCard) {
			ar.getClientActions().clear();
		}
		return ar;
	}

	/**
	 * 召唤卡牌
	 *
	 * @param action
	 * @param psp
	 */
	public BattleCard doSummonCard(Action action, PerformSkillParam psp) {
		Player player = psp.getPerformPlayer();
		int toPos = getTargetPos(psp);
		if (toPos == -1) {
			return null;
		}
		BattleCard performCard = psp.getPerformCard();
		BattleCard card = buildCard(performCard);
		if (card == null) {
			return null;
		}
		runesPerformService.runInitCardRunes(psp.getPerformPlayer(), card);
		if (psp.getCombat().getWxType() != null && psp.getCombat().getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
			card.getSkills().removeIf(p -> p.getId() == CombatSkillEnum.FH.getValue() || p.getId() == CombatSkillEnum.HH.getValue() || p.getId() == CombatSkillEnum.FS.getValue());
		}
		card.setPos(toPos);
		//初始化卡
		battleCardService.replaceCard(player, card);
		AnimationSequence as = new AnimationSequence(psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_ADD);
		AnimationSequence.Animation animation = new AnimationSequence.Animation();
		animation.setPos1(performCard.getPos());
		animation.setPos2(toPos);
		animation.setSkill(getMySkillId());
		animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
		as.add(animation);
		action.addClientAction(as);
		return card;
	}

	/**
	 * 获取目标位置
	 *
	 * @param psp
	 * @return
	 */
	private int getTargetPos(PerformSkillParam psp) {
		Player player = psp.getPerformPlayer();
		int[] emptyPos = player.getEmptyBattlePos(true);
		if (emptyPos.length == 0) {
			return -1;
		}

		int index = PowerRandom.getRandomBySeed(emptyPos.length) - 1;
		int toPos = emptyPos[index];
		return toPos;
	}

	/**
	 * 构建召唤的卡牌
	 *
	 * @param performCard 发动召唤的卡牌
	 * @return
	 */
	private BattleCard buildCard(BattleCard performCard) {
		CfgCardEntity cfgCard = CardTool.getCardById(CardEnum.WEN_DAO_REN.getCardId());
		BattleCard hero = new BattleCard();
		hero.setId(cfgCard.getId());
		if (cfgCard.getPerfect() != null) {
			hero.setIsUseSkillScroll(cfgCard.getPerfect());
		}
		hero.setImgId(cfgCard.getId());
		hero.setStars(cfgCard.getStar());
		hero.setName(cfgCard.getName());
		hero.setType(TypeEnum.fromValue(cfgCard.getType()));
		hero.setHv(performCard.getHv());
		hero.setLv(performCard.getLv());
		if (null != cfgCard.getGroup()) {
			hero.setGroupId(cfgCard.getGroup());
		}
		int initAtk = CombatInitService.getAtk(cfgCard.getAttack(), hero.getLv(), hero.getHv());
		int initHp = CombatInitService.getHp(cfgCard.getHp(), hero.getLv(), hero.getHv());
		hero.setInitAtk(initAtk);
		hero.setInitHp(initHp);
		hero.setRoundAtk(initAtk);
		hero.setRoundHp(initHp);
		hero.setAtk(initAtk);
		hero.setHp(initHp);
		Integer[] skillIds = {cfgCard.getZeroSkill(), cfgCard.getFiveSkill(), cfgCard.getTenSkill()};
		for (int i = 0; i < skillIds.length; i++) {
			if (null == skillIds[i] || 0 == skillIds[i]) {
				continue;
			}
			if (hero.getLv() < i * 5) {
				continue;
			}
			Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillIds[i]);
			if (!csOp.isPresent()) {
				continue;
			}
			BattleSkill skill = BattleSkill.instanceBornSkill(csOp.get());
			hero.addSkill(skill);
		}
		return hero;
	}
}
