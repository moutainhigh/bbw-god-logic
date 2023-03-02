package com.bbw.god.gameuser.statistic.resource.nightmarecity;

import com.bbw.common.DateUtil;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 梦魇城池监听统计类
 * @date 2020/9/15 10:39
 **/
@Component
@Slf4j
@Async
public class NightmareCityResListener {
    @Autowired
    private NightmareCityResStatisticService statisticService;

    @Order(2)
    @EventListener
    @SuppressWarnings("unchecked")
    public void addCity(UserCityAddEvent event) {
        try {
            EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
            // 非梦魇城池直接return
            if (!ep.getValue().isNightmare()) {
                return;
            }
            EPCityAdd epCityAdd = ep.getValue();
            int cityId = epCityAdd.getCityId();
            CfgCityEntity city = CityTool.getCityById(cityId);
            statisticService.addCity(ep.getGuId(), DateUtil.getTodayInt(), city.getLevel(), city.getCountry());
            NightmareCityStatistic statistic = statisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
