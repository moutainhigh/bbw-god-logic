package com.bbw.god.gameuser.achievement;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.flx.FlxCaiShuZiBet;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 */
@Service
public class UserAchievementResetService {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ServerDataService serverDataService;

	/**
	 * 判断是否需要重置成就进度
	 *
	 * @param uAchievement
	 * @param achievementId
	 * @param progress
	 * @return
	 */
	public boolean needResetProgress(UserAchievement uAchievement, int achievementId, int progress) {
		if (Arrays.asList(13750, 13830, 13940, 13950, 13960, 13970, 14080).contains(achievementId)) {
			if (uAchievement == null || progress == 0) {
				return false;
			}
			if (!uAchievement.getStatus().equals(AchievementStatusEnum.NO_ACCOMPLISHED.getValue())) {
				return false;
			}
			long lastUpdateTime = uAchievement.getLastUpdateTime();
			if (DateUtil.toDateInt(DateUtil.fromDateLong(lastUpdateTime)) < DateUtil.toDateInt(DateUtil.now())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 重置玩家成就进度
	 *
	 * @param uid
	 * @param uAchievement
	 * @param achievementId
	 */
	public void resetUserAchievementProgress(long uid, UserAchievement uAchievement, int achievementId) {
		if (achievementId == 14080) {
			// 押押乐和猜数字
			GameUser gu = gameUserService.getGameUser(uid);
			Integer serverId = gu.getServerId();
			Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
			List<FlxCaiShuZiBet> shuZiBets = serverDataService.getServerDatas(serverId, FlxCaiShuZiBet.class).stream().filter(
					s -> s.getUid().equals(uid) && s.getDateInt() == DateUtil.toDateInt(yesterday)).collect(Collectors.toList());
			List<FlxYaYaLeBet> yaYaLeBets = serverDataService.getServerDatas(serverId, FlxYaYaLeBet.class).stream().filter(
					s -> s.getUid().equals(uid) && s.getDateInt() == DateUtil.toDateInt(yesterday)).collect(Collectors.toList());
			// 没下注的重置为0
			if (shuZiBets.size() == 0 && yaYaLeBets.size() == 0) {
				uAchievement.setValue(0);
				uAchievement.setLastUpdateTime(DateUtil.toDateTimeLong());
				gameUserService.updateItem(uAchievement);
			}
		} else if (Arrays.asList(13750, 13830, 13940, 13950, 13960, 13970).contains(achievementId)) {
			// 一日内要求完成的成就
			uAchievement.setValue(0);
			uAchievement.setLastUpdateTime(DateUtil.toDateTimeLong());
			gameUserService.updateItem(uAchievement);
		}
	}
}
