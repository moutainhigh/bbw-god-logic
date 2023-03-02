package com.bbw.god.gameuser.achievement;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.achievement.RDAchievementList.RDAchievement;
import com.bbw.god.gameuser.achievement.event.AchievementEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAchievementLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserAchievementService achievementService;
    @Autowired
    private AchievementRankService achievementRankService;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;

    /**
     * 获得成就列表
     *
     * @param uid
     * @param type
     * @return
     */
    public RDAchievementList listAchievement(long uid, AchievementTypeEnum type) {
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == info) {
            info = initUserAchievementInfo(uid);
            this.gameUserService.addItem(uid, info);
        }
        List<RDAchievement> rdAchievements = new ArrayList<>();
        int finishNums = 0;
        int finishScore = 0;
        List<CfgAchievementEntity> achievements = AchievementTool.getAchievements(type);
        for (CfgAchievementEntity achievement : achievements) {
            int achievementId = achievement.getId();
            int status = info.getAchievementStatus(achievementId);
            int progress = achievementServiceFactory.getById(achievementId).getMyProgress(uid, info);
            rdAchievements.add(new RDAchievement(achievementId, status, progress, type.getValue()));
            if (AchievementStatusEnum.AWARED.getValue() == status) {
                finishNums++;
                finishScore += achievement.getScore();
            }
        }
        if (type == AchievementTypeEnum.SECRET) {
            rdAchievements = rdAchievements.stream().filter(a -> a.getStatus() >= AchievementStatusEnum.ACCOMPLISHED.getValue()).collect(Collectors.toList());
        }
        return RDAchievementList.getInstance(finishNums, finishScore, rdAchievements);
    }

    /**
     * 获取成就奖励
     *
     * @param uid
     * @param achievementId
     * @return
     */
    public RDCommon getAchievementAward(long uid, int achievementId) {
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == info) {
            info = initUserAchievementInfo(uid);
            this.gameUserService.addItem(uid, info);
        }
        BitSet awardedIds = info.getAwardedIds();
        BitSet accomplishedIds = info.getAccomplishedIds();
        // 已领取
        if (awardedIds.get(achievementId)) {
            throw new ExceptionForClientTip("achievement.already.award");
        }
        // 未达成
        if (!accomplishedIds.get(achievementId)) {
            throw new ExceptionForClientTip("achievement.not.accomplish");
        }
        info.awardedAchievement(achievementId);
        gameUserService.updateItem(info);
        // 更新排名
        CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
        achievementRankService.incrementRankValue(uid, achievement.getScore());
        // 发放
        RDCommon rd = new RDCommon();
        awardService.fetchAward(uid, achievement.getAwards(), WayEnum.ACHIEVEMENT, "达成成就【" + achievement.getName() + "】", rd);
        AchievementEventPublisher.pubAchievementFinishEvent(achievementId, new BaseEventParam(uid, WayEnum.ACHIEVEMENT, rd));
        return rd;
    }

    /**
     * 获取成就主页面信息
     *
     * @param uid
     * @return
     */
    public RDAchievementInfo getAchievementInfo(Long uid) {
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == info) {
            info = initUserAchievementInfo(uid);
            gameUserService.addItem(uid, info);
        }
        RDAchievementInfo.RDUserAchievementInfo achievementInfo = getAchievementInfo(uid, info);
        List<RDAchievement> recentAchievements = getRecentAchievements(uid, info);
        return RDAchievementInfo.getInstance(achievementInfo, recentAchievements);
    }

    private List<RDAchievementList.RDAchievement> getRecentAchievements(Long uid, UserAchievementInfo info) {
        List<RDAchievementList.RDAchievement> recentAchievements = new LinkedList<>();
        List<Integer> recentAccomplishedIds = info.getRecentAccomplishedIds();
        List<Integer> achievementIds = AchievementTool.getAllAchievements().stream().map(CfgAchievementEntity::getId).collect(Collectors.toList());
        // 错误的最近完成成就id集合
        List<Integer> errorRecentIds = new ArrayList<>();
        if (ListUtil.isNotEmpty(recentAccomplishedIds)) {
            for (int i = recentAccomplishedIds.size() - 1; i >= 0; i--) {
                int achievementId = recentAccomplishedIds.get(i);
                if (!achievementIds.contains(achievementId)) {
                    continue;
                }
                CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
                if (!achievement.getIsValid()) {
                    errorRecentIds.add(achievementId);
                    continue;
                }
                int status = info.getAchievementStatus(achievementId);
                int progress = achievementServiceFactory.getById(achievementId).getMyProgress(uid, info);
                Integer type = achievement.getType();
                recentAchievements.add(new RDAchievement(achievementId, status, progress, type));
            }
        }
        if (ListUtil.isNotEmpty(errorRecentIds)) {
            recentAccomplishedIds.removeAll(errorRecentIds);
            gameUserService.updateItem(info);
        }
        return recentAchievements;
    }

    private RDAchievementInfo.RDUserAchievementInfo getAchievementInfo(Long uid, UserAchievementInfo info) {
        RDAchievementInfo.RDUserAchievementInfo rdUserAchievementInfo = new RDAchievementInfo.RDUserAchievementInfo();
        BitSet awardedIds = info.getAwardedIds();
        BitSet accomplishedIds = info.getAccomplishedIds();
        AchievementTypeEnum[] typeEnums = AchievementTypeEnum.values();
        for (AchievementTypeEnum typeEnum : typeEnums) {
            List<CfgAchievementEntity> achievements = AchievementTool.getAchievements(typeEnum);
            int awarded = 0;
            int accomplish = 0;
            for (CfgAchievementEntity achievement : achievements) {
                Integer achievementId = achievement.getId();
                if (awardedIds.get(achievementId)) {
                    awarded++;
                    Integer score = AchievementTool.getAchievement(achievementId).getScore();
                    rdUserAchievementInfo.addScore(score);
                } else if (accomplishedIds.get(achievementId)) {
                    accomplish++;
                }
            }
            boolean ableAward = accomplish > 0;
            rdUserAchievementInfo.addFinishInfo(typeEnum.getValue(), awarded, ableAward);
        }
        return rdUserAchievementInfo;
    }

    public RDAchievementRankInfo getGameRDAchievementRankInfo(Long guId, int start, int end) {
        // 封装排行榜玩家信息
        List<RDAchievementRankInfo.RDUserRankInfo> rankInfoList = new ArrayList<>();
        CfgServerEntity server = gameUserService.getOriServer(guId);
        Set<ZSetOperations.TypedTuple<Long>> gameRankers = achievementRankService.getGameRankers(server.getGroupId(), start, end);
        for (ZSetOperations.TypedTuple<Long> gameRanker : gameRankers) {
            Long uid = gameRanker.getValue();
            RDAchievementRankInfo.RDUserRankInfo userGameRankInfo = getUserGameRankInfo(uid);
            rankInfoList.add(userGameRankInfo);
        }
        // 封装自己的信息
        RDAchievementRankInfo.RDUserRankInfo myRankInfo = getUserGameRankInfo(guId);
        int gameRankSize = Math.min(achievementRankService.getAllGameRankersSize(server.getGroupId()), 100);
        return RDAchievementRankInfo.getInstance(rankInfoList, myRankInfo, gameRankSize);
    }

    public RDAchievementRankInfo getServerRDAchievementRankInfo(Long guId, Integer sid, int start, int end) {
        if (sid == null) {
            sid = gameUserService.getGameUser(guId).getServerId();
        }
        // 封装排行榜玩家信息
        List<RDAchievementRankInfo.RDUserRankInfo> rankInfoList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Long>> serverRankers = achievementRankService.getServerRankers(sid, start, end);
        for (ZSetOperations.TypedTuple<Long> serverRanker : serverRankers) {
            Long uid = serverRanker.getValue();
            RDAchievementRankInfo.RDUserRankInfo userServerRankInfo = getUserServerRankInfo(uid, sid);
            rankInfoList.add(userServerRankInfo);
        }
        // 封装自己的信息
        RDAchievementRankInfo.RDUserRankInfo myRankInfo = getUserServerRankInfo(guId, sid);
        int serverRankSize = Math.min(achievementRankService.getAllServerRankers(sid).size(), 100);
        return RDAchievementRankInfo.getInstance(rankInfoList, myRankInfo, serverRankSize);
    }

    private RDAchievementRankInfo.RDUserRankInfo getUserGameRankInfo(Long uid) {
        CfgServerEntity cfgServerEntity = gameUserService.getOriServer(uid);
        String server = cfgServerEntity.getShortName();
        int gameRank = achievementRankService.getGameRank(uid, cfgServerEntity.getGroupId());
        int finishNums = achievementService.getAwardedAchievementNums(uid);
        int totalScore = achievementRankService.getAchievementScoreCount(uid);
        String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        return new RDAchievementRankInfo.RDUserRankInfo(gameRank, server, nickname, finishNums, totalScore);
    }

    private RDAchievementRankInfo.RDUserRankInfo getUserServerRankInfo(Long uid, int sid) {
        int serverRank = achievementRankService.getServerRank(uid, sid);
        int finishNums = achievementService.getAwardedAchievementNums(uid);
        int totalScore = achievementRankService.getAchievementScoreCount(uid, sid);
        String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        return new RDAchievementRankInfo.RDUserRankInfo(serverRank, nickname, finishNums, totalScore);
    }

    /**
     * 初始化玩家成就信息
     *
     * @param uid 玩家id
     * @return 家成就信息
     */
    public UserAchievementInfo initUserAchievementInfo(long uid) {
        List<UserAchievement> userAchievements = gameUserService.getMultiItems(uid, UserAchievement.class);
        List<Integer> errorIds = AchievementTool.getInvalidAchievements().stream().map(CfgAchievementEntity::getId).collect(Collectors.toList());
        List<Integer> accomplishedIds = userAchievements.stream().filter(uc -> uc.getStatus() == AchievementStatusEnum.ACCOMPLISHED.getValue()).map(UserCfgObj::getBaseId).collect(Collectors.toList());
        UserAwardedAchievements awardedAchievements = gameUserService.getSingleItem(uid, UserAwardedAchievements.class);
        List<Integer> awardedIds = new ArrayList<>();
        if (null != awardedAchievements) {
            awardedIds = awardedAchievements.getAwardeds();
        }
        UserRecentAchievements recentAchievements = gameUserService.getSingleItem(uid, UserRecentAchievements.class);
        List<Integer> recentAccomplishedIds = new ArrayList<>();
        if (null != recentAchievements) {
            recentAccomplishedIds = recentAchievements.getRecentAchievementIds();
        }
        accomplishedIds.removeAll(errorIds);
        awardedIds.removeAll(errorIds);
        recentAccomplishedIds.removeAll(errorIds);
        return new UserAchievementInfo(uid, accomplishedIds, awardedIds, recentAccomplishedIds);
    }
}
