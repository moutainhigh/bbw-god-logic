package com.bbw.god.activityrank.server.fight;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.chanjie.event.ChanjieFightEvent;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 战斗荣耀榜
 *
 * @author suhq
 * @date 2019-09-18 09:20:09
 */
@Async
@Component
public class FightRankListener {
    private ActivityRankEnum rankType = ActivityRankEnum.FIGHT_RANK;

    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private UserCityService userCityService;

    @EventListener
    @Order(1000)
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        long uid = ep.getGuId();
        FightTypeEnum fightType = ep.getFightType();
        int pos = ep.getPos();
        CfgCityEntity city = CityTool.getCityByRoadId(pos);
        int point = getPoint(uid, city.getId(), fightType);
        activityRankService.incrementRankValue(uid, point, rankType);
    }

    @EventListener
    @Order(1000)
    public void chanJieFight(ChanjieFightEvent event) {
        long uid = event.getEP().getGuId();
        int point = getPoint(uid, 0, FightTypeEnum.CJDF);
        activityRankService.incrementRankValue(uid, point, rankType);
    }

    /**
     * 获得积分
     *
     * @param city
     * @return
     */
    private int getPoint(long uid, Integer cityId, FightTypeEnum fightType) {
        switch (fightType) {
            case YG:
                return 10;
            case ZXZ:
                return 3;
            case CJDF:
                return 10;
            case ATTACK:
                return getPointAsAttack(cityId);
            case PROMOTE:
                return getPointAsPromote(cityId);
            case TRAINING:
                return getPointAsTrainning(uid, cityId);
            default:
                return 0;
        }
    }

    private int getPointAsAttack(int cityId) {
        CfgCityEntity city = CityTool.getCityById(cityId);
        int level = city.getLevel();
        switch (level) {
            case 1:
            case 2:
                return 10;
            case 3:
            case 4:
                return 15;
            case 5:
                return 25;
            default:
                return 0;
        }
    }

    private int getPointAsPromote(int cityId) {
        CfgCityEntity city = CityTool.getCityById(cityId);
        int level = city.getLevel();
        switch (level) {
            case 1:
            case 2:
                return 20;
            case 3:
            case 4:
                return 30;
            case 5:
                return 50;
            default:
                return 0;
        }
    }

    private int getPointAsTrainning(Long uid, int cityId) {
        UserCity userCity = userCityService.getUserCity(uid, cityId);
        if (userCity == null) {
            return 0;
        }
        CfgCityEntity city = CityTool.getCityById(cityId);
        int level = city.getLevel();
        int point = 0;
        switch (level) {
            case 1:
            case 2:
                point = 10;
                break;
            case 3:
            case 4:
                point = 15;
                break;
            case 5:
                point = 25;
                break;
            default:
                break;
        }
        if (userCity.getHierarchy() > 0) {
            point *= 2;
        }
        return point;
    }
}
