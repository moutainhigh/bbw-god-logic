package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.*;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 伏虎 3501：每回合，优先于其他回合技能发动，召唤1张与施法者等级阶数相同，且与持有卡牌相同技能的玄坛黑虎至随机阵位，最多同时召唤1张。且该回合获得其所有非上场技能。
 * （1）该技能为复合技能。
 * （2）该技能为新的施法阶段“先制法术效果”：该阶段的在上场技能之后，法术效果之前。
 * （3）与【解封】的描述逻辑相同，后段效果为具体描述，但实际作用与其他复合技能的自带【】相同。自带的技能为【隐藏伏虎 3502】。
 * （4）召唤的逻辑与【幻术】一致，定向召唤玄坛黑虎。
 * （5）召唤出的玄坛黑虎与【幻术】召唤出的卡牌一致，离场时立即消失。
 * （6）当场上已经存在一张通过召唤出现的玄坛黑虎时，则不进行召唤。
 * （7）技能附加逻辑与【玄幻】一致，若玩家持有玄坛黑虎，则按照玩家拥有的卡牌配置的技能、符箓进行生成。
 * （8）默认状态下玄坛黑虎不会召唤至云台位。在玩家持有时，若该卡牌上拥有【飞行】则会将其召唤至云台位。
 *
 * @author: suhq
 * @date: 2021/9/25 6:13 上午
 */
@Service
public class BattleSkill3501 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.FU_HU.getValue();
	@Autowired
	private CombatRunesPerformService runesPerformService;
	@Autowired
	private BattleCardService battleCardService;
	@Autowired
	private UserCardService userCardService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();

		int seq = psp.getNextAnimationSeq();
		//文字动画要在召唤动画前面
		ar.addClientAction(ClientAnimationService.getSkillAction(seq, getMySkillId(), psp.getPerformCard().getPos()));

		BattleCard summonCard= doSummonCard(ar, psp);
		if (null == summonCard){
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
		int[] emptyBattlePos = player.getEmptyBattlePos(true);
		int toPos = getTargetPos(emptyBattlePos);
		if (toPos == -1) {
			return null;
		}
		for (BattleCard playingCard : player.getPlayingCards()) {
			if (null != playingCard && playingCard.getImgId() == CardEnum.XIAN_THH.getCardId()) {
				return null;
			}
		}
		// 召唤卡牌
		BattleCard sumonCard = getSumonCard(psp);
		//有飞行技优先上飞行位
		if (sumonCard.hasSkill(CombatSkillEnum.FX.getValue())) {
			if (player.yunTaiIsEmpty()) {
				toPos = PositionService.getYunTaiPos(psp.getPerformPlayerId());
			}

		}
		sumonCard.setPos(toPos);
		runesPerformService.runInitCardRunes(psp.getPerformPlayer(), sumonCard);
		if (psp.getCombat().getWxType() != null && psp.getCombat().getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
			sumonCard.getSkills().removeIf(p -> p.getId() == CombatSkillEnum.FH.getValue() || p.getId() == CombatSkillEnum.HH.getValue() || p.getId() == CombatSkillEnum.FS.getValue());
		}
		//初始化卡
		battleCardService.replaceCard(player, sumonCard);
		BattleCard performCard = psp.getPerformCard();
		//将召唤的卡牌的技能赋予召唤者
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getMySkillId(), performCard.getPos());
		effect.setSequence(psp.getNextAnimationSeq());
		for (BattleSkill skill : sumonCard.getSkills()) {
			effect.addSkill(skill.getId(), TimesLimit.oneTimeLimit());
		}
		action.addEffect(effect);

		AnimationSequence as = new AnimationSequence(psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_ADD);
		AnimationSequence.Animation animation = new AnimationSequence.Animation();
		animation.setPos1(performCard.getPos());
		animation.setPos2(toPos);
		animation.setSkill(getMySkillId());
		animation.setCards(CombatCardTools.getCardStr(sumonCard, "", sumonCard.getPos()));
		as.add(animation);
		action.addClientAction(as);
		return sumonCard;
	}


	/**
	 * 获取召唤卡牌
	 *
	 * @param psp
	 * @return
	 */
	private BattleCard getSumonCard(PerformSkillParam psp) {
		BattleCard performCard = psp.getPerformCard();
		Player player = psp.getPerformPlayer();
		BattleCard sumonCard = buildSumonCard(player.getUid(),performCard);
		return sumonCard;
	}


	/**
	 * 构建卡牌
	 *
	 * @param performCard
	 * @return
	 */
	private BattleCard buildSumonCard(long uid,BattleCard performCard) {

		CfgCardEntity cfgCard = CardTool.getCardById(CardEnum.XIAN_THH.getCardId());
		BattleCard hero = new BattleCard();
		hero.setId(-1000);
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
		List<Integer> skillIds = cfgCard.getSkills(hero.getLv());
		if (uid >0){
			UserCard userCard = userCardService.getUserCard(uid, hero.getImgId());
			if (null != userCard){
				skillIds = userCard.gainActivedSkills();
				hero.setIsUseSkillScroll(userCard.ifUseSkillScroll()?1:0);
			}
		}
		for (int i = 0; i < skillIds.size(); i++) {
			if (null == skillIds.get(i) || 0 == skillIds.get(i)) {
				continue;
			}
			Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillIds.get(i));
			if (!csOp.isPresent()) {
				continue;
			}
			CombatInitService.battleCardAddSKill(hero, csOp.get().getId());
		}
		return hero;
	}

	/**
	 * 更新召唤卡牌的技能信息
	 *
	 * @param performCard
	 * @param sumonCard
	 */
	private void addPerformSkillsFromSumonCard(BattleCard performCard, BattleCard sumonCard) {
		List<BattleSkill> battleSkills = new ArrayList<>();
		for (BattleSkill performSkill : performCard.getSkills()) {
			Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(performSkill.getId());
			BattleSkill skill = BattleSkill.instanceBornSkill(csOp.get());
			battleSkills.add(skill);
		}
		sumonCard.setSkills(battleSkills);
		sumonCard.setIsUseSkillScroll(performCard.getIsUseSkillScroll());
	}


	/**
	 * 获取目标位置
	 *
	 * @param emptyPos
	 * @return
	 */
	private int getTargetPos(int[] emptyPos) {
		// 随机一个位置
		if (emptyPos.length == 0) {
			return -1;
		}

		int index = PowerRandom.getRandomBySeed(emptyPos.length) - 1;
		int toPos = emptyPos[index];
		return toPos;
	}
}
