package com.bbw.god.gameuser.res.exp;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.gameuser.config.GameUserExpTool;
import com.bbw.god.gameuser.config.GameUserExpTool.LevelExpRate;
import com.bbw.god.gameuser.level.EPGuLevelUp;
import com.bbw.god.gameuser.level.GuLevelEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class UserExpListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserTreasureEffectService userTreasureEffectService;
	/**
	 * 执行优先级低于活动经验加倍
	 * 
	 * @param event
	 */
	@EventListener
	@Order(2)
	public void addExp(ExpAddEvent event) {
		EPExpAdd ep = event.getEP();
		GameUser gu = gameUserService.getGameUser(ep.getGuId());
		//判断是否双倍经验丹是否生效
		UserTreasureEffect utEffect = userTreasureEffectService.getEffect(gu.getId(), TreasureEnum.DOUBLE_EXPERIENCE_MEDICINE.getValue());
		if (utEffect != null){
			//计算已经使用的秒数
			long usedSecond = DateUtil.getSecondsBetween(utEffect.getEffectTime(), DateUtil.now());
			if (usedSecond <= utEffect.getRemainEffect()){
				ep.addExp(ResWayType.DOUBLE_EXP_MEDICINE, ep.gainNormalExp());
			}
		}
		RDCommon rd = ep.getRd();
		WayEnum way = ep.getWay();
		handleExp(gu, ep, way, rd);
	}

	/**
	 * 根据经验获得等级
	 *
	 * @param gu
	 * @param expAdd
	 * @param way
	 * @param rd
	 */
	public void handleExp(GameUser gu, EPExpAdd expAdd, WayEnum way, RDCommon rd) {
		long gainAddExp = expAdd.gainAddExp();
		if (gu.getLevel() >= GameUserConfig.bean().getGuTopLevel()) {
			return;
		}
		if (gainAddExp > 0) {
			gu.addExperience(gainAddExp);
		}
		long exp = gu.getExperience();
		LevelExpRate levelExpRate = GameUserExpTool.getLevelExpRateByExp(exp);
		int newLevel = levelExpRate.getLevel();
		int expRate = levelExpRate.getExpRate();
		// 等级信息
		rd.setExpRate(expRate);
		rd.setGuExp(exp - GameUserExpTool.getExpByLevel(newLevel));
//		rd.setAddedGuExps(expAdd.buildRD());
		Long addedGuExp = null == rd.getAddedGuExp()?0:rd.getAddedGuExp();
		rd.setAddedGuExp(addedGuExp + gainAddExp);
		rd.setGuLevel(newLevel);

		// 升级
		if (newLevel > gu.getLevel()) {
			BaseEventParam bep = new BaseEventParam(gu.getId(), way, rd);
			EPGuLevelUp ep = new EPGuLevelUp(bep, gu.getLevel(), newLevel);
			GuLevelEventPublisher.pubLevelUpEvent(ep);
		}

	}

}
