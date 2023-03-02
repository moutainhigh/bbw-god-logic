package com.bbw.god.game.transmigration;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.transmigration.rd.RDTransmigrationChallengeInfo;
import com.bbw.god.game.transmigration.rd.RDTransmigrationHighLights;
import com.bbw.god.game.transmigration.rd.RDTransmigrationInfo;
import com.bbw.god.game.transmigration.rd.RDTransmigrationRecords;
import com.bbw.god.gameuser.card.RDCardGroups;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 轮回接口入口
 *
 * @author: suhq
 * @date: 2021/9/15 9:54 上午
 */
@RestController
public class TransmigrationCtrl extends AbstractController {
	@Autowired
	private TransmigrationLogic transmigrationLogic;
	@Autowired
	private TransmigrationCardGroupLogic transmigrationCardGroupLogic;
	@Autowired
	private UserTransmigrationTargetLogic userTransmigrationTargetLogic;

	@GetMapping(CR.Transmigration.GET_INFO)
	public RDTransmigrationInfo getInfo() {
		return transmigrationLogic.getInfo(getUserId());
	}

	@GetMapping(CR.Transmigration.LIST_CHALLENGE_RECORDS)
	public RDTransmigrationRecords listChallengeRecords() {
		return transmigrationLogic.listChallengeRecords(getUserId());
	}

	@GetMapping(CR.Transmigration.GET_CHALLENGE_INFO)
	public RDTransmigrationChallengeInfo getChallengeInfo(int cityId) {
		return transmigrationLogic.getChallengeInfo(getUserId(), cityId);
	}

	/**
	 * @param isPersonal 1 个人 0 全服
	 * @return
	 */
	@GetMapping(CR.Transmigration.LIST_HIGH_LIGHTS)
	public RDTransmigrationHighLights listHighLights(Integer isPersonal) {
		return transmigrationLogic.listHighLights(getUserId(), isPersonal == 1);
	}

	@GetMapping(CR.Transmigration.GET_CARD_GROUP)
	public RDCardGroups getCardGroup() {
		return transmigrationCardGroupLogic.getAttackCardGroup(getUserId());
	}

	@GetMapping(CR.Transmigration.SET_CARD_GROUP)
	public RDSuccess setCardGroup(String cardIds) {
		return transmigrationCardGroupLogic.setAttackCardGroup(getUserId(), cardIds);
	}

	@GetMapping(CR.Transmigration.SYNC_CARD_GROUP)
	public RDSuccess synCardGroup() {
		return transmigrationCardGroupLogic.synAttackCardGroup(getUserId());
	}

	@GetMapping(CR.Transmigration.GET_TARGET_AWARDS)
	public RDSuccess gainTargetAwards(int targetId) {
		return userTransmigrationTargetLogic.gainTargetAwards(getUserId(), targetId);
	}

	@GetMapping(CR.Transmigration.GET_FIGTHT_AWARDS)
	public RDSuccess gainFightAwards(int cityId, int index) {
		return transmigrationLogic.gainFightAwards(getUserId(), cityId, index);
	}
}
