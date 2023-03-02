package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.sxdh.SxdhRankAwardService;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 神仙大会发放奖励
 *
 * @author suhq
 * @date 2020-04-25 16:44
 **/
@Slf4j
@Component("sxdhRankAwardsJob")
public class SxdhRankAwardsJob extends GameJob {
    @Autowired
    private SxdhRankAwardService sxdhRankAwardService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    /**
     * 定时器执行时，用于获得指定分钟前的战区
     */
    private static final int SEND_ADVANCE_MINUTE = -30;

    @Override
    public void job() {
        Date lastSeasonDate = DateUtil.addMinutes(DateUtil.now(), SEND_ADVANCE_MINUTE);
        //发放奖励
        sxdhRankAwardService.sendAward(lastSeasonDate);
        //生成下赛季
        List<SxdhZone> zones = sxdhZoneService.getZones(lastSeasonDate);
        if (ListUtil.isNotEmpty(zones) && sxdhRankAwardService.isSeasonAwardDay(zones.get(zones.size() - 1))) {
            log.info("生成新赛季");
            sxdhZoneService.newZones(1);
        }
    }

    @Override
    public String getJobDesc() {
        return "神仙大会奖励发送";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
