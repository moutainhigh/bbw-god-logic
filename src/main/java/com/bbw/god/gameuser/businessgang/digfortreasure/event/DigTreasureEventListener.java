package com.bbw.god.gameuser.businessgang.digfortreasure.event;

import com.bbw.god.city.event.CityArriveEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.businessgang.digfortreasure.DigTreasureService;
import com.bbw.god.gameuser.businessgang.digfortreasure.RDDigTreasureInfo;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 挖宝监听器
 *
 * @author: huanghb
 * @date: 2023/2/14 10:11
 */
@Component
public class DigTreasureEventListener {
    @Autowired
    private DigTreasureService digTreasureService;

    @EventListener
    @Order(1)
    public void arriveCity(CityArriveEvent event) {
        EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
        Integer roadId = ep.getValue();
        Long uid = ep.getGuId();
        RDAdvance rd = (RDAdvance) ep.getRd();
        //挖宝信息
        if (digTreasureService.isShowDigTreasure(uid)) {
            RDDigTreasureInfo digForTreasureInfo = digTreasureService.refreshMyDigStatusToNewPalac(uid, roadId);
            rd.setArriveDigTreasure(digForTreasureInfo);
        }

    }
}
