package com.bbw.god.gameuser.res.copper;

import com.bbw.god.city.UserCityService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDResAddInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CopperListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;

    /**
     * 事件处理优先级低于活动铜钱加倍
     *
     * @param event
     */
    @EventListener
    @Order(2)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        long addedCopper = ep.gainAddCopper();
        //特殊铜钱获取途径在此加
        switch (ep.getWay()) {
            case JB:
            case QZ_AWARD:
            case XCS:
            case DCS:
            case BBX_PICK:
            case YD:
                double buff = userCityService.addNightmareCopperBuff(gu);
                addedCopper *= (1 + buff);
                break;
            default:
        }
//        if (WayEnum.FIGHT_TRAINING == ep.getWay()) {
//            log.info("{}练兵获得铜钱{}", ep.getGuId(), ep.gainAddCopper());
//        }
        gu.addCopper(addedCopper, ep.getWay());
        RDCommon rd = ep.getRd();

        if (ep.getWay() == WayEnum.JB) {
            RDAdvance rdAdvance = (RDAdvance) rd;
            rdAdvance.setBoundCopper(addedCopper);// 兼容界碑需要特殊处理
        } else {
            rd.addCopper(addedCopper);
            List<RDResAddInfo> addCoppers = ep.getAddCoppers().stream().map(tmp ->
                    new RDResAddInfo(tmp.getWayType().getValue(), tmp.getValue())).collect(Collectors.toList());
            rd.setAddCoppers(addCoppers);
        }
    }

    @EventListener
    public void deductCopper(CopperDeductEvent event) {
        EPCopperDeduct ep = event.getEP();
        GameUser gu = gameUserService.getGameUser(ep.getGuId());

        gu.deductCopper(ep.getDeductCopper());
        RDCommon rd = ep.getRd();
        rd.addCopper(-ep.getDeductCopper());
    }

}
