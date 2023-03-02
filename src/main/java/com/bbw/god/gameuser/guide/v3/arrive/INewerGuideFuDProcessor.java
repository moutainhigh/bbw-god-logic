package com.bbw.god.gameuser.guide.v3.arrive;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.fud.RDArriveFuD;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 新手引导福地处理器
 * @date 2020/12/11 14:56
 **/
//@Component
public class INewerGuideFuDProcessor implements INewerGuideCityArriveProcessor {
    @Autowired
    private NewerGuideService newerGuideService;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return Arrays.asList(CityTypeEnum.FD, CityTypeEnum.FD2, CityTypeEnum.FD3);
    }

    @Override
    public Class<RDArriveFuD> getRDArriveClass() {
        return RDArriveFuD.class;
    }

    /**
     * 处理到达一座功能建筑要处理的业务
     *
     * @param gu
     * @param city
     * @param rd
     */
    @Override
    public RDCityInfo arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        int addedGold = 10;
        long uid = gu.getId();
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.FD, rd);
        EPGoldAdd goldAdd = new EPGoldAdd(bep, addedGold);
        ResEventPublisher.pubGoldAddEvent(goldAdd);
        //newerGuideService.sendTreasureToNum(uid, TreasureEnum.QXC.getValue(), 1, WayEnum.FD, rd);
        return RDArriveFuD.fromRDCommon(rd);
    }
}
