package com.bbw.god.activityrank.game.celebration;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 庆典冲榜
 *
 * @author fzj
 * @date 2021/12/16 16:33
 */
@Slf4j
@Component
public class CelebrationRankListener {
    private static final List<WayEnum> WAY = Arrays.asList(WayEnum.BUILDING_ALTAR, WayEnum.CELEBRATION_INVITE, WayEnum.DISPATCH_TASK);
    @Autowired
    private ActivityRankService activityRankService;

    @Async
    @EventListener
    @Order(1000)
    public void addIntegral(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        boolean outPutWay = WAY.contains(ep.getWay());
        if (!outPutWay){
            return;
        }
        EVTreasure et = ep.getAddTreasures().stream()
                .filter(e -> e.getId() == TreasureEnum.CELEBRATION_POINTS.getValue()).findFirst().orElse(null);
        if (null == et){
            return;
        }
        long uid = ep.getGuId();
        activityRankService.incrementRankValue(uid, et.getNum(), ActivityRankEnum.CELEBRATION_RANK);
        activityRankService.incrementRankValue(uid, et.getNum(), ActivityRankEnum.CELEBRATION_DAY_RANK);
    }
}
