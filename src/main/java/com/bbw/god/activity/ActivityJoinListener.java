package com.bbw.god.activity;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 活动加入的监听器
 * @date 2020/7/6 9:09
 **/
@Component
public class ActivityJoinListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;

    @Async
    @EventListener
    @Order(1000)
    public void joinActivity(LoginEvent event) {
        LoginPlayer loginPlayer = event.getLoginPlayer();
        GameUser gu = gameUserService.getGameUser(loginPlayer.getUid());
        activityService.joinAccRechargeActivity(gu);
    }
}
