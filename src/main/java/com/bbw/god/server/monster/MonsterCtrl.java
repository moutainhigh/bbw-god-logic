package com.bbw.god.server.monster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.fight.processor.FightProcessorFactory;
import com.bbw.god.game.CR;
import com.bbw.god.login.LoginPlayer;

@RestController
public class MonsterCtrl extends AbstractController {
	@Autowired
	private MonsterLogic monsterLogic;
	@Autowired
	private FightProcessorFactory fightProcessorFactory;

	/**
	 * 获得怪物信息
	 * 
	 * @return
	 */
	@GetMapping(CR.Monster.LIST_MONSTERS)
	public RDMonsterList listMonsters() {
		LoginPlayer player = this.getUser();
		return monsterLogic.getBuddyMonster(player.getUid(), player.getServerId());
	}

	/**
	 * 获得对手信息
	 * 
	 * @return
	 */
	@GetMapping(CR.Monster.ATTACK_MONSTER)
	public RDFightsInfo attackMonster(long monsterId) {
		//LoginPlayer player = this.getUser();
		//return monsterLogic.attackMonster(player.getUid(), player.getServerId(), monsterId);
		return null;
	}

	/**
	 * 提交挑战结果
	 * 
	 * @return
	 */
	@GetMapping(CR.Monster.SUBMIT_FIGHT_RESULT)
	public RDFightResult submitHelpAttackYG(FightSubmitParam param) {
		return fightProcessorFactory.makeFightProcessor(FightTypeEnum.HELP_YG).submitFightResult(getUserId(), param);
	}
}
