package com.bbw.god.game.combat;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.cache.CombatCache;
import com.bbw.god.game.combat.cache.CombatCacheUtil;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.BattleSkillType;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseTableService;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import com.bbw.god.game.combat.skill.service.ISkillNormalBuffDefenseService;
import com.bbw.god.game.config.card.CardEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-10 00:27
 */
@Slf4j
@Service
public class SectionSkillService {
	@Autowired
	private AttackServiceFactory serviceFactory;
	@Autowired
	private CombatRunesPerformService runesPerformService;

	/**
	 * 按照卡牌技能顺序执行卡牌此环节所有技能的第一个
	 *
	 * @param psp
	 * @param section
	 * @return
	 */
	public Optional<Action> runSectionSkillFirst(SkillSection section, PerformSkillParam psp) {
		if (null==psp || null == psp.getPerformCard()) {
			return Optional.empty();
		}
		List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(section);
		if (effectSkills.isEmpty()) {
			return Optional.empty();
		}
		return runSkill(section, effectSkills.get(0), psp);
	}

	/**
	 * 依次执行卡牌此环节所有技能，其中一个产生效果则终止
	 *
	 * @param psp
	 * @param section
	 * @return
	 */
	public Optional<Action> runSectionSkillFirstEffect(SkillSection section, PerformSkillParam psp) {
		if (null == psp.getPerformCard()) {
			return Optional.empty();
		}
		List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(section);
		if (effectSkills.isEmpty()) {
			return Optional.empty();
		}
		for (BattleSkill sectionSkill : effectSkills) {
			if (null == psp.getPerformCard()) {
				return Optional.empty();
			}
			Optional<Action> skillResult = runSkill(section, sectionSkill, psp);
			if (skillResult.isPresent() && skillResult.get().getTakeEffect()) {
				return skillResult;
			}
		}
		return Optional.empty();
	}

	/**
	 * 依次执行卡牌此环节所有技能
	 *
	 * @param section
	 * @param psp
	 * @return
	 */
	public List<Action> runSectionSkills(SkillSection section, PerformSkillParam psp) {
		List<Action> actions = new ArrayList<>();
		if (null == psp.getPerformCard()) {
			return actions;
		}
		List<BattleSkill> effectSkills = psp.getPerformCard().getEffectiveSkills(section);
		if (effectSkills.isEmpty()) {
			return actions;
		}
		for (BattleSkill sectionSkill : effectSkills) {
			Optional<Action> skillResult = runSkill(section, sectionSkill, psp);
			if (skillResult.isPresent() && skillResult.get().getTakeEffect()) {
				actions.add(skillResult.get());
			}
		}
		return actions;
	}

	/**
	 * 技能防护
	 *
	 * @param combat
	 * @param effects
	 * @param defenseSection
	 * @return
	 */
	public List<Effect> runDefenseSkillResult(SkillSection defenseSection, Combat combat, List<Effect> effects, boolean checkOtherDefense) {
		if (effects.isEmpty()) {
			return effects;
		}
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		for (Effect effect : effects) {
			// 伤害力3的技能无法防御
			if (effect.isMaxPower()) {
				acceptCardEffects.add(effect);
				continue;
			}
			if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
				acceptCardEffects.add(effect);
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam performSkillParam = new PerformSkillParam(combat, effect);
			int effectSkillId=effect.getPerformSkillID();
			BattleCard performCard = performSkillParam.getPerformCard();
			if (performCard == null) {
				// 卡牌已死亡,且不在战场了
				continue;
			}
			// 进行技能防御
			Optional<Action> defenseSkillAction = runSectionSkillFirstEffect(defenseSection, performSkillParam);
			if (defenseSkillAction.isPresent() && defenseSkillAction.get().getTakeEffect()) {
				if (defenseSkillAction.get().existsEffect()) {
					// 发动技能，添加防御后的伤害
					acceptCardEffects.addAll(defenseSkillAction.get().getEffects());
				}
			}
			try {
				if (performSkillParam.getReceiveEffect() != null) {
					acceptCardEffects.addAll(runDefenseFromOtherCard(performCard,performSkillParam));
				}else if (performCard.getImgId()== CardEnum.GOD_CHONG_HOU_HU.getCardId() && performSkillParam.getPerformPlayer().getUid()>0){
					CombatCache cache = CombatCacheUtil.getCombatCache(performSkillParam.getPerformPlayer().getUid(), combat.getId());
					cache.addGodChongHouHuDefenseMagicAttack(effectSkillId);
					CombatCacheUtil.setCombatCache(cache);
				}
			}catch (Exception e){
				log.error(e.getMessage(),e);
			}

		}
		return acceptCardEffects;
	}

