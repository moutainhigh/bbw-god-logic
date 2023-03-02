package com.bbw.god.mall.lottery;

import com.bbw.common.CloneUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * @author suchaobin
 * @description 奖券工具
 * @date 2020/7/6 16:05
 **/
public class LotteryTool {
	public static List<CfgLotteryAward> getLotteryAwards() {
		return Cfg.I.get(CfgLotteryAward.class);
	}

	public static CfgLotteryAward getLotteryAward(int id) {
		CfgLotteryAward award = Cfg.I.get(id, CfgLotteryAward.class);
		if (null == award) {
			throw CoderException.high("奖券奖励id=" + id + "不存在");
		}
		return CloneUtil.clone(award);
	}

	public static CfgLotteryAward getLotteryAwardByLevel(int level) {
		List<CfgLotteryAward> awards = getLotteryAwards();
		CfgLotteryAward award = awards.stream().filter(a -> a.getLevel().equals(level)).findFirst().orElse(null);
		if (null == award) {
			throw CoderException.high("奖券奖励level=" + level + "不存在");
		}
		return CloneUtil.clone(award);
	}
}
