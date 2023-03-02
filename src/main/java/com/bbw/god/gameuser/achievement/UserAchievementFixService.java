package com.bbw.god.gameuser.achievement;

import com.bbw.god.city.UserCityService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangNpcEnum;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.alonemaou.UserAloneMaouData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAchievementFixService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserBusinessGangService userBusinessGangService;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    private BusinessGangService businessGangService;

    /**
     * 获取独战魔王成就正确数量，部分支持
     *
     * @param guId
     * @param achievementId
     * @return
     */
    private int getMaouAloneAchievementValue(long guId, int achievementId) {
        int maouType = achievementId - 840;
        UserAloneMaouData userAloneMaouData = this.gameUserService.getSingleItem(guId, UserAloneMaouData.class);
        if (userAloneMaouData == null) {
            return 0;
        } else {
            Optional<Integer> optional = userAloneMaouData.getMaouAuthList().stream().filter(uam ->
                    uam / 100 * 10 == maouType).max(Integer::compareTo);
            return optional.map(integer -> integer % 100).orElse(0);
        }
    }


    public void fixUserAchievementScore(long uid) {
        // 删掉所有的成就积分
        int treasureNum = userTreasureService.getTreasureNum(uid, TreasureEnum.ACHIEVEMENT_SCORE.getValue());
        if (treasureNum > 0) {
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.ACHIEVEMENT_SCORE.getValue(), treasureNum, WayEnum.NONE, new RDCommon());
        }
        // 补发积分
        userAchievementService.reissueScore(uid);
    }

    /**
     * 修复商帮声望成就进度
     *
     * @param uid
     * @param achievementId
     */
    public void repairGangPrestigeAchievementProgress(long uid, int achievementId, UserAchievementInfo info) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return;
        }
        int targetValue;
        switch (achievementId) {
            case 17080:
                targetValue = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
                break;
            case 17090:
                targetValue = userBusinessGang.getPrestige(BusinessGangEnum.ZHAO_BAO.getType());
                break;
            case 17100:
                targetValue = userBusinessGang.getPrestige(BusinessGangEnum.ZHAO_CAI.getType());
                break;
            default:
                return;
        }
        if (targetValue > 0 && targetValue < 9300) {
            //更新统计数据
            businessGangService.updateGangAchievementProgress(userBusinessGang, achievementId);
            return;
        }
        repairAchievement(uid, targetValue, achievementId, info);
    }

    /**
     * 商帮npc好感度
     *
     * @param uid
     * @param achievementId
     */
    public void repairGangFavorabilityAchievementProgress(long uid, int achievementId, UserAchievementInfo info) {
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        if (null == userBusinessGang) {
            return;
        }
        int targetValue;
        switch (achievementId) {
            case 17130:
                targetValue = userBusinessGang.getFavorability(BusinessGangNpcEnum.ZHAO_GM.getId());
                break;
            case 17140:
                targetValue = userBusinessGang.getFavorability(BusinessGangNpcEnum.XIAO_S.getId());
                break;
            case 17150:
                targetValue = userBusinessGang.getFavorability(BusinessGangNpcEnum.CHEN_JG.getId());
                break;
            default:
                return;
        }
        if (targetValue > 0 && targetValue < 6500) {
            //更新统计数据
            businessGangService.updateGangAchievementProgress(userBusinessGang, achievementId);
            return;
        }
        repairAchievement(uid, targetValue, achievementId, info);
    }

    /**
     * 修复成就
     *
     * @param uid
     * @param achievementId
     */
    public void repairAchievement(long uid, int value, int achievementId, UserAchievementInfo info) {
        BaseAchievementService service = achievementServiceFactory.getById(achievementId);
        service.achieve(uid, value, info, new RDCommon());
    }
}
