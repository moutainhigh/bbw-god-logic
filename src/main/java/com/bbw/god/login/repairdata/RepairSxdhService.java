package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.sxdh.SxdhFighter;
import com.bbw.god.game.sxdh.SxdhFighterService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.login.repairdata.RepairDataConst.*;

/**
 * @author suchaobin
 * @description 修复神仙大会数据
 * @date 2020/7/7 15:16
 **/
@Service
@Slf4j
public class RepairSxdhService implements BaseRepairDataService {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserTreasureService userTreasureService;
	@Autowired
	private SxdhFighterService sxdhFighterService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		// 神仙大会3.0仙豆转时限
		if (lastLoginDate.before(SXDH3_0)) {
			UserTreasure userTreasure = userTreasureService.getUserTreasure(gu.getId(),
					TreasureEnum.XIAN_DOU.getValue());
			if (userTreasure != null) {
				Date endTransferDate = DateUtil.fromDateTimeString("2020-06-07 20:59:59");
				if (DateUtil.now().before(endTransferDate)) {
					userTreasure.addTimeLimitNum(userTreasure.getOwnNum(), endTransferDate);
				}
				userTreasure.setOwnNum(0);
				gameUserService.updateItem(userTreasure);
			}
			//赛季重置
			SxdhFighter sxdhFighter = sxdhFighterService.getFighter(gu.getId());
			sxdhFighter.resetForNewSeason();
			gameUserService.updateItem(sxdhFighter);
		}

		// 转移神仙大会仙豆
		if (lastLoginDate.before(MIGRATE_SXDH_BEAN)) {
			migrateSxdhBean(gu.getId());
		}

		// 转移神仙大会门票
		if (lastLoginDate.before(MIGRATE_SXDH_TICKET_TIME)) {
			migrateSxdhTicket(gu.getId());
		}

		//TODO 8月8号后删除代码
		UserTreasure bean = userTreasureService.getUserTreasure(gu.getId(), TreasureEnum.XIAN_DOU.getValue());
		if (bean != null && ListUtil.isNotEmpty(bean.getLimitInfos())) {
			List<UserTreasure.LimitInfo> limitInfos = bean.getLimitInfos().stream().filter(tmp -> {
				String expireStr = tmp.getExpireTime().toString();
				return expireStr.contains("07205959");
			}).collect(Collectors.toList());
			if (ListUtil.isNotEmpty(limitInfos)) {
				limitInfos.forEach(tmp -> {
					String expireStr = tmp.getExpireTime().toString();
					expireStr = expireStr.replace("07205959", "08205959");
					tmp.setExpireTime(Long.valueOf(expireStr));
				});
				gameUserService.updateItem(bean);
			}
		}
	}

	/**
	 * 转移神仙大会门票
	 *
	 * @param uid
	 */
	private void migrateSxdhBean(long uid) {
		SxdhFighter fighter = gameUserService.getSingleItem(uid, SxdhFighter.class);
		if (fighter != null) {
			int bean = fighter.getBean();
			if (bean > 0) {
				log.info(uid + "迁移仙豆：" + bean);
				fighter.setBean(0);
				gameUserService.updateItem(fighter);
				TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.XIAN_DOU.getValue(), bean,
						WayEnum.LOGIN_REPAIR, new RDCommon());
			}
		}
	}

	/**
	 * 转移神仙大会门票
	 *
	 * @param uid
	 */
	private void migrateSxdhTicket(long uid) {
		SxdhFighter fighter = gameUserService.getSingleItem(uid, SxdhFighter.class);
		if (fighter != null) {
			Integer ticket = fighter.getTicket();
			if (ticket > 0) {
				log.info(uid + "迁移神仙大会门票：" + ticket);
				fighter.setTicket(0);
				gameUserService.updateItem(fighter);
				TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SXDH_TICKET.getValue(), ticket,
						WayEnum.LOGIN_REPAIR, new RDCommon());
			}
		}
	}
}
