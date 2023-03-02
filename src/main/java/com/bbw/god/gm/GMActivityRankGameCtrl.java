package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.GameRankAwardRecord;
import com.bbw.god.activityrank.game.GameActivityRank;
import com.bbw.god.activityrank.game.GameActivityRankService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.mail.UserMail;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 冲榜相关管理服务
 *
 * @author: hzf
 * @create: 2022-11-30 14:25
 **/
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMActivityRankGameCtrl extends AbstractController {

    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private GameActivityRankService gameActivityRankService;

    /**
     * 查看跨服冲榜
     *
     * @param serverGroup
     * @return
     */
    @ApiOperation(value = "查看跨服冲榜")
    @GetMapping("game!showGameActivityRanks")
    public Rst showGameActivityRanks(int serverGroup) {
        Rst rst = Rst.businessOK();
        List<GameActivityRank> gars = this.gameDataService.getGameDatas(GameActivityRank.class);
        List<GameActivityRank> gameActivityRanks = gars.stream().filter(tmp -> tmp.getServerGroup() == serverGroup).collect(Collectors.toList());
        List<String> gameActivityRank = gameActivityRanks.stream().map(GameActivityRank::toDesString).collect(Collectors.toList());
        rst.put("" + serverGroup, gameActivityRank);
        return rst;
    }

    /**
     * 添加跨服榜单
     *
     * @param serverGroups
     * @param type
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("server!addGameActivityRank")
    public Rst addGameActivityRank(String serverGroups, int type, String begin, String end) {
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
        if (scope != ActivityScopeEnum.GAME.getValue()) {
            return Rst.businessFAIL("非跨服榜单");
        }
        List<Integer> sgs = ListUtil.parseStrToInts(serverGroups);
//        Date date = DateUtil.addMinutes(beginDate, -10);
        for (Integer sg : sgs) {
            // 如果已存在生效中的实例，则删除
            List<GameActivityRank> gars = this.getGameActivityRanks(sg, type);
            if (ListUtil.isNotEmpty(gars)) {
                List<Long> ids = gars.stream().filter(tmp -> tmp.getType() == type).map(GameActivityRank::getId).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(ids)) {
                    this.gameDataService.deleteGameDatas(ids, GameActivityRank.class);
                }

            }
            // 创建全服活动
            if (ActivityRankEnum.fromValue(type).isDayRank()) {
                int days = DateUtil.getDaysBetween(beginDate, endDate);
                for (int i = 0; i <= days; i++) {
                    GameActivityRank gar = null;
                    if (i == 0) {
                        gar = GameActivityRank.instance(sg, type, beginDate, DateUtil.getDateEnd(beginDate));
                    } else {
                        Date b = DateUtil.addDays(beginDate, i);
                        b = DateUtil.getDateBegin(b);
                        Date e = DateUtil.getDateEnd(b);
                        gar = GameActivityRank.instance(sg, type, b, e);
                    }
                    this.gameDataService.addGameData(gar);
                }
            } else {
                GameActivityRank gar = GameActivityRank.instance(sg, type, beginDate, endDate);
                this.gameDataService.addGameData(gar);
            }
            log.info("区服组{}{}初始化完成{}~{}", sg, car.getName(), begin, end);
        }

        return Rst.businessOK();
    }

    /**
     * 获得某个时间对应的跨服冲榜活动
     *
     * @param serverGroup
     * @param type
     * @return
     */
    public List<GameActivityRank> getGameActivityRanks(int serverGroup, int type) {
        List<GameActivityRank> gars = this.gameDataService.getGameDatas(GameActivityRank.class);
        return gars.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getType() == type)
                .collect(Collectors.toList());
    }

    @GetMapping("game!delRankAwardMail")
    public Rst delRankAwardMail(String serverGroups, String title, String date) {
        List<Integer> sgs = ListUtil.parseStrToInts(serverGroups);
        for (Integer serverGroup : sgs) {
            GameActivityRank gar = this.gameActivityRankService.getGameActivityRank(serverGroup, DateUtil.fromDateTimeString(date), ActivityRankEnum.GOLD_CONSUME_DAY_RANK);
            if (gar == null) {
                continue;
            }
            List<ZSetOperations.TypedTuple<Long>> allRankers = this.activityRankService.getAllRankers(gar);
            for (ZSetOperations.TypedTuple<Long> ranker : allRankers) {
                try {
                    Long uid = ranker.getValue();
                    if (uid == null) {
                        log.error("错误的玩家id{}", uid);
                        continue;
                    }
                    List<UserMail> mails = this.gameUserService.getMultiItems(uid, UserMail.class);
                    mails = mails.stream().filter(m -> m.getTitle().contains(title)).collect(Collectors.toList());
                    mails.forEach(m -> m.setDeleted(true));
                    this.gameUserService.updateItems(mails);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Rst.businessOK();
    }

    @GetMapping("game!getNeedRepairUser")
    public List<String> getNeedRepairUser(String serverGroups, String title, String date) {
        List<String> userInfos = new ArrayList<>();
        List<Integer> sgs = ListUtil.parseStrToInts(serverGroups);
        for (Integer serverGroup : sgs) {
            GameActivityRank gar = this.gameActivityRankService.getGameActivityRank(serverGroup, DateUtil.fromDateTimeString(date), ActivityRankEnum.GOLD_CONSUME_DAY_RANK);
            if (gar == null) {
                continue;
            }
            List<ZSetOperations.TypedTuple<Long>> allRankers = this.activityRankService.getAllRankers(gar);
            for (ZSetOperations.TypedTuple<Long> ranker : allRankers) {
                try {
                    Long uid = ranker.getValue();
                    if (uid == null) {
                        log.error("错误的玩家id{}", uid);
                        continue;
                    }
                    List<UserMail> mails = this.gameUserService.getMultiItems(uid, UserMail.class);
                    mails = mails.stream().filter(m -> m.getTitle().contains(title)).collect(Collectors.toList());
                    int awarded = mails.stream().filter(m -> m.getAccepted() && m.getSendTime()
                            .after(DateUtil.getDateBegin(DateUtil.now()))).collect(Collectors.toList()).size();
                    if (awarded == 0) {
                        UserMail userMail = mails.get(0);
                        userMail.setDeleted(false);
                        this.gameUserService.updateItems(mails);
                    } else if (awarded > 1) {
                        GameUser gu = this.gameUserService.getGameUser(uid);
                        CfgServerEntity cfgServerEntity = gameUserService.getOriServer(uid);
                        String server = cfgServerEntity.getShortName();
                        String userInfo = server + "--" + gu.getRoleInfo().getNickname();
                        userInfos.add(userInfo);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return userInfos;
    }

    @GetMapping("game!resendAwardMail")
    public Rst resendAwardMail(String serverGroups, String date, Integer fromRank) {
        List<Integer> sgs = ListUtil.parseStrToInts(serverGroups);
        List<UserMail> mailList = new ArrayList<>();
        List<GameRankAwardRecord> recordList = new ArrayList<>();
        for (Integer serverGroup : sgs) {
            GameActivityRank gar = this.gameActivityRankService.getGameActivityRank(serverGroup, DateUtil.fromDateTimeString(date), ActivityRankEnum.GOLD_CONSUME_DAY_RANK);
            if (gar == null) {
                continue;
            }
            try {
                int rank = fromRank;
                List<ZSetOperations.TypedTuple<Long>> allRankers = this.activityRankService.getAllRankers(gar);
                if (allRankers.size() <= rank) {
                    continue;
                }
                allRankers = allRankers.subList(rank, allRankers.size());
                for (ZSetOperations.TypedTuple<Long> ranker : allRankers) {
                    rank++;
                    Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
                    // 非玩家
                    if (uid <= 0) {
                        continue;
                    }
                    List<CfgActivityRankEntity> activities = activityRankService.getActivities(ActivityRankEnum.GOLD_CONSUME_DAY_RANK);
                    // 发放奖励
                    for (CfgActivityRankEntity a : activities) {
                        if (a.getMinRank() <= rank && a.getMaxRank() >= rank) {
                            List<Award> awards = activityRankService.getAwards(gar, a);
                            String content = LM.I.getMsgByUid(uid, "mail.activity.rank.content", ActivityRankEnum.GOLD_CONSUME_DAY_RANK.getName(), rank);
                            log.info(uid + content);
                            UserMail mail = UserMail.newAwardMail(a.getName(), content, uid, awards);
                            mailList.add(mail);
                            GameRankAwardRecord record = GameRankAwardRecord.newInstance(uid, ActivityRankEnum.GOLD_CONSUME_DAY_RANK.getName(), rank, mail.getId());
                            double score = activityRankService.getScore(uid, gar);
                            record.setScore(score);
                            recordList.add(record);
                        }
                    }
                }
                this.gameUserService.addItems(mailList);
                // 记录发放记录
                this.gameDataService.addGameDatas(recordList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return Rst.businessOK();
    }

    /**
     * @param
     * @param date
     * @param rankType ActivityRankEnum
     * @param fromRank
     * @return
     */
    @GetMapping("game!sendRankAwardMail")
    public Rst resendAwardMail(int gid, String date, int rankType, Integer fromRank) {
        List<UserMail> mailList = new ArrayList<>();
        List<GameRankAwardRecord> recordList = new ArrayList<>();
        ActivityRankEnum rankEnum = ActivityRankEnum.fromValue(rankType);
        Date d = DateUtil.fromDateTimeString(date);
        GameActivityRank gar = this.gameActivityRankService.getGameActivityRank(gid, d, rankEnum);
        if (gar == null) {
            return Rst.businessFAIL("没有对应的榜单实例！");
        }
        int rank = fromRank;
        List<ZSetOperations.TypedTuple<Long>> allRankers = this.activityRankService.getAllRankers(gar);
        if (allRankers.size() <= rank) {
            return Rst.businessFAIL("结算失败，入榜人数：" + allRankers.size() + "小于结算排名" + fromRank);
        }
        allRankers = allRankers.subList(rank, allRankers.size());
        for (ZSetOperations.TypedTuple<Long> ranker : allRankers) {
            rank++;
            Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
            // 非玩家
            if (uid <= 0) {
                continue;
            }
            List<CfgActivityRankEntity> activities = activityRankService.getActivities(rankEnum);
            // 发放奖励
            for (CfgActivityRankEntity a : activities) {
                if (a.getMinRank() <= rank && a.getMaxRank() >= rank) {
                    List<Award> awards = activityRankService.getAwards(gar, a);
                    String content = LM.I.getMsgByUid(uid, "mail.activity.rank.content", rankEnum.getName(), rank);
                    log.info(uid + content);
                    UserMail mail = UserMail.newAwardMail(a.getName(), content, uid, awards);
                    mailList.add(mail);
                    GameRankAwardRecord record = GameRankAwardRecord.newInstance(uid, rankEnum.getName(), rank, mail.getId());
                    double score = activityRankService.getScore(uid, gar);
                    record.setScore(score);
                    recordList.add(record);
                }
            }
        }
        this.gameUserService.addItems(mailList);
        // 记录发放记录
        this.gameDataService.addGameDatas(recordList);
        return Rst.businessOK();
    }


    /**
     * 删除某条跨服冲榜
     *
     * @param dataIds
     * @return
     */
    @ApiOperation(value = "删除某条跨服冲榜")
    @GetMapping("server!delGameActivityRanks")
    public Rst delGameActivityRanks(String dataIds) {
        Rst rst = Rst.businessOK();
        List<Long> dataIdList = ListUtil.parseStrToLongs(dataIds);
        if (ListUtil.isEmpty(dataIdList)) {
            return rst;
        }
        gameDataService.deleteGameDatas(dataIdList, GameActivityRank.class);
        return rst;
    }
}
