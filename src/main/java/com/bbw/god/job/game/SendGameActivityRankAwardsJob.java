package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activityrank.*;
import com.bbw.god.activityrank.game.GameActivityRank;
import com.bbw.god.activityrank.game.GameActivityRankService;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全服冲榜定时发放奖励
 *
 * @author suhq
 * @date 2020年3月21日 上午00:05:06
 */
@Slf4j
@Component("sendGameActivityRankAwardsJob")
public class SendGameActivityRankAwardsJob extends GameJob {
    private static final String PATTERN = "yyyy-MM-dd HH";
    private static int minutesInAdvance = -30;
    @Autowired
    private GameActivityRankService gameActivityRankService;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private GuessActivityRankAwardService guessActivityRankAwardService;

    @Override
    public void job() {
        Date now = DateUtil.now();

        // 获取30分钟前的时间
        Date dateBeforeHours = DateUtil.addMinutes(now, minutesInAdvance);
        final String dateStrBeforeHours = DateUtil.toString(dateBeforeHours, PATTERN);
        List<Integer> serverGroups = ServerTool.getServerGroups();
        for (Integer serverGroup : serverGroups) {
            if (serverGroup==17){
                continue;
            }
            log.info("发送区服组{}{}的榜单奖励...", serverGroup, dateStrBeforeHours);
            List<GameActivityRank> ars = gameActivityRankService.getGameActivityRanks(serverGroup, dateBeforeHours);
            if (ListUtil.isEmpty(ars)) {
                continue;
            }
            ars = ars.stream().filter(sar -> DateUtil.toString(sar.gainEnd(), PATTERN).equals(dateStrBeforeHours))
                    .collect(Collectors.toList());
            if (ars.size() == 0) {
                log.info("区服组{}{}没有要结算的榜单", serverGroup, dateStrBeforeHours);
                continue;
            }
            String raNamesToSend = ars.stream().map(tmp -> ActivityRankEnum.fromValue(tmp.gainType()).getName())
                    .collect(Collectors.joining(","));
            log.info("区服组{}{}开始结算榜单：{}", serverGroup, dateStrBeforeHours, raNamesToSend);
            for (IActivityRank ar : ars) {
                // 是否为竞猜榜单奖励发放
                boolean isGuessActivityRank = ar.gainType().equals(ActivityRankEnum.GUESSING_COMPETITION_RANK.getValue())
                        || ar.gainType().equals(ActivityRankEnum.GUESSING_COMPETITION_DAY_RANK.getValue());
                if (isGuessActivityRank){
                    guessActivityRankAwardService.sendRankerAwardsByType(ar);
                }else {
                    activityRankService.sendRankerAwardsByType(ar);
                }
            }
            log.info("区服组{}{}结算完所有榜单", serverGroup, dateStrBeforeHours);
        }
    }

    @Override
    public String getJobDesc() {
        return "全服冲榜奖励发送";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}