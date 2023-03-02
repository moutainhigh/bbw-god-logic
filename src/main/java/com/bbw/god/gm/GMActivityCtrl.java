package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.game.GameActivityService;
import com.bbw.god.activity.processor.MultipleRebateProcessor;
import com.bbw.god.activity.processor.PerDayAccProcessor;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activity.server.ServerActivityService;
import com.bbw.god.city.UserCityService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerUserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
public class GMActivityCtrl extends AbstractController {
    public static List<Integer> dayActivityIds = Arrays.asList(
            ActivityEnum.HOLIDAY_PER_DAY_ACC_R.getValue(),
            ActivityEnum.HOLIDAY_DAY_ACC_R.getValue(),
            ActivityEnum.HOLIDAY_DAY_ACC_R_1_51.getValue(),
            ActivityEnum.HOLIDAY_DAY_ACC_R_1_52.getValue(),
            ActivityEnum.HOLIDAY_DAY_ACC_R2.getValue(),
            ActivityEnum.HOLIDAY_DAILY_TASK.getValue(),
            ActivityEnum.TODAY_ACC_R.getValue(),
            ActivityEnum.TODAY_ACC_R_2.getValue(),
            ActivityEnum.TODAY_ACC_R_3.getValue(),
            ActivityEnum.HOLIDAY_PER_ACC_R_10.getValue(),
            ActivityEnum.HOLIDAY_PER_ACC_R_10_51.getValue(),
            ActivityEnum.HOLIDAY_PER_ACC_R_10_52.getValue()
    );
    @Autowired
    private GameActivityService gameActivityService;
    @Autowired
    private ServerActivityService serverActivityService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private MultipleRebateProcessor multipleRebateProcessor;
    @Autowired
    private PerDayAccProcessor perDayAccProcessor;

    @Value("${game-data-result-days:30}")
    private int prepareDays;// 提前生成多少天的结果数据

    @GetMapping("server!delOldActivities")
    public Rst delOldActivities(String beforeDate) {
        Rst rst = Rst.businessOK();
        Date beforeDateObj = DateUtil.fromDateTimeString(beforeDate);
        Date now = DateUtil.now();
        if (DateUtil.getDaysBetween(beforeDateObj, now) <= 60) {
            return Rst.businessFAIL("不能删除60天内的活动数据");
        }

        List<GameActivity> gas = gameDataService.getGameDatas(GameActivity.class);
        List<GameActivity> gasToDel = gas.stream().filter(tmp -> tmp.getEnd().before(beforeDateObj)).collect(Collectors.toList());
        gameActivityService.delGameActivities(gasToDel);
        if (ListUtil.isNotEmpty(gasToDel)) {
            List<String> strs = gasToDel.stream().map(tmp -> tmp.toDesString()).collect(Collectors.toList());
            rst.put("全服过期活动实例数:", strs.size());
            rst.put("全服过期活动实例:", strs);
        }

        List<ServerActivity> sasToDel = new ArrayList<>();
        List<CfgServerEntity> servers = ServerTool.getServers(ServerTool.ALL_SERVER);
        for (CfgServerEntity server : servers) {
            int sid = server.getMergeSid();
            List<ServerActivity> sas = serverDataService.getServerDatas(sid, ServerActivity.class);
            sas = sas.stream().filter(tmp -> tmp.getEnd().before(beforeDateObj)).collect(Collectors.toList());
            sasToDel.addAll(sas);
            if (ListUtil.isNotEmpty(sas)) {
                List<String> strs = sas.stream().map(tmp -> tmp.toDesString()).collect(Collectors.toList());
                rst.put(server.getName() + "过期活动实例数:", strs.size());
                rst.put(server.getName() + "过期活动实例:", strs);
            }
        }
        serverActivityService.delServerActivities(sasToDel);
        return rst;
    }

