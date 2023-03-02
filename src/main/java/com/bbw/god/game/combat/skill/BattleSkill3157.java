package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.AttackServiceFactory;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseTableService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 *
 * 神光（3157）：对敌方全体卡牌释放一个合理的技能，可对云台生效，自带【返照】
 *
 *
 */
@Service
public class BattleSkill3157 extends BattleSkillService {
	private static final int SKILL_ID = 3157;// 技能ID
	@Autowired
	private AttackServiceFactory attackServiceFactory;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cardList=psp.getOppoPlayingCards(true);
		if (ListUtil.isEmpty(cardList)){
			return ar;
		}
		ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(),psp.getPerformCard().getPos()));
		int fromPos=psp.getPerformCard().getPos();
		for (BattleCard card : cardList) {
			int skillId = ShenGuang.randomSkillId(card);
			if (skillId==0){
				continue;
			}
			BattleSkillService service = attackServiceFactory.getSkillAttackService(skillId);
			Action action = service.buildEffects(card, psp);
			if (action.existsEffect()){
				for (Effect effect : action.getEffects()) {
					effect.setSourcePos(fromPos);
					effect.setSourceID(getMySkillId());
					effect.setPerformSkillID(skillId);
					if (effect.getSequence()<=0){
						effect.setSequence(psp.getNextAnimationSeq());
					}
				}
				ar.addEffects(action.getEffects());
			}
			ar.getClientActions().addAll(getClientAnimation(skillId,psp,action));
		}
		return ar;
	}

	private List<AnimationSequence> getClientAnimation(int skillId, PerformSkillParam psp, Action action) {
		// 技能内部自定义实现了动画
		if (!action.getClientActions().isEmpty()) {
			return action.getClientActions();
		}
		// 不采用默认动画方式，或者技能没有发动
		if (!action.getTakeEffect() || !action.isNeedAddAnimation()) {
			return new ArrayList<>();
		}
		// 技能发动，但是没有产生需要执行的效果。如：防守技能，清除原来的行动效果。
		if (!action.existsEffect()) {
			AnimationSequence animation = ClientAnimationService.getSkillAction(psp.getCombat().getAnimationSeq(),
					skillId, psp.getPerformCard().getPos());
			return Arrays.asList(animation);
		}
		List<AnimationSequence> sequences=new ArrayList<>();
		// 默认动画环节
		for (Effect effect : action.getEffects()) {
			AnimationSequence animation = ClientAnimationService.getSkillAction(effect.getSequence(), skillId,
					effect.getSourcePos(), effect.getTargetPos());
			sequences.add(animation);
		}
		return sequences;
	}

	@Getter
	@AllArgsConstructor
	public enum ShenGuang{
		FJ(3101,false,false,4),
		LJ(3102,true,true,4),
		MH(3106,false,false,4),
		WF(3107,false,false,4),
		SD(3108,false,false,4),
		JS(3112,false,false,4),
		RD(3127,false,false,4),
		FZ(3131,false,true,1),
		XIXING(3133,false,false,4),
		ZHEN_SHE(3135,false,false,4),
		HUO_QIU(3139,false,false,4),
		TIAO_BO(3144,false,false,4),
		LY(3145,true,true,4),
		SY(3147,false,true,4),
		;
		private int skillId;
		private boolean onlyYunTai;
		private boolean yunTai;
		private int possibility;

		public static int randomSkillId(BattleCard targetCard){
			boolean yunTai= PositionService.isYunTaiPos(targetCard.getPos());
			Set<Integer> ignores=new HashSet<>();
			for (BattleSkill skill : targetCard.getSkills()) {
				if (!BattleSkill3147.canBan(skill.getId())){
					ignores.add(CombatSkillEnum.SY.getValue());
				}else if (skill.getId()!= CombatSkillEnum.HG.getValue()){
					int[] table = BattleSkillDefenseTableService.getDefenseTableBySkillId(skill.getId());
					for (int i : table) {
						ignores.add(i);
					}
				}
			}
			List<ShenGuang> randomList=new ArrayList<>();
			int sumPossibility=0;
			for (ShenGuang shenGuang : values()) {
				if (shenGuang.isOnlyYunTai() && !yunTai){
					continue;
				}
				if (yunTai && !shenGuang.isYunTai()){
					continue;
				}
				if (ignores.contains(shenGuang.getSkillId())){
					continue;
				}
				randomList.add(shenGuang);
				sumPossibility+=shenGuang.getPossibility();
			}
			if (sumPossibility==0){
				return 0;
			}
			int seed= PowerRandom.getRandomBySeed(sumPossibility);
			int sum=0;
			for (ShenGuang guang : randomList) {
				sum+=guang.getPossibility();
				if (sum>=seed){
					return guang.getSkillId();
				}
			}
			return 0;
		}
	}
}
