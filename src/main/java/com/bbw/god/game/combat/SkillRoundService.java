package com.bbw.god.game.combat;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-10 00:14
 */
@Slf4j
@Service
@Primary
public class SkillRoundService {
	@Autowired
	private SectionSkillService sectionSkillService;
	@Autowired
	private AcceptEffectService acceptEffectService;
	@Autowired
	private BattleCardDieService battleCardDieService;
	@Autowired
	private NormalSkillRoundService normalSkillRoundService;
	@Autowired
	private CombatRunesPerformService runesPerformService;


	protected void roundDeployEnd(Combat combat) {
		// 回合布阵结束技能
		SkillSection[] sections = {SkillSection.getEndDeploySection0(), SkillSection.getEndDeploySection1()};
		performSkill(combat, sections);
	}

	protected void roundSkill(Combat combat) {
		// 上场技能[1001,1099]、 先制技能[2201,2299],法术攻击[3101,3199]
		SkillSection[] sections = {
				SkillSection.getDeploySection(),
				SkillSection.getPriority1Section(),
				SkillSection.getPrioritySkillAttackSection(),
				SkillSection.getSkillAttackSection(),
				SkillSection.getBeforeNormalSection()};
		performSkill(combat, sections);
	}

	private void performSkill(Combat combat, SkillSection[] sections) {
		Player firstPlayer = combat.getFirstPlayer();
		Player secondPlayer = combat.getSecondPlayer();
		for (SkillSection section : sections) {
			// 先制技能阶段,执行持久性伤害
			if (section.contains(CombatSkillEnum.LG.getValue())) {
				// 持久性伤害
				lastingEffect(combat, firstPlayer, secondPlayer);
			}
			for (int cardIndex = 0; cardIndex < CombatConfig.MAX_BATTLE_CARD; cardIndex++) {// 先手、后手卡牌
				PerformSkillParam firstPsp = new PerformSkillParam(combat, firstPlayer, cardIndex);
				PerformSkillParam secondPsp = new PerformSkillParam(combat, secondPlayer, cardIndex);
				runSectionSkillAll(section, combat, firstPsp, secondPsp);
				if (combat.hadEnded()) {
					return;
				}
			}
		}
	}
	public void runSectionSkillAll(SkillSection section, Combat combat, PerformSkillParam firstSkillParam, PerformSkillParam secondSkillParam) {
		PerformSkillParam[] skillParams = {firstSkillParam, secondSkillParam};
		for (int playerIndex = 0; playerIndex < skillParams.length; playerIndex++) {
			if (combat.hadEnded()) {
				return;
			}
			// 没有对象，或者没有卡牌
			if (null == skillParams[playerIndex] || null == skillParams[playerIndex].getPerformCard()) {
				continue;
			}
			PerformSkillParam psp= skillParams[playerIndex];
			if (!psp.checkPerformCardIndex()){
				continue;
			}
			int cardId=psp.getPerformCard().getImgId();
			perform(combat,psp,section);
			psp.updatePerformCardByIndex();
			if (null == psp.getPerformCard()){
				continue;
			}
			int newId=psp.getPerformCard().getImgId();
			if (section.contains(CombatSkillEnum.RW.getValue()) && cardId!=newId){
				//上场技阶段
				perform(combat,psp,section);
			}
		}
	}
	private void perform(Combat combat,PerformSkillParam psp,SkillSection section){
		BattleCard performCard = psp.getPerformCard();
		if (performCard==null){
			return;
		}
		int cardId= performCard.getImgId();
		// 没有此环节的技能
		List<BattleSkill> effectSkills = performCard.getEffectiveSkills(section);
		if (effectSkills.isEmpty()) {
			return;
		}
		for (BattleSkill skill : effectSkills) {
			psp.updatePerformCardByIndex();
			if (psp.getPerformCard()==null || psp.getPerformCard().getImgId()!=cardId){
				break;
			}
			// 技能产生的效果
			Optional<Action> actionOp = sectionSkillService.runSkill(section, skill, psp);
			if (!actionOp.isPresent() || !actionOp.get().existsEffect()) {
				continue;
			}
			List<Effect> allEffects = actionOp.get().getEffects();
			// 不可防御技能
			if (!skill.isDefensible()) {
				List<Effect> todoEffects = runZhsEffects(combat,allEffects);
				if (!todoEffects.isEmpty()) {
					acceptEffectService.acceptSkillAttackEffect(combat, todoEffects);
				}
				continue;
			}
			produceEffects(combat,allEffects);
			// 倾国被金刚防御后需要移除一些动画
			if (skill.getId() == CombatSkillEnum.QING_GUO.getValue()&& ListUtil.isNotEmpty(allEffects)){
				List<AnimationSequence> animSequences = actionOp.get().getClientActions();
				if (ListUtil.isEmpty(animSequences)){
					return;
				}
				List<Integer> defendedTargetPoses = allEffects.stream().filter(tmp -> tmp.isDefended()).map(Effect::getTargetPos).collect(Collectors.toList());
				if (ListUtil.isEmpty(defendedTargetPoses)){
					return;
				}
				for (AnimationSequence animSequence : animSequences) {
					if (animSequence.getType() != Effect.EffectResultType.SKILL_STATUS_CHANGE.getValue()) {
						continue;
					}
					List<AnimationSequence.Animation> newAnims = animSequence.getList().stream().filter(anim -> !defendedTargetPoses.contains(anim.getPos())).collect(Collectors.toList());
					animSequence.setList(newAnims);
				}
			}
		}
	}

