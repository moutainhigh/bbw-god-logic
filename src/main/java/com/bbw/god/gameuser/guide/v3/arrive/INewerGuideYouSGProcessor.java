package com.bbw.god.gameuser.guide.v3.arrive;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.yousg.RDArriveYouSG;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.GodEventPublisher;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 新手引导游商馆处理器
 * @date 2020/12/11 15:00
 **/
//@Component
public class INewerGuideYouSGProcessor implements INewerGuideCityArriveProcessor {
    @Autowired
    private GodService godService;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return Arrays.asList(CityTypeEnum.YSG);
    }

    @Override
    public Class<RDArriveYouSG> getRDArriveClass() {
        return RDArriveYouSG.class;
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
        RDArriveYouSG rdArriveYouSG = new RDArriveYouSG();
        rdArriveYouSG.setSpecials(Arrays.asList(14, 17, 24));
        // 虚拟一个百宝箱
        ServerGod serverGod = godService.getUnrealServerGod(gu.getServerId(), GodEnum.BBX.getValue());// 生成配置
        GodEventPublisher.pubAttachNewGodEvent(gu.getId(), serverGod, rd);
        return rdArriveYouSG;
    }


}