	private List<Effect> runDefenseFromOtherCard(BattleCard performCard,PerformSkillParam performSkillParam){
		// 伤害未清空 套盾防御
		List<Integer> ids = performCard.getCardStatusSkills(SkillSection.getShieldSection());
		try {
			for (Integer skillId : ids) {
				ISkillDefenseService service = serviceFactory.getBattleSkillDefenseService(skillId);
				Action action = service.takeDefense(performSkillParam);
				if(action.getTakeEffect()){
					performCard.incTimesCardStatus(skillId);
					return action.getEffects();
				}
			}
		} catch (Exception e) {
			log.error("执行[{}]卡牌额外添加的延迟技能失败！{}", performCard.getName(),JSONUtil.toJson(performCard.getStatus()));
			log.error(e.getMessage(), e);
		}
		SkillSection[] sections={SkillSection.getSkillShareDefenseSection(),SkillSection.getShareDefenseSection()};
		for (SkillSection section : sections) {
			if (performSkillParam.getReceiveEffect() == null) {
				break;
			}
			BattleCard[] cards = performSkillParam.getPerformPlayer().getPlayingCards();
			for (BattleCard card:cards){
				if (card==null){
					continue;
				}
				PerformSkillParam psp=new PerformSkillParam(performSkillParam.getCombat(),card);
				psp.setReceiveEffect(performSkillParam.getReceiveEffect());
				boolean skillIV=false;
				try{
					int targetPos = performSkillParam.getReceiveEffect().getTargetPos();
					if (PositionService.isPlayingPos(targetPos)){
						Optional<BattleCard> battleCard = PositionService.getCard(psp.getPerformPlayer(), targetPos);
						if (battleCard.isPresent() && battleCard.get().getImgId()==CardEnum.GOD_CHONG_HOU_HU.getCardId()){
							int[] skillIds= BattleSkillDefenseTableService.getDefenseTableBySkillId(CombatSkillEnum.FA_SHEN.getValue());
							int performId=performSkillParam.getReceiveEffect().getPerformSkillID();
							for (int skillId : skillIds) {
								if (skillId==performId){
									skillIV=true;
									break;
								}
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}

				Optional<Action> optionalAction = runSectionSkillFirstEffect(section, psp);
				if (optionalAction.isPresent() && optionalAction.get().getTakeEffect()){
					if (skillIV){
						try {
							if (performSkillParam.getPerformPlayer().getUid()>0){
								CombatCache cache = CombatCacheUtil.getCombatCache(performSkillParam.getPerformPlayer().getUid(), psp.getCombat().getId());
								cache.setGodChongHouHuDefenseFengJin(cache.getGodChongHouHuDefenseFengJin()+1);
								cache.setGodChongHouHuDefenseMagicAttack(cache.getGodChongHouHuDefenseMagicAttack()+1);
								CombatCacheUtil.setCombatCache(cache);
							}
						}catch (Exception e){
							log.error(e.getMessage(),e);
						}
					}
					return optionalAction.get().getEffects();
				}
			}
		}
		List<Effect> acceptCardEffects=new ArrayList<>();
		acceptCardEffects.add(performSkillParam.getReceiveEffect());
		return acceptCardEffects;
	}

	/**
	 * 执行芒刺 类型
	 * @param defenseSection
	 * @param combat
	 * @param effects
	 * @return
	 */
	public List<Effect> runDefenseAfter(SkillSection defenseSection, Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return effects;
		}
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		for (Effect effect : effects) {
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam performSkillParam = new PerformSkillParam(combat, effect);
			BattleCard performCard = performSkillParam.getPerformCard();
			performSkillParam.setReceiveEffects(effects);
			if (performCard == null) {
				// 卡牌已死亡,且不在战场了
				continue;
			}
			// 进行技能防御
			Optional<Action> defenseSkillAction = runSectionSkillFirstEffect(defenseSection, performSkillParam);
			if (defenseSkillAction.isPresent() && defenseSkillAction.get().getTakeEffect()) {
				if (defenseSkillAction.get().existsEffect()) {
					// 发动技能，添加防御后的伤害
					acceptCardEffects.addAll(defenseSkillAction.get().getEffects());
				}
			}
		}
		return acceptCardEffects;
	}

	/**
	 * 执行物理反制
	 *
	 * @param physicalCounterSection
	 * @param combat
	 * @param effects
	 * @return
	 */
	public List<Effect> runPysicalCounter(SkillSection physicalCounterSection, Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return effects;
		}
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		for (Effect effect : effects) {
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam performSkillParam = new PerformSkillParam(combat, effect);
			BattleCard performCard = performSkillParam.getPerformCard();
			performSkillParam.setReceiveEffects(effects);
			if (performCard == null) {
				// 卡牌已死亡,且不在战场了
				continue;
			}
			// 进行技能反制
			List<Action> actions = runSectionSkills(physicalCounterSection, performSkillParam);
			if (ListUtil.isNotEmpty(actions)) {
				for (Action action : actions) {
					if (action.existsEffect()) {
						// 发动技能，反制的效果
						acceptCardEffects.addAll(action.getEffects());
					}
				}
			}
		}
		return acceptCardEffects;
	}

	/**
	 * 执行物理反制后
	 *
	 * @param physicalCounterSection
	 * @param combat
	 * @param effects
	 * @return
	 */
	public List<Effect> runAfterPysicalCounter(SkillSection physicalCounterSection, Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return effects;
		}
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		for (Effect effect : effects) {
			// 只处理阵位卡牌
			if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam performSkillParam = new PerformSkillParam(combat, effect);
			performSkillParam.setReceiveEffects(effects);
			// 进行技能反制
			List<Action> actions = runSectionSkills(physicalCounterSection, performSkillParam);
			if (ListUtil.isNotEmpty(actions)) {
				for (Action action : actions) {
					if (action.existsEffect()) {
						// 发动技能，反制的效果
						acceptCardEffects.addAll(action.getEffects());
					}
				}
			}
		}
		return acceptCardEffects;
	}

