package com.bbw.god.server.god.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;

/**
 * 大财神:送随机元素x1。20步内增加高星卡牌掉率。
 * 
 * @author suhq
 * @date 2018年10月19日 下午2:08:45
 */
@Component
public class BBXGodProcessor extends AbstractGodProcessor {
	@Autowired
	private BoxService boxService;

	public BBXGodProcessor() {
		this.godType = GodEnum.BBX;
	}

	@Override
	public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
		rd.setAttachedGod(GodEnum.BBX.getValue());
		// 开百宝箱
		boxService.open(gameUser.getId(), TreasureEnum.BBX.getValue(), WayEnum.BBX_PICK, rd);
	}

}
