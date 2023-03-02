package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.exception.ErrorLevel;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.service.LoginDetailService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.copper.CopperStatistic;
import com.bbw.mc.Person;
import com.bbw.mc.dingding.DingDingMsg;
import com.bbw.mc.dingding.DingdingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 玩家资源监控定时器
 * @date 2020/11/5 10:10
 **/
@Component("monitorUserResourceJob")
@Slf4j
public class MonitorUserResourceJob extends GameJob {
    @Autowired
    private LoginDetailService loginDetailService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DingdingService dingdingService;
    @Autowired
    private StatisticServiceFactory serviceFactory;

    private static final List<AwardEnum> MONITOR_RESOURCE_LIST = Arrays.asList(AwardEnum.YB, AwardEnum.TQ, AwardEnum.YS, AwardEnum.TL);

    /**
     * 获取任务描述
     *
     * @return
     */
    @Override
    public String getJobDesc() {
        return "玩家资源监控定时器";
    }

    /**
     * 具体的任务
     */
    @Override
    public void job() {
        // 获取昨日登录的所有玩家
        Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
        Set<Long> uids = loginDetailService.getUidByDate(DateUtil.toDateString(yesterday));
        for (AwardEnum awardEnum : MONITOR_RESOURCE_LIST) {
            monitorResource(yesterday, uids, awardEnum);
        }
    }

    private void monitorResource(Date yesterday, Set<Long> uidSet, AwardEnum awardEnum) {
        // 获取对应service
        ResourceStatisticService service = serviceFactory.getByAwardEnum(awardEnum);
        // 筛选出当日资源获得前10的玩家id
        List<Long> uids = uidSet.stream().sorted(Comparator.comparing(uid -> {
            CfgServerEntity oriServer = gameUserService.getOriServer((Long) uid);
            if (null == oriServer) {
                log.warn("monitorResource获取{}玩家的原始区服", uid);
                return 0L;
            }
            if (!ServerTool.getServerGroups().contains(oriServer.getGroupId())) {
                return 0L;
            }
            ResourceStatistic statistic = service.fromRedis((Long) uid, StatisticTypeEnum.GAIN, DateUtil.toDateInt(yesterday));
            if (statistic instanceof CopperStatistic) {
                CopperStatistic copperStatistic = (CopperStatistic) statistic;
                return copperStatistic.getTodayNum();
            }
            return Long.valueOf(statistic.getToday());
        }).reversed()).limit(10).collect(Collectors.toList());
        // 发钉钉消息给运营
        String title = String.format("每日%s监控", awardEnum.getName());
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < uids.size(); i++) {
            int rank = i + 1;
            long uid = uids.get(i);
            GameUser gu = gameUserService.getGameUser(uid);
            String nickname = gu.getRoleInfo().getNickname();
            int sid = gameUserService.getOriServer(uid).getMergeSid();
            CfgServerEntity server = ServerTool.getServer(sid);
            String shortName = server.getShortName();
            ResourceStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.toDateInt(yesterday));
            long value = 0;
            if (statistic instanceof CopperStatistic) {
                CopperStatistic copperStatistic = (CopperStatistic) statistic;
                value += copperStatistic.getTodayNum();
            } else {
                value += statistic.getToday();
            }
            msg.append(rank).append(".").append(shortName).append(nickname).append(",").append(value).append("\n");
        }
        dingdingService.sendMailToOperator(new DingDingMsg(Person.Operator, ErrorLevel.NONE, title, msg.toString()));
    }
}
