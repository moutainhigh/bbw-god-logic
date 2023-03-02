package com.bbw.god.activityrank;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityConfig;
import com.bbw.god.activityrank.RDActivityRankList.RDActivityRank;
import com.bbw.god.activityrank.RDActivityRankerAwardList.RDActivityRanker;
import com.bbw.god.activityrank.RDActivityRankerAwardList.RDActivityRankerAward;
import com.bbw.god.activityrank.game.GameActivityRank;
import com.bbw.god.activityrank.game.GameActivityRankService;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.fst.game.FstRankingType;
import com.bbw.god.server.fst.robot.FstGameRobot;
import com.bbw.god.server.fst.robot.FstRobotService;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 冲榜逻辑
 *
 * @author suhq
 * @date 2019年3月7日 下午5:04:41
 */
@Service
@Slf4j
public class ActivityRankLogic {
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private ActivityConfig activityConfig;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private FstRobotService fstRobotService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private GameActivityRankService gameActivityRankService;

    private static final Integer RANK_LIMIT = 200;
    private static final Integer DAY_RANK_LIMIT = 100;

    /**
     * 获取冲榜活动
     *
     * @param guId
     * @return
     */
    public RDActivityRankList getRankActivities(long guId) {
        // List<Integer> excludes =
        // Arrays.asList(ActivityRankEnum.XIAN_YUAN_DAY_RANK.getValue());
        RDActivityRankList rd = new RDActivityRankList();
        Date now = DateUtil.now();
        int sId = this.gameUserService.getActiveSid(guId);
        List<RDActivityRank> rankActivities = new ArrayList<>();
        List<IActivityRank> ars = getActivityRanks(sId, now);
        if (ListUtil.isNotEmpty(ars)) {
            rankActivities = ars.stream().filter(tmp -> {
                ActivityRankEnum activityRankEnum = ActivityRankEnum.fromValue(tmp.gainType());
                return activityRankEnum != null && !activityRankEnum.getName().contains("每日");
            }).map(tmp -> {
                RDActivityRank rdAR = new RDActivityRank();
                rdAR.setType(tmp.gainType());
                rdAR.setRemainTime(tmp.gainEnd().getTime() - System.currentTimeMillis());
                rdAR.setActivityTime(this.getActivityTime(tmp));
                int myRank = this.activityRankService.getRank(guId, tmp);
                if (ActivityRankEnum.fromValue(tmp.gainType()) == ActivityRankEnum.GOLD_CONSUME_RANK) {
                    myRank = myRank > 200 ? 0 : myRank;
                }
                rdAR.setMyRank(myRank);
                ActivityRankEnum rankEnum = ActivityRankEnum.fromValue(tmp.gainType());
                if (null != rankEnum && rankEnum.isShowAfterEnd()) {
                    rdAR.setIsShowAfterEnd(true);
                }
                return rdAR;
            }).collect(Collectors.toList());
        }
        rd.setRankActivities(rankActivities);
        return rd;
    }

    private List<IActivityRank> getActivityRanks(int sId, Date date) {
        List<IActivityRank> ars = activityRankService.getActivityRanks(sId, date);
        List<GameActivityRank> gas = getYesterdayEndGameActivityRanks(sId);
        for (GameActivityRank ga : gas) {
            if (ars.stream().anyMatch(a -> a.gainId().equals(ga.gainId()))) {
                continue;
            }
            ars.add(ga);
        }
        return ars;
    }

    private IActivityRank getActivityRank(int sId, ActivityRankEnum type) {
        List<IActivityRank> activityRanks = getActivityRanks(sId, DateUtil.now());
        return activityRanks.stream().filter(a -> a.gainType().equals(type.getValue())).findFirst().orElse(null);
    }

    private List<GameActivityRank> getYesterdayEndGameActivityRanks(int sId) {
        Integer groupId = ServerTool.getServerGroup(sId);
        if (groupId == 17) {
            groupId = 16;
        }
        List<GameActivityRank> gas = this.gameActivityRankService.getGameActivityRank();
        Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
        List<GameActivityRank> rd = new ArrayList<>();
        if (ListUtil.isNotEmpty(gas)) {
            for (GameActivityRank tmp : gas) {
                if (tmp.getServerGroup().equals(groupId) && DateUtil.toDateInt(tmp.getEnd()) == DateUtil.toDateInt(yesterday)) {
                    rd.add(tmp);
                }
            }
        }
        return rd;
    }

