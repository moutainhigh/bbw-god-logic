package com.bbw.god.server.guild;
/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月16日 上午9:36:44 
* 类说明 
*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGuild;
import com.bbw.god.game.config.CfgGuild.BoxReward;
import com.bbw.god.game.config.CfgGuild.Level;
import com.bbw.god.game.config.CfgGuild.ProductAward;
import com.bbw.god.game.config.CfgGuild.TaskInfo;
import com.bbw.god.game.config.treasure.TreasureEnum;

public class GuildTools {

	public static CfgGuild getCfgGuild() {
		CfgGuild cfgGuild = Cfg.I.getUniqueConfig(CfgGuild.class);
		return cfgGuild;
	}

	public static List<GuildShop> getCfgGuildShops() {
		CfgGuild cfgGuild = getCfgGuild();
		return ListUtil.copyList(cfgGuild.getProducts(), GuildShop.class);
	}

	public static GuildShop getCfgGuildShopById(int id) {
		Optional<GuildShop> guiOptional = getCfgGuildShops().stream().filter(p -> p.getId() == id).findFirst();
		if (guiOptional.isPresent()) {
			return guiOptional.get();
		}
		throw new ExceptionForClientTip("store.goods.no.exists");
	}

	public static List<TaskInfo> getTaskInfos() {
		CfgGuild cfgGuild = getCfgGuild();
		return ListUtil.copyList(cfgGuild.getTasks(), TaskInfo.class);
	}

	public static int getMaxLevel() {
		return getCfgGuild().getMaxLevel();
	}

	public static BoxReward getBoxReward(int lv) {
		CfgGuild cfgGuild = getCfgGuild();
		Optional<BoxReward> boxOptional = cfgGuild.getBoxRewards().stream().filter(p -> p.getLv() == lv).findFirst();
		if (boxOptional.isPresent()) {
			return boxOptional.get();
		}
		return null;
	}
	/**
	 * 获取一个随机任务 //每个任务都有任务属性attr 相同类任务 值一样，先随机获取一个属性值，再通过属性值过滤 得到任务集合，然后再从中获取一个任务
	 * 
	 */
	public static TaskInfo getRandomTask() {
		List<TaskInfo> tasks = getTaskInfos();
		Set<Integer> attrs = new HashSet<>();
		for (TaskInfo task : tasks) {
			attrs.add(task.getAttr());
		}
		Integer index = PowerRandom.getRandomFromList(new ArrayList<>(attrs));// 获取随机任务属性值
		List<TaskInfo> taskInfos = tasks.stream().filter(p -> p.getAttr().equals(index)).collect(Collectors.toList());
		return PowerRandom.getRandomFromList(taskInfos);
	}

	public static List<GuildReward> getTaskAwards(int pid, int guildLv) {
		CfgGuild cfgGuild = getCfgGuild();
		List<ProductAward> productAwards = cfgGuild.getTaskRewards();
		Optional<ProductAward> rewardOp = productAwards.stream().filter(p -> p.getProductId() == pid).findFirst();
		List<GuildReward> guildRewards = new ArrayList<GuildReward>();
		if (rewardOp.isPresent()) {
			for (Award award : rewardOp.get().getAwardList()) {
				if (award.getItem()== GuildConstant.REWARD_TYPE
						&& guildLv >= cfgGuild.getMaxLevel()) {
					continue;
				}
				guildRewards.add(GuildReward.instance(award));
			}
		}
		return guildRewards;
	}

	public static Level getLevel(int lv) {
		CfgGuild cfgGuild = getCfgGuild();
		Optional<Level> level = cfgGuild.getLevels().stream().filter(p -> p.getLv() == lv).findFirst();
		return level.get();
	}
}
