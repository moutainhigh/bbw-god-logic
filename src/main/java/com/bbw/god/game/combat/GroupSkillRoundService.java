package com.bbw.god.game.combat;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.group.GroupSkillService;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-10 00:05
 */
@Slf4j
@Scope("prototype")
@Service
public class GroupSkillRoundService {
	@Autowired
	private AttackServiceFactory serviceFactory;
	@Autowired
	private SectionSkillService sectionSkillService;
	@Autowired
	private AcceptEffectService acceptEffectService;
	@Autowired
	private SkillRoundService skillRoundService;
	@Autowired
	private CombatRunesPerformService combatRunesPerformService;
	// 法术防御
	private static final SkillSection SKILL_DEFENSE = SkillSection.getSkillDefenseSection();
	//法术削弱
	private static final SkillSection SKILL_WEAKEN = SkillSection.getSkillEffectWeakenSection();

	public void round(Combat combat) {
		// 组合技能
		int[] groupSkills = SkillSection.getGroupSkillSection().getSkills();
		// 结算组合技能[2000,2099]
		Player firster = combat.getFirstPlayer();
		Player seconder = combat.getSecondPlayer();
		List<Player> playerList = new ArrayList<>();
		if (!seconder.hasStatus(RunesEnum.TIAO_LI.getRunesId())&& !seconder.hasStatus(RunesEnum.TIAO_LI_PLAYER.getRunesId())
				&& !firster.hasStatus(RunesEnum.LI_JIAN_ENTRY.getRunesId())) {
			playerList.add(firster);
		}
		if (!firster.hasStatus(RunesEnum.TIAO_LI.getRunesId()) && !firster.hasStatus(RunesEnum.TIAO_LI_PLAYER.getRunesId())
				&& !seconder.hasStatus(RunesEnum.LI_JIAN_ENTRY.getRunesId())) {
			playerList.add(seconder);
		}


		if (playerList.isEmpty()) {
			return;
		}
		for (int groupId : groupSkills) {
			// 获取此组合技能的算法
			GroupSkillService groupService = serviceFactory.getGroupAttackService(groupId);
			// 先手、后手 产生攻击效果
			for (Player player : playerList) {
				Optional<Action> playerAction = groupService.attack(groupId, combat, player);
				if (!playerAction.isPresent()) {
					continue;
				}
				// 执行攻击结果
				List<Effect> todoEffects = playerAction.get().getEffects();
				todoEffects = skillRoundService.runZhsEffects(combat, todoEffects);
				combatRunesPerformService.runBeforeSkillDefenceRunes(combat, todoEffects);
				if (CardSkillTool.getCardSkillOpById(groupId).get().isDefensible()) {
					// 返回法术防御效果
					List<Effect> defenseL1Effects = sectionSkillService.runDefenseSkillResult(SKILL_DEFENSE, combat,
							todoEffects,false);
					if (!defenseL1Effects.isEmpty()){
						defenseL1Effects = sectionSkillService.runDefenseSkillResult(SKILL_WEAKEN, combat,
								defenseL1Effects,true);
					}
					if (!defenseL1Effects.isEmpty()) {
						// 法术反击[3201,3299]
						List<Effect> fightBackEffects = sectionSkillService.runStrikeBack(combat, defenseL1Effects);
						//处理法术反制
						List<Effect> counterEffects = sectionSkillService.runSpecllCounter(combat, defenseL1Effects);
						acceptEffectService.acceptSkillAttackEffect(combat, defenseL1Effects);
						acceptEffectService.acceptSkillAttackEffect(combat, fightBackEffects);
						acceptEffectService.acceptSkillAttackEffect(combat, counterEffects);

						combatRunesPerformService.runBeforeSkillDefenceRunes(combat, fightBackEffects);
						// 返照
						List<Effect> defenseL2Effects = sectionSkillService.runDefenseSkillResult(SKILL_DEFENSE, combat,
								fightBackEffects, false);
						if (!defenseL2Effects.isEmpty()) {
							defenseL2Effects = sectionSkillService.runDefenseSkillResult(SKILL_WEAKEN, combat,
									defenseL2Effects, true);
						}
						acceptEffectService.acceptSkillAttackEffect(combat, defenseL2Effects);
					}
				} else {
					acceptEffectService.acceptSkillAttackEffect(combat, todoEffects);
				}
				if (combat.getOppoPlayer(player.getId()).isKilled()) {
					combat.setWinnerId(player.getId().getValue());
					return;
				}
			}
		}
	}
}