	//处理产生的结果
	public void produceEffects(Combat combat, List<Effect> effects) {
		if (effects == null || effects.isEmpty()) {
			return;
		}
		List<Effect> todoEffects = runZhsEffects(combat, effects);
		if (todoEffects.isEmpty()) {
			return;
		}
		SkillSection physicalParticlSection = SkillSection.getPhysicalParticl();
		List<Effect> physicalParticls = todoEffects.stream().filter(p -> physicalParticlSection.contains(p.getSourceID())).collect(Collectors.toList());
		if (physicalParticls != null && !physicalParticls.isEmpty()) {
			//物理溅射，则需要物理防御
			normalSkillRoundService.normalDefense(combat, physicalParticls);
			todoEffects.removeAll(physicalParticls);
		}
		if (todoEffects.isEmpty()) {
			return;
		}
		runesPerformService.runBeforeSkillDefenceRunes(combat, effects);
		// 处理不可防御的效果
		acceptEffectService.acceptCanNotDefenseEffects(combat, effects);
		// --------处理可以防御的效果------------------
		//护身符防御
		// 效果可以防御
		SkillSection skillDefenseSection = SkillSection.getSkillDefenseSection();
		// 法术防御后的效果
		List<Effect> defenseL1Effects = sectionSkillService.runDefenseSkillResult(skillDefenseSection, combat, todoEffects,false);
		// 如果防御后，效果被清除了
		if (defenseL1Effects.isEmpty()) {
			return;
		}
		// 进行法术削弱防御=》减伤
		SkillSection skillWeakenSection = SkillSection.getSkillEffectWeakenSection();
		List<Effect> weakenEffects = sectionSkillService.runDefenseSkillResult(skillWeakenSection, combat, defenseL1Effects, true);
		// 如果防御后，效果被清除了
		if (weakenEffects.isEmpty()) {
			return;
		}
		// 如果防御后还存在效果，则进行反击
		// 反击
		List<Effect> fightBackEffects = sectionSkillService.runStrikeBack(combat, weakenEffects);
		//处理法术反制
		List<Effect> counterEffects = sectionSkillService.runSpecllCounter(combat, weakenEffects);
		// 接受攻击效果
		acceptEffectService.acceptSkillAttackEffect(combat, weakenEffects);
		// 处理反击效果
		if (!fightBackEffects.isEmpty()) {
			// 防御反击的伤害
			List<Effect> defenseL2Effects = sectionSkillService.runDefenseSkillResult(skillDefenseSection, combat, fightBackEffects, false);
			defenseL2Effects = sectionSkillService.runDefenseSkillResult(skillWeakenSection, combat, defenseL2Effects, true);
			// 如果防御后，效果被清除了
			if (!defenseL2Effects.isEmpty()) {
				acceptEffectService.acceptSkillAttackEffect(combat, defenseL2Effects);
			}
		}
		//处理反制效果
		acceptEffectService.acceptSkillAttackEffect(combat, counterEffects);
	}

	/**
	 * 执行召唤师相关的效果
	 * @param combat
	 * @param allEffects
	 * @return
	 */
	public List<Effect> runZhsEffects(Combat combat,List<Effect> allEffects) {
		if (allEffects==null || allEffects.isEmpty()) {
			return new ArrayList<>();
		}
		//分离针对召唤师的负面效果效果，暂时只处理了扣血效果
		List<Effect> tozhsEffects = allEffects.stream().filter(effect -> PositionService.isZhaoHuanShiPos(effect.getTargetPos()) && (effect.isAttakHpEffect()||effect.isAttakMpEffect())).collect(Collectors.toList());
		allEffects.removeAll(tozhsEffects);
		// 执行针对召唤师的攻击
		runZhsCardDefense(combat, tozhsEffects);
		return allEffects;
	}

	/**
	 * 召唤师卡牌防御
	 *
	 * @param combat
	 * @param effects
	 * @return
	 */
	public void runZhsCardDefense(Combat combat, List<Effect> effects) {
		if (effects==null || effects.isEmpty()) {
			return;
		}
		for (Effect effect : effects) {
			Player targetPlayer=combat.getPlayer(effect.getTargetPos());
			runesPerformService.runAttackPlayHpRunes(combat,targetPlayer,effect);
		}
		SkillSection defenseSection= SkillSection.getZhsSection();
		List<Effect> acceptEffects = runZhsCardDefense(combat,effects,defenseSection,true);
		SkillSection defenseAfterSection= SkillSection.getZhsAttakAtferSection();
		List<Effect> acceptAfterEffects = runZhsCardDefense(combat,acceptEffects,defenseAfterSection,false);
		acceptEffects.addAll(acceptAfterEffects);
		acceptEffectService.acceptSkillAttackEffect(combat,acceptEffects);
	}