    /**
     * 获取冲榜奖励
     *
     * @param guId
     * @param rankType
     * @return
     */
    public RDActivityRankerAwardList getRankerAwards(long guId, int rankType, Integer page, Integer limit, Boolean isToday) {
        ActivityRankEnum type = ActivityRankEnum.fromValue(rankType);
        if (type == null) {
            throw new ExceptionForClientTip("activity.not.valid.choose");
        }
        int sId = this.gameUserService.getActiveSid(guId);
        List<CfgActivityRankEntity> cas = this.activityRankService.getActivities(type);
        IActivityRank ar = getActivityRank(sId, type);
        if (ar == null) {
            throw new ExceptionForClientTip("activity.not.actived");
        }
        RDActivityRankerAwardList rd = new RDActivityRankerAwardList();

        List<RDActivityRankerAward> rdRankerAwards = cas.stream()
                .map(tmp -> this.toRDRankerAward(guId, ar, tmp)).collect(Collectors.toList());
        rd.setRankersAwards(rdRankerAwards);
        rd.setActivityType(type.getValue());
        rd.setRemainTime(ar.gainEnd().getTime() - System.currentTimeMillis());
        rd.setActivityTime(this.getActivityTime(ar));
        if (isToday || !type.isDayRank()) {
            this.setRankers(guId, ar, page, limit, rd);
        } else {
            Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
            // 23:59:59的时间，获取不到正确的IActivityRank对象，暂时先这么处理
            yesterday = DateUtil.addMinutes(yesterday, -1);
            IActivityRank yesterdayAr = this.activityRankService.getActivityRank(sId, yesterday, type);
            if (yesterdayAr != null) {
                this.setRankers(guId, yesterdayAr, page, limit, rd);
            }
        }
        return rd;
    }

    /**
     * 获取冲榜排行
     *
     * @param guId
     * @param ar
     * @param page
     * @param limit
     * @param rd
     */
    private void setRankers(long guId, IActivityRank ar, Integer page, Integer limit, RDActivityRankerAwardList rd) {
        if (ar.gainType() == ActivityRankEnum.FST_RANK.getValue()) {
            this.getRankersAsFst(guId, ar, rd);
            return;
        }
        int myRank = 0;// 我的排名
        int myValue = 0;// 我的值
        Integer beforerValue = null;// 前一名的值
        Integer firsterValue = null;// 第一名的值
        GameUser rankGu = null;
        List<RDActivityRanker> rdRankers = new ArrayList<>();
        int start = 0;
        int end = this.activityConfig.getNumRankersToShow();
        if (page != null && limit != null) {
            start = (page - 1) * limit;
            end = page * limit;
        }
        // 全榜人员
        List<TypedTuple<Long>> allRankers = this.activityRankService.getAllRankers(ar);
        if (start > allRankers.size()) {
            throw new ExceptionForClientTip("activity.param.error");
        }
        int size = ActivityRankEnum.fromValue(ar.gainType()).isDayRank() ?
                Math.min(allRankers.size(), DAY_RANK_LIMIT) : Math.min(allRankers.size(), RANK_LIMIT);
        end = Math.min(end, size);
        // 封装指定排名范围内的玩家信息
        List<TypedTuple<Long>> rankers = allRankers.subList(start, end);
        int rank = start;
        for (TypedTuple<Long> ranker : rankers) {
            rank++;
            long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
            Double score = Optional.ofNullable(ranker.getScore()).orElse(0.0);
            String nickname = "虚位以待";
            int head = 1;
            int level = 120;
            int icon = TreasureEnum.HEAD_ICON_Normal.getValue();
            String server = "";
            // 玩家，小于等于为榜单站位格
            if (uid > 0) {
                rankGu = this.gameUserService.getGameUser(uid);
                nickname = rankGu.getRoleInfo().getNickname();
                head = rankGu.getRoleInfo().getHead();
                level = rankGu.getLevel();
                icon = rankGu.getRoleInfo().getHeadIcon();
                CfgServerEntity cfgServerEntity = gameUserService.getOriServer(uid);
                server = cfgServerEntity.getShortName();
            }
            RDActivityRanker rdRanker = new RDActivityRanker(nickname, server, head, level, rank, score.intValue(), icon);
            rdRankers.add(rdRanker);
        }
        // 封装我的信息
        myRank = this.activityRankService.getRank(guId, ar);
        myValue = this.activityRankService.getScore(guId, ar);
        // 封装第一名积分
        if (myRank == 1) {
            firsterValue = myValue;
        } else if (ListUtil.isNotEmpty(allRankers)) {
            Long firstUid = Optional.ofNullable(allRankers.get(0).getValue()).orElse(0L);
            firsterValue = this.activityRankService.getScore(firstUid, ar);
        }
        // 封装上一名积分
        // 榜单有人，且自己不是第一名
        if (ListUtil.isNotEmpty(allRankers) && myRank > 1) {
            int beforeRank = myRank - 1;
            Long beforeUid = Optional.ofNullable(allRankers.get(beforeRank - 1).getValue()).orElse(0L);
            beforerValue = this.activityRankService.getScore(beforeUid, ar);
        }
        rd.setRankers(rdRankers);
        // 未入榜判断
        rd.setMyRank(myRank > size ? 0 : myRank);
        rd.setMyValue(myValue);
        rd.setBeforerValue(beforerValue);
        rd.setFirstValue(firsterValue);
        rd.setTotalSize(size);
    }


