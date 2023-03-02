package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.road.RoadEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 定风珠
 *
 * @author suhq
 * @date 2018年11月28日 下午4:58:07
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DingFZProcessor extends TreasureUseProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;

    private NewerGuideService newerGuideService = SpringContextUtil.getBean(NewerGuideService.class);

    public DingFZProcessor() {
        this.treasureEnum = TreasureEnum.DFZ;
        this.isAutoBuy = true;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        // 体力是否足够
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        ResChecker.checkDice(gu, config.getDiceOneShake());
    }

    @Override
    public int getNeedNum(GameUser gu, int useTimes, WayEnum way) {
        // TODO 待优化
/*		if (!newerGuideService.isPassNewerGuide(gu.getId())) {
			return 1;
		}*/

        UserTreasureRecord utr = userTreasureRecordService.getUserTreasureRecord(gu.getId(), this.treasureEnum.getValue());
        if (utr != null && utr.getLastUsePos() == gu.getLocation().getPosition()) {
            return 1 + utr.getUseTimes();
        }
        return 1;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {

        CfgRoadEntity road = RoadTool.getRoadById(gu.getLocation().getPosition());
        // 到达
        CityEventPublisher.publCityArriveEvent(gu.getId(), gu.getLocation().getPosition(), WayEnum.NONE, rd);
        // 加使用次数
        TreasureEventPublisher.pubTRecordAddEvent(gu.getId(), this.treasureEnum.getValue(), WayEnum.TREASURE_USE);
        // 扣体力
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        ResEventPublisher.pubDiceDeductEvent(gu.getId(), config.getDiceOneShake(), WayEnum.TREASURE_USE, rd);
        // 行走触发格子事件（界碑）
        RoadEventPublisher.publishRoadEvent(gu.getId(), road.getId(), WayEnum.TREASURE_USE, rd);
        //重置铲子
        this.userTreasureRecordService.resetTreasureRecordAsChangZi(gu.getId());
    }

}
