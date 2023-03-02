package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activity.server.ServerActivityGeneratorService;
import com.bbw.god.activity.server.ServerActivityService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动相关管理服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:30
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMActivityServerCtrl extends AbstractController {
    @Autowired
    private ServerActivityGeneratorService serverActivityGeneratorService;
    @Autowired
    private ServerActivityService serverActivityService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ActivityService activityService;

    @Value("${game-data-result-days:30}")
    private int prepareDays;// 提前生成多少天的结果数据

    @GetMapping("server!showServerActivities")
    public Rst showServerActivityies(String serverNames, String sinceDate) {
        Rst rst = Rst.businessOK();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date sinceDateObj = DateUtil.fromDateTimeString(sinceDate);
        for (CfgServerEntity server : servers) {
            List<ServerActivity> sas = serverDataService.getServerDatas(server.getMergeSid(), ServerActivity.class);
            sas.sort(Comparator.comparing(ServerActivity::getType).thenComparing((o1, o2) -> o1.gainBegin().compareTo(o1.gainBegin())));
            List<String> strs =
                    sas.stream().filter(tmp -> tmp.gainBegin().after(sinceDateObj)).map(tmp -> tmp.toDesString()).collect(Collectors.toList());
            rst.put(server.getName(), strs);
        }

        return rst;
    }

    /**
     * 从指定天开始生产活动
     *
     * @param serverName
     * @param beginDate
     * @param days
     * @return
     */
    @GetMapping("server!reappendServerActivity")
    public Rst reappendActivity(String serverName, String beginDate, int days) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        this.serverActivityGeneratorService.reappendActivities(server, DateUtil.fromDateTimeString(beginDate), days);
        return Rst.businessOK();
    }

    @GetMapping("server!updateServerActivityTime")
    public Rst updateServerActivityTime(String serverNames, int type, String beginDate, String endDate) {

        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        List<ServerActivity> sasToUpdate = new ArrayList<>();
        ActivityEnum activityType = ActivityEnum.fromValue(type);
        CfgActivityEntity ca = ActivityTool.getActivityByType(activityType);
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            // 获取当前生效的榜单
            ServerActivity sa = this.serverActivityService.getSa(sId, ca);
            if (sa != null) {
                // 更新开始时间
                if (StrUtil.isNotBlank(beginDate)) {
                    sa.setBegin(DateUtil.fromDateTimeString(beginDate));
                }
                // 更新结束时间
                if (StrUtil.isNotBlank(endDate)) {
                    sa.setEnd(DateUtil.fromDateTimeString(endDate));
                }
                sasToUpdate.add(sa);
            }
            log.info("{}{}时间更新完成", LogUtil.getLogServerPart(server), activityType);
        }
        serverActivityService.updateServerActivities(sasToUpdate);
        return Rst.businessOK();
    }

    /**
     * 修复七天活动时间
     *
     * @param serverNames
     * @param type
     * @param baseBeginDate
     * @param sinceDate
     * @return
     */
    @GetMapping("server!fixSevenDaysActivityTime")
    public Rst fixSevenDaysActivityTime(String serverNames, int type, String baseBeginDate, String sinceDate) {
        ActivityEnum activityType = ActivityEnum.fromValue(type);
        List<ActivityEnum> sevenDaysActivities = Arrays.asList(ActivityEnum.ACC_R_DAYS_7);
        if (!sevenDaysActivities.contains(activityType)) {
            throw new ExceptionForClientTip("不是七日循环活动");
        }
        Rst rst = Rst.businessOK();
        Date begin = DateUtil.fromDateTimeString(baseBeginDate);
        Date since = DateUtil.fromDateTimeString(sinceDate);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        List<ServerActivity> sasToUpdate = new ArrayList<>();
        for (CfgServerEntity server : servers) {
            int sId = server.getMergeSid();
            List<ServerActivity> sas = serverDataService.getServerDatas(sId, ServerActivity.class);            // 获取进行中或者未来的活动
            sas = sas.stream()
                    .filter(tmp -> tmp.getType() == activityType.getValue() && tmp.getEnd().after(since))
                    .collect(Collectors.toList());

            for (int i = 0; i < sas.size(); i++) {

                Date dateBegin = DateUtil.addWeeks(begin, i);
                // 第七天
                Date sevenEndDate = DateUtil.addHours(dateBegin, 24 * 6);
                Date dateEnd = DateUtil.getDateEnd(sevenEndDate);
                ServerActivity sa = sas.get(i);
                sa.setBegin(dateBegin);
                sa.setEnd(dateEnd);
            }
            sasToUpdate.addAll(sas);
            rst.put(server.getName(), "需修正的" + activityType.getName() + "活动数为：" + sas.size());
        }
        serverActivityService.updateServerActivities(sasToUpdate);
        return rst;
    }

    @GetMapping("server!updateServerActivityParentType")
    public Rst updateServerActivityParentType(String serverNames, int activityType, int parentType) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(activityType));
        if (ListUtil.isEmpty(cas)) {
            return Rst.businessFAIL("该活动未配置");
        }
        List<ServerActivity> sasToUpdate = new ArrayList<>();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            Integer sid = server.getId();
            List<ServerActivity> sas = serverDataService.getServerDatas(sid, ServerActivity.class);
            sas = sas.stream()
                    .filter(sa -> sa.getType() == activityType).collect(Collectors.toList());
            sas.forEach(sa -> sa.setParentType(parentType));
            sasToUpdate.addAll(sas);
        }
        serverActivityService.updateServerActivities(sasToUpdate);
        return Rst.businessOK();
    }

    @GetMapping("server!delServerActivities")
    public Rst delServerActivities(String serverNames, int activityType, String sinceDate) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(activityType));
        if (ListUtil.isEmpty(cas)) {
            return Rst.businessFAIL("该活动未配置");
        }
        List<ServerActivity> sasToDel = new ArrayList<>();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date date = DateUtil.fromDateTimeString(sinceDate);
        for (CfgServerEntity server : servers) {
            Integer sid = server.getId();
            List<ServerActivity> sas = serverDataService.getServerDatas(sid, ServerActivity.class);
            sas = sas.stream()
                    .filter(sa -> sa.getType() == activityType && sa.getBegin().after(date))
                    .collect(Collectors.toList());
            sasToDel.addAll(sas);
        }
        serverActivityService.delServerActivities(sasToDel);
        return Rst.businessOK();
    }

    /**
     * 创建活动实例
     *
     * @param serverNames
     * @param types
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("server!addServerActivity")
    public Rst addServerActivity(String serverNames, String types, String begin, String end) {
        List<Integer> typeInts = ListUtil.parseStrToInts(types);
        typeInts.stream().forEach(typeInt -> {
            ActivityEnum activityEnum = ActivityEnum.fromValue(typeInt);
            if (activityEnum == null) {
                throw ExceptionForClientTip.fromMsg("不存在活动type:" + typeInt);
            }
            List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
            if (ListUtil.isEmpty(cas)) {
                throw ExceptionForClientTip.fromMsg("该活动未配置");
            }
            CfgActivityEntity ca = cas.get(0);
            if (ca.getScope() != ActivityScopeEnum.SERVER.getValue()) {
                throw ExceptionForClientTip.fromMsg(typeInt + "不是区服活动");
            }

        });
        Date now = DateUtil.now();
        Date beginDate = DateUtil.fromDateTimeString(begin);
        Date endDate = DateUtil.fromDateTimeString(end);
        if (beginDate.after(endDate)) {
            return Rst.businessFAIL("活动开始时间需早于结束时间");
        }
        if (now.after(beginDate) || now.after(endDate)) {
            return Rst.businessFAIL("活动时间必需晚于当前时间");
        }
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        List<ActivityEnum> typeEnums = typeInts.stream().map(typeInt -> ActivityEnum.fromValue(typeInt)).collect(Collectors.toList());
        List<CfgActivityEntity> cas = typeEnums.stream().map(typeEnum -> ActivityTool.getActivitiesByType(typeEnum).get(0)).collect(Collectors.toList());
        // 如果已存在生效中的实例，则删除
        List<ServerActivity> sasToDel = new ArrayList<>();
        for (CfgActivityEntity ca : cas) {
            for (CfgServerEntity server : servers) {
                int sId = server.getMergeSid();
                List<ServerActivity> sas = serverDataService.getServerDatas(sId, ServerActivity.class);
                List<ServerActivity> toDels = sas.stream()
                        .filter(tmp -> tmp.getType().intValue() == ca.getType() && tmp.getEnd().after(beginDate))
                        .collect(Collectors.toList());
                sasToDel.addAll(toDels);
            }
        }
        serverActivityService.delServerActivities(sasToDel);

        //添加新实例
        List<ServerActivity> sasToAdd = new ArrayList<>();
        for (CfgActivityEntity ca : cas) {
            for (CfgServerEntity server : servers) {
                int sId = server.getMergeSid();
                // 创建区服活动
                if (GMActivityCtrl.dayActivityIds.contains(ca.getType())) {
                    int days = DateUtil.getDaysBetween(beginDate, endDate);
                    for (int i = 0; i <= days; i++) {
                        ServerActivity serverActivity = null;
                        if (i == 0) {
                            serverActivity = ServerActivity.fromActivity(ca, beginDate,
                                    DateUtil.getDateEnd(beginDate), sId);
                        } else {
                            Date b = DateUtil.addDays(beginDate, i);
                            b = DateUtil.getDateBegin(b);
                            Date e = DateUtil.getDateEnd(b);
                            serverActivity = ServerActivity.fromActivity(ca, b, e, sId);
                        }
                        sasToAdd.add(serverActivity);
                    }

                } else {
                    ServerActivity serverActivity = ServerActivity.fromActivity(ca, beginDate, endDate, sId);
                    sasToAdd.add(serverActivity);
                }

            }
            log.info("{}初始化完成{}~{}", ca.getName(), begin, end);
        }
        serverActivityService.addServerActivities(sasToAdd);

        return Rst.businessOK();
    }
    
    
    @PostMapping("server!loginAwardActivity")
    public Rst loginActivity(String uidStr){
        String[] split = uidStr.split(";");
        for (String s : split) {
            Long guId = Long.valueOf(s);
            this.activityService.handleUaProgress(guId, gameUserService.getActiveSid(guId), 1, ActivityEnum.LOGIN_AWARD);
        }
        return Rst.businessOK();
    }
}
