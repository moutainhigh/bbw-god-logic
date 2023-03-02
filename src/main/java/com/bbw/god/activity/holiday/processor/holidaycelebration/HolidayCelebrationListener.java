package com.bbw.god.activity.holiday.processor.holidaycelebration;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 全服庆典积分监听
 *
 * @author: huanghb
 * @date: 2021/12/16 22:26
 */
@Component
public class HolidayCelebrationListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameCelebrationService gameCelebrationService;
    public static final List<Integer> WAY_IDS = Arrays.asList(
            WayEnum.CELEBRATION_INVITE.getValue(),
            WayEnum.BUILDING_ALTAR.getValue()
    );


    /**
     * 全服庆典个人积分监听
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void celebrationPointAdd(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        //途径检测
        int wayId = ep.getWay().getValue();
        if (!WAY_IDS.contains(wayId)) {
            return;
        }

        //是否庆典积分增加检测
        List<EVTreasure> treasures = ep.getAddTreasures();
        Optional<EVTreasure> evTreasure = treasures.stream()
                .filter(treasure -> treasure.getId() == TreasureEnum.CELEBRATION_POINTS.getValue()).findFirst();
        if (!evTreasure.isPresent()) {
            return;
        }
        //活动是否开启检测
        long uid = ep.getGuId();
        int sid = gameUserService.getActiveSid(uid);
        boolean isOpen = gameCelebrationService.isOpened(sid);
        if (!isOpen) {
            return;
        }
        gameCelebrationService.addCelebrationPointProgress(evTreasure.get());
    }


}
