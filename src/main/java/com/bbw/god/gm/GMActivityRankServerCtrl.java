package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.activityrank.*;
import com.bbw.god.activityrank.game.GameActivityRankService;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRankGeneratorService;
import com.bbw.god.activityrank.server.attack.AttackRankService;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gm.service.ActivityRankRegenerateService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 冲榜相关管理服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:30
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMActivityRankServerCtrl extends AbstractController {
    @Autowired
    private ServerActivityRankGeneratorService activityRankGeneratorService;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ActivityRankRegenerateService activityRankRegenerateService;
    @Autowired
    private GameActivityRankService gameActivityRankService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private AttackRankService attackRankService;

    /**
     * 获取某些区服某个时间后的榜单活动
     *
     * @param serverNames
     * @param sinceDateTime
     * @return
     */
    @GetMapping("server!showServerActivityRanks")
    public Rst showServerActivityRanks(String serverNames, String sinceDateTime) {
        Rst rst = Rst.businessOK();
        Date since = DateUtil.fromDateTimeString(sinceDateTime);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            List<ServerActivityRank> sars = this.serverService.getServerDatas(server.getMergeSid(), ServerActivityRank.class);
            List<String> strs = sars.stream()
                                    .filter(tmp -> tmp.gainBegin().after(since))
                                    .map(tmp -> tmp.toDesString())
                                    .collect(Collectors.toList());
            rst.put(server.getName(), strs);
        }
        return rst;
    }

    /**
     * 获取某天结束的榜单
     *
     * @param endDateTime
     * @return
     */
    @GetMapping("server!showEndActivityRanks")
    public Rst showEndActivityRanks(String endDateTime) {
        Rst rst = Rst.businessOK();
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        for (CfgServerEntity server : servers) {
            List<ServerActivityRank> sars = this.serverService.getServerDatas(server.getMergeSid(), ServerActivityRank.class);
            List<String> strs = sars.stream()
                                    .filter(tmp -> DateUtil.toDateTimeString(tmp.gainEnd()).equals(endDateTime))
                                    .map(tmp -> tmp.toDesString())
                                    .collect(Collectors.toList());
            rst.put(server.getName(), strs);
        }
        return rst;
    }


    @GetMapping("server!addServerActivityRank")
    public Rst addServerActivityRank(String serverNames, int type, String begin, String end) {
        ActivityRankEnum typeEnum = ActivityRankEnum.fromValue(type);
        if (typeEnum == null) {
            return Rst.businessFAIL("无效的活动");
        }
        Date now = DateUtil.now();
        Date beginDate = DateUtil.fromDateTimeString(begin);
        Date endDate = DateUtil.fromDateTimeString(end);
        if (beginDate.after(endDate)) {
            return Rst.businessFAIL("活动开始时间需早于结束时间");
        }
        if (now.after(beginDate) || now.after(endDate)) {
            return Rst.businessFAIL("活动时间必需晚于当前时间");
        }
        CfgActivityRankEntity car = this.activityRankService.getActivities(typeEnum).get(0);
        int scope = car.getScope();
        if (scope == ActivityScopeEnum.GAME.getValue()) {
            return Rst.businessFAIL("非区服榜单");
        }
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date date = DateUtil.addMinutes(beginDate, 10);
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            // 如果已存在生效中的实例，则删除
            List<ServerActivityRank> sars = this.getServerActivityRanks(sId, date);
            if (ListUtil.isNotEmpty(sars)) {
                List<Long> ids = sars.stream().filter(tmp -> tmp.getType() == type).map(ServerActivityRank::getId).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(ids)) {
                    this.serverService.deleteServerDatas(sId, ids, ServerActivityRank.class);
                }

            }
            int openWeek = this.serverService.getOpenWeek(sId, date);
            ServerActivityRank sar = ServerActivityRank.instance(sId, type, openWeek, beginDate, endDate);
            this.serverService.addServerData(sId, sar);
            log.info("{}{}初始化完成{}~{}", LogUtil.getLogServerPart(server), car.getName(), begin, end);
        }
        return Rst.businessOK();
    }

    /**
     * 从指定天开始生成榜单
     *
     * @param serverNames
     * @param beginDate
     * @param week
     * @return
     */
    @GetMapping("server!addServerActivityRanks")
    public Rst addServerActivityRanks(String serverNames, String beginDate, int week) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            this.activityRankGeneratorService.addActivityRanks(server, DateUtil.fromDateTimeString(beginDate), week);
        }

        return Rst.businessOK();
    }

    @GetMapping("server!reappendServerActivityRanks")
    public Rst reappendServerActivityRanks(String serverNames, String sinceDate, int week) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            this.activityRankGeneratorService.reappendActivityRanks(server, DateUtil.fromDateTimeString(sinceDate),
                    week);
        }

        return Rst.businessOK();
    }

    @GetMapping("server!appendServerActivityRanks")
    public Rst appendServerActivityRanks(String serverNames, int week) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            this.activityRankGeneratorService.appendActivityRanks(server, week);
        }
        return Rst.businessOK();
    }

    /**
     * 修复榜单
     *
     * @param serverNames
     * @param beginDate
     * @return
     */
    @GetMapping("server!repairActivityRankTime")
    public Rst repairActivityRankTime(String serverNames, String beginDate) {
        Date baseDate = DateUtil.fromDateTimeString(beginDate);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            List<ServerActivityRank> sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
            //删除该时间后的所有榜单
            List<Long> sarIdsToDel = sars.stream().filter(tmp -> tmp.getEnd().after(baseDate))
                    .mapToLong(ServerActivityRank::getId).boxed().collect(Collectors.toList());

            this.serverService.deleteServerDatas(sId, sarIdsToDel, ServerActivityRank.class);
            sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
            int generatedWeek = 1;
            // 获取最近生成的一期富豪榜
            Optional<ServerActivityRank> lastFhbOptional = sars.stream().filter(tmp -> tmp.getType() == ActivityRankEnum.FUHAO_RANK.getValue()).max(Comparator.comparing(ServerActivityRank::getOpenWeek));
            if (lastFhbOptional.isPresent()) {
                generatedWeek = lastFhbOptional.get().getOpenWeek();
            }
            ServerActivityRank sar = null;
            List<Integer> loop = Arrays.asList(10110, 10050, 10130);
            for (int i = 1; i <= 5; i++) {
                // 生成第几期
                int weekToGenerate = generatedWeek + i;
                sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);

                List<CfgActivityRankGenerateRule> generateRules = Cfg.I.get(CfgActivityRankGenerateRule.class);
                for (CfgActivityRankGenerateRule generateRule : generateRules) {
                    if (!generateRule.isEnableWeek(weekToGenerate)) {
                        continue;
                    }
                    ServerActivityRankGeneratorService.ArParam arParam = this.activityRankGeneratorService.getArParam(server, sars, generateRule, weekToGenerate);
                    if (i == 1) {
                        arParam.setBegin(baseDate);
                        if (loop.contains(arParam.getArType())) {
                            arParam.setEnd(DateUtil.addDays(arParam.getEnd(), 2));
                        }
                    }

                    // 生成实例
                    sar = ServerActivityRank.instance(sId, arParam.getArType(), weekToGenerate, arParam.getBegin(), arParam.getEnd());
                    this.serverService.addServerData(sId, sar);
                }
            }
        }
        return Rst.businessOK();

    }

    /**
     * 修复当前榜单
     *
     * @param serverNames
     * @param type
     * @return
     */
    @GetMapping("server!fixActivityRank")
    public Rst fixActivityRank(String serverNames, int type) {
        ActivityRankEnum typeEnum = ActivityRankEnum.fromValue(type);
        List<ActivityRankEnum> notSupportRanks = Arrays.asList(ActivityRankEnum.XIAN_YUAN_RANK, ActivityRankEnum.XIAN_YUAN_DAY_RANK);
        if (notSupportRanks.contains(typeEnum)) {
            return Rst.businessFAIL("当前不支持" + typeEnum.getName() + "榜单修复");
        }
        long beginTime = System.currentTimeMillis();
        String successServer = "，成功的服务器：";
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);

        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            String logServerPart = LogUtil.getLogServerPart(server);
            log.info("====================重新构建{}{}的榜单========================", logServerPart, typeEnum.getName());
            IActivityRank ar = this.activityRankService.getActivityRank(sId, typeEnum);
            if (ar == null) {
                continue;
            }
            this.activityRankService.removeRank(ar);
            log.info("开始加入玩家数据。。。");
            int rankerSize = this.activityRankRegenerateService.regenerateRankers(ar);
            log.info("{}{}的榜单创建完成,共有{}名玩家入榜", logServerPart, typeEnum.getName(), rankerSize);
            successServer += "【" + server.getName() + "】入榜人数：" + rankerSize + ";";
        }
        successServer = "总执行时(ms)" + (System.currentTimeMillis() - beginTime) + successServer;
        return Rst.businessOK(successServer);
    }

    /**
     * 删除当前活动榜单
     *
     * @param serverNames
     * @return
     */
    @GetMapping("server!delServerActivityRank")
    public Rst delServerActivityRank(String serverNames, String delDate) {

        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date date = DateUtil.fromDateTimeString(delDate);
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            // 获取当前生效的榜单
            List<ServerActivityRank> sars = this.getServerActivityRanks(sId, date);
            if (ListUtil.isNotEmpty(sars)) {
                List<Long> ids = sars.stream().map(ServerActivityRank::getId).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(ids)) {
                    this.serverService.deleteServerDatas(sId, ids, ServerActivityRank.class);
                }

            }
            log.info("{}当前榜单删除完成", LogUtil.getLogServerPart(server));
        }
        return Rst.businessOK();
    }

    @GetMapping("server!delServerActivityRankSinceDate")
    public Rst delServerActivityRankSinceDate(String serverNames, String sinceDate) {

        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date date = DateUtil.fromDateTimeString(sinceDate);
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            // 获取当前生效的榜单
            List<ServerActivityRank> sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
            if (ListUtil.isNotEmpty(sars)) {
                List<Long> ids = sars.stream().filter(tmp -> tmp.getEnd().after(date)).map(ServerActivityRank::getId).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(ids)) {
                    this.serverService.deleteServerDatas(sId, ids, ServerActivityRank.class);
                }

            }
            log.info("{}当前榜单删除完成", LogUtil.getLogServerPart(server));
        }
        return Rst.businessOK();
    }

    /**
     * 更新当前榜单的时间
     *
     * @param serverNames
     * @return
     */
    @GetMapping("server!updateServerActivityRankTime")
    public Rst updateServerActivityRankTime(String serverNames, int type, String beginDate, String endDate) {

        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date now = DateUtil.now();
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            // 获取当前生效的榜单
            List<ServerActivityRank> sars = this.getServerActivityRanks(sId, now);
            if (ListUtil.isNotEmpty(sars)) {
                ServerActivityRank sar = sars.stream().filter(tmp -> tmp.getType() == type).findFirst().orElse(null);
                if (sar != null) {
                    // 更新开始时间
                    if (StrUtil.isNotBlank(beginDate)) {
                        sar.setBegin(DateUtil.fromDateTimeString(beginDate));
                    }
                    // 更新结束时间
                    if (StrUtil.isNotBlank(endDate)) {
                        sar.setEnd(DateUtil.fromDateTimeString(endDate));
                    }
                    this.serverService.updateServerData(sar);
                }

            }
            log.info("{}{}时间更新完成", LogUtil.getLogServerPart(server), ActivityRankEnum.fromValue(type));
        }
        return Rst.businessOK();
    }

    /**
     * 更新区服所有榜单结束时间
     *
     * @param serverNames
     * @param endDate
     * @return
     */
    @ApiOperation(value = "更新区服所有榜单结束时间")
    @GetMapping("server!updateServerAllActivityRankEndTime")
    public Rst updateServerAllActivityRankEndTime(String serverNames, String endDate) {
        //获取区服
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        if (servers.isEmpty()) {
            return Rst.businessFAIL("区服为空！");
        }
        if (StrUtil.isBlank(endDate)) {
            return Rst.businessFAIL("结束时间为空！");
        }
        Date now = DateUtil.now();
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            // 获取当前生效的榜单
            List<ServerActivityRank> sars = this.getServerActivityRanks(sId, now);
            if (sars.isEmpty()) {
                continue;
            }
            //更新结束时间
            for (ServerActivityRank serverActivityRank : sars) {
                serverActivityRank.setEnd(DateUtil.fromDateTimeString(endDate));
            }
            this.serverService.updateServerData(sars);
        }
        return Rst.businessOK();
    }

    /**
     * 获得某个时间对应的冲榜活动
     *
     * @param sId
     * @param date
     * @return
     */
    private List<ServerActivityRank> getServerActivityRanks(int sId, Date date) {
        List<ServerActivityRank> sars = this.serverService.getServerDatas(sId, ServerActivityRank.class);
        return sars.stream().filter(sar -> DateUtil.isBetweenIn(date, sar.getBegin(), sar.getEnd()))
                .collect(Collectors.toList());
    }

    /**
     * 给某些区服的某天的某个榜单补发奖励
     *
     * @param serverNames 区服全称1,区服全称2
     * @param date        要补发的榜单的的时间
     * @param rankType    要补发的榜单
     * @return
     */
    @GetMapping("server!sendServerRankAwardMail")
    public Rst sendServerRankAwardMail(String serverNames, String date, int rankType) {
        //是否有效时间格式
        Boolean vaildDatePatter = DateUtil.isVaildDatePattern(date, DateUtil.DATE_TIME_STRING_PATTERN);
        if (!vaildDatePatter) {
            return Rst.businessFAIL("错误的时间格式");
        }
        //活动是否结束
        Date dateToSend = DateUtil.fromDateTimeString(date);
        if (DateUtil.now().before(dateToSend)) {
            return Rst.businessFAIL("活动还未结束，不能补发奖励");
        }
        //获取区服配置
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        //获取冲榜枚举
        ActivityRankEnum activityRankEnum = ActivityRankEnum.fromValue(rankType);
        //是否有对应榜单
        if (null == activityRankEnum) {
            return Rst.businessFAIL("没有对应的榜单");
        }
        Rst rst = Rst.businessOK();
        for (CfgServerEntity server : servers) {
            //获取指定结束时间冲榜活动
            List<ServerActivityRank> sars = this.serverService.getServerDatas(server.getMergeSid(), ServerActivityRank.class);
            List<ServerActivityRank> activityRanks = sars.stream().filter(sar -> 0 == DateUtil.getSecondsBetween(sar.getEnd(), dateToSend))
                    .collect(Collectors.toList());
            //是否有冲榜枚举
            if (ListUtil.isEmpty(activityRanks)) {
                rst.put(server.getName(), "该时间没有该榜单");
                continue;
            }
            //是否有榜单
            ServerActivityRank activityRank = activityRanks.stream().filter(tmp -> tmp.getType() == rankType).findFirst().orElse(null);
            if (null == activityRank) {
                rst.put(server.getName(), "该时间没有该榜单");
                continue;
            }
            //是否有上榜人员
            List<ZSetOperations.TypedTuple<Long>> rankers = activityRankService.getRankers(activityRank, 1, 1);
            if (ListUtil.isEmpty(rankers)) {
                rst.put(server.getName(), "该榜单没有人参与");
                continue;
            }
            //榜单是否已经下发奖励
            long firstUid = rankers.get(0).getValue();
            List<RankAwardRecord> rankRecords = serverDataService.getServerDatas(server.getMergeSid(), RankAwardRecord.class);
            long hasSend = rankRecords.stream().filter(tmp -> tmp.getUid() == firstUid && tmp.getSendTime().after(activityRank.getEnd()) && tmp.getActivityName().equals(activityRankEnum.getName())).count();
            if (hasSend > 0) {
                rst.put(server.getName(), "该榜单已下发");
                continue;
            }
            //补发奖励
            activityRankService.sendRankerAwardsByType(activityRank);
            rst.put(server.getName(), "补发成功");
        }
        return rst;
    }

    /**
     * 修复区服王者之路榜单
     *
     * @param uids 玩家ID，多个玩家时（用逗号隔开）：uid,uid
     * @return
     */
    @GetMapping("server!repairServerAttackRank")
    public Rst repairServerAttackRank(String uids) {
        if (StrUtil.isNull(uids)){
            return Rst.businessFAIL("参数不能为空");
        }
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            GameUser gameUser = gameUserService.getGameUser(uid);
            if (gameUser == null) {
                return Rst.businessFAIL("玩家" + uid + "不存在");
            }
        }
        Rst rst = Rst.businessOK();
        Map<Integer, List<Long>> uidMapWithSid = uidList.stream().collect(Collectors.groupingBy(tmp -> gameUserService.getActiveSid(tmp)));
        for (Map.Entry<Integer, List<Long>> entry : uidMapWithSid.entrySet()) {
            int sid = entry.getKey();
            List<Long> serverUids = entry.getValue();
            List<ServerActivityRank> activityRanks = getServerActivityRanks(sid, DateUtil.now());
            if (ListUtil.isEmpty(activityRanks)) {
                rst.put(""+sid,"当前没有王者之路榜单");
                continue;
            }
            ServerActivityRank activityRank = activityRanks.stream().filter(tmp -> tmp.getType() == ActivityRankEnum.ATTACK_RANK.getValue()).findFirst().orElse(null);
            if (null == activityRank) {
                rst.put(""+sid,"当前没有王者之路榜单");
                continue;
            }

            // 修复玩家王者之路榜单数据
            for (Long uid : serverUids) {
                // 玩家实际积分
                int realPoint = getAttackPoints(uid);
                // 玩家榜单积分
                int rankScore = activityRankService.getScore(uid, activityRank);
                // 积分是否异常
                if (realPoint != rankScore) {
                    // 设置玩家王者之路榜单数据
                    activityRankService.setRankValue(uid, sid, realPoint, ActivityRankEnum.ATTACK_RANK);
                }
            }

        }

        return Rst.businessOK();
    }

    /**
     * 移除过期的榜单数据
     *
     * @param serverNames
     * @param beforeDateTime
     * @return
     */
    @GetMapping("server!removeExpiredActivityRanks")
    public Rst removeExpiredActivityRanks(String serverNames, String beforeDateTime) {
        Date before = DateUtil.fromDateTimeString(beforeDateTime);
        if (DateUtil.getDaysBetween(before, DateUtil.now()) < 60) {
            return Rst.businessFAIL("不能删除60天内的榜单");
        }
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            for (ActivityRankEnum activityRank : ActivityRankEnum.values()) {
                IActivityRank ar = this.activityRankService.getActivityRank(sId, activityRank);
                if (ar == null) {
                    continue;
                }
                if (ar.gainEnd().before(before)) {
                    this.activityRankService.removeRank(ar);
                }
            }
        }
        return Rst.businessOK();
    }

    /**
     * 处理玩家王者之路积分
     *
     * @param uid 玩家ID
     * @return
     */
    private int getAttackPoints(long uid) {
        // 获取玩家已占领城市
        List<UserCity> ownCities = userCityService.getUserOwnCities(uid);
        // 计算积分，city.getHierarchy()-等阶（城市每次升阶都获得积分） 默认0， +1 代表已占领后的积分，
        return ownCities.stream().mapToInt(city -> (city.getHierarchy() + 1) * attackRankService.getPoint(city.gainCity())).sum();
    }
}