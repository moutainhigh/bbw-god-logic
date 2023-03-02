package com.bbw.god.gameuser.biyoupalace.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 碧游宫配置
 *
 * @author suhq
 * @date 2019-09-06 17:13:00
 */
@Data
public class CfgBYPalace implements CfgInterface, CfgPrepareListInterface {

    private String key;
    private Boolean isOpen;// 是否开放
    private Integer tongTCJPrice;
    private List<Integer> tongTCJEffects;// 单个通天残卷可能加值
    private Integer realizationTime;// 领悟时间（ms）
    private Integer resetByPalaceGold;// 重置碧游宫元宝
    private List<Award> finalAwards;
    private List<List<Integer>> possibleConditionGroups;// 可能的条件组合
    //秘传技能筛选最大次数（计算生成）
    private Integer excludeSkillMaxTimes;
    //秘传技能筛选价格
    private List<ExcludeSkillPrice> excludeSkillPrices;
    private Integer excludeSkillNeedTTLP;
    private List<TypeAward> firstInit;// 第一次初始化碧游宫
    /** 通天令牌抵用秘传数量 */
    private Integer ttlpCreditNum;

    @Override
    public void prepare() {
        excludeSkillMaxTimes = excludeSkillPrices.stream().mapToInt(ExcludeSkillPrice::getTimes).max().getAsInt();
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class TypeAward {
        private String type;
        List<Integer> chapters;
        private List<Integer> conditionGroups;
        private List<String> chapterAwards;
    }

    @Data
    public static class ExcludeSkillPrice {
        private Integer times;
        private Integer needGold;
    }
}
