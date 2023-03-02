package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author lwb
 * @description 金鼠宝箱
 * 使用后可获得卡牌-虚日鼠*1，一星灵石*50，神砂*100。
 * @date 2020/11/10 10:02
 */
@Service
public class GoldRatBoxProcessor extends TreasureUseProcessor {
	public GoldRatBoxProcessor() {
		this.treasureEnum = TreasureEnum.GOLD_RAT_BOX;
		this.isAutoBuy = false;
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

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		//使用后可获得卡牌-虚日鼠*1，一星灵石*50，神砂*100。
		CardEventPublisher.pubCardAddEvent(gu.getId(), 140, WayEnum.TREASURE_USE, "在金鼠宝箱中", rd);
		TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.WNLS1.getValue(), 50, WayEnum.TREASURE_USE, rd);
		TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.SS.getValue(), 100, WayEnum.TREASURE_USE, rd);
	}
}
