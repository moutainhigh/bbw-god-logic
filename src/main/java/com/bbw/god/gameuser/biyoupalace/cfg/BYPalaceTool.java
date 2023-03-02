package com.bbw.god.gameuser.biyoupalace.cfg;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 碧游宫配置读取工具类
 *
 * @author suhq
 * @date 2019-09-06 16:44:06
 */
@Slf4j
public class BYPalaceTool {

    /**
     * 获得碧游宫参数配置类
     *
     * @return
     */
    public static CfgBYPalace getCfgBYPalace() {
        return Cfg.I.getUniqueConfig(CfgBYPalace.class);
    }

    public static List<Integer> getTongTCJEffects() {
        return ListUtil.copyList(getCfgBYPalace().getTongTCJEffects(), Integer.class);
    }

    /**
     * 根据奖励名称获得奖励对象
     *
     * @param name
     * @return
     */
    public static Award getChapterAward(String name) {
        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureByName(name);
        if (treasureEntity == null) {
            throw CoderException.high("不存在的奖励:" + name);
        }
        return new Award(treasureEntity.getId(), AwardEnum.FB, 1);
    }

    /**
     * 从配置中读取章节奖励
     *
     * @param type
     * @param chapter
     * @return
     */
    public static Award getChapterAward(int type, int chapter) {
//		System.out.println(type + "," + chapter);
        CfgBYPalaceSkillEntity byPalaceSkillEntity = getBYPSkillEntity(type, chapter);
        CfgBYPalaceChapterEntity chapterEntity = getChapterEntity(chapter);
        List<Integer> probs = Arrays.asList(chapterEntity.getSkillScrollProb(), chapterEntity.getTypeSymbolProb(), chapterEntity.getUniversalSymbolProb());
//		System.out.println(probs);
        int index = PowerRandom.getIndexByProbs(probs, 100);
//		System.out.println(index);
        String awardName = "";
        switch (index) {
            case 1:
                awardName = PowerRandom.getRandomFromList(chapterEntity.getSymbols());
                break;
            case 2:
                awardName = TreasureEnum.WangNFL.getName();
                break;
            default:
                if (ChapterType.SecretBiography.getValue() == type) {
                    //秘传
                    List<CfgBYPalaceWeightEntity> weightEntities = getWeightEntity(type);
                    List<CfgBYPalaceWeightEntity> weightValid = weightEntities.stream().filter(p -> byPalaceSkillEntity.getSkills().contains(p.getName())).collect(Collectors.toList());
                    int sumSeed = weightValid.stream().collect(Collectors.summingInt(CfgBYPalaceWeightEntity::getWeight));
                    List<Integer> weight = weightValid.stream().map(CfgBYPalaceWeightEntity::getWeight).collect(Collectors.toList());
                    int indexByWeight = PowerRandom.getIndexByProbs(weight, sumSeed);
                    awardName = weightValid.get(indexByWeight).getName();
                } else {
                    awardName = PowerRandom.getRandomFromList(byPalaceSkillEntity.getSkills());
                }
                break;
        }
        return getChapterAward(awardName);
    }

    /**
     * 获得章节信息
     *
     * @param type
     * @param chapter
     * @return
     */
    public static CfgBYPalaceSkillEntity getBYPSkillEntity(int type, int chapter) {
        int chapterSkillAwardId = type * 10 + chapter;
        CfgBYPalaceSkillEntity byPalaceSkillEntity = Cfg.I.get(chapterSkillAwardId, CfgBYPalaceSkillEntity.class);
        return byPalaceSkillEntity;
    }

    /**
     * 获取所有技能卷轴篇
     *
     * @return
     */
    public static List<CfgBYPalaceSkillEntity> getBYPSkillEntityList() {
        return Cfg.I.get(CfgBYPalaceSkillEntity.class);
    }

    /**
     * 获取所有指定等级篇幅的技能卷轴篇
     *
     * @return
     */
    public static List<CfgBYPalaceSkillEntity> getBYPSkillEntityList(int chapter) {
        return getBYPSkillEntityList().stream().filter(p -> p.getChapter() == chapter).collect(Collectors.toList());
    }

    /**
     * 随机获取指定篇幅的中的技能卷轴
     *
     * @param chapter
     * @return
     */
    public static int randomChapterSkillScrollId(int chapter, List<String> ignore) {
        List<CfgBYPalaceSkillEntity> entityList = getBYPSkillEntityList(chapter);
        CfgBYPalaceSkillEntity random = PowerRandom.getRandomFromList(entityList);
        List<String> list = new ArrayList<>();
        for (String name : random.getSkills()) {
            if (ignore.contains(name)) {
                continue;
            }
            list.add(name);
        }
        String skillScrollName = PowerRandom.getRandomFromList(list);
        return TreasureTool.getTreasureByName(skillScrollName).getId();
    }

