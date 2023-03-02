package com.bbw.god.login.repairdata;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.*;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.*;

/**
 * @author suchaobin
 * @description 修复成就相关数据
 * @date 2020/7/7 15:04
 **/
@Slf4j
@Service
public class RepairAchievementService implements BaseRepairDataService {
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserAchievementFixService userAchievementFixService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserAchievementLogic userAchievementLogic;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;

    // 需要修复状态的成就id集合
    private static final List<Integer> NEED_REPAIR_ACHIEVEMENT_IDS = Arrays.asList(14920);

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        // 修复成就积分
        if (lastLoginDate.before(REISSUE_SCORE_DATE)) {
            int treasureNum = userTreasureService.getTreasureNum(gu.getId(),
                    TreasureEnum.ACHIEVEMENT_SCORE.getValue());
            if (treasureNum > 0) {
                TreasureEventPublisher.pubTDeductEvent(gu.getId(), TreasureEnum.ACHIEVEMENT_SCORE.getValue(),
                        treasureNum, WayEnum.NONE, new RDCommon());
            }
            userAchievementFixService.fixUserAchievementScore(gu.getId());
        }

        // 修正userAchievementInfo数据，保证初始化时就有且仅有一个对应的数据
        if (lastLoginDate.before(RESET_INFO_DATE)) {
            List<UserAchievementInfo> infos = gameUserService.getMultiItems(gu.getId(), UserAchievementInfo.class);
            if (infos.size() > 1 || ListUtil.isEmpty(infos)) {
                gameUserService.deleteItems(gu.getId(), infos);
                UserAchievementInfo info = userAchievementLogic.initUserAchievementInfo(gu.getId());
                gameUserService.addItem(gu.getId(), info);
            }
            UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
            if (null != info) {
                BitSet accomplishedIds = info.getAccomplishedIds();
                accomplishedIds.andNot(info.getAwardedIds());
                gameUserService.addItem(gu.getId(), info);
            }
        }
        // 修复成就状态
        if (lastLoginDate.before(REPAIR_ACHIEVEMENT_STATUS_TIME)) {
            UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
            if (null == info) {
                info = userAchievementLogic.initUserAchievementInfo(gu.getId());
                gameUserService.addItem(gu.getId(), info);
            }
            for (Integer achievementId : NEED_REPAIR_ACHIEVEMENT_IDS) {
                repairAchievement(info, achievementId);
            }
        }
        // 修复成就状态
        if (lastLoginDate.before(REPAIR_2107V2_TIME)) {
            List<Integer> achievementIdToRepair = Arrays.asList(15560, 15570, 15580, 15590, 15600);
            UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
            for (Integer achievementId : achievementIdToRepair) {
                repairAchievement(info, achievementId);
            }
        }
        //修复商帮声望成就进度
        if (lastLoginDate.before(REPAIR_2203V2_TIME)) {
            List<Integer> achievementIdToRepair = Arrays.asList(17080, 17090, 17100);
            UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
            for (Integer achievementId : achievementIdToRepair) {
                userAchievementFixService.repairGangPrestigeAchievementProgress(gu.getId(), achievementId, info);
            }
        }
        //修复商帮好感度成就进度
        if (lastLoginDate.before(REPAIR_2203V2_TIME)) {
            List<Integer> achievementIdToRepair = Arrays.asList(17130, 17140, 17150);
            UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
            for (Integer achievementId : achievementIdToRepair) {
                userAchievementFixService.repairGangFavorabilityAchievementProgress(gu.getId(), achievementId, info);
            }
        }
        //修复群星册卡牌集齐成就进度
        if (lastLoginDate.before(REPAIR_FLOCKSTAR_BOOK_TIME)) {
            List<Integer> achievementIdToRepair = Arrays.asList(17300, 17310, 17320, 17330);
            UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
            for (Integer achievementId : achievementIdToRepair) {
                repairAchievement(info, achievementId);
            }
        }
    }

    public void repairAchievement(UserAchievementInfo achievementInfo, int achievementId) {
        BaseAchievementService service = achievementServiceFactory.getById(achievementId);
        if (service.isAccomplished(achievementInfo)) {
            return;
        }
        if (null == achievementInfo.getGameUserId()) {
            log.info("该成就的dataId为:{}", achievementInfo.getId());
        }
        int value = service.getMyValueForAchieve(achievementInfo.getGameUserId(), achievementInfo);
        service.achieve(achievementInfo.getGameUserId(), value, achievementInfo, new RDCommon());
    }
}
