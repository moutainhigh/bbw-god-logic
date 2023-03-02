package com.bbw.god.gameuser.card.juling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.common.StrUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;

@RestController
public class JuLingCtrl extends AbstractController {
	@Autowired
	private JuLingLogic juLingLogic;

	/**
	 * 获得聚灵卡牌
	 * 
	 * @return
	 */
	@GetMapping(CR.Card.GAIN_JL_CARDS)
	public RdJuLJ gainJLCards() {
		return juLingLogic.getJLJInfo(getUserId());
	}

	/**
	 * 聚灵
	 * 
	 * @param cardId
	 * @return
	 */
	@GetMapping(CR.Card.JU_LING)
	public RDJuling juLing(String cardId, int type) {
		if (StrUtil.isBlank(cardId)) {
			cardId = "0";
		}
		boolean isXd = type == 1;// 是否是聚灵限定卡
		return juLingLogic.juLing(getUserId(), Integer.valueOf(cardId), isXd);
	}
}
