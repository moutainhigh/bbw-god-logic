package com.bbw.god.city.xianrd;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 仙人洞 - 赠送法宝或元素
 *
 * @author suhq
 * @date 2018年10月24日 下午5:48:52
 */
@Component
public class XianRDProcessor implements ICityArriveProcessor {

    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.XRD);

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return this.cityTypes;
    }

    @Override
    public Class<RDArriveXianRD> getRDArriveClass() {
        return RDArriveXianRD.class;
    }

    @Override
    public RDArriveXianRD arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        int random = PowerRandom.getRandomBySeed(100);
        if (random <= 20) {
            CfgTreasureEntity treasure = TreasureTool.getRandomOldTreasure(20, 50, 260);
            TreasureEventPublisher.pubTAddEvent(gu.getId(), treasure.getId(), 1, WayEnum.XRD, rd);
        } else {
            ResEventPublisher.pubEleAddEvent(gu.getId(), 1, WayEnum.XRD, rd);
        }
        return RDArriveXianRD.fromRDCommon(rd);
    }

}
