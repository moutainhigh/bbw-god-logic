package com.bbw.god.fight.processor;

import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 村庄战斗
 *
 * @author: suhq
 * @date: 2021/8/10 11:51 上午
 */
@Slf4j
@Service
public class CunZFightProcessor extends AbstractFightProcessor {

	@Override
	public FightTypeEnum getFightType() {
		return FightTypeEnum.CZ_TASK_FIGHT;
	}

	@Override
	public WayEnum getWay() {
		return WayEnum.CZ_TASK_FIGHT;
	}

	@Override
	public void settleBefore(GameUser gu, FightSubmitParam param) {

	}

	@Override
	public int getGodCopperRate(GameUser gu) {
		return 0;
	}

	@Override
	public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
	}

	@Override
	public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {

	}
}
