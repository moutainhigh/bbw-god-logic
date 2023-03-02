package com.bbw.god.statistics.serverstatistic;

import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @title: ServerTreasureListener
 * @projectName bbw-god-logic-server
 * @description: 区服法宝统计，具体到某个法宝
 * @date 2019/6/199:21
 */
@Component
@Async
public class ServerTreasureListener {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private GodServerStatisticService godServerStatisticService;

    @EventListener
    @Order(2)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        int sid = gameUserService.getActiveSid(ep.getGuId());
        ep.getAddTreasures().forEach(s -> {
            Integer id = s.getId();
            Integer num = s.getNum();
            CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(id);
            if (treasureEntity == null) {
                godServerStatisticService.addOutput(sid, ep.getWay(), num, id.toString());
            } else {
                godServerStatisticService.addOutput(sid, ep.getWay(), num, treasureEntity.getName());
            }
        });
    }

    @EventListener
    @Order(2)
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        int sid = gameUserService.getActiveSid(ep.getGuId());
        Integer id = ep.getDeductTreasure().getId();
        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(id);
        Integer num = ep.getDeductTreasure().getNum();
        if (treasureEntity == null) {
            godServerStatisticService.addConsume(sid, ep.getWay(), num, "法宝:" + id.toString());
        } else {
            godServerStatisticService.addConsume(sid, ep.getWay(), num, treasureEntity.getName());
        }
    }
}
