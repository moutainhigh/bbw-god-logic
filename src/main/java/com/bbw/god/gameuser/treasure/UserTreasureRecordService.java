package com.bbw.god.gameuser.treasure;

import com.bbw.cache.UserCacheService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserTreasureRecordService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;

    public List<UserTreasureRecord> getRecords(long uid) {
        return userCacheService.getUserDatas(uid, UserTreasureRecord.class);
    }

    public UserTreasureRecord getRecord(long uid, int treasureId, WayEnum way) {
        UserTreasureRecord utr = getRecords(uid).stream()
                .filter(t -> t.getBaseId() == treasureId && t.getWay() == way)
                .findFirst().orElse(null);
        return utr;
    }

    public void addRecord(UserTreasureRecord utr) {
        userCacheService.addUserData(utr);
    }

    public void delRecord(UserTreasureRecord utr) {
        userCacheService.delUserData(utr);
    }

    public void delRecords(List<UserTreasureRecord> utrs) {
        userCacheService.delUserDatas(utrs);
    }

    /**
     * 获取法宝使用次数
     *
     * @param guId
     * @param treasureId
     * @return
     */
    public int getUseTimes(long guId, int treasureId) {
        UserTreasureRecord utRecord = getUserTreasureRecord(guId, treasureId);
        int useTimes = 0;
        if (utRecord != null) {
            useTimes = utRecord.getUseTimes();
        }
        return useTimes;
    }

    /**
     * 获取记录
     * @param guId
     * @param treasureId
     * @return
     */
    public UserTreasureRecord getUserTreasureRecord(long guId, int treasureId){
        UserTreasureRecord utRecord = getRecords(guId).stream()
                .filter(ut -> ut.getBaseId() == treasureId).findFirst().orElse(null);
        return utRecord;
    }

    /**
     * 获取记录，如果没有则创建
     * @param uid
     * @param treasureId
     * @param usedNum
     * @return
     */
    public UserTreasureRecord getOrCreateRecord(long uid, int treasureId,int usedNum){
        UserTreasureRecord utr = getUserTreasureRecord(uid, treasureId);
        if (utr == null) {
            utr = UserTreasureRecord.instance(uid, treasureId);
            utr.setUseTimes(usedNum);
            addRecord(utr);
        }
        return utr;
    }
    /**
     * 到达一个新地方需要重置法宝的使用状态
     *
     * @param uid
     * @param way
     */
    public void resetTreasureRecordAsNewPos(long uid, WayEnum way) {
        TreasureEventPublisher.pubTRecordResetEvent(uid, TreasureEnum.DFZ.getValue(), WayEnum.TREASURE_USE);
        TreasureEventPublisher.pubTRecordResetEvent(uid, TreasureEnum.XJZ.getValue(), WayEnum.TREASURE_USE);
        TreasureEventPublisher.pubTRecordResetEvent(uid, TreasureEnum.CHANG_ZI.getValue(), WayEnum.TREASURE_USE);
    }

    public void resetTreasureRecordAsChangZi(long uid) {
        TreasureEventPublisher.pubTRecordResetEvent(uid, TreasureEnum.CHANG_ZI.getValue(), WayEnum.TREASURE_USE);
    }

    /**
     * 清除限制卷轴使用记录
     *
     * @param uc
     * @param skillLevel
     */
    public void deductSkillScrollRecord(UserCard uc, int skillLevel) {
        if (!uc.ifOriginalSkill(skillLevel)) {
            return;
        }
        Integer usedSkillScrollId = uc.gainUsedSkillScrollId(skillLevel);
        if (0 == usedSkillScrollId) {
            return;
        }
        CfgSkillScrollLimitEntity skillScrollLimitEntity = TreasureTool.getSkillScrollLimitEntity(usedSkillScrollId);
        if (skillScrollLimitEntity != null && skillScrollLimitEntity.getLimit() > 0) {
            log.info("扣除限制卷轴【{}】使用记录", skillScrollLimitEntity.getName());
            UserTreasureRecord utr = getUserTreasureRecord(uc.getGameUserId(), skillScrollLimitEntity.getId());
            if (utr != null) {
                utr.deductTimes();
                gameUserService.updateItem(utr);
            }
        }
    }

}
