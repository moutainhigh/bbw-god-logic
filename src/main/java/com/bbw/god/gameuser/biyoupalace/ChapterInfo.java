package com.bbw.god.gameuser.biyoupalace;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalace.TypeAward;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.biyoupalace.cfg.ChapterType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 篇章信息
 *
 * @author suhq
 * @date 2019-09-11 10:09:51
 */
@Data
public class ChapterInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer chapter;// 篇章
    private Integer conditionGroup = 0;// 开启条件
    private Integer progress = 0;// 进度
    private Integer useNum = 0;// 使用数量
    private Integer needNum;// 需要的数量
    private List<Award> awards;// 奖励
    private List<Integer> excludes;
    private Integer status;// 状态

    /**
     * 第一次进入碧游宫初始化多实例
     *
     * @param type
     * @return
     */
    public static List<ChapterInfo> instanceForFirstEnter(TypeAward type) {
        ChapterType chapterType = ChapterType.fromName(type.getType());
        AwardStatus defaultStatus = BYPalaceTool.getDefaultChapterStatus(chapterType);
        List<ChapterInfo> chapterInfos = new ArrayList<>();
        List<Integer> chapters = type.getChapters();
        for (int i = 0; i < chapters.size(); i++) {
            ChapterInfo chapterInfo = new ChapterInfo();
            chapterInfo.setChapter(chapters.get(i));
            String awardName = type.getChapterAwards().get(i);
            chapterInfo.setAwards(Arrays.asList(BYPalaceTool.getChapterAward(awardName)));
            if (type.getConditionGroups() != null) {
                chapterInfo.setConditionGroup(type.getConditionGroups().get(i));
            }
            chapterInfo.setStatus(defaultStatus.getValue());
            chapterInfo.setNeedNum(BYPalaceTool.getNeedTongTCJ(chapters.get(i)));
            chapterInfos.add(chapterInfo);
        }
        return chapterInfos;
    }

    /**
     * 常规实例化，如重置
     *
     * @param type
     * @param chapter
     * @return
     */
    public static ChapterInfo instance(ChapterType type, Chapter chapter) {
        ChapterInfo chapterInfo = new ChapterInfo();
        chapterInfo.setChapter(chapter.getValue());
        AwardStatus defaultStatus = BYPalaceTool.getDefaultChapterStatus(type);
        chapterInfo.setStatus(defaultStatus.getValue());
        chapterInfo.setNeedNum(BYPalaceTool.getNeedTongTCJ(chapter.getValue()));
        return chapterInfo;
    }

    /**
     * 更新实例
     *
     * @param type
     * @param chapter
     * @return
     */
    public void refresh(ChapterType type, Chapter chapter) {
        AwardStatus defaultStatus = BYPalaceTool.getDefaultChapterStatus(type);
        this.status = defaultStatus.getValue();
        this.needNum = BYPalaceTool.getNeedTongTCJ(chapter.getValue());
        this.progress = 0;
        this.useNum = 0;
        this.conditionGroup = 0;
    }

    public void addProgress(int addedProgress) {
        this.progress += addedProgress;
    }

    public void addUseNum(int addedUseNum) {
        this.useNum += addedUseNum;
        if (this.useNum >= this.needNum) {
            this.status = AwardStatus.ENABLE_AWARD.getValue();
        }
    }

    /**
     * 添加排除技能
     *
     * @param excludeIds
     */
    public void addExcludes(List<Integer> excludeIds) {
        if (null == excludes) {
            excludes = new ArrayList<>();
        }
        excludes.addAll(excludeIds);
    }

    /**
     * 获取已排除的技能的数量
     *
     * @return
     */
    public int gainExcludedNums() {
        if (null == excludes) {
            return 0;
        }
        return excludes.size();
    }

    public boolean ifRealized() {
        return this.status >= AwardStatus.ENABLE_AWARD.getValue();
    }

    /**
     * 是否秘境
     *
     * @return
     */
    public boolean ifSecretBiography() {
        return this.chapter == Chapter.SB1.getValue() || this.chapter == Chapter.SB2.getValue();
    }
}
