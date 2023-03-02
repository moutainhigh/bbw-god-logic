package com.bbw.god.gameuser.card.juling;

import com.bbw.god.game.config.Cfg;

public class JuLingTool {

	public static CfgJuLing getConfig() {
		return Cfg.I.getUniqueConfig(CfgJuLing.class);
	}

}