	/**
	 * 物理击中
	 *
	 * @param combat
	 * @param effects
	 */
	public void runBeHitNormalSkillResult(Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return;
		}
		SkillSection beHitSection = SkillSection.getNormalBeHitSectionSection();
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		for (Effect effect : effects) {
			// 伤害力3的技能无法防御
			if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
				acceptCardEffects.add(effect);
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam performSkillParam = new PerformSkillParam(combat, effect);
			runSectionSkillFirstEffect(beHitSection, performSkillParam);
		}
	}

	/**
	 * 召唤师相关防御
	 *
	 * @param defenseSection
	 * @param combat
	 * @param effects
	 * @return
	 */
	public List<Effect> runZhsDefenseSkillResult(SkillSection defenseSection, Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return effects;
		}
		List<Effect> acceptCardEffects = new ArrayList<>(effects.size());
		//
		for (Effect effect : effects) {
			if (effect.getPerformSkillID()== RunesEnum.TIAN_JIE.getRunesId()){
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			Player targetPlayer=combat.getPlayer(effect.getTargetPos());
			runesPerformService.runAttackPlayHpRunes(combat,targetPlayer,effect);
			BattleCard[] cards = targetPlayer.getPlayingCards();
			boolean defense = false;
			for (BattleCard card : cards) {
				if (card != null && !card.isKilled() && card.getFirstSkill(defenseSection) != null) {
					PerformSkillParam targetCardSkill = new PerformSkillParam(combat, card);
					targetCardSkill.setReceiveEffect(effect);
					// 进行技能防御
					Optional<Action> defenseSkillAction = runSectionSkillFirstEffect(defenseSection, targetCardSkill);
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
			if (!defense) {
				acceptCardEffects.add(effect);
			}
		}
		return acceptCardEffects;
	}

	public List<Effect> runStrikeBack(Combat combat, List<Effect> effects) {
		// 法术反击[3201,3299]
		SkillSection fightBackSection = SkillSection.getFightBackSection();
		if (effects.isEmpty()) {
			return effects;
		}
		List<Effect> acceptCardEffects = new ArrayList<>();
		//
		for (Effect effect : effects) {
			// 作用自身的不反击
			if (effect.isEffectSelf()) {
				continue;
			}
			Optional<Effect> effectOptional = acceptCardEffects.stream()
					.filter(p -> p.getResultType().equals(EffectResultType.CARD_POSITION_CHANGE)
							&& p.getTargetPos() == effect.getSourcePos())
					.findAny();
			if (effectOptional.isPresent()) {
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam targetCardSkill = new PerformSkillParam(combat, effect);
			// 进行技能反击
			Optional<Action> defenseSkillAction = runSectionSkillFirstEffect(fightBackSection, targetCardSkill);
			if (defenseSkillAction.isPresent() && defenseSkillAction.get().getTakeEffect()) {
				if (defenseSkillAction.get().existsEffect()) {
					// 发动技能，反击后的伤害
					acceptCardEffects.addAll(defenseSkillAction.get().getEffects());
				}
			}
		}
		return acceptCardEffects;

	}

	/**
	 * 执行法术反制
	 *
	 * @param combat
	 * @param effects
	 * @return
	 */
	public List<Effect> runSpecllCounter(Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return effects;
		}
		SkillSection specllCounterSection = SkillSection.getSpecllCounterSection();
		List<Effect> acceptCardEffects = new ArrayList<>();
		//
		for (Effect effect : effects) {
			// 作用自身的不反制
			if (effect.isEffectSelf()) {
				continue;
			}
			Optional<Effect> effectOptional = acceptCardEffects.stream()
					.filter(p -> p.getResultType().equals(EffectResultType.CARD_POSITION_CHANGE)
							&& p.getTargetPos() == effect.getSourcePos())
					.findAny();
			if (effectOptional.isPresent()) {
				continue;
			}
			// 设置效果影响的目标卡牌，发送技能的参数
			PerformSkillParam targetCardSkill = new PerformSkillParam(combat, effect);
			// 进行技能反制
			List<Action> actions = runSectionSkills(specllCounterSection, targetCardSkill);
			if (ListUtil.isNotEmpty(actions)) {
				for (Action action : actions) {
					if (action.existsEffect()) {
						// 发动技能，反制的效果
						acceptCardEffects.addAll(action.getEffects());
					}
				}
			}
		}
		return acceptCardEffects;

	}

	// 释放法术
	public Optional<Action> runSkill(SkillSection section, BattleSkill skill, PerformSkillParam psp) {
		BattleCard performCard = psp.getPerformCard();
		if (performCard == null || !performCard.effectiveSkill(skill.getId())) {
			// 二次确认 技能是否有效
			return Optional.empty();
		}
		PositionType type = PositionService.getPositionType(performCard.getPos());
		SkillSection dieSection = SkillSection.getDieSection();
		// 存在逃遁技能执行，跳过判断卡牌是否死亡
		if (skill.getId() != CombatSkillEnum.TAO_DUN.getValue()){
			// 不是死亡技能，但卡牌已死或卡不在战场上 则不发动
			if (!dieSection.contains(skill.getId()) && (performCard.isKilled() || !PositionType.BATTLE.equals(type))) {
				return Optional.empty();
			}
		}
		try {
			Action action =takeSkillService(skill.getId(),section,psp);
			boolean needMultiple=false;
			if (psp.getPerformCard()!=null && psp.getPerformCard().hasEffectiveSkill(CombatSkillEnum.XF.getValue())){
				needMultiple=true;
			}
			// 设置行动效果来源
			SkillSection weak=SkillSection.getSkillEffectWeakenSection();
			SkillSection fightBack=SkillSection.getFightBackSection();
			for (Effect effect : action.getEffects()) {
				if (effect.getTargetPos() < 0) {
					throw CoderException.high("不能没有效果目标！" + effect);
				}
				if (effect.getFromCardId()<=0 && psp.getPerformCard()!=null){
					effect.setFromCardId(psp.getPerformCard().getImgId());
				}
				if (effect.getPerformSkillID() == 0) {
					effect.setPerformSkillID(skill.getId());
				}
				if (effect.getSourceID() == 0) {
					effect.setSourceID(skill.getId());
				}
				if (skill.getId() != 4599 && effect.getSourcePos() < 0) {// 物理防御不设置
					effect.setSourcePos(psp.getPerformCard().getPos());
				}
				if (effect.getSequence() < 0) {
					effect.setSequence(psp.getNextAnimationSeq());
				}
				if (needMultiple && effect.isValueEffect() && !weak.contains(skill.getId()) && !fightBack.contains(skill.getId()) && skill.getId()>3100 && skill.getId()<3200){
					CardValueEffect valueEffect=effect.toValueEffect();
					if (valueEffect.getRoundHp()<0){
						valueEffect.setRoundHp(valueEffect.getRoundHp()*2);
					}else if (valueEffect.getHp()<0){
						valueEffect.setHp(valueEffect.getHp()*2);
					}
					if (CombatSkillEnum.LIE_YAN.getValue()== skill.getId()){
						if (valueEffect.getRoundAtk()<0){
							valueEffect.setRoundAtk(valueEffect.getRoundAtk()*2);
						}else if (valueEffect.getAtk()<0){
							valueEffect.setAtk(valueEffect.getAtk()*2);
						}
					}
				}
				log.debug("\n{} 结果：{}", effect.printString(), JSONUtil.toJson(effect));
				performCard.addSkillLog(skill.getId(), psp.getCombat().getRound(), effect.getTargetPos());
			}
			// ---给客户端的动画处理,这里只生产卡牌行动动画，所有结果性的动画暂不处理。结果性动画由AcceptEffectService处理
			genClientAnimation(section, skill, psp, action);
			// 上场技能只能用1次
			SkillSection oneTimes = SkillSection.getPerformOneTimeSection();
			SkillSection magic=SkillSection.getSkillAttackSection();
			if (oneTimes.contains(skill.getId())) {
				skill.getTimesLimit().forbid();
			} else if (magic.contains(skill.getId())) {
				skill.getTimesLimit().forbidOneRound(-1);
			} else {
				// 减少1次使用机会
				skill.getTimesLimit().lostTimes();
			}
			skill.addPerformTimes();
//			//存在效果 添加符文效果
			boolean isDefenseSkill = SkillSection.getSkillDefenseSection().contains(skill.getId());
			isDefenseSkill = isDefenseSkill || SkillSection.getShareDefenseSection().contains(skill.getId());
			isDefenseSkill = isDefenseSkill || SkillSection.getSkillShareDefenseSection().contains(skill.getId());
			if (action.existsEffect() && !isDefenseSkill) {
				List<Effect> effects = runesPerformService.runPerformSkillRunes(psp.getCombat(), performCard, action.getEffects());
				action.setEffects(effects);
			}
			return Optional.of(action);
		} catch (Exception e) {
			log.error("执行[{}]卡牌[{}]技能失败！", performCard.getName(), skill.getId());
			System.err.println("执行[" + performCard.getName() + "]卡牌[" + skill.getId() + "]技能失败！");
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
	public Optional<Action> runDieSkill(SkillSection section, BattleSkill skill, PerformSkillParam psp) {
		BattleCard performCard = psp.getPerformCard();
		try {
			Action action =takeSkillService(skill.getId(),section,psp);
			boolean needMultiple=false;
			if (psp.getPerformCard()!=null && psp.getPerformCard().hasEffectiveSkill(CombatSkillEnum.XF.getValue())){
				needMultiple=true;
			}
			// 设置行动效果来源
			SkillSection weak=SkillSection.getSkillEffectWeakenSection();
			SkillSection fightBack=SkillSection.getFightBackSection();
			for (Effect effect : action.getEffects()) {
				if (effect.getTargetPos() < 0) {
					throw CoderException.high("不能没有效果目标！" + effect);
				}
				if (effect.getFromCardId()<=0 && psp.getPerformCard()!=null){
					effect.setFromCardId(psp.getPerformCard().getImgId());
				}
				if (effect.getPerformSkillID() == 0) {
					effect.setPerformSkillID(skill.getId());
				}
				if (effect.getSourceID() == 0) {
					effect.setSourceID(skill.getId());
				}
				if (skill.getId() != 4599 && effect.getSourcePos() < 0) {// 物理防御不设置
					effect.setSourcePos(psp.getPerformCard().getPos());
				}
				if (effect.getSequence() < 0) {
					effect.setSequence(psp.getNextAnimationSeq());
				}
				if (needMultiple && effect.isValueEffect() && !weak.contains(skill.getId()) && !fightBack.contains(skill.getId()) && skill.getId()>3100 && skill.getId()<3200){
					CardValueEffect valueEffect=effect.toValueEffect();
					if (valueEffect.getRoundHp()<0){
						valueEffect.setRoundHp(valueEffect.getRoundHp()*2);
					}else if (valueEffect.getHp()<0){
						valueEffect.setHp(valueEffect.getHp()*2);
					}
					if (CombatSkillEnum.LIE_YAN.getValue()== skill.getId()){
						if (valueEffect.getRoundAtk()<0){
							valueEffect.setRoundAtk(valueEffect.getRoundAtk()*2);
						}else if (valueEffect.getAtk()<0){
							valueEffect.setAtk(valueEffect.getAtk()*2);
						}
					}
				}
				log.debug("\n{} 结果：{}", effect.printString(), JSONUtil.toJson(effect));
				performCard.addSkillLog(skill.getId(), psp.getCombat().getRound(), effect.getTargetPos());
			}
			// ---给客户端的动画处理,这里只生产卡牌行动动画，所有结果性的动画暂不处理。结果性动画由AcceptEffectService处理
			genClientAnimation(section, skill, psp, action);
			// 上场技能只能用1次
			SkillSection oneTimes = SkillSection.getPerformOneTimeSection();
			SkillSection magic=SkillSection.getSkillAttackSection();
			if (oneTimes.contains(skill.getId())) {
				skill.getTimesLimit().forbid();
			} else if (magic.contains(skill.getId())) {
				skill.getTimesLimit().forbidOneRound(-1);
			} else {
				// 减少1次使用机会
				skill.getTimesLimit().lostTimes();
			}
			skill.addPerformTimes();
//			//存在效果 添加符文效果
			boolean isDefenseSkill = SkillSection.getSkillDefenseSection().contains(skill.getId());
			isDefenseSkill = isDefenseSkill || SkillSection.getShareDefenseSection().contains(skill.getId());
			isDefenseSkill = isDefenseSkill || SkillSection.getSkillShareDefenseSection().contains(skill.getId());
			if (action.existsEffect() && !isDefenseSkill) {
				List<Effect> effects = runesPerformService.runPerformSkillRunes(psp.getCombat(), performCard, action.getEffects());
				action.setEffects(effects);
			}
			return Optional.of(action);
		} catch (Exception e) {
			log.error("执行[{}]卡牌[{}]技能失败！", performCard.getName(), skill.getId());
			System.err.println("执行[" + performCard.getName() + "]卡牌[" + skill.getId() + "]技能失败！");
			log.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
	private Action takeSkillService(int skillId,SkillSection section,PerformSkillParam psp){
		if (BattleSkillType.DEFENSE.equals(section.getBelongTo())){
			ISkillDefenseService defenseService=serviceFactory.getBattleSkillDefenseService(skillId);
			return defenseService.takeDefense(psp);
		}
		if (BattleSkillType.DEFENSE_NORMAL_BUFF.equals(section.getBelongTo())){
			ISkillNormalBuffDefenseService defenseService=serviceFactory.getNormalBuffDefenseService(skillId);
			return defenseService.takeNormalBuffDefense(psp);
		}
		BattleSkillService service = serviceFactory.getSkillAttackService(skillId);
		return service.productAction(psp);
	}
	private void genClientAnimation(SkillSection section, BattleSkill skill, PerformSkillParam psp, Action action) {
		// 技能内部自定义实现了动画
		if (!action.getClientActions().isEmpty()) {
			psp.getCombat().addAnimations(action.getClientActions());
			return;
		}
		// 不采用默认动画方式，或者技能没有发动
		if (!section.isAutoAnimation() || !action.getTakeEffect() || !action.isNeedAddAnimation()) {
			return;
		}
		// 技能发动，但是没有产生需要执行的效果。如：防守技能，清除原来的行动效果。
		if (!action.existsEffect()) {
			AnimationSequence animation = ClientAnimationService.getSkillAction(psp.getCombat().getAnimationSeq(),
					skill.getId(), psp.getPerformCard().getPos());
			psp.getCombat().addAnimation(animation);
			return;
		}

		// 默认动画环节
		for (Effect effect : action.getEffects()) {
			AnimationSequence animation = ClientAnimationService.getSkillAction(effect.getSequence(), skill.getId(),
					effect.getSourcePos(), effect.getTargetPos());
			psp.getCombat().addAnimation(animation);
		}
	}
}