    /**
     * 修复活动进度
     *
     * @param serverName
     * @param nicknames
     * @param type
     * @param addProgress
     * @return
     */
    @GetMapping("server!repairActivityProgress")
    public Rst repairActivityProgress(String serverName, String nicknames, int type, int addProgress) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        if (server == null) {
            return Rst.businessFAIL("无效的区服");
        }
        int sId = server.getMergeSid();
        ActivityEnum aType = ActivityEnum.fromValue(type);
        if (aType == null) {
            return Rst.businessFAIL("无效的活动类型");
        }
        IActivity activity = this.activityService.getActivity(sId, aType);
        if (activity == null) {
            return Rst.businessFAIL("该活动未开启或已过期");
        }
        String[] nicknameArray = nicknames.split(",");
        String noExistNames = "";
        for (String nickname : nicknameArray) {
            Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
            if (!uidOptional.isPresent()) {
                noExistNames += nickname + ",";
                continue;
            }
            long uid = uidOptional.get();
            repairActivityProgressByUid(uid, aType, addProgress);
        }

        Rst rst = Rst.businessOK();
        rst.put("noExistNames", noExistNames);
        return rst;
    }

    /**
     * 批量修复玩家的进度
     *
     * @param uids
     * @param type
     * @param addProgress
     * @return
     */
    @GetMapping("server!repairActivityProgressByUids")
    public Rst repairActivityProgressByUids(String uids, int type, int addProgress) {
        ActivityEnum aType = ActivityEnum.fromValue(type);
        if (aType == null) {
            return Rst.businessFAIL("无效的活动类型");
        }
        String[] uidArray = uids.split(",");
        String failUids = "";
        for (String uid : uidArray) {
            try {
                repairActivityProgressByUid(Long.valueOf(uid), aType, addProgress);
            } catch (Exception e) {
                failUids = failUids + "," + uid;
            }
        }

        Rst rst = Rst.businessOK();
        rst.put("failUids", failUids);
        return rst;
    }

    /**
     * 修复某个玩家的进度
     *
     * @param uid
     * @param aType
     * @param addProgress
     */
    private void repairActivityProgressByUid(long uid, ActivityEnum aType, int addProgress) {
        int sid = gameUserService.getActiveSid(uid);
        if (aType == ActivityEnum.ACC_R) {
            this.activityService.handleUaProgressAsRound(uid, sid, addProgress, aType);
        } else if (aType != ActivityEnum.GongCLD && aType != ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10) {
            this.activityService.handleUaProgress(uid, sid, addProgress, aType);
        }
        if (aType == ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10) {
            IActivity a = this.activityService.getActivity(sid, ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
            if (null == a) {
                return;
            }
            List<UserActivity> userActivities = activityService.getUserActivities(uid, a.gainId(), ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
            UserActivity userActivity = null;
            List<CfgActivityEntity> cfgActivityEntities = ActivityTool.getActivitiesByType(ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
            if (ListUtil.isEmpty(userActivities)) {
                userActivity = UserActivity.fromActivity(uid, a.gainId(), addProgress, cfgActivityEntities.get(userActivities.size()));
                activityService.addUserActivity(uid, userActivity);
            } else {
                userActivity = UserActivity.fromActivity(uid, a.gainId(), userActivities.get(userActivities.size() - 1).getProgress(), cfgActivityEntities.get(userActivities.size()));
                userActivities.add(userActivity);
                activityService.addUserActivity(uid, userActivity);
                userActivities.get(userActivities.size() - 1).setDate(userActivities.get(userActivities.size() - 2).getDate());
                userActivities.get(userActivities.size() - 2).setProgress(addProgress);
                userActivities.get(userActivities.size() - 2).setStatus(AwardStatus.ENABLE_AWARD.getValue());
                gameUserService.updateItems(userActivities);
            }
        }
    }

    /**
     * 修复攻城略地进度
     *
     * @param serverName
     * @param nicknames
     * @return
     */
    @GetMapping("user!repairGCLDProgress")
    public Rst repairGCLDProgress(String serverName, String nicknames) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        if (server == null) {
            return Rst.businessFAIL("无效的区服");
        }
        int sId = server.getMergeSid();
        String[] nicknameArray = nicknames.split(",");
        String noExistNames = "";
        for (String nickname : nicknameArray) {
            try {

            } catch (Exception e) {

            }
            Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sId, nickname);
            if (!uidOptional.isPresent()) {
                noExistNames += nickname + ",";
                continue;
            }
            long uid = uidOptional.get();
            List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.GongCLD);
            IActivity activity = activityService.getActivity(sId, ActivityEnum.GongCLD);
            List<UserActivity> uas = this.activityService.getUserActivities(uid, activity.gainId(),
                    ActivityEnum.GongCLD);
            for (CfgActivityEntity ca : cas) {
                if (ca.getSeries() == null) {
                    continue;
                }
                int cityLevel = ca.getSeries();
                int num = userCityService.getOwnCityNumAsLevel(uid, cityLevel);
                UserActivity uActivity = null;
                if (ListUtil.isNotEmpty(uas)) {
                    uActivity = uas.stream().filter(ua -> ua.getBaseId().equals(ca.getId())).findFirst().orElse(null);
                }
                if (uActivity == null) {
                    uActivity = UserActivity.fromActivity(uid, activity.gainId(), num, ca);
                    activityService.addUserActivity(uid, uActivity);
                } else if (uActivity.getStatus() == AwardStatus.UNAWARD.getValue()) {
                    uActivity.setProgress(num);
                    if (uActivity.getProgress() >= ca.getNeedValue()) {
                        uActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
                        uActivity.setProgress(ca.getNeedValue());
                    }
                    gameUserService.updateItem(uActivity);
                }
            }
        }

        Rst rst = Rst.businessOK();
        rst.put("noExistNames", noExistNames);
        return rst;
    }

    /**
     * 修复全服游戏进度
     * @param sgId
     * @param type
     * @param uids
     * @param toProgress
     * @return
     */
    @GetMapping("user!repairGameActivityToProgress")
    public Rst repairGameActivityToProgress(int sgId,int type,String uids,int toProgress) {
        ActivityEnum aType = ActivityEnum.fromValue(type);
        if (aType == null) {
            return Rst.businessFAIL("无效的活动类型");
        }
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(aType);
        if (ListUtil.isEmpty(cas)){
            return Rst.businessFAIL("没有该活动相关的配置");
        }
        if (cas.get(0).getScope() != ActivityScopeEnum.GAME.getValue()) {
            return Rst.businessFAIL("该接口只支持全局活动");
        }
        List<GameActivity> gas = gameActivityService.getGameActivitiesByServerGroup(sgId);
        GameActivity activity = gas.stream().filter(ga -> ga.getType().intValue() == type && ga.ifTimeValid())
                .findFirst().orElse(null);
        if (activity == null) {
            return Rst.businessFAIL("该活动未开启或已过期");
        }
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            List<UserActivity> uas = activityService.getUserActivities(uid, activity.gainId(), aType);
            for (CfgActivityEntity ca : cas) {
                UserActivity uActivity = null;
                if (ListUtil.isNotEmpty(uas)) {
                    uActivity = uas.stream().filter(ua -> ua.getBaseId().equals(ca.getId())).findFirst().orElse(null);
                }
                if (uActivity == null) {
                    uActivity = UserActivity.fromActivity(uid, activity.gainId(), toProgress, ca);
                    activityService.addUserActivity(uid, uActivity);
                } else if (uActivity.getStatus() == AwardStatus.UNAWARD.getValue()) {
                    uActivity.setProgress(toProgress);
                    if (uActivity.getProgress() >= ca.getNeedValue()) {
                        uActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
                        uActivity.setProgress(ca.getNeedValue());
                    }
                    gameUserService.updateItem(uActivity);
                }
            }
        }

        Rst rst = Rst.businessOK();
        return rst;
    }


    /**
     * 迁移累充数据
     *
     * @param serverName
     * @return
     */
    @GetMapping("server!transferAccRechargeRecord")
    public Rst transferAccRechargeRecord(String serverName) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        IActivity activity = this.activityService.getActivity(server.getMergeSid(), ActivityEnum.ACC_R);
        List<Long> rechargeUids = this.insRoleInfoService.getRechargeUids(server.getMergeSid());
        if (ListUtil.isNotEmpty(rechargeUids)) {
            log.info("充值玩家数：{}",rechargeUids.size());
            for (long rechargeUid : rechargeUids) {
                List<UserActivity> uas = this.gameUserService.getMultiItems(rechargeUid, UserActivity.class);
                List<UserActivity> rechargeActivities =
                        uas.stream().filter(tmp -> ActivityTool.getActivity(tmp.getBaseId()).getType() == ActivityEnum.ACC_R.getValue()).collect(Collectors.toList());
                rechargeActivities.forEach(tmp -> tmp.setAId(activity.gainId()));
                this.gameUserService.updateItems(rechargeActivities);
            }
        }
        return Rst.businessOK();
    }

    @GetMapping("server!repairMultipleRebate")
    public Rst repairMultipleRebate(String serverName, String nicknames) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        IActivity a = this.activityService.getActivity(server.getMergeSid(), ActivityEnum.MULTIPLE_REBATE);
        if (null == a) {
            return Rst.businessFAIL("活动不存在！");
        }
        String[] nicknameArray = nicknames.split(",");
        StringBuilder noExistNames = new StringBuilder();
        int sid = server.getMergeSid();
        for (String nickname : nicknameArray) {
            Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sid, nickname);
            if (!uidOptional.isPresent()) {
                noExistNames.append(nickname).append(",");
                continue;
            }
            long uid = uidOptional.get();
            multipleRebateProcessor.repaireProgress(uid);
        }
        Rst rst = Rst.businessOK();
        rst.put("noExistNames", noExistNames.toString());
        return rst;
    }
    /**
     * 根据dataId 关闭区服活动
     * @param sid
     * @param dataIds
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("server!closeActivity")
    public Rst closeGameActivity(int sid,String dataIds,String beginTime,String endTime){
        Date beginDate = DateUtil.fromDateTimeString(beginTime);
        Date endDate = DateUtil.fromDateTimeString(endTime);
        if (beginDate.after(endDate)) {
            return Rst.businessFAIL("活动开始时间需早于结束时间");
        }
        //dataId 集合
        List<Long> dataIdList = ListUtil.parseStrToLongs(dataIds);
        List<ServerActivity> serverActivities = new ArrayList<>();
        for (Long dataId : dataIdList) {
            ServerActivity serverActivity = serverDataService.getServerData(sid, ServerActivity.class, dataId);
            if (null == serverActivity) {
                continue;
            }
            serverActivity.setBegin(beginDate);
            serverActivity.setEnd(endDate);
            serverActivities.add(serverActivity);
        }
        serverDataService.updateServerData(serverActivities);
        return Rst.businessOK();
    }
    @PostMapping("server!repairLeiChong")
    public Rst repairLeiChong(@RequestBody List<Info> list){
        ActivityEnum aType = ActivityEnum.HOLIDAY_PER_DAY_ACC_R;
        Rst rst = Rst.businessOK();
        for (Info info : list) {
            try {
                IActivity activity = this.activityService.getActivity(info.getSid(), aType);
                if (activity == null) {
                    rst.put("失败："+JSONUtil.toJson(info),"该活动未开启或已过期");
                    continue;
                }
                this.activityService.handleUaProgress(info.getUid(), info.getSid(), info.getPrice()*10, aType);
            }catch (Exception e){
                e.printStackTrace();
                rst.put("失败："+JSONUtil.toJson(info),"失败");
            }
        }
        return rst;
    }

    @Data
    public static class Info{
        private Long uid;
        private int sid;
        private int price;
    }
}
