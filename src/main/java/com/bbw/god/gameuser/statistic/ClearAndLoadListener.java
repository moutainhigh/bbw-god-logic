package com.bbw.god.gameuser.statistic;

import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.EPFirstLoginPerDay;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import com.bbw.god.login.event.LoginEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 清理统计数据监听类
 * @date 2020/4/1 9:48
 */
@Component
@Slf4j
@Async
public class ClearAndLoadListener {
    @Autowired
    private StatisticServiceFactory statisticServiceFactory;

    @EventListener
    public void clearAndLoadStatistic(FirstLoginPerDayEvent event) {
        try {
            EPFirstLoginPerDay ep = event.getEP();
            statisticServiceFactory.clean(ep.getGuId());
            load(ep.getUid());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async
    public void load(LoginEvent event) {
        try {
            LoginPlayer loginPlayer = event.getLoginPlayer();
            load(loginPlayer.getUid());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 加载统计数据
     *
     * @param uid
     */
    private void load(long uid) {
        if (statisticServiceFactory.isNeedLoad(uid)) {
            statisticServiceFactory.delFromRedis(uid);
            statisticServiceFactory.loadStatistic(uid);
        }
    }
}
