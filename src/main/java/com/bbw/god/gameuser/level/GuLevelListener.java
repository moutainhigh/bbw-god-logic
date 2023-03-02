package com.bbw.god.gameuser.level;

import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GuLevelListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;

	/**
	 * 执行优先级低于冲榜【等级榜】
	 *
	 * @param event
	 */
	@EventListener
	public void levelUp(GuLevelUpEvent event) {
		EPGuLevelUp ep = event.getEP();
		GameUser gu = gameUserService.getGameUser(ep.getGuId());
		levelUp(gu, ep);
		// 更新角色概要信息
		InsRoleInfoEntity role = new InsRoleInfoEntity();
		role.setUid(ep.getGuId());
		role.setLevel(ep.getNewLevel());
		updateRoleInfoAsyncHandler.setRoleInfo(role, 4);
	}

	/**
	 * 玩家等级提升处理逻辑
	 * @param gu
	 * @param ep
	 */
	public void levelUp(GameUser gu, EPGuLevelUp ep) {
		RDCommon rd = ep.getRd();
		int oldLevel = ep.getOldLevel();
		int newLevel = ep.getNewLevel();
		int addLevel = newLevel - oldLevel;
		gu.incLevel(addLevel);
		// 每升一级奖励10元宝、10行动点
		int addedGold = addLevel * 10;
		int addedDice = getLevelUpDice(newLevel);
		ResEventPublisher.pubGoldAddEvent(gu.getId(), addedGold, WayEnum.GU_LEVEL_UP, rd);
		ResEventPublisher.pubDiceAddEvent(gu.getId(), addedDice, WayEnum.GU_LEVEL_UP, rd);
		// 野怪难度设置
		updateYgDifficulty(gu, oldLevel);

	}

	/**
	 * 升级奖励体力数
	 * 
	 * @param newLevel
	 * @return
	 */
	private int getLevelUpDice(int newLevel) {
		if (newLevel <= 5) {
			return 30;
		}
		if (newLevel <= 10) {
			return 48;
		}
		if (newLevel <= 15) {
			return 60;
		}
		return 90;
	}

	/**
	 * 设置野怪难度
	 * 
	 * @param gu
	 * @param oldLevel
	 */
	private void updateYgDifficulty(GameUser gu, int oldLevel) {
		// 11级到20级玩家会遇到低于自己5级到高于自己10级的野怪。（-5~10）
		// 20级以上玩家会遇到与自己平级到高于自己15级的野怪。（0~15）
		List<String> ygWins = Stream.of(gu.getStatus().getYgWin().split(",")).collect(Collectors.toList());
		if (gu.getLevel() >= 21 && oldLevel < 21) {
			ygWins.subList(0, 5).clear();
			ygWins.addAll(Arrays.asList("0", "0", "0", "0", "0"));
		} else if (gu.getLevel() > 11) {
			ygWins.subList(0, 1).clear();
			ygWins.add("0");
		}
		gu.getStatus().setYgWin(String.join(",", ygWins));
		gu.updateStatus();
	}
}
