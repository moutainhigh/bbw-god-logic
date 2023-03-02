package com.bbw.god.activity.combinedserver.guildtarget;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 合服活动行会目标监听类
 *
 * @author fzj
 * @date 2022/2/14 15:42
 */
@Component
@Slf4j
@Async
public class GuildTargetListener {
    @Autowired
    GuildTargetProcessor guildTargetProcessor;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    ActivityService activityService;

    /**
     * 行会贡献值增加
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void GuildContributeAdd(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        Long uid = ep.getGuId();
        int sid = gameUserService.getActiveSid(uid);
        boolean opened = guildTargetProcessor.isOpened(sid);
        if (!opened){
            return;
        }
        List<EVTreasure> addTreasures = ep.getAddTreasures();
        List<EVTreasure> guildContributes = addTreasures.stream()
                .filter(t -> TreasureEnum.GUILD_CONTRIBUTE.getValue() == t.getId()).collect(Collectors.toList());
        if (guildContributes.isEmpty()){
            return;
        }
        for (EVTreasure treasure : guildContributes){
            activityService.handleUaProgress(uid, sid, treasure.getNum(), ActivityEnum.GUILD_TARGET);
        }
    }
}
