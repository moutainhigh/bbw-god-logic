package com.bbw.god.gameuser.biyoupalace;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.ChapterType;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class BYPalaceService {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;

    public UserBYPalace getUserBYPalace(long uid) {
        UserBYPalace ubyp = gameUserService.getSingleItem(uid, UserBYPalace.class);
        return ubyp;
    }

    @NonNull
    public UserBYPalace getUserBYPalaceWithIntance(long uid) {
        UserBYPalace userBYPalace = getUserBYPalace(uid);
        if (userBYPalace == null) {
            userBYPalace = UserBYPalace.instance(uid);
            gameUserService.addItem(uid, userBYPalace);
        }
        return userBYPalace;
    }

    /**
     * 获得要产出的奖励
     *
     * @param uid
     * @param type
     * @param chapter
     * @return
     */
    public List<Award> getChapterAwardToOutPut(long uid, int type, int chapter, List<Integer> toExclude) {
        CfgTreasureEntity treasureEntity = null;
        Award award = null;
        do {
            award = BYPalaceTool.getChapterAward(type, chapter);
            treasureEntity = TreasureTool.getTreasureById(award.gainAwardId());
        } while ((treasureEntity.ifSkillScroll() && !ifAbleOutputSkillScroll(uid, treasureEntity.getId()))
                || toExclude.contains(treasureEntity.getId()));

        return Arrays.asList(award);
    }

    /**
     * 是否可产出该技能卷轴
     *
     * @param uid
     * @param skillScrollId
     * @return
     */
    private boolean ifAbleOutputSkillScroll(long uid, int skillScrollId) {
        CfgSkillScrollLimitEntity skillScrollLimitEntity = TreasureTool.getSkillScrollLimitEntity(skillScrollId);
        if (skillScrollLimitEntity.getLimit() == 0) {
            return true;
        }
        int ownNum = userTreasureService.getTreasureNum(uid, skillScrollId);
        UserTreasureRecord utr = userTreasureRecordService.getUserTreasureRecord(uid, skillScrollId);
        if (utr != null) {
            ownNum += utr.getUseTimes();
        }

        return ownNum < skillScrollLimitEntity.getLimit();
    }

    /**
     * 将玩家正在修炼的技能卷轴id改成新的技能卷轴id(已达成或者已领取的不改！！！)
     *
     * @param uid              玩家id
     * @param oldSkillScrollId 修改更改的技能卷轴id
     * @param newSkillScrollId 新的技能卷轴id
     */
    public void changeUserSkillScroll(long uid, int oldSkillScrollId, int newSkillScrollId) {
        UserBYPalace userBYPalace = getUserBYPalace(uid);
        if (userBYPalace == null) {
            return;
        }
        HashMap<String, List<ChapterInfo>> typesChapters = userBYPalace.getTypesChapters();
        List<ChapterInfo> chapterInfos = typesChapters.get(ChapterType.SecretBiography.getName());
        for (ChapterInfo chapterInfo : chapterInfos) {
            // 可领取或者已领取的跳过
            if (chapterInfo.getStatus() >= AwardStatus.ENABLE_AWARD.getValue()) {
                continue;
            }
            List<Award> awards = chapterInfo.getAwards();
            awards.forEach(award -> {
                if (award.getAwardId().equals(oldSkillScrollId)) {
                    award.setAwardId(newSkillScrollId);
                }
            });
        }
        gameUserService.updateItem(userBYPalace);
    }
}