    public static CfgBYPalaceSkillEntity getBYPSkillEntity(String skillName) {
        List<CfgBYPalaceSkillEntity> list = getBYPSkillEntityList();
        for (CfgBYPalaceSkillEntity skillEntity : list) {
            if (skillEntity.getSkills().contains(skillName)) {
                return skillEntity;
            }
        }
        return null;
    }

    /**
     * 获得篇配置信息
     *
     * @param chapter
     * @return
     */
    public static CfgBYPalaceChapterEntity getChapterEntity(int chapter) {
        return Cfg.I.get(chapter, CfgBYPalaceChapterEntity.class);
    }

    /**
     * 获得符箓配置信息
     *
     * @param symbol
     * @return
     */
    public static CfgBYPalaceSymbolEntity getSymbolEntity(int symbol) {
        return Cfg.I.get(symbol, CfgBYPalaceSymbolEntity.class);
    }

    /**
     * 获取技能权重
     *
     * @param chapterType
     * @return
     */
    public static List<CfgBYPalaceWeightEntity> getWeightEntity(int chapterType) {
        List<CfgBYPalaceWeightEntity> list = Cfg.I.get(CfgBYPalaceWeightEntity.class);
        return list.stream().filter(p -> p.getChapterType() == chapterType).collect(Collectors.toList());
    }

    /**
     * 获得符箓加成值
     *
     * @param symbol
     * @return
     */
    public static int getSymbolEffect(int symbol) {
        if (symbol == 0) {
            return 0;
        }
        CfgBYPalaceSymbolEntity entity = getSymbolEntity(symbol);
        if (entity != null) {
            return entity.getEffect();
        }
        return 0;
    }

    /**
     * 获得下一级符箓
     *
     * @param symbol
     * @return
     */
    public static CfgBYPalaceSymbolEntity getNextSymbolEntity(int symbol) {
        CfgBYPalaceSymbolEntity symbolEntity = getSymbolEntity(symbol);
        final int color = Color.fromName(symbolEntity.getColor()).getValue();
        List<CfgBYPalaceSymbolEntity> symbolEntities = Cfg.I.get(CfgBYPalaceSymbolEntity.class);
        CfgBYPalaceSymbolEntity next = symbolEntities.stream().filter(tmp -> tmp.getType().intValue() == symbolEntity.getType() && Color.fromName(tmp.getColor()).getValue() > color).findFirst().orElse(null);
        return next;
    }

    /**
     * 获得默认状态
     *
     * @param chapterType
     * @return
     */
    public static AwardStatus getDefaultChapterStatus(ChapterType chapterType) {
        AwardStatus awardStatus = AwardStatus.UNAWARD;
        if (chapterType == ChapterType.SecretBiography) {
            awardStatus = AwardStatus.LOCK;
        }
        return awardStatus;
    }

    /**
     * 获得领悟需要的通天残卷数
     *
     * @param chapter
     * @return
     */
    public static int getNeedTongTCJ(int chapter) {
        CfgBYPalaceChapterEntity chapterEntity = BYPalaceTool.getChapterEntity(chapter);
        int need = PowerRandom.getRandomBetween(chapterEntity.getMinTongTCJ(), chapterEntity.getMaxTongTCJ());
        return need;
    }

    public static CfgBYPalaceConditionEntity getConditionEntity(int id) {
        return Cfg.I.get(id, CfgBYPalaceConditionEntity.class);
    }

    /**
     * 获取限定的技能卷轴id
     *
     * @return
     */
    public static List<Integer> getUniqueSkillScrollIds() {
        return Arrays.asList(21124, 21218, 21214);
    }

    /**
     * 获取秘传筛选需要消耗的数量
     *
     * @param from
     * @return
     */
    public static int getExcludesNeedGold(int from, int to) {
        CfgBYPalace cfg = getCfgBYPalace();
        List<CfgBYPalace.ExcludeSkillPrice> prices = cfg.getExcludeSkillPrices();
        return prices.stream().filter(tmp -> tmp.getTimes() >= from && tmp.getTimes() <= to)
                .mapToInt(CfgBYPalace.ExcludeSkillPrice::getNeedGold).sum();
    }

    /**
     * 获得碧游宫专属技能配置
     *
     * @return
     */
    public static List<CfgBYPalaceExclusiveSkillEntity> getBYPalaceExclusiveSkillEntity() {
        return Cfg.I.get(CfgBYPalaceExclusiveSkillEntity.class);
    }

    /**
     * 获得碧游宫专属技能
     *
     * @return
     */
    public static List<Integer> getBYPalaceExclusiveSkills() {
        return getBYPalaceExclusiveSkillEntity().stream().map(CfgBYPalaceExclusiveSkillEntity::getSkillId).collect(Collectors.toList());
    }
}
