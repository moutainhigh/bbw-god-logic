package com.bbw.god.gameuser.biyoupalace;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalace;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalace.TypeAward;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.biyoupalace.cfg.ChapterType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 玩家碧游宫
 *
 * @author suhq
 * @date 2019-09-11 10:15:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBYPalace extends UserSingleObj {
    private HashMap<String, List<ChapterInfo>> typesChapters;// 碧游宫篇章信息
    // private Integer curChapterType = -1;// 当前修炼
    private Integer finalAwardStatus = AwardStatus.UNAWARD.getValue();
    private Boolean isAutoBuy;// 领悟道具不足时是否自动购买
    private Date lastEnterDate;// 最近1次进入碧游宫的时间
    private Integer refreshDayTimes = 0;// 日刷新次数
    private Integer resetDayTimes = 0;// 日重置次数

    public static UserBYPalace instance(long uid) {
        CfgBYPalace cfgBYPalace = Cfg.I.getUniqueConfig(CfgBYPalace.class);
        UserBYPalace instance = new UserBYPalace();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setLastEnterDate(DateUtil.now());
        HashMap<String, List<ChapterInfo>> typesChapters = new HashMap<>();
        List<TypeAward> typeAwards = cfgBYPalace.getFirstInit();
        for (TypeAward type : typeAwards) {
            ChapterType chapterType = ChapterType.fromName(type.getType());
            List<ChapterInfo> chapterInfos = ChapterInfo.instanceForFirstEnter(type);
            typesChapters.put(chapterType.getName(), chapterInfos);
        }
        instance.setTypesChapters(typesChapters);
        return instance;
    }

    /**
     * 获得篇信息
     *
     * @param type
     * @param chapterId
     * @return
     */
    public ChapterInfo gainChapterInfo(ChapterType type, int chapterId) {
        Chapter chapter = Chapter.fromValue(chapterId);
        if (chapter == Chapter.SB1 || chapter == Chapter.SB2) {
            return typesChapters.get(type.getName()).get(chapterId - Chapter.SB1.getValue());
        }
        return typesChapters.get(type.getName()).get(chapterId - 1);
    }

    /**
     * 获得当前修炼的方向
     *
     * @return
     */
    public int getCurChapterType() {
        Set<String> chapterTypes = typesChapters.keySet();
        for (String chapterTypeName : chapterTypes) {
            ChapterType chapterType = ChapterType.fromName(chapterTypeName);
            List<ChapterInfo> chapterInfos = typesChapters.get(chapterTypeName);
            for (ChapterInfo chapterInfo : chapterInfos) {
                if (chapterInfo.getProgress() > 0 && chapterInfo.getStatus() < AwardStatus.AWARDED.getValue()) {
                    // if (chapterType == ChapterType.SecretBiography) {
                    // return -1;
                    // }
                    return chapterType.getValue();
                }
            }
        }
        return -1;
    }

    /**
     * 是否领悟完所有的篇章
     *
     * @return
     */
    public boolean ifRealizedAllChapters() {
        return finalAwardStatus >= AwardStatus.ENABLE_AWARD.getValue();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.BI_YOU_PALACE;
    }

}
