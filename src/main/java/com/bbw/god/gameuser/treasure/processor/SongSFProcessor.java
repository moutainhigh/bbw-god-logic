package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.server.god.GodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 送神符
 * 
 * @author suhq
 * @date 2018年11月29日 上午9:12:46
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SongSFProcessor extends TreasureUseProcessor {
	@Autowired
	private GodService userGodService;

	public SongSFProcessor() {
		this.treasureEnum = TreasureEnum.SSF;
		this.isAutoBuy = false;
	}

	@Override
	public void check(GameUser gu, CPUseTreasure param) {
		Optional<UserGod> userGodOp = userGodService.getAttachGod(gu);
		if (userGodOp.isPresent()) {
			UserGod userGod = userGodOp.get();
			if (!userGod.isCanUseSSF()) {
				throw new ExceptionForClientTip("hexagram.god.cant.use.ssf");
			}
		}
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		Optional<UserGod> userGod = userGodService.getAttachGod(gu);
		if (userGod.isPresent()) {
			userGodService.setUnvalid(gu, userGod.get());
		}
	}

}
