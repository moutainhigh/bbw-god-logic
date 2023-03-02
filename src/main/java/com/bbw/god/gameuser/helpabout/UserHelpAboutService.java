package com.bbw.god.gameuser.helpabout;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgHelpAbout;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.helpabout.UserHelpAbout.Info;
import com.bbw.god.gameuser.helpabout.event.ReadMenuHelpEventPublisher;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;

/**
 * 用户帮助 阅读奖励
 * 
 * @author lwb
 * @date 2019年4月10日
 * @version 1.0
 */
@Service
public class UserHelpAboutService {
	@Autowired
	private GameUserService gameUserService;

	public static final int TYPE_HAVE = 0;// 可以领取
	public static final int TYPE_GONE = 1;// 已领取
	public static final int AWARD_Gold = 5;// 完成阅读奖励的元宝数量
	
	/**
	 * 获取帮助信息
	 * 
	 * @param uid
	 * @return
	 */
	public RDHelpAbout getRDHelpAboutList(long uid) {
		List<Info> infos = getCfgInfos();
		RDHelpAbout rd = new RDHelpAbout();
		UserHelpAbout helpAbout = getHelpAbout(uid);
		if (helpAbout != null) {
			// 全部可领取
			List<Integer> awardedIds = helpAbout.getAwardedHelpIds();
			for (Info info : infos) {
				if (awardedIds.contains(info.getHelpId())) {
					info.setStatus(TYPE_GONE);
				}
			}
		}
		rd.setHelpAbouts(infos);
		// 阅读帮助事件发布
		BaseEventParam bep = new BaseEventParam(uid, WayEnum.Help);
		ReadMenuHelpEventPublisher.pubMenuHelpEvent(bep);
		return rd;
	}

	public UserHelpAbout getHelpAbout(long uid) {
		List<UserHelpAbout> list = gameUserService.getMultiItems(uid, UserHelpAbout.class);
		if (list.isEmpty()) {
			return null;
		}
		if (list.size() > 1) {
			gameUserService.deleteItems(uid, list.subList(1, list.size()));
		}
		return list.get(0);
	}
	/**
	 * 领取阅读帮助奖励 奖励内容为5元宝
	 * 
	 * @param uid
	 * @param dataid 即创建奖励时的资源id
	 * @return
	 */
	public RDCommon gainAward(long uid, int helpId) {
		RDCommon rd = new RDCommon();
		List<Info> infos = getCfgInfos();
		Optional<Info> cfgOp = infos.stream().filter(p -> p.getHelpId() == helpId).findFirst();
		if (!cfgOp.isPresent()) {
			// 该奖励已去除
			throw new ExceptionForClientTip("help.about.not.exist");
		}
		Info info = cfgOp.get();
		UserHelpAbout userHelpAbout = getHelpAbout(uid);
		if (userHelpAbout == null) {
			userHelpAbout=initUserHelpAbout(uid);
		} else if (userHelpAbout.awarded(helpId)) {
			throw new ExceptionForClientTip("help.about.award.gone");
		}
		userHelpAbout.gainAward(info);
		gameUserService.updateItem(userHelpAbout);
		ResEventPublisher.pubGoldAddEvent(uid, AWARD_Gold, WayEnum.Help, rd);
		return rd;
	}

	/**
	 * 获取可领取的数量
	 * 
	 * @param uid
	 * @return
	 */
	public Integer getCanAwardNum(long uid) {
		UserHelpAbout userHelpAbout = getHelpAbout(uid);
		 List<Integer> ids=getCfgShowHepleIds();
		int num = ids.size();
		if (userHelpAbout != null && userHelpAbout.getInfos() != null) {
			for (Info info : userHelpAbout.getInfos()) {
				if (info.getStatus() == TYPE_GONE && ids.contains(info.getHelpId())) {
					num--;
				}
			}
		}
		return num;
	}

	/**
	 * 生成用户的 待领取的阅读帮助奖励
	 * 
	 * @param uid
	 */
	public UserHelpAbout initUserHelpAbout(long uid) {
		UserHelpAbout about = new UserHelpAbout();
		about.setGameUserId(uid);
		about.setId(UserRedisKey.getNewUserDataId());
		gameUserService.addItem(uid, about);
		return about;
	}

	public List<Integer> getCfgShowHepleIds() {
		List<Info> cfgInfos = getCfgInfos();
		return cfgInfos.stream().filter(p -> p.getShow() == 1).map(Info::getHelpId).collect(Collectors.toList());
	}

	private List<Info> getCfgInfos() {
		List<Info> cfgInfos = Cfg.I.getUniqueConfig(CfgHelpAbout.class).getList();
		return ListUtil.copyList(cfgInfos, Info.class);
	}
}
