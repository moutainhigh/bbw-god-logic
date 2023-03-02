package com.bbw.god.server.guild.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.CfgGuild;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.guild.GuildRD;
import com.bbw.god.server.guild.GuildReward;
import com.bbw.god.server.guild.GuildShop;
import com.bbw.god.server.guild.GuildTools;
import com.bbw.god.server.guild.event.EPAddGuildExp;
import com.bbw.god.server.guild.event.GuildEventPublisher;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月16日 上午10:27:02 类说明 奖励处理
 */
@Service
public class GuildAwardService {

	public void buyGoodsAward(GuildShop goods, int buyNum, long uid, RDCommon rd) {
		int need = goods.getPrice() * buyNum;
		// 核对资金
		TreasureChecker.checkIsEnough(TreasureEnum.GUILD_CONTRIBUTE.getValue(), need, uid);
		// 扣除
		TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.GUILD_CONTRIBUTE.getValue(), need, WayEnum.Guild_SHOP,
				rd);
		// 下发
		Integer goodsId = goods.getGoodId();
		if (goods.getPropType() == 40) {
			// 40为卡牌
			if (goodsId == 0) {
				// 未指定卡牌，即随机卡牌
				CfgCardEntity card = CardTool.getRandomNotSpecialCard(goods.getStar());
				goodsId = card.getId();
			}
			CardEventPublisher.pubCardAddEvent(uid, goodsId, WayEnum.Guild_SHOP, "行会商店购买", rd);
		} else {
			// 60为法宝
			TreasureEventPublisher.pubTAddEvent(uid, goodsId, goods.getQuantity(), WayEnum.Guild_SHOP, rd);
		}
	}

	/**
	 * 发放任务奖励
	 * 
	 * @param rewards
	 * @param uid
	 * @param rd
	 */
	public void sendTaskAward(List<GuildReward> rewards, long uid, GuildRD rd) {
		for (GuildReward reward : rewards) {
			if (reward.getType() == 66) {
				// 经验
				EPAddGuildExp ep = EPAddGuildExp.instance(new BaseEventParam(uid, WayEnum.Guild_TASK),
						reward.getQuantity(), 0);
				GuildEventPublisher.pubAddGuildExp(ep);
				rd.setAddedGuildExp(reward.getQuantity());
				continue;
			}
			// 发送贡献值
			TreasureEventPublisher.pubTAddEvent(uid, reward.getRealId(), reward.getQuantity(), WayEnum.Guild_TASK, rd);
		}
	}

	public void openBox(CfgGuild.BoxReward box, int guildLv, long uid, GuildRD rd) {
		if (box.getCopper() > 0) {
			ResEventPublisher.pubCopperAddEvent(uid, box.getCopper(), WayEnum.Guild_Box, rd);
		}
		if (box.getExp() > 0 && GuildTools.getMaxLevel() > guildLv) {
			EPAddGuildExp ep = EPAddGuildExp.instance(new BaseEventParam(uid, WayEnum.Guild_Box), box.getExp(), 0);
			GuildEventPublisher.pubAddGuildExp(ep);
			rd.setAddedGuildExp(box.getExp());
		}
		if (box.getContrbution() > 0) {
			// 发送贡献值
			TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.GUILD_CONTRIBUTE.getValue(), box.getContrbution(),
					WayEnum.Guild_Box, rd);
		}
	}
}
