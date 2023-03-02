package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.road.RoadEventPublisher;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 山河社稷图
 *
 * @author suhq
 * @date 2018年11月28日 下午5:04:09
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShanHSJTProcessor extends TreasureUseProcessor {
    @Autowired
    private GodService godService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;

    public ShanHSJTProcessor() {
        this.treasureEnum = TreasureEnum.SHSJT;
        this.isAutoBuy = false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        // 体力是否足够
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        ResChecker.checkDice(gu, config.getDiceOneShake());
        // 悬浮位置检测
        int position = param.gainPosition();
        // CfgRoadEntity road = CfgRoad.I.getRoadById(position);
        Optional<ServerGod> serverGod = this.godService.getGodRemainOnRoad(gu.getServerId(), position, gu.getId());
        if (serverGod.isPresent()) {
            throw new ExceptionForClientTip("treasure.use.godOn");
        }
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        int position = param.gainPosition();
        CfgRoadEntity road = RoadTool.getRoadById(position);
        // 新的方向
        int direction = road.getNextDirection(0, 0);
        rd.setDirection(direction);

        // 移到新的位置
        gu.moveTo(position, direction);
        // 到达
        CityEventPublisher.publCityArriveEvent(gu.getId(), position, WayEnum.NONE, rd);
        // 扣体力
        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
        ResEventPublisher.pubDiceDeductEvent(gu.getId(), config.getDiceOneShake(), WayEnum.TREASURE_USE, rd);
        // 行走触发格子事件（界碑）
        RoadEventPublisher.publishRoadEvent(gu.getId(), road.getId(), WayEnum.TREASURE_USE, rd);
        // 重置定风珠、醒酒毡使用记录
        this.userTreasureRecordService.resetTreasureRecordAsNewPos(gu.getId(), WayEnum.TREASURE_USE);
    }

}
