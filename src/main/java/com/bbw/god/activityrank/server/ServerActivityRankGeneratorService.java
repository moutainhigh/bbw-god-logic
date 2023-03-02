package com.bbw.god.activityrank.server;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.god.activity.config.ActivityConfig;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.CfgActivityRankGenerateRule;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.server.ServerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerActivityRankGeneratorService {
    @Autowired
    private ServerService serverService;
    @Autowired
    private ActivityConfig activityConfig;

    /**
     * 开服冲榜活动初始化(开服调用前要执行清除处理)
     *
     * @param server
     */
    public void initServerActivityRankForNewServer(CfgServerEntity server) {
        // 生成5期榜单
        appendActivityRanks(server, 5);

    }

    public void appendActivityRanks(CfgServerEntity server, int week) {
        for (int i = 0; i < week; i++) {
            appendActivityRanks(server);
        }
    }

    public void reappendActivityRanks(CfgServerEntity server, Date sinceDate, int week) {
        int sId = server.getMergeSid();
        List<ServerActivityRank> sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
        List<Long> sarIdsToDel = sars.stream().filter(tmp -> tmp.getEnd().after(sinceDate))
                .mapToLong(ServerActivityRank::getId).boxed().collect(Collectors.toList());

        this.serverService.deleteServerDatas(sId, sarIdsToDel, ServerActivityRank.class);

        appendActivityRanks(server, week);

    }

    /**
     * 初始化一定期数的榜单（以追加的方式）
     * 冲榜定时提前生成    *
     *
     * @param server
     */
    private void appendActivityRanks(CfgServerEntity server) {
        String serverPart = LogUtil.getLogServerPart(server);
        log.info("开始追加{}榜单", serverPart);
        int sId = server.getMergeSid();
        List<ServerActivityRank> sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
        int weekToGenerate = 1;
        // 获取最近生成的一期
        Optional<ServerActivityRank> lastFhbOptional = sars.stream().filter(tmp -> tmp.getType() == ActivityRankEnum.FUHAO_RANK.getValue()).max(Comparator.comparing(ServerActivityRank::getOpenWeek));
        if (lastFhbOptional.isPresent()) {
            weekToGenerate = lastFhbOptional.get().getOpenWeek() + 1;
        }
        List<CfgActivityRankGenerateRule> generateRules = Cfg.I.get(CfgActivityRankGenerateRule.class);
        for (CfgActivityRankGenerateRule generateRule : generateRules) {
            if (!generateRule.isEnableWeek(weekToGenerate)) {
                continue;
            }
            ArParam arParam = getArParam(server, sars, generateRule, weekToGenerate);
            // 如果榜单存在则跳过
            boolean isExist = sars.stream().anyMatch(tmp -> {
                return generateRule.getLoop().contains(tmp.getType())
                        && DateUtil.toDateTimeLong(tmp.getBegin()) == DateUtil.toDateTimeLong(arParam.getBegin())
                        && DateUtil.toDateTimeLong(tmp.getEnd()) == DateUtil.toDateTimeLong(arParam.getEnd());
            });
            if (isExist) {
                continue;
            }

            // 生成实例
            ServerActivityRank sar = ServerActivityRank.instance(sId, arParam.getArType(), weekToGenerate, arParam.getBegin(), arParam.getEnd());
            // 处理加奖
            if (arParam.getArType() == ActivityRankEnum.RECHARGE_RANK.getValue()) {
                Award award = getExtraAwardCard(weekToGenerate);
                sar.setExtraAward(JSONUtil.toJson(Arrays.asList(award)));
            }
            this.serverService.addServerData(sId, sar);
        }
    }

    public ArParam getArParam(CfgServerEntity server, List<ServerActivityRank> sars, CfgActivityRankGenerateRule generateRule, int weekToGenerate) {
        Optional<ServerActivityRank> lastOptional = sars.stream().filter(tmp -> generateRule.getLoop().contains(tmp.getType())).max(Comparator.comparing(ServerActivityRank::getOpenWeek));
        Date beginDate = null;
        Date endDate = null;
        int arType = 0;
        if (lastOptional.isPresent()) {
            beginDate = DateUtil.addDays(lastOptional.get().getEnd(), 1);
            arType = generateRule.getNextType(lastOptional.get().getType());
        } else {
            beginDate = DateUtil.addDays(server.getBeginTime(), generateRule.getBeginDay() - 1);
            arType = generateRule.getFirstType();
        }
        beginDate = DateUtil.getDateBegin(beginDate);
        endDate = DateUtil.addDays(beginDate, generateRule.getDuration() - 1);
        endDate = DateUtil.toDate(endDate, generateRule.getEndHms());
        ArParam arParam = new ArParam(arType, weekToGenerate, beginDate, endDate);
        return arParam;
    }

    /**
     * 从指定日期追加榜单
     *
     * @param server
     * @param baseDate
     * @param week
     */
    public void addActivityRanks(CfgServerEntity server, Date baseDate, int week) {
        int sId = server.getMergeSid();
        List<ServerActivityRank> sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
        List<Long> sarIdsToDel = sars.stream().filter(tmp -> tmp.getEnd().after(baseDate))
                .mapToLong(ServerActivityRank::getId).boxed().collect(Collectors.toList());

        this.serverService.deleteServerDatas(sId, sarIdsToDel, ServerActivityRank.class);

        int openWeek = this.serverService.getOpenWeek(sId, baseDate);
        int generatedWeek = openWeek - 1;
        ServerActivityRank sar = null;
        for (int i = 1; i <= week; i++) {
            // 生成第几期
            int weekToGenerate = generatedWeek + i;

            List<CfgActivityRankGenerateRule> generateRules = Cfg.I.get(CfgActivityRankGenerateRule.class);
            for (CfgActivityRankGenerateRule generateRule : generateRules) {
                if (!generateRule.isEnableWeek(weekToGenerate)) {
                    continue;
                }
                ArParam arParam = getArParam(server, sars, generateRule, weekToGenerate);
                if (i == 1) {
                    arParam.setBegin(baseDate);
                }
                // 如果榜单存在则跳过
                boolean isExist = sars.stream().anyMatch(tmp -> {
                    return generateRule.getLoop().contains(tmp.getType())
                            && DateUtil.toDateTimeLong(tmp.getBegin()) == DateUtil.toDateTimeLong(arParam.getBegin())
                            && DateUtil.toDateTimeLong(tmp.getEnd()) == DateUtil.toDateTimeLong(arParam.getEnd());
                });
                if (isExist) {
                    continue;
                }

                // 生成实例
                sar = ServerActivityRank.instance(sId, arParam.getArType(), weekToGenerate, arParam.getBegin(), arParam.getEnd());
                this.serverService.addServerData(sId, sar);
            }
        }

    }

    /**
     * 获得充值榜的额外奖励卡牌
     *
     * @return
     */
    public Award getExtraAwardCard(int openWeek) {
        List<Integer> rechargeCards = this.activityConfig.getRechargeCard();
        if (openWeek <= rechargeCards.size()) {
            return new Award(rechargeCards.get(openWeek - 1), AwardEnum.KP, 1);
        }
        // 第七周起奖励
        return this.activityConfig.getRechargeCardAward();
    }

    @Data
    @AllArgsConstructor
    public static class ArParam {
        private int arType;
        private int weekToGenerage;
        private Date begin;
        private Date end;
    }
}
