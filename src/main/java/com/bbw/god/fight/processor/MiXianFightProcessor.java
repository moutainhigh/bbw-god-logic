package com.bbw.god.fight.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.mixd.nightmare.MiXianEnemy;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianLogic;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 战斗处理.继承这个类的必须为多态
 *
 * @author suhq
 * @date 2019年2月18日 下午4:02:25
 */
@Slf4j
@Service
public class MiXianFightProcessor extends AbstractFightProcessor {
	@Autowired
	private NightmareMiXianLogic nightmareMiXianLogic;

	@Override
	public FightTypeEnum getFightType() {
		return FightTypeEnum.MXD;
	}

	@Override
	public WayEnum getWay() {
		return WayEnum.MXD_FIGHT;
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
		nightmareMiXianLogic.handleFightFail(gu.getId(), param,rd);
	}

	@Override
	public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
		nightmareMiXianLogic.handleFightAward(gu.getId(), param.getOpponentId(), rd);
		int gainExp = getExp(gu, param.getOppLostBlood(), param);// 经验
		gainExp /= 3;
		int baseCopper = gainExp * PowerRandom.getRandomBetween(1, 3);
		baseCopper *= (1 + getBaseCopperBuf(gu));
		gainExp *= (1 + getBaseExpBuf(gu,param.getAdditionExp()));
		// 处理战斗经验
		gainJinYanDan(gu.getId(), param, gainExp, rd);
		ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
		// 铜钱加成
		int godNum = godService.getCopperAddRate(gu);
		double copperAddRate = getCopperAddRate(param.getZcTimes(), godNum);
		int extraCopper = (int) (baseCopper * copperAddRate);
		// 处理战斗铜钱
		EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(gu.getId(), getWay(), rd), baseCopper, baseCopper);
		copperAdd.addCopper(ResWayType.Extra, extraCopper);
		ResEventPublisher.pubCopperAddEvent(copperAdd);
	}

	@Override
	public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
		//置为未结算状态
		TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
		MiXianEnemy enemy = nightmareMiXianLogic.getEnemyById(uid, oppId);
		CombatPVEParam param= new CombatPVEParam();
		param.setFightAgain(fightAgain);
		param.setOpponentId(oppId);
		CPlayerInitParam initParam=new CPlayerInitParam();
		initParam.setUid(oppId);
		initParam.setLv(enemy.getLevel());
		initParam.setCards(enemy.getCardParams());
		if (enemy.getBlood()>0){
			initParam.setInitHP(enemy.getBlood());
		}
		if (enemy.getHeadIcon()!=null){
			initParam.setHeadIcon(enemy.getHeadIcon());
		}else {
			initParam.setHeadIcon(TreasureEnum.HEAD_ICON_Normal.getValue());
		}
		initParam.setHeadImg(enemy.getHead());
		initParam.setNickname(enemy.getNickname());
		param.setAiPlayer(initParam);
		nightmareMiXianLogic.fightBefore(uid,oppId);
		return param;
	}
}
