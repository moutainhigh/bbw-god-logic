package com.bbw.god.activity.holiday.config;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节日锦鲤祈愿相关配置
 *
 * @author: huanghb
 * @date: 2022/9/16 15:20
 */
@Data
public class CfgHolidayKoiPray implements CfgEntityInterface, Serializable {
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
    /** 活动法宝ids */
    private List<Integer> treasureIds;
    /** 祈愿倍率 */
    private List<PrayMultiple> prayMultiples;
    /** 锦鲤兑换基数 */
    private Map<Integer, Integer> koiExchangeBases;
    /** 初始售出价格 */
    private List<InitialSellingPrice> initialSellingPrice;
    ;

    @Data
    public static class InitialSellingPrice {
        /** 法宝id */
        private int treasureId;
        /** 价格 */
        private int price;
    }


    ;

    @Data
    public static class PrayMultiple {
        /** 倍数 */
        private int multiple;
        /** 概率 */
        private int prob;
    }

    public static CfgHolidayKoiPray getCfg() {
        return Cfg.I.getUniqueConfig(CfgHolidayKoiPray.class);
    }

    /**
     * 根据概率随机获得倍数
     *
     * @param
     * @return
     */
    public static Integer randomMultipleByProb() {
        List<PrayMultiple> prayMultiplea = getCfg().prayMultiples;
        List<Integer> probs = prayMultiplea.stream().map(PrayMultiple::getProb).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return prayMultiplea.get(awardIndex).getMultiple();
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
