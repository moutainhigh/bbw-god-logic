package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.wanxianzhen.CfgWanXianBox;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 万仙阵宝箱处理器
 * @date 2020/6/4 9:38
 **/
@Service
public class WanXianBoxProcessor extends TreasureUseProcessor {
	@Autowired
	private AwardService awardService;

	public WanXianBoxProcessor() {
		// 处理多种包类道具，该类实际未用到该参数
		this.treasureEnum = null;
		this.isAutoBuy = false;
	}

	@Override
	public boolean isMatch(int treasureId) {
		if (treasureId >= 41001 && treasureId <= 41008) {
			return true;
		}
		return treasureId >= 42001 && treasureId <= 42008;
	}

	/**
	 * 是否宝箱类
	 *
	 * @return
	 */
	@Override
	public boolean isChestType() {
		return true;
	}

	/**
	 * 法宝生效
	 *
	 * @param gu
	 * @param param
	 * @param rd
	 */
	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		int treasureId = param.getProId();
		CfgWanXianBox cfgWanXianBox = WanXianTool.getCfgWanXianBox();
		List<CfgWanXianBox.BoxAward> boxAwards = cfgWanXianBox.getBoxAwards();
		CfgWanXianBox.BoxAward boxAward = boxAwards.stream().filter(box ->
				box.getTreasureId() == treasureId).findFirst().orElse(null);
		if (null == boxAward) {
			throw new ExceptionForClientTip("wanxian.error.boxId");
		}
		List<Award> awards = new ArrayList<>(boxAward.getAwards());
		int probability = boxAward.getProbability() / 100;
		int random = PowerRandom.getRandomBySeed(100);
		if (random <= probability) {
			Integer type = boxAward.getType();
			List<CfgWanXianBox.CardProbability> cardProbabilityList = cfgWanXianBox.getCardProbabilityList()
					.stream().filter(c -> c.getType().intValue() == type).collect(Collectors.toList());
			if (ListUtil.isNotEmpty(cardProbabilityList)) {
				CfgWanXianBox.CardProbability cardProbability = PowerRandom.getRandomFromList(cardProbabilityList);
				Integer cardId = cardProbability.getCardId();
				awards.add(new Award(cardId, AwardEnum.KP, 1));
			}
		}
		CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureId);
		String broadcastWayInfo = String.format("在开%s中获得", treasure.getName());
		awardService.fetchAward(gu.getId(), awards, WayEnum.OPEN_WAN_XIAN_BOX, broadcastWayInfo, rd);
	}
}
