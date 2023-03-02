package com.bbw.god.login.repairdata;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.unique.UserZxz;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.UserGuild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.bbw.god.login.repairdata.RepairDataConst.MIGRATE_DATE;

/**
 * @author suchaobin
 * @description 迁移数据service
 * @date 2020/7/7 15:06
 **/
@Service
@Slf4j
public class MigrateDataService implements BaseRepairDataService {
	@Autowired
	private FstServerService fstService;
	@Autowired
	private ServerDataService serverDataService;
	@Autowired
	private GameUserService gameUserService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		// 迁移封神台、诛仙阵积分、仙豆
		if (lastLoginDate.before(MIGRATE_DATE)) {
			migrateFstAndZxzPoint(gu);
		}
	}

	private void migrateFstAndZxzPoint(GameUser gu) {
		//迁移封神台积分
		Optional<FstRanking> optional = fstService.getFstRanking(gu.getId());
		if (optional.isPresent()) {
			FstRanking fstRanking = optional.get();
			int fstPoint = fstRanking.getPoints();
			log.info(gu.getId() + "迁移封神台积分：" + fstPoint);
			fstRanking.setPoints(0);
			serverDataService.updateServerData(fstRanking);
			TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.FST_POINT.getValue(), fstPoint,
					WayEnum.LOGIN_REPAIR, new RDCommon());
		}
		//迁移诛仙阵积分
		UserZxz userZxz = gameUserService.getSingleItem(gu.getId(), UserZxz.class);
		if (userZxz != null) {
			int zxzPoint = userZxz.getPoints();
			log.info(gu.getId() + "迁移诛仙阵积分：" + zxzPoint);
			userZxz.setPoints(0);
			gameUserService.updateItem(userZxz);
			TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.ZXZ_POINT.getValue(), zxzPoint,
					WayEnum.LOGIN_REPAIR, new RDCommon());
		}
		long uid = gu.getId();
		UserGuild userGuild = gameUserService.getSingleItem(uid, UserGuild.class);
		if (userGuild != null) {
			if (userGuild.getContrbution() > 0) {
				int num = userGuild.getContrbution();
				log.info(uid + "迁移行会贡献：" + userGuild.getContrbution());
				userGuild.setContrbution(0);
				TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.GUILD_CONTRIBUTE.getValue(),
						num, WayEnum.UPDATE, new RDCommon());
				gameUserService.updateItem(userGuild);
			}
		}
	}
}
