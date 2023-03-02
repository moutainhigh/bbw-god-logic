package com.bbw.god.city.yed;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.fight.processor.YeDFightProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author suchaobin
 * @description 强盗事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class QiangDaoProcessor extends ExtraYeDEventProcessor implements YeDEventExtraOperation {
	@Autowired
	private YeDFightProcessor yeDFightProcessor;
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.QIANG_DAO.getValue();
	}

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
		Optional<UserGod> userGod = godService.getAttachGod(gameUser);
		//卦象buff
		if (hasHexagramBuff(gameUser.getId())){
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
        // 送子观音附体
        if (userGod.isPresent() && userGod.get().getBaseId() == GodEnum.SZGY.getValue()) {
            rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
            return;
        }
        // 没钱给抢了
        if (gameUser.getCopper() <= 0) {
            rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
            return;
        }
        long deductCopper = 1200 * (gameUser.getLevel() + PowerRandom.getRandomBetween(-1, 1));
        deductCopper = gameUser.getCopper() <= deductCopper ? gameUser.getCopper() : deductCopper;
        rdArriveYeD.setDeductCopper(deductCopper);
        TimeLimitCacheUtil.setArriveCache(gameUser.getId(), rdArriveYeD);
        ResEventPublisher.pubCopperDeductEvent(gameUser.getId(), deductCopper, WayEnum.YD, rd);
        BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
        CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromLoss(YdEventEnum.QIANG_DAO, deductCopper));
    }

	/**
	 * 额外操作（如：推荐特产，反抗逃跑等）
	 */
	@Override
	public void extraOperation(GameUser gu, Integer num, RDArriveYeD arriveYeD, RDAdvance rd) {
		List<UserCity> ownCities = userCityService.getUserOwnCities(gu.getId());
		int cityLv=1;
		int cityId=0;
		if (ListUtil.isEmpty(ownCities)){
			List<Integer> ids = CityTool.getAllCityIdByLevel(1);
			cityId=PowerRandom.getRandomFromList(ids);
		}else {
			UserCity random = PowerRandom.getRandomFromList(ownCities);
			cityLv=random.gainCity().getLevel();
			cityId=random.getBaseId();
		}
		RDFightsInfo fightsInfo = yeDFightProcessor.buildRDFightsInfo(gu.getId(),cityLv,cityId,YdEventEnum.QIANG_DAO);
		fightsInfo.setNickname("强盗");
		fightsInfo.setHead(TreasureEnum.HEAD_QD.getValue());
		fightsInfo.setYeDEventType(getMyId());
		// 初始战斗为未结算
		TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
		// 设置更新缓存
		arriveYeD.setFightsInfo(fightsInfo);
		arriveYeD.setFightType(FightTypeEnum.YED_EVENT.getValue());
		TimeLimitCacheUtil.setArriveCache(gu.getId(), arriveYeD);
	}
}