    /**
     * 待优化
     *
     * @param guId
     * @param ar
     * @param rd
     */
    private void getRankersAsFst(long guId, IActivityRank ar, RDActivityRankerAwardList rd) {
        int myRank = 0;// 我的排名
        int myValue = 0;// 我的值
        Integer beforerValue = null;// 前一名的值
        Integer firsterValue = null;// 第一名的值
        int rank = 0;
        GameUser rankGu = null;
        List<RDActivityRanker> rdRankers = new ArrayList<>();
        // TypedTuple<Long> beforer = null;// 前一名
        // 全榜人员
        List<TypedTuple<Long>> rankers = this.activityRankService.getAllRankers(ar);
        for (TypedTuple<Long> ranker : rankers) {
            rank++;
            Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
            // 第一名,且是玩家
            if (rank == 1 && uid > 0) {
                firsterValue = fstServerService.getPointByRank(rank, FstRankingType.SERVER);
            }

            // 处理要显示的前几名
            if (rank <= this.activityConfig.getNumRankersToShow()) {
                String nickname = "-";
                int head = 1;
                int level = 120;
                int icon = TreasureEnum.HEAD_ICON_Normal.getValue();
                if (uid > 0) {// 玩家，小于等于为榜单站位格
                    rankGu = this.gameUserService.getGameUser(uid);
                    nickname = rankGu.getRoleInfo().getNickname();
                    head = rankGu.getRoleInfo().getHead();
                    level = rankGu.getLevel();
                    icon = rankGu.getRoleInfo().getHeadIcon();
                } else {
                    Optional<FstGameRobot> optional = fstRobotService.getRobotInfoOp(uid);
                    if (optional.isPresent()) {
                        FstGameRobot robot = optional.get();
                        nickname = robot.getNickname();
                        head = robot.getHead();
                        level = robot.getLevel();
                    }
                }
                // Double score = Optional.ofNullable(ranker.getScore()).orElse(0.0);

                RDActivityRanker rdRanker = new RDActivityRanker(nickname, head, level, rank, fstServerService.getPointByRank(rank, FstRankingType.SERVER), icon);
                rdRankers.add(rdRanker);
            }
            // 我的排名信息
            if (uid == guId) {
                myRank = rank;
                myValue = fstServerService.getPointByRank(rank, FstRankingType.SERVER);
                // 如果我不是第一名则获取前一名的信息
                if (myRank > 1) {
                    beforerValue = fstServerService.getPointByRank(myRank - 1, FstRankingType.SERVER);
                }
            }
            // 如果我的排名已获得，并超过要显示的数量，则跳出循环
            if (myRank > this.activityConfig.getNumRankersToShow()) {
                break;
            }
            // beforer = ranker;
        }
        rd.setRankers(rdRankers);
        rd.setMyRank(myRank);
        rd.setMyValue(myValue);
        rd.setBeforerValue(beforerValue);
        rd.setFirstValue(firsterValue);
    }

    /**
     * 获得返给客户端的奖励数据
     *
     * @param guId
     * @param ar
     * @param activity
     * @return
     */
    RDActivityRankerAward toRDRankerAward(long guId, IActivityRank ar, CfgActivityRankEntity activity) {
        RDActivityRankerAward rdRankerAward = null;
        List<Award> awards = this.activityRankService.getAwards(ar, activity);
        for (Award award : awards) {
            if (StrUtil.isNotBlank(award.getStrategy()) && "充值榜第一名加奖".equals(award.getStrategy())) {
                award.setIsNotOwn(1);
            }
        }
        List<RDAward> rdAwards = awards.stream().map(RDAward::getInstance).collect(Collectors.toList());
        rdRankerAward = new RDActivityRankerAward(activity, rdAwards);
        return rdRankerAward;
    }

    private String getActivityTime(IActivityRank ar) {
        return DateUtil.toString(ar.gainBegin(), "M-d HH:mm:ss") + "至" + DateUtil
                .toString(ar.gainEnd(), "M-d HH:mm:ss");
    }
}
