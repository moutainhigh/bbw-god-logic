package com.bbw.god.job.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ErrorLevel;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.IActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRankService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 区服冲榜定时发放奖励
 *
 * @author suhq
 * @date 2019年3月8日 上午11:10:06
 */
@Slf4j
@Component("sendActivityRankAwardsJob")
public class SendActivityRankAwardsJob extends ServerJob {
    private static final String PATTERN = "yyyy-MM-dd HH";
    private static int minutesInAdvance = -30;
    @Autowired
    private ServerActivityRankService serverActivityRankService;
    @Autowired
    private ActivityRankService activityRankService;

    @Override
    public void job(CfgServerEntity server) {
        Date now = DateUtil.now();

        // 获取30分钟前的时间
        Date dateBeforeHours = DateUtil.addMinutes(now, minutesInAdvance);
        final String dateStrBeforeHours = DateUtil.toString(dateBeforeHours, PATTERN);

        String partInfo = LogUtil.getLogServerPart(server);
        log.info("发送{}{}的榜单奖励...", partInfo, dateStrBeforeHours);
        List<ServerActivityRank> ars = serverActivityRankService.getServerActivityRanks(server.getId(), dateBeforeHours);
        if (!ListUtil.isNotEmpty(ars)) {
            String title = partInfo + "没有" + dateStrBeforeHours + "活动中的榜单.";
            log.info(title);
            notify.notifyCoder(ErrorLevel.HIGH, title, "-");
            return;
        }
        ars = ars.stream().filter(sar -> DateUtil.toString(sar.gainEnd(), PATTERN).equals(dateStrBeforeHours))
                .collect(Collectors.toList());
        if (ars.size() == 0) {
            log.info("{}{}没有要结算的榜单", partInfo, dateStrBeforeHours);
            return;
        }
        String raNamesToSend = ars.stream().map(tmp -> ActivityRankEnum.fromValue(tmp.gainType()).getName())
                .collect(Collectors.joining(","));
        log.info("{}{}开始结算榜单：{}", partInfo, dateStrBeforeHours, raNamesToSend);
        for (IActivityRank ar : ars) {
            activityRankService.sendRankerAwardsByType(ar);
        }
        log.info("{}{}结算完所有榜单", partInfo, dateStrBeforeHours);

    }

    @Override
    public String getJobDesc() {
        return "区服冲榜奖励发送";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
