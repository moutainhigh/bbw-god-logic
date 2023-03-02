package com.bbw.god.gm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.*;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activity.server.ServerActivityGeneratorService;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRankGeneratorService;
import com.bbw.god.activityrank.server.ServerActivityRankService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.*;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.pool.ServerDataDAO;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.unique.UserZxz;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.flx.FlxCaiShuZiBet;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import com.bbw.god.server.fst.FstLogic;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.redis.ServerRedisUtil;
import com.bbw.god.server.special.ServerSpecialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 合服相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMMergeCtrl extends AbstractController {
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ServerRedisUtil serverRedisUtil;
    @Autowired
    private ServerSpecialService serverSpecialService;
    @Autowired
    private ServerActivityRankGeneratorService activityRankGeneratorService;
    @Autowired
    private ServerActivityGeneratorService serverActivityGeneratorService;
    @Autowired
    private ServerActivityRankService serverActivityRankService;
    @Autowired
    private FstLogic fstLogic;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private FstServerService fstServerService;

    /**
     * 更新封神台、富临轩、行会、玩家行会任务、诛仙阵等
     *
     * @param serverNames
     * @return
     */
    @GetMapping("server!beforeMerge")
    public Rst beforeMerge(String serverNames) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            // updateCommonServerData(server, ServerDataType.ACTIVITY);
            // updateCommonServerData(server, ServerDataType.ACTIVITY_RANK);
            // updateFlxData(server, ServerDataType.FLXCAISHUZI);
            makeUpAsFlx(server);
            updateFst(server);
            updateServerGuildInfo(server);
            updateZxz(server);
            updateUserGuildInfo(server);
        }
        return Rst.businessOK();
    }

    /**
     * 设置目标服数据
     *
     * @param targetServer
     * @param mergeDateTime
     * @return
     */
    @GetMapping("server!setTargetServerAfterDBMerge")
    public Rst afterDBMerge(String targetServer, String mergeDateTime) {
        CfgServerEntity server = ServerTool.getServer(targetServer);
        int sId = server.getMergeSid();

        log.info("移除区服加载状态...");
        serverRedisUtil.deleteLoadStatus(sId);

        //        log.info("重新初始化特产...");
        //        serverSpecialService.initSpecialPriceForNewServer(server);
        Date mergeDate = DateUtil.fromDateTimeString(mergeDateTime);

        log.info("追加榜单...");
        activityRankGeneratorService.reappendActivityRanks(server, mergeDate, 5);

        log.info("追加活动...");
        serverActivityGeneratorService.reappendActivities(server, mergeDate, 35);

        log.info("充值首冲翻倍...");
        CfgActivityEntity ca = ActivityTool.getActivitiesByType(ActivityEnum.RESET_FIRST_DOUBLE_R).get(0);
        ServerActivity serverActivity = ServerActivity.fromActivity(ca, mergeDate, DateUtil.fromDateInt(20990101), sId);
        serverService.addServerData(sId, serverActivity);

        log.info("修正本周榜单初始时间、充值榜奖励,并保证榜单排行为空...");
        Date dateAfterMergeDate = DateUtil.addMinutes(mergeDate, 5);
        List<ServerActivityRank> sars = serverActivityRankService.getServerActivityRanks(sId, dateAfterMergeDate);
        sars.forEach(tmp -> {
            tmp.setBegin(mergeDate);
            if (tmp.getType() == ActivityRankEnum.RECHARGE_RANK.getValue()) {
                Award award = activityRankGeneratorService.getExtraAwardCard(2);
                tmp.setExtraAward(JSONUtil.toJson(Arrays.asList(award)));
            }
            // 检查榜单，保证榜单排行为空
            activityRankService.removeRank(tmp);
        });
        serverService.updateServerData(sars);

        log.info("封神台榜单清空...");
        fstServerService.removeRanks(sId);
        fstLogic.initServerFst(sId);

        log.info("修复昵称冲突...");
        updateConflictNickname(server);

        log.info("修复行会名称冲突...");
        updateConflictGuildName(server);

//        log.info("迁移累充数据");
//        IActivity activity = activityService.getActivity(server.getMergeSid(), ActivityEnum.ACC_R);
//        List<Long> rechargeUids = insRoleInfoService.getRechargeUids(server.getMergeSid());
//        if (ListUtil.isNotEmpty(rechargeUids)) {
//            log.info("充值玩家数：" + rechargeUids.size());
//            for (long rechargeUid : rechargeUids) {
//                List<UserActivity> uas = gameUserService.getMultiItems(rechargeUid, UserActivity.class);
//                List<UserActivity> rechargeActivities = uas.stream().filter(tmp -> {
//                    boolean isValid = tmp != null && tmp.getBaseId() != null;
//                    if (isValid) {
//                        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivity(tmp.getBaseId());
//                        isValid = cfgActivityEntity != null;
//                        if (isValid) {
//                            isValid = cfgActivityEntity.getType() == ActivityEnum.ACC_R.getValue();
//                        }
//                    }
//                    return isValid;
//                }).collect(Collectors.toList());
//                rechargeActivities.forEach(tmp -> tmp.setAId(activity.gainId()));
//                gameUserService.updateItems(rechargeActivities);
//            }
//        }
        log.info("目标服数据设置成功!!!");
        return Rst.businessOK();
    }

    private void makeUpAsFlx(CfgServerEntity server) {
        String loopKey = String.valueOf(DateUtil.getTodayInt());
        // 押押乐补还元宝
        List<FlxYaYaLeBet> yaYaLeBets = serverDataService.getServerDatas(server.getId(), FlxYaYaLeBet.class, loopKey);
        Map<Long, Long> uidCountMap = yaYaLeBets.stream().collect(Collectors.groupingBy(FlxYaYaLeBet::getUid, Collectors.counting()));
        Set<Long> yylUids = uidCountMap.keySet();
        if (SetUtil.isNotEmpty(yylUids)) {
            PlayerDataDAO dao = SpringContextUtil.getBean(PlayerDataDAO.class, server.getId());
            List<InsUserEntity> needToUpdates = new ArrayList<>();
            for (Long uid : yylUids) {
                InsUserEntity insUserEntity = dao.dbSelectInsUserEntity(uid);
                int addGold = uidCountMap.get(uid).intValue() * 10;
                log.info("{}押押乐返回元宝：{}", insUserEntity.getNickname(), addGold);
                insUserEntity.setGold(insUserEntity.getGold() + addGold);
                JSONObject data = JSON.parseObject(insUserEntity.getDataJson());
                data.put("gold", data.getIntValue("gold") + addGold);
                insUserEntity.setDataJson(data.toJSONString());
                needToUpdates.add(insUserEntity);
            }
            dao.dbUpdateUserBatch(needToUpdates);
        }
        // 猜数字补还
        List<FlxCaiShuZiBet> caiShuZiBets = serverDataService.getServerDatas(server.getId(), FlxCaiShuZiBet.class, loopKey);
        Map<Long, List<FlxCaiShuZiBet>> uidResMap = caiShuZiBets.stream().collect(Collectors.groupingBy(FlxCaiShuZiBet::getUid));
        Set<Long> cszUids = uidResMap.keySet();
        if (SetUtil.isNotEmpty(cszUids)) {
            PlayerDataDAO dao = SpringContextUtil.getBean(PlayerDataDAO.class, server.getId());
            List<InsUserEntity> needToUpdates = new ArrayList<>();
            for (Long uid : cszUids) {
                InsUserEntity insUserEntity = dao.dbSelectInsUserEntity(uid);
                List<FlxCaiShuZiBet> userBets = uidResMap.get(uid);
                int addGold = userBets.stream().collect(Collectors.summingInt(FlxCaiShuZiBet::getBetGold));
                int addCopper = userBets.stream().collect(Collectors.summingInt(FlxCaiShuZiBet::getBetCopper));
                log.info("{}猜数字返回元宝：{},返回铜钱：{}", insUserEntity.getNickname(), addGold, addCopper);
                insUserEntity.setGold(insUserEntity.getGold() + addGold);
                insUserEntity.setCopper(insUserEntity.getCopper() + addCopper);

                JSONObject data = JSON.parseObject(insUserEntity.getDataJson());
                data.put("gold", data.getIntValue("gold") + addGold);
                data.put("copper", data.getIntValue("copper") + addCopper);
                insUserEntity.setDataJson(data.toJSONString());

                needToUpdates.add(insUserEntity);
            }
            dao.dbUpdateUserBatch(needToUpdates);
        }

        log.info(server.getName() + "富临轩补还成功");
    }

    private void updateFst(CfgServerEntity server) {
        ServerDataDAO serverDataDAO = SpringContextUtil.getBean(ServerDataDAO.class, server.getId());
        List<InsServerDataEntity> datas = serverDataDAO.dbSelectServerDataByType(getSid(server), ServerDataType.FSTPVPRanking.getRedisKey());
        if (ListUtil.isNotEmpty(datas)) {
            // 修正区服ID
            datas.forEach(tmp -> {
                tmp.setSid(server.getMergeSid());
                JSONObject js = JSON.parseObject(tmp.getDataJson());
                js.put("sid", server.getMergeSid());
                js.put("challengeNum", 5);
                js.put("ranking", 0);
                js.put("winTimes", 0);
                js.put("winStreak", 0);
                js.put("challengeTotalTimes", 0);
                js.put("videoLogs", new JSONArray());
                js.put("todayFightTimes", 0);
                tmp.setDataJson(js.toString());
            });
            // 更新数据
            serverDataDAO.dbUpdateServerDataBatch(datas);
        }
        log.info(server.getName() + ServerDataType.FSTPVPRanking.getRedisKey() + "更新成功");
    }

    private void updateServerGuildInfo(CfgServerEntity server) {
        ServerDataDAO serverDataDAO = SpringContextUtil.getBean(ServerDataDAO.class, server.getId());
        List<InsServerDataEntity> datas = serverDataDAO.dbSelectServerDataByType(getSid(server), ServerDataType.Guild_Info.getRedisKey());
        if (ListUtil.isNotEmpty(datas)) {
            // 修正区服ID
            datas.forEach(tmp -> {
                tmp.setSid(server.getMergeSid());
                JSONObject js = JSON.parseObject(tmp.getDataJson());
                js.put("sid", server.getMergeSid());
                js.put("eightDiagrams", Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
                tmp.setDataJson(js.toString());
            });
            // 更新数据
            serverDataDAO.dbUpdateServerDataBatch(datas);
        }
        log.info(server.getName() + ServerDataType.Guild_Info.getRedisKey() + "更新成功");
    }

    private void updateZxz(CfgServerEntity server) {
        PlayerDataDAO dao = SpringContextUtil.getBean(PlayerDataDAO.class, server.getId());
        List<InsUserDataEntity> userZxzs = dao.dbSelectUserDataByType(UserDataType.ZXZ.getRedisKey());
        if (ListUtil.isNotEmpty(userZxzs)) {
            // 修正区服ID
            userZxzs.forEach(tmp -> {
                JSONObject js = JSON.parseObject(tmp.getDataJson());
                js.put("guardians", UserZxz.DEFAULT_GUARDIANS);
                js.put("status", 0);
                tmp.setDataJson(js.toString());
            });
            // 更新数据
            dao.dbUpdateUserDataBatch(userZxzs);
        }
        log.info(server.getName() + UserDataType.ZXZ.getRedisKey() + "更新成功");
    }

    private void updateUserGuildInfo(CfgServerEntity server) {
        PlayerDataDAO dao = SpringContextUtil.getBean(PlayerDataDAO.class, server.getId());
        List<InsUserDataEntity> userUserGuilds = dao.dbSelectUserDataByType(UserDataType.Guild_User_Info.getRedisKey());
        if (ListUtil.isNotEmpty(userUserGuilds)) {
            // 修正区服ID
            userUserGuilds.forEach(tmp -> {
                JSONObject js = JSON.parseObject(tmp.getDataJson());
                js.put("taskInfo", null);
                tmp.setDataJson(js.toString());
            });
            // 更新数据
            dao.dbUpdateUserDataBatch(userUserGuilds);
        }
        log.info(server.getName() + UserDataType.Guild_User_Info.getRedisKey() + "更新成功");
    }

    /**
     * 处理昵称冲突的玩家
     */
    private void updateConflictNickname(CfgServerEntity server) {
        int sid = server.getMergeSid();
        List<InsRoleInfoEntity> roles = insRoleInfoService.getByServer(sid);
        PlayerDataDAO dao = SpringContextUtil.getBean(PlayerDataDAO.class, server.getId());
        List<InsUserEntity> users = dao.dbSelectUsers();
        Map<String, List<InsRoleInfoEntity>> nicknameRolesMap = roles.stream().collect(Collectors.groupingBy(InsRoleInfoEntity::getNickname));
        Set<String> nicknames = nicknameRolesMap.keySet();
        List<InsRoleInfoEntity> rolesToUpdate = new ArrayList<>();
        List<InsUserEntity> usersToUpdate = new ArrayList<>();
        for (String nickname : nicknames) {
            List<InsRoleInfoEntity> roleList = nicknameRolesMap.get(nickname);
            // 有重复
            if (roleList.size() > 1) {
                log.info("{},昵称数：" + roleList.size(), nickname);
                roleList.sort(Comparator.comparing(InsRoleInfoEntity::getLevel).reversed());
                for (int i = 1; i < roleList.size(); i++) {
                    InsRoleInfoEntity role = roleList.get(i);
                    // 新昵称 = 旧昵称 + 邀请码
                    String newNickname = role.getNickname() + "@" + role.getInviCode();
                    role.setNickname(newNickname);
                    rolesToUpdate.add(role);
                    // 修改区服角色
                    InsUserEntity user = users.stream().filter(tmp -> tmp.getUid().longValue() == role.getUid()).findFirst().orElse(null);
                    if (user != null) {
                        user.setNickname(newNickname);
                        JSONObject data = JSON.parseObject(user.getDataJson());
                        data.getJSONObject("roleInfo").put("nickname", newNickname);
                        user.setDataJson(data.toJSONString());
                        usersToUpdate.add(user);
                    }

                }
            }
        }

        if (rolesToUpdate.size() > 0) {
            insRoleInfoService.insertOrUpdateBatch(rolesToUpdate);
        }
        if (usersToUpdate.size() > 0) {
            dao.dbUpdateUserBatch(usersToUpdate);
        }
        log.info("昵称冲突修复成功，修复数量：" + rolesToUpdate.size());
    }

    private void updateConflictGuildName(CfgServerEntity server) {
        List<GuildInfo> guildInfos = serverService.getServerDatas(server.getId(), GuildInfo.class);
        Map<String, List<GuildInfo>> nameGuildMap = guildInfos.stream().collect(Collectors.groupingBy(GuildInfo::getGuildName));
        Set<String> guildNames = nameGuildMap.keySet();
        List<GuildInfo> needToUpdate = new ArrayList<>();
        for (String guildName : guildNames) {
            List<GuildInfo> guildList = nameGuildMap.get(guildName);
            // 有重复
            if (guildList.size() > 1) {
                log.info("{},行会名称数：" + guildList.size(), guildName);
                guildList.sort(Comparator.comparing(GuildInfo::getExp).reversed());
                for (int i = 1; i < guildList.size(); i++) {
                    GuildInfo guild = guildList.get(i);
                    // 新昵称 = 旧昵称 + 随机数
                    String newName = guild.getGuildName() + "@" + PowerRandom.getRandomBetween(1000, 10000);
                    guild.setGuildName(newName);
                    needToUpdate.add(guild);
                }
            }
        }

        if (needToUpdate.size() > 0) {
            serverService.updateServerData(needToUpdate);
        }
        log.info("行会名称冲突修复成功，修复数量：" + needToUpdate.size());
    }

    private int getSid(CfgServerEntity server) {
        int sid = server.getId();
        // switch (sid) {
        // case 81:
        // sid = 2071;
        // break;
        // case 82:
        // sid = 2073;
        // break;
        // case 83:
        // sid = 2074;
        // break;
        // case 84:
        // sid = 2075;
        // break;
        // case 85:
        // sid = 2077;
        // break;
        // }
        return sid;
    }

}
