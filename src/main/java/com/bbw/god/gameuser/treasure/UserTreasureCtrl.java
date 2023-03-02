package com.bbw.god.gameuser.treasure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;

/**
 * 法宝相关接口
 * 
 * @author suhq
 * @date 2018年11月5日 下午4:38:25
 */
@RestController
public class UserTreasureCtrl extends AbstractController {

	@Autowired
	private UserTreasureLogic treasureLogic;

	/**
	 * 使用宝物
	 *
	 * @param param
	 * @return
	 */
	@GetMapping(CR.Treasure.USE_TREASURE)
	public RDCommon useTreasure(CPUseTreasure param) {
		return treasureLogic.useMapTreasure(getUserId(), param);
	}

	/**
	 * 使用战斗法宝
	 * 
	 * @param proId
	 * @return
	 * @throws NumberFormatException
	 */
	@GetMapping(CR.Treasure.USE_FIGHT_TREASURE)
	public RDCommon useFightTreasure(int proId) {
		return treasureLogic.useFightTreasure(getUserId(), proId);
	}

	/**
	 * 升级符箓
	 * 
	 * @param param
	 * @return
	 */
	@GetMapping(CR.Treasure.UPDATE_SYMBOL)
	public RDUpdateSymbol updateSymbol(CPUpdateSymbol param) {
		return treasureLogic.updateSymbol(getUserId(), param);
	}

	/**
	 * 查看奖励内容
	 *
	 * @param treasureId
	 * @return
	 */
	@GetMapping(CR.Treasure.SEE_AWARD)
	public RDSeeAward seeAward(Integer treasureId) {
		return treasureLogic.seeAward(treasureId);
	}

	/**
	 * 合成法宝
	 * @param treasureId  消耗的灵石ID
	 * @param num 消耗的灵石数量
	 * @return
	 */
	@RequestMapping(CR.Treasure.SYNTHESIS_TREASURE)
	public RDCommon synthesisTreasure(int treasureId,int num){
		return treasureLogic.synthesisTreasure(getUserId(),treasureId,num);
	}
}
