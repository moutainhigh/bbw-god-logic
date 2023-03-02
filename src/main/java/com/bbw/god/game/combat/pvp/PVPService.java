package com.bbw.god.game.combat.pvp;

import com.bbw.god.game.combat.weapon.WeaponLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.PlayerService;
import com.bbw.god.game.combat.RoundService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.pvp.PvPCombatParam.UptoCard;

import lombok.extern.slf4j.Slf4j;

/**
 * pve服务
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-14 20:41
 */
@Slf4j
@Service
public class PVPService {
	@Autowired
	private RoundService roundService;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private CombatRedisService combatService;
	@Autowired
	private WeaponLogic weaponLogic;
	/**
	 * 下一回合
	 * 
	 * @param combat
	 * @param moveToBattle
	 */
	public void nextRound(Combat combat, UptoCard p1, UptoCard p2) {
		if (combat.hadEnded()) {
			return;
		}
		// 回合开始 设置先手玩家
		PlayerId fid = combat.getPlayerByUid(p1.getUid()).getId();
		combat.setFirst(fid);
		combat.getAnimationList().clear();
		//法宝 补充动画阶段
		weaponLogic.addInTimeWeaponAnimation(combat);
		upToCards(combat, p1);
		upToCards(combat, p2);
		// 法宝生效阶段
		weaponLogic.takeUsedWeaponEffect(combat);
		roundService.run(combat);
		roundService.after(combat);
	}
	/**
	 * 上牌
	 * 
	 * @param combat
	 * @param uc
	 */
	public void upToCards(Combat combat, UptoCard uc) {
		// 玩家 布阵
		Player player = combat.getPlayerByUid(uc.getUid());
		if (uc.isAuto()) {
			playerService.autoDeployCards(combat, player, uc.getMoveToBattle());
		} else {
			playerService.deployCards(combat, player, uc.getMoveToBattle());
		}
	}

	public int getZcTimes(long combatId, long uid) {
		Combat combat = combatService.get(combatId);
		Player winner = combat.getPlayerByUid(uid);
		int zcTimes = winner.getStatistics().getZhaoCaiEffectTimes();
		return zcTimes;
	}
}
