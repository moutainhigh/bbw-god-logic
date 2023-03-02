package com.bbw.god.gameuser.achievement;

import com.bbw.cache.UserCacheService;
import com.bbw.common.BitSetUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAchievementService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private AchievementRankService achievementRankService;
    @Autowired
    private UserAchievementLogic userAchievementLogic;

    /**
     * 获取玩家单个成就记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param achievementId
     * @return
     */
    public UserAchievement getUserAchievement(long uid, int achievementId) {
        UserAchievement uAchievement = this.userCacheService.getCfgItem(uid, achievementId, UserAchievement.class);
        return uAchievement;
    }

    /**
     * 获取玩家所有成就记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    public List<UserAchievement> getUserAchievements(long uid) {
        return this.userCacheService.getUserDatas(uid, UserAchievement.class);
    }


    /**
     * 添加成绩,游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param uAchievement
     */
    public void addUserAchievement(long uid, UserAchievement uAchievement) {
        userCacheService.addUserData(uAchievement);
    }

    /**
     * 删除成绩,游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param uAchievement
     */
    public void delUserAchievement(long uid, UserAchievement uAchievement) {
        userCacheService.delUserData(uAchievement);
    }

    /**
     * 补发积分，用于老玩家已达成的成就
     *
     * @param uid
     */
    public void reissueScore(long uid) {
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == info) {
            info = userAchievementLogic.initUserAchievementInfo(uid);
            gameUserService.addItem(uid, info);
        }
        BitSet awardedIds = info.getAwardedIds();
        if (!awardedIds.isEmpty()) {
            // 补发积分总数
            int totalScore = 0;
            List<CfgAchievementEntity> allAchievements = AchievementTool.getAllAchievements();
            for (CfgAchievementEntity achievement : allAchievements) {
                if (awardedIds.get(achievement.getId())) {
                    totalScore += achievement.getScore();
                }
            }
            // 追加积分到榜单
            achievementRankService.repairRankValue(uid, totalScore);
            // 发放积分
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.ACHIEVEMENT_SCORE.getValue(), totalScore,
                    WayEnum.REISSUE_ACHIEVEMENT_SCORE, new RDCommon());
        }
    }

    /**
     * 获取待领取的成就数量
     *
     * @param uid
     * @param type
     * @return
     */
    public int getAbleAward(long uid, AchievementTypeEnum type) {
        UserAwardedAchievements awardedAchievements = this.getAwardedAchievements(uid);
        List<UserAchievement> userAchievements = this.getUserAchievements(uid, type);
        if (ListUtil.isNotEmpty(userAchievements)) {
            Long accomplishNum = userAchievements.stream().filter(ua -> !awardedAchievements.getAwardeds().contains(ua.getBaseId()) && ua.getStatus() == AchievementStatusEnum.ACCOMPLISHED.getValue()).count();
            return accomplishNum.intValue();
        }
        return 0;
    }

    public int getAbleAward(long uid) {
        UserAwardedAchievements awardedAchievements = this.getAwardedAchievements(uid);
        List<UserAchievement> userAchievements = this.getUserAchievements(uid);
        if (ListUtil.isNotEmpty(userAchievements)) {
            Long accomplishNum = userAchievements.stream().filter(ua -> !awardedAchievements.getAwardeds().contains(ua.getBaseId()) && ua.getStatus() == AchievementStatusEnum.ACCOMPLISHED.getValue()).count();
            return accomplishNum.intValue();
        }
        return 0;
    }

    public int getAbleAward(long uid, Integer type) {
        UserAwardedAchievements awardedAchievements = this.getAwardedAchievements(uid);
        List<UserAchievement> userAchievements = this.getUserAchievements(uid);
        if (ListUtil.isNotEmpty(userAchievements)) {
            long accomplishNum = userAchievements.stream().filter(ua -> {
                CfgAchievementEntity achievement = AchievementTool.getAchievement(ua.getBaseId());
                return !awardedAchievements.getAwardeds().contains(ua.getBaseId()) &&
                        ua.getStatus() == AchievementStatusEnum.ACCOMPLISHED.getValue() &&
                        achievement.getIsValid() && achievement.getType().equals(type);
            }).count();
            return (int) accomplishNum;
        }
        return 0;
    }

    /**
     * 已领取的成就
     *
     * @param uid
     * @return
     */
    public UserAwardedAchievements getAwardedAchievements(long uid) {
        UserAwardedAchievements awardedAchievements = this.gameUserService.getSingleItem(uid, UserAwardedAchievements.class);

        List<Integer> awardeds = new ArrayList<>();
        List<UserAchievement> uas = this.getUserAchievements(uid);
        // 如果原有奖励有已领取的成就，则删除原有的成就记录，并记录到已领取的成就集合
        if (ListUtil.isNotEmpty(uas)) {
            List<UserAchievement> toDels = uas.stream().filter(tmp -> tmp.getStatus() == AchievementStatusEnum.AWARED.getValue()
                    && AchievementTool.getAchievement(tmp.getBaseId()).getIsValid()).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(toDels)) {
                awardeds = toDels.stream().map(UserAchievement::getBaseId).collect(Collectors.toList());
                this.gameUserService.deleteItems(uid, toDels);
                LogUtil.logDeletedUserDatas(toDels, "已领取的成就");
            }
        }
        if (awardedAchievements == null) {
            awardedAchievements = UserAwardedAchievements.instance(uid, awardeds);
            this.gameUserService.addItem(uid, awardedAchievements);
        }
        return awardedAchievements;
    }

    public List<UserAchievement> getUserAchievements(long uid, AchievementTypeEnum type) {
        List<UserAchievement> uAchievements = getUserAchievements(uid);
        if (ListUtil.isNotEmpty(uAchievements)) {
            uAchievements = uAchievements.stream().filter(tmp -> tmp != null &&
                    AchievementTool.getAchievement(tmp.getBaseId()).getType() == type.getValue()).collect(Collectors.toList());
        }
        return uAchievements;
    }

    public int getUserFinishedAchievementsNum(long uid) {
        UserAchievementInfo userAchievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == userAchievementInfo) {
            return 0;
        }
        return getFinishAchievementIds(userAchievementInfo).size();
    }

    /**
     * 判断某个秘闻成就是否验证
     *
     * @param uid
     * @param secretAchievementId
     * @return
     */
    public boolean isVerifySecretAchievement(long uid, int secretAchievementId) {
        UserAchievementInfo userAchievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == userAchievementInfo) {
            return false;
        }
        return userAchievementInfo.getVerifyAccomplishedIds().contains(secretAchievementId);
    }

    /**
     * 获得已验证或未验证的秘闻成就
     *
     * @param uid
     * @return
     */
    public List<Integer> getStatusSecretAchievement(long uid, boolean isCheck) {
        UserAchievementInfo userAchievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == userAchievementInfo) {
            return null;
        }
        List<Integer> finishAchievementIds = getFinishAchievementIds(userAchievementInfo);
        List<Integer> finishSecretAchievementIds = AchievementTool.getAllAchievements().stream()
                .filter(a -> a.getType() == AchievementTypeEnum.SECRET.getValue() && finishAchievementIds.contains(a.getId()))
                .map(CfgAchievementEntity::getId).collect(Collectors.toList());
        //返回已验证的秘闻成就
        if (isCheck) {
            return finishSecretAchievementIds.stream().filter(a -> userAchievementInfo.getVerifyAccomplishedIds()
                    .contains(a)).collect(Collectors.toList());
        }
        // 返回未验证的秘闻成就
        return finishSecretAchievementIds.stream().filter(a -> !userAchievementInfo.getVerifyAccomplishedIds()
                .contains(a)).collect(Collectors.toList());
    }

    /**
     * 获得已完成成就，待领取也算
     *
     * @param userAchievementInfo
     * @return
     */
    public List<Integer> getFinishAchievementIds(UserAchievementInfo userAchievementInfo) {
        BitSet awarded = userAchievementInfo.getAwardedIds();
        BitSet accomplished = userAchievementInfo.getAccomplishedIds();
        awarded.andNot(accomplished);
        List<Integer> awardedIds = BitSetUtil.toList(awarded);
        List<Integer> accomplishedIds = BitSetUtil.toList(accomplished);
        //获取已完成的成就
        List<Integer> finishAchievementIds = new ArrayList<>();
        List<Integer> allAchievementIds = AchievementTool.getAllAchievements().stream()
                .map(CfgAchievementEntity::getId).collect(Collectors.toList());
        finishAchievementIds.addAll(awardedIds.stream().filter(allAchievementIds::contains).collect(Collectors.toList()));
        finishAchievementIds.addAll(accomplishedIds.stream().filter(allAchievementIds::contains).collect(Collectors.toList()));
        return finishAchievementIds;
    }

    /**
     * 获取已领取奖励的成就数量
     *
     * @param uid
     * @return
     */
    public int getAwardedAchievementNums(Long uid) {
        UserAchievementInfo userAchievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null == userAchievementInfo) {
            return 0;
        }
        BitSet awardedIds = userAchievementInfo.getAwardedIds();
        List<Integer> awardeds = BitSetUtil.toList(awardedIds);
        awardeds =
                awardeds.stream().filter(a -> AchievementTool.getAchievement(a).getIsValid()).collect(Collectors.toList());
        return awardeds.size();
    }

    public int getAwardedAchievementNums(Long uid, AchievementTypeEnum type) {
        int finish = 0;
        UserAwardedAchievements awardedAchievements = getAwardedAchievements(uid);
        if (awardedAchievements == null) {
            return finish;
        }
        List<Integer> awardeds = awardedAchievements.getAwardeds();
        for (Integer achievementId : awardeds) {
            CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
            if (achievement.getType().equals(type.getValue()) && achievement.getIsValid()) {
                finish++;
            }
        }
        return finish;
    }

    /**
     * 获取已领取奖励的总积分
     *
     * @param uid
     * @return
     */
    public int getAwardedTotalScore(Long uid) {
        UserAwardedAchievements awardedAchievements = getAwardedAchievements(uid);
        int totalScore = 0;
        if (awardedAchievements == null) {
            return totalScore;
        }
        List<Integer> awardeds = awardedAchievements.getAwardeds();
        for (Integer achievementId : awardeds) {
            CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
            if (AchievementTool.getAchievement(achievementId).getIsValid()) {
                totalScore += achievement.getScore();
            }
        }
        return totalScore;
    }

    public int getAwardedTotalScore(Long uid, AchievementTypeEnum type) {
        UserAwardedAchievements awardedAchievements = getAwardedAchievements(uid);
        int totalScore = 0;
        if (awardedAchievements == null) {
            return totalScore;
        }
        List<Integer> awardeds = awardedAchievements.getAwardeds();
        for (Integer achievementId : awardeds) {
            CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
            if (achievement.getType().equals(type.getValue()) && achievement.getIsValid()) {
                totalScore += achievement.getScore();
            }
        }
        return totalScore;
    }

    public boolean isFinish(Long uid, Integer achievementId) {
        UserAchievement userAchievement = getUserAchievement(uid, achievementId);
        if (userAchievement != null) {
            if (userAchievement.getStatus() >= AchievementStatusEnum.ACCOMPLISHED.getValue()) {
                return true;
            }
            return false;
        }
        UserAwardedAchievements awardedAchievements = getAwardedAchievements(uid);
        return awardedAchievements.ifAwarded(achievementId);
    }

    /**
     * 删除无效的成就记录
     *
     * @param uid
     */
    public void delNoValidAchievement(long uid) {
        // 删除无效且还未完成的成就
        List<UserAchievement> userAchievements = getUserAchievements(uid);
        List<UserAchievement> noValidAchievement = userAchievements.stream().filter(ua ->
                !AchievementTool.getAchievement(ua.getBaseId()).getIsValid()).collect(Collectors.toList());
        gameUserService.deleteItems(uid, noValidAchievement);
        UserAwardedAchievements awardedAchievements = gameUserService.getSingleItem(uid, UserAwardedAchievements.class);
        // 删除已经完成的成就中无效的成就
        if (awardedAchievements != null) {
            List<Integer> awardeds = awardedAchievements.getAwardeds();
            awardeds = awardeds.stream().filter(a -> AchievementTool.getAchievement(a).getIsValid()).collect(Collectors.toList());
            awardedAchievements.setAwardeds(awardeds);
            gameUserService.updateItem(awardedAchievements);
        }
    }
}
