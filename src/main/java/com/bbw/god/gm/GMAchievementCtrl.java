package com.bbw.god.gm;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.achievement.*;
import com.bbw.god.login.repairdata.RepairAchievementService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 成就相关接口
 *
 * @author: huanghb
 * @date: 2022/6/21 10:29
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMAchievementCtrl extends AbstractController {
	@Autowired
	private ServerUserService serverUserService;
	@Autowired
	private RepairAchievementService repairAchievementService;
	@Autowired
	private AchievementServiceFactory achievementServiceFactory;
	@Autowired
	@Lazy
	private List<BaseAchievementService> baseAchievementServices;

	@GetMapping("server!addAllAchievement")
	public Rst addAllAchievement(int sId, String nickname, int type) {
		Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
		if (!uidOptional.isPresent()) {
			return Rst.businessFAIL("不存在该角色");
		}
		Long uid = uidOptional.get();
		UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
		List<Integer> ids = AchievementTool.getAchievements(AchievementTypeEnum.fromValue(type)).stream().map(CfgAchievementEntity::getId).collect(Collectors.toList());
		for (int id : ids) {
			info.accomplishAchievement(id);
		}
		gameUserService.updateItem(info);
		return Rst.businessOK();

	}

	/**
	 * 修复仙诀成就
	 *
	 * @param sId
	 * @param nickname
	 * @return
	 */
	@GetMapping("server!repairXianJueUpdateStarAchievement")
	public Rst repairXianJueUpdateStarAchievement(int sId, String nickname) {
		Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
		if (!uidOptional.isPresent()) {
			return Rst.businessFAIL("不存在该角色");
		}
		Long uid = uidOptional.get();
		List<Integer> achievementIdToRepair = Arrays.asList(17660, 17670, 17680, 17690, 17700, 17710, 17720, 17730);
		UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
		for (Integer achievementId : achievementIdToRepair) {
			repairAchievementService.repairAchievement(info, achievementId);
		}
		return Rst.businessOK();
	}

	/**
	 * 修复成就
	 *
	 * @param sId
	 * @param nickname
	 * @param achievementIds
	 * @return
	 */
	@GetMapping("server!repairAchievements")
	public Rst repairAchievement(int sId, String nickname, String achievementIds) {
		List<Integer> achievementIdToRepair = ListUtil.parseStrToInts(achievementIds);
		if (ListUtil.isEmpty(achievementIdToRepair)) {
			return Rst.businessFAIL("achievements参数不能为空");
		}
		Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
		if (!uidOptional.isPresent()) {
			return Rst.businessFAIL("不存在该角色");
		}
		Long uid = uidOptional.get();
		UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
		for (Integer achievementId : achievementIdToRepair) {
			repairAchievementService.repairAchievement(info, achievementId);
		}
		return Rst.businessOK();
	}

	/**
	 * 修复成就为完成
	 *
	 * @param uid
	 * @param achievementIds
	 * @return
	 */
	@GetMapping("server!repairAchievementToComplete")
	public Rst repairAchievementToComplete(long uid, String achievementIds) {
		//参数检测
		List<Integer> achievementIdToRepair = ListUtil.parseStrToInts(achievementIds);
		if (ListUtil.isEmpty(achievementIdToRepair)) {
			return Rst.businessFAIL("achievements参数不能为空");
		}
		//获得所有修复的成就
		UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
		List<BaseAchievementService> services = new ArrayList<>();
		for (Integer achievementId : achievementIdToRepair) {
			BaseAchievementService service = getById(achievementId);
			//如果成就id不存在
			if (null == service) {
				return Rst.businessFAIL(String.format("程序员没有编写成就id=%s的service", achievementId));
			}
			services.add(service);
		}
		//已完成成就
		List<Integer> achievementsCompleteds = new ArrayList<>();
		//修复的成就
		List<Integer> repairAchievementIds = new ArrayList<>();
		//修复成就
		for (BaseAchievementService service : services) {
			if (service.isAccomplished(info)) {
				achievementsCompleteds.add(service.getMyAchievementId());
				continue;
			}
			int value = service.getMyNeedValue();
			repairAchievementIds.add(service.getMyAchievementId());
			service.achieve(info.getGameUserId(), value, info, new RDCommon());
		}
		Rst rst = Rst.businessOK();
		rst.put("已完成，不需要修复的成就", achievementsCompleteds);
		rst.put("修复的成就", repairAchievementIds);
		return rst;
	}

	/**
	 * 通过成就id获取对应service
	 *
	 * @param achievementId 成就id
	 * @return 成就id的对应service
	 */
	private BaseAchievementService getById(int achievementId) {
		for (BaseAchievementService service : baseAchievementServices) {
			if (achievementId == service.getMyAchievementId()) {
				return service;
			}
		}
		return null;
	}
}
