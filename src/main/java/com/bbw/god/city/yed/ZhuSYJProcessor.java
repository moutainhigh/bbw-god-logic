package com.bbw.god.city.yed;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.bossmaou.BossMaouTool;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author suchaobin
 * @description 诸神遗迹处理器
 * @date 2020/6/2 17:23
 **/
@Service
public class ZhuSYJProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.ZSYJ.getValue();
	}

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
		int random = PowerRandom.getRandomBySeed(100);
		long uid = gameUser.getId();
		// 魔王魂+5
		if (random <= 30) {
			TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.MoWH.getValue(), 5, WayEnum.YD, rd);
		} else {
			// 独占魔王攻击次数+1
			// 9级前转成无所事事
			if (gameUser.getLevel() < 9) {
				rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
				return;
			}
			// 独占魔王不存在的转无所事事
			int sid = gameUser.getServerId();
			Optional<ServerAloneMaou> optional = this.serverAloneMaouService.getCurAloneMaou(sid);
			if (!optional.isPresent()) {
				rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
				return;
			}
			// 魔王降临期间转无所事事
			if (isMaouBossTime()) {
				rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
				return;
			}
			AloneMaouAttackSummary myAttack = this.aloneMaouAttackSummaryService.getMyAttackInfo(uid, optional.get());
			myAttack.addFreeTime();
			this.aloneMaouAttackSummaryService.setMyAttackInfo(uid, optional.get(), myAttack);
			rdArriveYeD.setAddAttackAloneMaouTimes(1);
		}
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromEnum(YdEventEnum.ZSYJ));
	}

	private boolean isMaouBossTime() {
		CfgBossMaou config = BossMaouTool.getConfig();
		List<CfgBossMaou.BossMaou> maous = config.getMaous();
		for (CfgBossMaou.BossMaou bossMaou : maous) {
			Integer beginTime = bossMaou.getBeginTime();
			Integer endTime = bossMaou.getEndTime();
			Integer todayInt = DateUtil.getTodayInt();
			String beginStr = todayInt.toString() + beginTime;
			String endStr = todayInt.toString() + endTime;
			Date begin = DateUtil.fromDateLong(Long.parseLong(beginStr));
			Date end = DateUtil.fromDateLong(Long.parseLong(endStr));
			if (DateUtil.now().before(end) && DateUtil.now().after(begin)) {
				return true;
			}
		}
		return false;
	}
}
