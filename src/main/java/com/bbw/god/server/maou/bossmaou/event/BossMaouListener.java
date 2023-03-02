package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BossMaouListener {
    @Autowired
    private ServerBossMaouService serverBossMaouService;

    /**
     * 最后一击的奖励是实时发放的
     *
     * @param event
     */
    @EventListener
    public void killMaou(BossMaouKilledEvent event) {
        EPBossMaou ep = event.getEP();
        ServerBossMaou bossMaou = ep.getBossMaou();
        // 最后一击奖励四星以上法宝
        TreasureEventPublisher.pubTAddEvent(bossMaou.getKiller(), bossMaou.getKillAward(), 1, WayEnum.FIGHT_MAOU, ep.getRd());
        // 发放魔王奖励
        BossMaouEventPublisher.pubAwardSendEvent(bossMaou);
    }

    @Async
    @EventListener
    public void sendMaouAward(BossMaouAwardSendEvent event) {
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        // 发放魔王奖励
        this.serverBossMaouService.sendMaouAwards(event.getEP().getBossMaou());
        this.serverBossMaouService.initNextMaou(event.getEP().getBossMaou());
    }

}
