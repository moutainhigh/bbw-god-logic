package com.bbw.god.job.game;

import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.god.activity.holiday.processor.HolidayHorseRacingProcessor;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author：lwb
 * @date: 2021/3/16 15:35
 * @version: 1.0
 */
@Slf4j
@Component("holidayHorseJob")
public class HolidayHorseJob {
    @Autowired
    private HolidayHorseRacingProcessor horseRacingProcessor;
    @Autowired
    private App app;

    public void doJob() {
        Date beginDate = DateUtil.fromDateTimeString("2022-12-07 00:00:00");
        Date endDate = DateUtil.fromDateTimeString("2022-12-16 23:59:59");
        Date now = DateUtil.now();
        if (DateUtil.millisecondsInterval(now, beginDate) < 0 || DateUtil.millisecondsInterval(now, endDate) > 0) {
            log.info("节日赛马不在结算时间内！");
            return;
        }
        List<Integer> groups = ServerTool.getServerGroups();
        for (Integer group : groups) {
            horseRacingProcessor.settleNowByGid(group);
            log.info("结算节日赛马平台：{}", group);
        }
    }
}
