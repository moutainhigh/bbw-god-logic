package com.bbw.god.game.combat;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.AnimationSequence.Animation;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 23:42
 */
@Scope("prototype")
@Service
public class NormalSkillRoundService {
	@Autowired
	private AcceptEffectService acceptEffectService;
	@Autowired
	private SectionSkillService sectionSkillService;
	@Autowired
	private SkillRoundService skillRoundService;
	@Autowired
	private CombatRunesPerformService combatRunesPerformService;

	public void round(Combat combat) {
		Player firstPlayer = combat.getFirstPlayer();
		Player secondPlayer = combat.getSecondPlayer();

		for (int cardIndex = 0; cardIndex < CombatConfig.MAX_BATTLE_CARD; cardIndex++) {
			//相互对位的两个位置都没有卡则跳过
			if (null == firstPlayer.getPlayingCards(cardIndex) && null == secondPlayer.getPlayingCards(cardIndex)) {
				continue;
			}
//			PerformSkillParam firstSkillParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
//			PerformSkillParam secondSkillParam = new PerformSkillParam(combat, secondPlayer, cardIndex);
//			//相互对位的两张卡
//			PerformSkillParam[] params = {firstSkillParam, secondSkillParam};
//			//重置普攻
//			for (PerformSkillParam param : params) {
//				if (param.getPerformCard() != null) {
//					param.getPerformCard().updateNormalAttackPreAtk(0);
//				}
//			}
//			normalBuffStage(params);
//			// 物理攻击先手攻击
//			List<Effect> skillEffects = firstAttack(combat, firstSkillParam, secondSkillParam);
//			if (!skillEffects.isEmpty()) {
//				// 防御
//				normalDefense(combat, skillEffects);
//				attakAfter(combat, firstPlayer, secondPlayer, cardIndex, skillEffects);
//			}
//			// 物理攻击
//			// 以上产生影响卡牌结果等情况，就必须重新初始化技能参数
//			firstSkillParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
//			secondSkillParam = new PerformSkillParam(combat, secondPlayer, cardIndex);
//			SkillSection[] normalAttacks={SkillSection.getNormalAttackSection()};
//			List<Effect> normalAttackEffects=new ArrayList<>();
//			for (SkillSection section : normalAttacks) {
//				skillEffects =runSectionSkillFirst(section, firstSkillParam, secondSkillParam);
//				if (!skillEffects.isEmpty()) {
//					// 防御
//					normalDefense(combat, skillEffects);
//					normalAttackEffects.addAll(skillEffects);
//				}
//			}
//			// 物理攻击后
//			attakAfter(combat, firstPlayer, secondPlayer, cardIndex, normalAttackEffects);
			normalAttack(combat, firstPlayer, secondPlayer, cardIndex);
			// 战斗结束
			if (combat.hadEnded()) {
				return;
			}
//			List<Effect> effects = new ArrayList<>();
//			for (int i = 0; i < 2; i++) {
//				PerformSkillParam param = params[i];
//				Optional<Action> action = sectionSkillService.runSectionSkillFirst(SkillSection.getNormalAttackSection2(), param);
//				if (action.isPresent() && action.get().getTakeEffect()) {
//					effects.addAll(action.get().getEffects());
//				}
//			}
			PerformSkillParam firstSkillParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
			PerformSkillParam secondSkillParam = new PerformSkillParam(combat, secondPlayer, cardIndex);
			PerformSkillParam[] params = {firstSkillParam, secondSkillParam};
			List<Player> lianJPlayers = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				PerformSkillParam param = params[i];
				Optional<Action> action = sectionSkillService.runSectionSkillFirst(SkillSection.getNormalAttackSection2(), param);
				if (action.isPresent() && action.get().getTakeEffect()) {
					acceptEffectService.acceptSkillAttackEffect(combat, action.get().getEffects());
					lianJPlayers.add(i == 0 ? firstPlayer : secondPlayer);
				}
			}
			if (lianJPlayers.size() == 2) {
				normalAttack(combat, lianJPlayers.get(0), lianJPlayers.get(1), cardIndex);
			} else if (lianJPlayers.size() == 1) {
				normalAttack(combat, lianJPlayers.get(0), null, cardIndex);
			}
			// 战斗结束
			if (combat.hadEnded()) {
				return;
			}
		}
	}

	private void normalAttack(Combat combat, Player firstPlayer, Player secondPlayer, int cardIndex) {
		PerformSkillParam firstSkillParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
		PerformSkillParam secondSkillParam = null;
		if (null != secondPlayer) {
			secondSkillParam = new PerformSkillParam(combat, secondPlayer, cardIndex);
		}
		//相互对位的两张卡
		PerformSkillParam[] params = {firstSkillParam, secondSkillParam};
		if (null == secondSkillParam) {
			params = new PerformSkillParam[]{firstSkillParam};
		}
		//重置普攻
		for (PerformSkillParam param : params) {
			if (param.getPerformCard() != null) {
				param.getPerformCard().updateNormalAttackPreAtk(0);
			}
		}
		normalBuffStage0(params);
		normalBuffStage1(params);
		// 物理攻击先手攻击
		List<Effect> skillEffects = firstAttack(combat, firstSkillParam, secondSkillParam);
		if (!skillEffects.isEmpty()) {
			// 防御
			normalDefense(combat, skillEffects);
			attakAfter(combat, firstPlayer, secondPlayer, cardIndex, skillEffects);
		}
		// 物理攻击
		// 以上产生影响卡牌结果等情况，就必须重新初始化技能参数
		firstSkillParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
		if (null != secondPlayer) {
			secondSkillParam = new PerformSkillParam(combat, secondPlayer, cardIndex);
		}
		SkillSection[] normalAttacks = {SkillSection.getNormalAttackSection()};
		List<Effect> normalAttackEffects = new ArrayList<>();
		for (SkillSection section : normalAttacks) {
			skillEffects = runSectionSkillFirst(section, firstSkillParam, secondSkillParam);
			if (!skillEffects.isEmpty()) {
				// 防御
				normalDefense(combat, skillEffects);
				normalAttackEffects.addAll(skillEffects);
			}
		}
		// 物理攻击后
		attakAfter(combat, firstPlayer, secondPlayer, cardIndex, normalAttackEffects);
	}

	private void normalAttack(Combat combat, Player firstPlayer, int cardIndex) {
		PerformSkillParam performParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
		PerformSkillParam[] params = {performParam};
		for (PerformSkillParam param : params) {
			if (param.getPerformCard() != null) {
				param.getPerformCard().updateNormalAttackPreAtk(0);
			}
		}
		normalBuffStage0(params);
		normalBuffStage1(params);
		// 物理攻击先手攻击
		List<Effect> skillEffects = firstAttack(combat, performParam, null);
		if (!skillEffects.isEmpty()) {
			// 防御
			normalDefense(combat, skillEffects);
			attakAfter(combat, firstPlayer, null, cardIndex, skillEffects);
		}
		// 物理攻击
		// 以上产生影响卡牌结果等情况，就必须重新初始化技能参数
		performParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
		SkillSection[] normalAttacks = {SkillSection.getNormalAttackSection()};
		List<Effect> normalAttackEffects = new ArrayList<>();
		for (SkillSection section : normalAttacks) {
			skillEffects = runSectionSkillFirst(section, performParam, null);
			if (!skillEffects.isEmpty()) {
				// 防御
				normalDefense(combat, skillEffects);
				normalAttackEffects.addAll(skillEffects);
			}
		}
		// 物理攻击后
		attakAfter(combat, firstPlayer, null, cardIndex, normalAttackEffects);
	}
	/**
	 * 物理攻击buff阶段0（普通之前）。比如追风，蚀骨
	 * <br/>
	 * eg:属性克制、暴击4104、龙息4111、神剑4114、诛仙4119
	 *
	 * @param params
	 */
	private void normalBuffStage0(PerformSkillParam[] params) {
		SkillSection buffSection = SkillSection.getNormalBuffSection0();
		SkillSection buffDefenseSection = SkillSection.getNormalBuffDefenseSection();
		for (PerformSkillParam psp : params) {
			if (null == psp.getPerformCard()) {
				continue;
			}
			List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(buffSection);
			for (BattleSkill sectionSkill : effectSkills) {
				Optional<Action> skillEffect = sectionSkillService.runSkill(buffSection, sectionSkill, psp);
				List<Effect> effects = new ArrayList<>();
				Action action = skillEffect.orElse(null);
				if (skillEffect.isPresent()) {
					effects = skillEffect.get().getEffects();
				}
				combatRunesPerformService.runAfterAttackBuffRunes(psp.getCombat(), psp.getPerformCard(), effects);
				if (ListUtil.isNotEmpty(effects)) {
					acceptEffectService.acceptNormalAttackEffect(psp.getCombat(), effects);
					runSectionSkillAll(buffDefenseSection, params);
				}
			}
		}
	}
	/**
	 * 物理攻击buff阶段1（普通之前）。比如增加攻击力，或者使这次攻击增加额外效果
	 * <br/>
	 * eg:属性克制、暴击4104、龙息4111、神剑4114、诛仙4119
	 *
	 * @param params
	 */
	private void normalBuffStage1(PerformSkillParam[] params) {
		SkillSection buffSection = SkillSection.getNormalBuffSection1();
		SkillSection buffDefenseSection = SkillSection.getNormalBuffDefenseSection();
		for (PerformSkillParam psp : params) {
			if (null == psp.getPerformCard()) {
				continue;
			}
			List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(buffSection);
			Optional<BattleSkill> tjOp = effectSkills.stream().filter(p -> p.getId() == CombatSkillEnum.TJ.getValue()).findFirst();
			if (tjOp.isPresent()) {
				//存在太极 则默认使用 克制属性
				Optional<BattleSkill> attrBuff = getAttributeRestraintReverseSkill(psp);
				if (attrBuff.isPresent()) {
					effectSkills.remove(tjOp.get());
					effectSkills.add(0, tjOp.get());
					effectSkills.add(1, attrBuff.get());
				}
			} else {
				Optional<BattleSkill> attrBuff = getAttributeRestraintSkill(psp);
				if (attrBuff.isPresent()) {
					effectSkills.add(0, attrBuff.get());
				}
			}
			if (effectSkills.isEmpty()) {
				continue;
			}
			for (BattleSkill sectionSkill : effectSkills) {
				Optional<Action> skillEffect = sectionSkillService.runSkill(buffSection, sectionSkill, psp);
				List<Effect> effects = new ArrayList<>();
				Action action = skillEffect.orElse(null);
				if (skillEffect.isPresent()) {
					effects = skillEffect.get().getEffects();
				}
				combatRunesPerformService.runAfterAttackBuffRunes(psp.getCombat(), psp.getPerformCard(), effects);
				if (ListUtil.isNotEmpty(effects)) {
					acceptEffectService.acceptNormalAttackEffect(psp.getCombat(), effects);
					runSectionSkillAll(buffDefenseSection, params);
				}
			}
		}
	}
	private void attakAfter(Combat combat, Player firstPlayer, Player secondPlayer, int cardIndex,
			List<Effect> skillEffects) {
		// 以上产生影响卡牌结果等情况，就必须重新初始化技能参数
		PerformSkillParam firstSkillParam = new PerformSkillParam(combat, firstPlayer, cardIndex);
		PerformSkillParam secondSkillParam = null;
		if (secondPlayer!=null){
			secondSkillParam=new PerformSkillParam(combat, secondPlayer, cardIndex);
		}
		SkillSection afterSection = SkillSection.getNormalEndAttackSection();
		Set<PerformSkillParam> psps = new HashSet<PerformSkillParam>();
		for (Effect effect : skillEffects) {
			if (firstSkillParam.getPerformCard() != null
					&& effect.getSourcePos() == firstSkillParam.getPerformCard().getPos()) {
				psps.add(firstSkillParam);
			}
			if (secondSkillParam!=null && secondSkillParam.getPerformCard() != null
					&& effect.getSourcePos() == secondSkillParam.getPerformCard().getPos()) {
				psps.add(secondSkillParam);
			}
		}
		psps.forEach(p -> {
			runSectionSkillAll(afterSection, p);
		});

	}

	public void normalDefense(Combat combat,List<Effect> skillEffects) {
		// ---客户端动画,补充一个物理攻击动画-----
		int sequence = combat.getAnimationSeq();
		AnimationSequence as = new AnimationSequence(sequence, EffectResultType.PLAY_ANIMATION);
		for (Effect effect : skillEffects) {
			if (effect.isNeedAnimation()){
				Animation animation = ClientAnimationService.getSkillAnimation(CombatSkillEnum.NORMAL_ATTACK.getValue(), effect.getSourcePos(), effect.getTargetPos());
				as.add(animation);
			}
		}
		combat.addAnimation(as);
		// 接受攻击召唤师的
		List<Effect> zhsEffects = skillEffects.stream().filter(effect -> PositionService.isZhaoHuanShiPos(effect.getTargetPos())).collect(Collectors.toList());
		skillEffects.removeAll(zhsEffects);
		zhsEffects = sectionSkillService.runZhsDefenseSkillResult(SkillSection.getZhsSection(), combat, zhsEffects);
		acceptEffectService.acceptNormalAttackEffect(combat, zhsEffects);

		combatRunesPerformService.runBeforeNormalNormalDefenceRunes(combat, skillEffects);

		SkillSection beforeNormalDefense = SkillSection.getBeforeNormalDefenseSection();
		List<Effect> beforeDefenseEffects = sectionSkillService.runDefenseSkillResult(beforeNormalDefense, combat, skillEffects, true);
		// 法术物理防御攻击
		SkillSection normalDefense = SkillSection.getNormalDefenseSection();
		List<Effect> defenseEffects = sectionSkillService.runDefenseSkillResult(normalDefense, combat, skillEffects, true);
		SkillSection defenseAfter = SkillSection.getNormalDefenseAfterSection();
		List<Effect> afterEffects = sectionSkillService.runDefenseAfter(defenseAfter, combat, defenseEffects);
		SkillSection physicalCounterSection = SkillSection.getPhysicalCounterSection();
		List<Effect> physicalCounterEffects = sectionSkillService.runPysicalCounter(physicalCounterSection, combat, defenseEffects);
		defenseEffects.addAll(afterEffects);
		defenseEffects.addAll(physicalCounterEffects);
		if (!defenseEffects.isEmpty()) {
			// 被击中处理 即执行4599
			sectionSkillService.runBeHitNormalSkillResult(combat, defenseEffects);
			acceptEffectService.acceptNormalAttackEffect(combat, defenseEffects);
		}

	}

	private void runSectionSkillAll(SkillSection section, PerformSkillParam[] params) {
		// 接受行动效果
		for (PerformSkillParam psp : params) {
			runSectionSkillAll(section, psp);
		}
	}

	private void runSectionSkillAll(SkillSection section, PerformSkillParam psp) {
		// 接受行动效果
		if (null == psp.getPerformCard()) {
			return;
		}
		List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(section);
		if (effectSkills.isEmpty()) {
			return;
		}
		for (BattleSkill sectionSkill : effectSkills) {
			if (null == psp.getPerformCard()) {
				return;
			}
			Optional<Action> skillEffect = sectionSkillService.runSkill(section, sectionSkill, psp);
			if (skillEffect.isPresent()) {
				List<Effect> effects = skillEffect.get().getEffects();
				if (effects != null) {
					effects = skillRoundService.runZhsEffects(psp.getCombat(), effects);
				}
				acceptEffectService.acceptNormalAttackEffect(psp.getCombat(), skillEffect.get().getEffects());
			}
		}
	}

	/**
	 * 获取属性克制的技能  如 type是金  则返回金克木
	 * @param psp
	 * @return
	 */
	private Optional<BattleSkill> getAttributeRestraintSkill(PerformSkillParam psp) {
		if (null == psp.getPerformCard()) {
			return Optional.empty();
		}
		//属性  为10~50
		int index=psp.getPerformCard().getType().getValue()/10-1;
		if (index<0 || index>5){
			//无效的索引
			return Optional.empty();
		}
		return Optional.of(BattleSkill.instanceAttributeRestraintSkill(CombatConfig.attributeRestraintList[index]));
	}

	/**
	 * 获取被属性克制的技能  如 type是木  则返回金克木
	 * @param psp
	 * @return
	 */
	private Optional<BattleSkill> getAttributeRestraintReverseSkill(PerformSkillParam psp) {
		if (!psp.getFaceToFaceCard().isPresent()) {
			return Optional.empty();
		}
		int index=psp.getFaceToFaceCard().get().getType().getValue()/10-1;
		if (index<0 || index>5){
			//无效的索引
			return Optional.empty();
		}
		return Optional.of(BattleSkill.instanceAttributeRestraintSkill(CombatConfig.attributeRestraintReverseList[index]));
	}

	private List<Effect> runSectionSkillFirst(SkillSection section, PerformSkillParam firstPsp, PerformSkillParam secondPsp) {
		List<Effect> effects = new ArrayList<>();
		// 行动
		Optional<Action> action1 = sectionSkillService.runSectionSkillFirst(section, firstPsp);
		Optional<Action> action2 = sectionSkillService.runSectionSkillFirst(section, secondPsp);
		if (action1.isPresent()) {
			effects.addAll(action1.get().getEffects());
		}
		if (action2.isPresent()) {
			effects.addAll(action2.get().getEffects());
		}
		// 修正effect的序列值，保证动画同步
		if (!effects.isEmpty()) {
			Effect maxSeqEffect = effects.stream().max(Comparator.comparing(Effect::getSequence)).get();
			for (int i = 0; i < effects.size(); i++) {
				effects.get(i).setSequence(maxSeqEffect.getSequence());
			}
		}

		return effects;
	}

	/**
	 * 物理攻击前的先手攻击
	 *
	 * @param combat
	 * @param firstPsp
	 * @param secondPsp
	 * @return
	 */
	private List<Effect> firstAttack(Combat combat, PerformSkillParam firstPsp, PerformSkillParam secondPsp) {
		List<Effect> resultEffects = new ArrayList<>();
		// 获取先手技能
		SkillSection beforeSection = SkillSection.getNormalPriority1Section();
		// 获取类似死斗、销魂技能
		// int[] skillList = {4404, 4406};
		// SkillSection attackSection = new SkillSection(skillList);
		PerformSkillParam[] psps = { firstPsp, secondPsp };
		for (PerformSkillParam psp : psps) {
			if (psp==null || null == psp.getPerformCard()) {
				continue;
			}
			// 先获得死斗和销魂等技能加成 再执行先手技能
			List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(beforeSection);
			if (effectSkills.isEmpty()) {
				continue;
			}
			for (BattleSkill skill:effectSkills){
				Optional<Action> optional = sectionSkillService.runSkill(beforeSection, skill, psp);
				if (optional.isPresent() && optional.get().existsEffect()) {
					resultEffects.addAll(optional.get().getEffects());
					break;
				}
			}
		}
		return resultEffects;
	}

}