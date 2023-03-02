package com.bbw.god.gameuser.biyoupalace;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.UserCityService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.*;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.*;
import com.bbw.god.gameuser.biyoupalace.event.BiyouEventPublisher;
import com.bbw.god.gameuser.biyoupalace.event.EPBiyouGainAward;
import com.bbw.god.gameuser.biyoupalace.rd.*;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.MallLogic;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BYPalaceLogic {
    @Autowired
    private BYPalaceService byPalaceService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MallLogic mallLogic;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 进入碧游宫
     *
     * @param uid
     * @return
     */
    public RDEnterBYPalace enterBYPalace(long uid) {
        CfgBYPalace cfgBYPalace = BYPalaceTool.getCfgBYPalace();
        // 碧游宫是否开放
        if (!cfgBYPalace.getIsOpen()) {
            throw new ExceptionForClientTip("bypalace.not.open");
        }
        // 是否攻下两座4级城
        int ownCity4 = this.userCityService.getOwnCityNumAsLevel(uid, 4);
        if (ownCity4 < 2) {
            throw new ExceptionForClientTip("bypalace.lock");
        }
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalaceWithIntance(uid);
        // 当前领悟
        int curChapterType = userBYPalace.getCurChapterType();
        Set<String> chapterTypes = userBYPalace.getTypesChapters().keySet();
        // 无属性篇章
        List<RDChapter> rdChapters = new ArrayList<>();
        Long realizationNum = 0L;
        for (String chapterTypeName : chapterTypes) {
            ChapterType chapterType = ChapterType.fromName(chapterTypeName);
            if (chapterType == ChapterType.SecretBiography) {
                continue;
            }
            List<ChapterInfo> chapterInfos = userBYPalace.getTypesChapters().get(chapterTypeName);
            List<RDChapter> rdTypeChapters = chapterInfos.stream().map(tmp -> this.toRdChapter(chapterType.getValue(), tmp)).collect(Collectors.toList());
            rdChapters.addAll(rdTypeChapters);
            realizationNum += chapterInfos.stream().filter(tmp -> tmp.ifRealized()).count();
        }
        // 秘传篇章
        List<ChapterInfo> sbChapters = userBYPalace.getTypesChapters().get(ChapterType.SecretBiography.getName());
        List<RDChapter> rdSbChapters = sbChapters.stream().map(tmp -> {
            if (!tmp.ifRealized() && tmp.getAwards().get(0).getAwardId() == TreasureEnum.WangNFL.getValue()) {
                tmp.setAwards(Arrays.asList(new Award(21159, AwardEnum.FB, 1)));
            }
            RDChapter rdChapter = this.toRdChapter(ChapterType.SecretBiography.getValue(), tmp);
            if (tmp.getStatus() == AwardStatus.LOCK.getValue()) {
                rdChapter.setAwards(new ArrayList<>());
            }
            return rdChapter;
        }).collect(Collectors.toList());
        if (userBYPalace != null) {
            if (userBYPalace.getLastEnterDate() != null && !DateUtil.isToday(userBYPalace.getLastEnterDate())) {
                userBYPalace.setRefreshDayTimes(0);
                userBYPalace.setResetDayTimes(0);
            }
            userBYPalace.setLastEnterDate(DateUtil.now());
            this.gameUserService.updateItem(userBYPalace);
        }
        RDEnterBYPalace rd = new RDEnterBYPalace();
        rd.setCurChapterType(curChapterType);
        rd.setTypesChapters(rdChapters);
        rd.setSbChapters(rdSbChapters);
        rd.setRefreshDayTimes(userBYPalace.getRefreshDayTimes());
        rd.setResetDayTimes(userBYPalace.getResetDayTimes());
        // 真传奖励
        RDFinalAward rdFinalAward = new RDFinalAward();
        List<RDAward> finalAwards = cfgBYPalace.getFinalAwards().stream().map(tmp -> new RDAward(tmp.gainAwardId(), tmp.getItem(), tmp.getNum())).collect(Collectors.toList());
        rdFinalAward.setAwards(finalAwards);
        // 真转奖励的状态
        rdFinalAward.setStatus(userBYPalace.getFinalAwardStatus());
        rd.setFinalAward(rdFinalAward);
        UserBYPalaceLockSkill lockSkill = this.gameUserService.getSingleItem(uid, UserBYPalaceLockSkill.class);
        if (lockSkill == null) {
            lockSkill = UserBYPalaceLockSkill.instance(uid);
            this.gameUserService.addItem(uid, lockSkill);
        }
        HashMap<String, List<ChapterInfo>> typesChapters = userBYPalace.getTypesChapters();
        Set<String> keySet = typesChapters.keySet();
        for (String key : keySet) {
            List<ChapterInfo> chapterInfos = typesChapters.get(key);
            for (ChapterInfo chapterInfo : chapterInfos) {
                Integer chapter = chapterInfo.getChapter();
                if (Arrays.asList(Chapter.SB1.getValue(), Chapter.SB2.getValue()).contains(chapter)) {
                    continue;
                }
                List<Award> awards = chapterInfo.getAwards();
                for (Award award : awards) {
                    lockSkill.addSkill(award.getAwardId(), chapter);
                }
            }
        }
        this.gameUserService.updateItem(lockSkill);
        rd.setLockSkill(lockSkill.getLockSkill());
        return rd;
    }

    /**
     * 重置
     *
     * @param uid
     * @return
     */
    public RDCommon reset(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        CfgBYPalace cfgBYPalace = Cfg.I.getUniqueConfig(CfgBYPalace.class);
        int needGold = cfgBYPalace.getResetByPalaceGold();
        RDCommon rd = new RDCommon();
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);
        if (!userBYPalace.ifRealizedAllChapters()) {
            //如果是全部修炼完 则刷新不需要元宝
            ResChecker.checkGold(gu, needGold);
            ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.BYPALACE_RESET, rd);
        }
        HashMap<String, List<ChapterInfo>> typesChapters = new HashMap<>();
        for (ChapterType type : ChapterType.values()) {
            List<ChapterInfo> chapterInfos = new ArrayList<>();
            List<Chapter> chapters;
            List<Integer> conditionGroup = null;
            if (type == ChapterType.SecretBiography) {
                chapters = Arrays.asList(Chapter.SB1, Chapter.SB2);
                List<List<Integer>> possibleConditionGroups = cfgBYPalace.getPossibleConditionGroups();
                int random = PowerRandom.getRandomBySeed(possibleConditionGroups.size()) - 1;
                conditionGroup = possibleConditionGroups.get(random);
            } else {
                chapters = Arrays.asList(Chapter.One, Chapter.Two, Chapter.Three, Chapter.Four, Chapter.Five);
            }
            List<Integer> excludes = new ArrayList<>();
            for (int i = 0; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);
                ChapterInfo chapterInfo = ChapterInfo.instance(type, chapter);
                List<Award> awards = this.byPalaceService.getChapterAwardToOutPut(uid, type.getValue(),
                        chapter.getValue(), excludes);
                chapterInfo.setAwards(awards);
                if (type == ChapterType.SecretBiography) {
                    excludes.add(awards.get(0).getAwardId());
                }
                if (ListUtil.isNotEmpty(conditionGroup)) {
                    chapterInfo.setConditionGroup(conditionGroup.get(i));
                }
                chapterInfos.add(chapterInfo);
            }
            typesChapters.put(type.getName(), chapterInfos);
        }
        userBYPalace.setFinalAwardStatus(AwardStatus.UNAWARD.getValue());
        userBYPalace.setTypesChapters(typesChapters);
        this.gameUserService.updateItem(userBYPalace);
        return rd;
    }

    /**
     * 刷新
     *
     * @param uid
     * @param locks eg:34;12;;; -> 金属性第三四篇;木属性一二篇锁住
     * @return
     */
    public RDCommon refresh(long uid, String locks) {
        RDCommon rd = new RDCommon();
        String[] lockInfo = locks.split(";");
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);
        if (userBYPalace.ifRealizedAllChapters()) {
            throw new ExceptionForClientTip("bypalace.unneed.refresh");
        }
        Set<String> chapterTypes = userBYPalace.getTypesChapters().keySet();
        int lockNum = 0;
        for (String chapterTypeName : chapterTypes) {
            ChapterType chapterType = ChapterType.fromName(chapterTypeName);
            if (chapterType == ChapterType.SecretBiography) {
                continue;
            }

            List<ChapterInfo> chapterInfos = userBYPalace.getTypesChapters().get(chapterTypeName);
            for (int i = 0; i < chapterInfos.size(); i++) {
                ChapterInfo chapterInfo = chapterInfos.get(i);
                // 已领悟跳过
                if (chapterInfo.ifRealized()) {
                    continue;
                }
                // 锁住跳过
                if (lockInfo.length >= chapterType.getValue() / 10 && lockInfo[chapterType.getValue() / 10 - 1].contains(chapterInfo.getChapter().toString())) {
                    lockNum++;
                    continue;
                }
                chapterInfo.refresh(chapterType, Chapter.fromValue(chapterInfo.getChapter()));
                List<Award> awards = this.byPalaceService.getChapterAwardToOutPut(uid,
                        chapterType.getValue(), chapterInfo.getChapter(),
                        Arrays.asList(chapterInfo.getAwards().get(0).gainAwardId()));
                chapterInfo.setAwards(awards);
            }
        }
        int needGold = 80 + 10 * lockNum;
        GameUser gu = this.gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.BYPALACE_REFRESH, rd);
        this.gameUserService.updateItem(userBYPalace);
        // 返回更新的奖励给客户端
        return rd;
    }

    /**
     * 领悟
     *
     * @param uid
     * @param isAutoBuy
     * @return
     */
    public RDRealization realization(long uid, int chapterTypeValue, int chapter, boolean isAutoBuy) {

        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);
        ChapterType chapterType = ChapterType.fromValue(chapterTypeValue);
        ChapterInfo chapterInfo = userBYPalace.gainChapterInfo(chapterType, chapter);
        if (chapterInfo == null) {
            throw new ExceptionForClientTip("bypalace.unvalid.chapter");
        }
        if (chapterInfo.ifRealized()) {
            throw new ExceptionForClientTip("bypalace.already.realization");
        }
        if (chapterInfo.getStatus() == AwardStatus.LOCK.getValue()) {
            throw new ExceptionForClientTip("bypalace.chapter.lock");
        }
        // 非秘境和第一篇需要先领悟前一篇
        if (!chapterInfo.ifSecretBiography() && chapter > Chapter.One.getValue()) {
            ChapterInfo preChapter = userBYPalace.gainChapterInfo(chapterType, chapter - 1);
            if (preChapter.getStatus() < AwardStatus.ENABLE_AWARD.getValue()) {
                throw new ExceptionForClientTip("ypalace.need.to.realizition.preOne");
            }
        }

        CfgBYPalaceChapterEntity cfgChapter = BYPalaceTool.getChapterEntity(chapter);
        int needTongTCJ = cfgChapter.getCostPerTime();

        UserTreasure ut = userTreasureService.getUserTreasure(uid, TreasureEnum.TongTCJ.getValue());

        if (!isAutoBuy) {
            // 不购买直接检查
            TreasureChecker.checkIsEnough(ut, TreasureEnum.TongTCJ.getValue(), needTongTCJ);
        }
        RDRealization rd = new RDRealization();
        int ownNum = ut == null ? 0 : ut.gainTotalNum();
        int needDeductForClient = needTongTCJ;
        int needBuyTime = needTongTCJ - ownNum;// 还需要几个
        // 不够自动购买
        if (needBuyTime > 0) {
            needDeductForClient = ownNum;
            CfgMallEntity mallEntity = MallTool.getMallTreasure(TreasureEnum.TongTCJ.getValue());
            RDCommon buyResult = this.mallLogic.buy(uid, mallEntity.getId(), needBuyTime);
            // 返回消耗的铜钱或者元宝
            rd.setAddedGold(buyResult.getAddedGold());
        }
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.TongTCJ.getValue(), needTongTCJ, WayEnum.BYPALACE_REALIZATION, rd);
        int addedProgress = 0;
        for (int i = 0; i < needTongTCJ; i++) {
            addedProgress += PowerRandom.getRandomFromList(BYPalaceTool.getTongTCJEffects());
        }
        chapterInfo.addProgress(addedProgress);
        chapterInfo.addUseNum(needTongTCJ);
        if (chapterInfo.getStatus() == AwardStatus.ENABLE_AWARD.getValue()) {
            this.toUnlockSecretBiography(userBYPalace, rd);
            this.toUpdateFinalAwardStatus(userBYPalace);
            BiyouEventPublisher.pubRealizedEvent(uid, chapterInfo.getChapter());
        }

        userBYPalace.setIsAutoBuy(isAutoBuy);
        this.gameUserService.updateItem(userBYPalace);
        rd.setAddedProgress(addedProgress);
        rd.setUseNum(needDeductForClient);
        rd.setStatus(chapterInfo.getStatus());
        return rd;
    }

    /**
     * 筛选要排除的技能
     *
     * @param uid
     * @param chapter
     * @return
     */
    public RDExcludeInfo getExcludeInfo(long uid, int chapter) {
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);
        ChapterType chapterType = ChapterType.fromValue(ChapterType.SecretBiography.getValue());
        ChapterInfo chapterInfo = userBYPalace.gainChapterInfo(chapterType, chapter);
        if (chapterInfo == null) {
            throw new ExceptionForClientTip("bypalace.unvalid.chapter");
        }
        if (chapterInfo.ifRealized()) {
            throw new ExceptionForClientTip("bypalace.already.realization");
        }
        if (chapterInfo.getStatus() == AwardStatus.LOCK.getValue()) {
            throw new ExceptionForClientTip("bypalace.chapter.lock");
        }
        RDExcludeInfo rd = new RDExcludeInfo();
        List<Integer> excludes = chapterInfo.getExcludes();
        if (null == excludes) {
            excludes = new ArrayList<>();
        }
        rd.setExcludes(excludes);
        return rd;
    }

    /**
     * 排除技能
     *
     * @param uid
     * @param chapter
     * @param excludes
     * @param useTTLPNum
     * @return
     */
    public RDCommon chooseExcludeSkills(long uid, int chapter, String excludes, int useTTLPNum) {
        List<Integer> excludeIds = ListUtil.parseStrToInts(excludes);
        if (ListUtil.isEmpty(excludeIds)) {
            throw ExceptionForClientTip.fromi18nKey("bypalace.exclude.not.choosed");
        }
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);
        ChapterType chapterType = ChapterType.fromValue(ChapterType.SecretBiography.getValue());
        ChapterInfo chapterInfo = userBYPalace.gainChapterInfo(chapterType, chapter);
        if (chapterInfo == null) {
            throw new ExceptionForClientTip("bypalace.unvalid.chapter");
        }
        if (chapterInfo.ifRealized()) {
            throw new ExceptionForClientTip("bypalace.already.realization");
        }
        if (chapterInfo.getStatus() == AwardStatus.LOCK.getValue()) {
            throw new ExceptionForClientTip("bypalace.chapter.lock");
        }
        int times = chapterInfo.gainExcludedNums() + excludeIds.size();
        CfgBYPalace cfgBYPalace = BYPalaceTool.getCfgBYPalace();
        if (times > cfgBYPalace.getExcludeSkillMaxTimes()) {
            throw ExceptionForClientTip.fromi18nKey("bypalace.exclude.out.of.times");
        }
        RDCommon rd = new RDCommon();
        times = Math.max(times - useTTLPNum * cfgBYPalace.getTtlpCreditNum(), 0);
        int needs = BYPalaceTool.getExcludesNeedGold(chapterInfo.gainExcludedNums() + 1, times);
        GameUser gu = gameUserService.getGameUser(uid);
        if (needs != 0) {
            ResChecker.checkGold(gu, needs);
            ResEventPublisher.pubGoldDeductEvent(uid, needs, WayEnum.BYPALACE_EXCLUDE_SKILL, rd);
        }
        if (useTTLPNum != 0) {
            TreasureChecker.checkIsEnough(TreasureEnum.TONG_TIAN_LING_PAI.getValue(), useTTLPNum, uid);
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.TONG_TIAN_LING_PAI.getValue(), useTTLPNum, WayEnum.BYPALACE_EXCLUDE_SKILL, rd);
        }

        chapterInfo.addExcludes(excludeIds);
        this.gameUserService.updateItem(userBYPalace);
        return rd;
    }

    /**
     * 获取奖励
     *
     * @param uid
     * @param chapterTypeValue
     * @param chapter
     * @return
     */
    public RDCommon getAward(long uid, int chapterTypeValue, int chapter) {
        if (chapterTypeValue == 0) {
            return this.getFinalAward(uid);
        }
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);
        ChapterType chapterType = ChapterType.fromValue(chapterTypeValue);
        ChapterInfo chapterInfo = userBYPalace.gainChapterInfo(chapterType, chapter);
        if (chapterInfo == null) {
            throw new ExceptionForClientTip("bypalace.unvalid.chapter");
        }
        if (chapterInfo.getStatus() < AwardStatus.ENABLE_AWARD.getValue()) {
            throw new ExceptionForClientTip("bypalace.not.realization");
        }
        if (chapterInfo.getStatus() == AwardStatus.AWARDED.getValue()) {
            throw new ExceptionForClientTip("bypalace.already.awarded");
        }
        List<Award> awards = chapterInfo.getAwards();
        //如果有要排除的技能，需重新生成
        if (chapterInfo.gainExcludedNums() > 0) {
            awards = byPalaceService.getChapterAwardToOutPut(uid, chapterType.getValue(), chapter, chapterInfo.getExcludes());
            chapterInfo.setAwards(awards);
        }
        RDCommon rd = new RDCommon();
        this.awardService.fetchAward(uid, awards, WayEnum.BYPALACE_GET_CHAPTER_AWARD, "", rd);
        chapterInfo.setStatus(AwardStatus.AWARDED.getValue());
        this.gameUserService.updateItem(userBYPalace);
        UserBYPalaceLockSkill lockSkill = gameUserService.getSingleItem(uid, UserBYPalaceLockSkill.class);
        if (lockSkill == null) {
            lockSkill = UserBYPalaceLockSkill.instance(uid);
            gameUserService.addItem(uid, lockSkill);
        }
        lockSkill.addSkill(awards.get(0).getAwardId(), chapterInfo.getChapter());
        gameUserService.updateItem(lockSkill);
        EPBiyouGainAward award = EPBiyouGainAward.instance(new BaseEventParam(uid), awards, chapterInfo.getChapter(), lockSkill.isNewSkill(awards.get(0).getAwardId(), chapterInfo.getChapter()));
        BiyouEventPublisher.pubGainAwardEvent(award);

        return rd;
    }

    /**
     * 获得真传奖励
     *
     * @param uid
     * @return
     */
    private RDCommon getFinalAward(long uid) {
        UserBYPalace userBYPalace = this.byPalaceService.getUserBYPalace(uid);

        if (userBYPalace.getFinalAwardStatus() < AwardStatus.ENABLE_AWARD.getValue()) {
            throw new ExceptionForClientTip("bypalace.not.realization");
        }
        if (userBYPalace.getFinalAwardStatus() == AwardStatus.AWARDED.getValue()) {
            throw new ExceptionForClientTip("bypalace.already.awarded");
        }
        RDCommon rd = new RDCommon();
        CfgBYPalace cfgBYPalace = BYPalaceTool.getCfgBYPalace();
        List<Award> awards = cfgBYPalace.getFinalAwards();
        this.awardService.fetchAward(uid, awards, WayEnum.BYPALACE_GET_FINAL_AWARD, "", rd);
        userBYPalace.setFinalAwardStatus(AwardStatus.AWARDED.getValue());
        this.gameUserService.updateItem(userBYPalace);
        return rd;
    }

    private RDChapter toRdChapter(int type, ChapterInfo chapter) {
        RDChapter rdChapter = new RDChapter();
        rdChapter.setType(type);
        rdChapter.setChapter(chapter.getChapter());
        rdChapter.setProgress(chapter.getProgress());
        if (ListUtil.isNotEmpty(chapter.getAwards())) {
            List<RDAward> rdAwards = chapter.getAwards().stream().map(tmp -> {
                int awardId = tmp.gainAwardId();
                int awardItem = tmp.getItem();
                CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(awardId);
                return new RDAward(treasureEntity.getId(), awardItem, treasureEntity.getType(), tmp.getNum());
            }).collect(Collectors.toList());
            rdChapter.setAwards(rdAwards);
        }
        rdChapter.setStatus(chapter.getStatus());
        return rdChapter;
    }

    /**
     * 解锁秘境
     *
     * @param userBYPalace
     */
    private void toUnlockSecretBiography(UserBYPalace userBYPalace, RDRealization rd) {
        List<ChapterInfo> chapterInfos = userBYPalace.getTypesChapters().get(ChapterType.SecretBiography.getName());
        int unlock = 0;
        for (ChapterInfo chapterInfo : chapterInfos) {
            if (chapterInfo.getStatus() != AwardStatus.LOCK.getValue()) {
                unlock++;
                continue;
            }
            int conditionGroup = chapterInfo.getConditionGroup();
            // 获得领悟进度
            CfgBYPalaceConditionEntity conditionEntity = BYPalaceTool.getConditionEntity(conditionGroup);
            List<Integer> contitionTypes = conditionEntity.getCondition();
            int realizatedNum = contitionTypes.stream().mapToInt(contitionType -> {
                List<ChapterInfo> typeChapters = userBYPalace.getTypesChapters().get(ChapterType.fromValue(contitionType).getName());
                Long realizationNum = typeChapters.stream().filter(tmp -> tmp.ifRealized()).count();
                return realizationNum.intValue();
            }).sum();
            if (realizatedNum >= conditionEntity.getValue()) {
                chapterInfo.setStatus(AwardStatus.UNAWARD.getValue());
                unlock++;
                BiyouEventPublisher.pubSecretBiographyUnlockEvent(userBYPalace.getGameUserId());
            }
        }
        rd.setSecretBiography(unlock);
    }

    /**
     * 获得真传奖励的状态
     *
     * @return
     */
    public void toUpdateFinalAwardStatus(UserBYPalace userBYPalace) {
        if (userBYPalace.getFinalAwardStatus() != AwardStatus.UNAWARD.getValue()) {
            return;
        }
        Long realizationNum = 0L;
        int needNum = 0;
        Set<String> chapterTypes = userBYPalace.getTypesChapters().keySet();
        for (String chapterTypeName : chapterTypes) {
            ChapterType chapterType = ChapterType.fromName(chapterTypeName);
            if (chapterType == ChapterType.SecretBiography) {
                continue;
            }
            List<ChapterInfo> chapterInfos = userBYPalace.getTypesChapters().get(chapterTypeName);
            needNum += chapterInfos.size();
            realizationNum += chapterInfos.stream().filter(tmp -> tmp.ifRealized()).count();
        }
        if (realizationNum >= needNum) {
            userBYPalace.setFinalAwardStatus(AwardStatus.ENABLE_AWARD.getValue());
        }
    }

}
