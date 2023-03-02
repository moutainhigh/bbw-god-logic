package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.login.repairdata.RepairStatisticService;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gm")
public class GMUserStatisticCtrl {
	@Autowired
	private StatisticServiceFactory statisticServiceFactory;
	@Autowired
	private RedisHashUtil<String, Object> redisHashUtil;
	@Autowired
	private InsRoleInfoService insRoleInfoService;
	@Autowired
	private ServerUserService serverUserService;
	@Autowired
	private RepairStatisticService repairStatisticService;

	/**
	 * 重新初始化玩家统计数据
	 *
	 * @param awardEnums
	 * @param behaviorTypes
	 * @param serverNames
	 * @param startDate
	 * @return
	 */
	@RequestMapping("reInitServerStatistic")
	public Rst reInitServerStatistic(String awardEnums, String behaviorTypes, String serverNames, int startDate) {
		List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
		long s = System.currentTimeMillis();
		int size = 0;
		for (CfgServerEntity server : servers) {
			List<Long> uids = insRoleInfoService.getUidsLoginAfter(server.getId(), startDate);
			for (Long uid : uids) {
				reInitResourceStatistic(uid, awardEnums);
				reInitBehaviorStatistic(uid, behaviorTypes);
			}
			size += uids.size();
		}
		long e = System.currentTimeMillis();
		Rst rst = Rst.businessOK();
		rst.put("time", String.format("重新初始化耗时%s毫秒", (e - s)));
		rst.put("size", String.format("重新初始化%s条数据", size));
		return rst;
	}

	@RequestMapping("reInitUserStatistic")
	public Rst reInitUserStatistic(String awardEnums, String behaviorTypes, String nickname, String serverName) {
		CfgServerEntity server = ServerTool.getServer(serverName);
		if (server == null) {
			return Rst.businessFAIL("无效的区服");
		}
		int sId = server.getMergeSid();
		Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
		if (!uidOptional.isPresent()) {
			return Rst.businessFAIL("该区服不存在该角色");
		}
		long s = System.currentTimeMillis();
		long uid = uidOptional.get();
		reInitResourceStatistic(uid, awardEnums);
		reInitBehaviorStatistic(uid, behaviorTypes);
		long e = System.currentTimeMillis();
		Rst rst = Rst.businessOK();
		rst.put("time", String.format("重新初始化耗时%s毫秒", (e - s)));
		return rst;
	}

	/**
	 * 重新初始化资源统计
	 *
	 * @param uid
	 * @param awardEnums
	 */
	private void reInitResourceStatistic(long uid, String awardEnums) {
		if (StrUtil.isBlank(awardEnums)) {
			return;
		}
		String[] awardEnumArr = awardEnums.split(",");
		for (String awardValueStr : awardEnumArr) {
			AwardEnum awardEnum = AwardEnum.fromValue(Integer.parseInt(awardValueStr));
			repairStatisticService.reInitResStatistic(uid, awardEnum);
		}
	}

	/**
	 * 重新初始化行为统计
	 *
	 * @param uid
	 * @param behaviorTypes
	 */
	private void reInitBehaviorStatistic(long uid, String behaviorTypes) {
		if (StrUtil.isBlank(behaviorTypes)) {
			return;
		}
		String[] behaviorTypeArr = behaviorTypes.split(",");
		for (String behaviorValueStr : behaviorTypeArr) {
			BehaviorType behaviorType = BehaviorType.fromValue(Integer.parseInt(behaviorValueStr));
			repairStatisticService.reInitBehaviorStatistic(uid, behaviorType);
		}
	}
}
