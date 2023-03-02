package com.bbw.god.gameuser.guide.v3.arrive;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.kez.RDArriveKeZ;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
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
 * @description 新手引导客栈处理
 * @date 2020/12/11 14:49
 **/
//@Component
public class INewerGuideKZProcessor implements INewerGuideCityArriveProcessor {
    @Autowired
    private GodService godService;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return Arrays.asList(CityTypeEnum.KZ);
    }

    @Override
    public Class<RDArriveKeZ> getRDArriveClass() {
        return RDArriveKeZ.class;
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
        RandomStrategy strategy = RandomCardService.getSetting(RandomKeys.KE_ZHAN_NEWER_GUIDE);
        RandomParam randomParam = new RandomParam();
        randomParam.setRoleType(gu.getRoleInfo().getCountry());
        RandomResult result = RandomCardService.getRandomList(strategy, randomParam);
        // 虚拟一个大福神
        ServerGod serverGod = godService.getUnrealServerGod(gu.getServerId(), GodEnum.DFS.getValue());// 生成配置
        GodEventPublisher.pubAttachNewGodEvent(gu.getId(), serverGod, rd);
        return RDArriveKeZ.getInstance(result.getCardList());
    }
}
