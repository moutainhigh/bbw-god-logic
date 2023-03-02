package com.bbw.god.activity.holiday.config;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.cfg.CfgCuteTigerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 暑气来袭
 *
 * @author: huanghb
 * @date: 2022/7/7 15:34
 */
@Data
public class CfgHolidaySummerHeat implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 触发野怪宝箱概率 */
    private int yeGuaiTriggerPro;
    /** 野怪宝箱概率产出 */
    private List<Award> yeGuaiBoxOutPut;
    /** 村庄触发概率 */
    private int cunZTriggerPro;
    /** 村庄产出 */
    private List<Award> cunZOutPut;
    /** 特产触发概率 */
    private int specialsTriggerPro;
    /** 特产产出 */
    private List<Award> specialsOutPut;
    /** 最大触发野地事件次数 */
    private int maxTriggerspecialYeDiTimes;
    /** 活动特殊野地事件概率 */
    private int specialYeDiEventPro;
    /** 糕点初始售出价格 */
    private List<CfgCuteTigerEntity.InitialSellingPrice> initialSellingPrice;
    ;

    @Data
    public static class InitialSellingPrice {
        /** 法宝id */
        private int treasureId;
        /** 价格 */
        private int price;
    }

    public static CfgHolidaySummerHeat getCfg() {
        return Cfg.I.getUniqueConfig(CfgHolidaySummerHeat.class);
    }

    /**
     * 根据概率随机获得奖励
     *
     * @param
     * @return
     */
    public static Award randomAwardByProb(List<Award> outPut) {
        List<Integer> probs = outPut.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return outPut.get(awardIndex);
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