	/**
	 *
	 * @param combat
	 * @param effects
	 * @param defenseSection
	 * @param returnAllRes  是否返回所以结果，即未防御的是否也返回
	 * @return
	 */
	private List<Effect> runZhsCardDefense(Combat combat, List<Effect> effects,SkillSection defenseSection,boolean returnAllRes) {
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		for (Effect effect : effects) {
			if (!PositionService.isZhaoHuanShiPos(effect.getTargetPos())){
				continue;
			}
			if (effect.getPerformSkillID()== RunesEnum.TIAN_JIE.getRunesId()){
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			BattleCard[] cards = combat.getPlayer(effect.getTargetPos()).getPlayingCards();
			boolean defense = false;
			for (BattleCard card : cards) {
				if (card != null && !card.isKilled() && card.getFirstSkill(defenseSection) != null) {
					PerformSkillParam targetCardSkill = new PerformSkillParam(combat, card);
					targetCardSkill.setReceiveEffect(effect);
					// 进行技能防御
					Optional<Action> defenseSkillAction = sectionSkillService.runSectionSkillFirstEffect(defenseSection, targetCardSkill);
					if (defenseSkillAction.isPresent() && defenseSkillAction.get().getTakeEffect()) {
						if (defenseSkillAction.get().existsEffect()) {
							// 发动技能，添加防御后的伤害
							acceptCardEffects.addAll(defenseSkillAction.get().getEffects());
							defense = true;
							break;
						}
					}
				}
			}
			if (!defense && returnAllRes) {
				acceptCardEffects.add(effect);
			}
		}
		return acceptCardEffects;
	}

    /**
     * 持续效果技能
     * @param combat
     * @param firstPlayer
     * @param secondPlayer
     */
	private void lastingEffect(Combat combat, Player firstPlayer, Player secondPlayer) {
		for (int cardIndex = 0; cardIndex < CombatConfig.MAX_BATTLE_CARD; cardIndex++) {
			BattleCard[] twoCards = {firstPlayer.getPlayingCards(cardIndex), secondPlayer.getPlayingCards(cardIndex)};
			for (BattleCard sourceCard : twoCards) {
				if (null == sourceCard || sourceCard.isKilled() || sourceCard.getLastingEffects().isEmpty()) {
					continue;
				}
				List<CardValueEffect> cardValueEffects=sourceCard.getLastingEffects();
				String content= JSONUtil.toJson(cardValueEffects);
				try{
					for (CardValueEffect effect :cardValueEffects ) {
						if (effect.getBeginRound() >= combat.getRound()) {
							// 该回合不需要重复失效该效果
							continue;
						}
						List<Effect> effects=new ArrayList<>();
						effects.add(effect);
						runesPerformService.runBeforeSkillDefenceRunes(combat, effects);
						// 处理不可防御的效果
						acceptEffectService.acceptCanNotDefenseEffects(combat, effects);
						if (!effects.isEmpty()){
							// --------处理可以防御的效果------------------
							//护身符防御
							// 效果可以防御
							SkillSection skillDefenseSection = SkillSection.getSkillDefenseSection();
							// 法术防御后的效果
							List<Effect> defenseL1Effects = sectionSkillService.runDefenseSkillResult(skillDefenseSection, combat, effects,false);
							// 如果防御后，效果被清除了
							if (defenseL1Effects.isEmpty()) {
								continue;
							}
							SkillSection skillWeakenSection = SkillSection.getSkillEffectWeakenSection();
							defenseL1Effects = sectionSkillService.runDefenseSkillResult(skillWeakenSection, combat, defenseL1Effects,true);
							if (defenseL1Effects.isEmpty()) {
								continue;
							}
							// 接受攻击效果
							for (Effect ef:defenseL1Effects){
								acceptEffectService.acceptEffect(combat,ef);
							}
						}
						effect.lostTime();
						// 持久伤害技能 可以造成卡牌死亡，所以需要对卡牌进行死亡判定
						if (sourceCard.isKilled()) {
							PerformSkillParam dieSkillParams = new PerformSkillParam(combat, effect);
							battleCardDieService.runBattleCardDieEvent(dieSkillParams);
							break;
						}else if (!PositionService.isPlayingPos(sourceCard.getPos())){
							break;
						}
					}
				}catch (ConcurrentModificationException e){
					//BUG已修复
					log.error("回合触发的持久伤害报数组同步修改错误！",e);
					log.error("待执行的列表："+content);
					log.error("玩家1战场卡：=======================");
					firstPlayer.printPlayingCardsInfo();
					log.error("玩家2战场卡：=======================");
					secondPlayer.printPlayingCardsInfo();
				}
			}
		}
	}


}
