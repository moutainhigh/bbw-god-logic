package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDSeeAward;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @description 至尊秘传宝箱2处理器
 * @date 2020/3/10 15:02
 */
@Service
public class ZhiZMZBox2Processor extends TreasureUseProcessor {
	private final static List<Integer> AWARDS = Arrays.asList(21159, 21160, 21161, 21162, 21163, 21164, 21165, 21166, 21183, 21184, 21185,21204,21205,21206,21207);
	private final static int ABLE_SELECTED = 1;

	public ZhiZMZBox2Processor() {
		this.treasureEnum = TreasureEnum.IMPERIAL_SECRET_SCROLL_BOX_2;
		this.isAutoBuy = false;
	}

	@Override
	public void check(GameUser gu, CPUseTreasure param) {
		List<Map<String, Integer>> goods = param.gainSelectedGoods();
		List<Integer> selectedGoods = goods.stream().map(g -> g.get(CPUseTreasure.ID)).collect(Collectors.toList());
		// 奖励不在有效范围内
		if (!AWARDS.containsAll(selectedGoods)) {
			throw new ExceptionForClientTip("award.not.valid.choose");
		}
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		List<Map<String, Integer>> goods = param.gainSelectedGoods();
		List<EVTreasure> treasureList = new ArrayList<>();
		List<RDCommon.RDTreasureInfo> infoList = new ArrayList<>();
		goods.forEach(g -> {
			int treasureId = g.get(CPUseTreasure.ID);
			int num = g.get(CPUseTreasure.NUM);
			treasureList.add(new EVTreasure(treasureId, num));
			infoList.add(new RDCommon.RDTreasureInfo(treasureId, num, TreasureType.SKILL_SCROLL.getValue()));
		});
		TreasureEventPublisher.pubTAddEvent(gu.getId(), treasureList, WayEnum.USE_ZHI_ZUN_MI_ZHUAN_BOX_2, new RDCommon());
		rd.setTreasures(infoList);
	}

	@Override
	public RDSeeAward seeAward(int treasureId) {
		return new RDSeeAward(AWARDS, ABLE_SELECTED);
	}
}
