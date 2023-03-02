package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 藏宝秘境活动配置类
 *
 * @author: huanghb
 * @date: 2021/12/15 17:37
 */
@Data
public class CfgTreasureTrove implements CfgInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /*刷新藏宝秘境需要的元宝*/
    private Integer refreshNeedGold;
    /*奖池总概率*/
    private Integer propSum;
    /*藏宝值上限*/
    private Integer troveValueLimit;
    /*大奖池*/
    private List<TroveAward> bigAwardPool;
    /** 常规大奖池的概率 */
    private Integer bigAwardPoolProbability;
    /*普通奖池*/
    private List<TroveAward> normalAwardPool;
    /** 概率衰减 */
    private List<ProbDecay> probDecays;
    /** 保底信息 */
    private List<GuaranteeInformation> guaranteeInformations;

    @Data
    public static class TroveAward implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        /*是否大奖*/
        private Boolean bigAward;
        /*大奖概率*/
        private Integer probability;
        /** 是否概率衰减 */
        private Boolean isProbDecays;

        private void addProb(int prob) {
            this.probability += prob;
        }

        private void deductProb(int prob) {
            this.probability -= prob;
        }
    }

    @Data
    public static class ProbDecay implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 旧的法宝id */
        private Integer oldTreasureId;
        /*新的法宝id*/
        private Integer newTreasureId;
        /*转移概率*/
        private Integer prob;
        /** 购买次数 */
        private Integer buyTimes;
    }

    @Data
    public static class GuaranteeInformation implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 保底次数 */
        private Integer guaranteedTimes;
        /*法宝id*/
        private Integer treasureId;
        /*奖池奖励id*/
        private Integer awardPoolId;
    }

    protected List<TroveAward> probDecays(Map<Integer, Integer> allAwardedNum) {
        List<TroveAward> newAwardPool = CloneUtil.cloneList(this.bigAwardPool);
        if (MapUtil.isEmpty(allAwardedNum)) {
            return newAwardPool;
        }
        for (TroveAward troveAward : newAwardPool) {
            if (!troveAward.isProbDecays) {
                continue;
            }
            if (!allAwardedNum.keySet().contains(troveAward.getId())) {
                continue;
            }
            int buyTimes = allAwardedNum.get(troveAward.getId());
            this.probDecays = this.probDecays.stream().sorted(Comparator.comparing(ProbDecay::getBuyTimes)).collect(Collectors.toList());
            List<ProbDecay> probDecays = this.probDecays.stream().filter(tmp -> tmp.getBuyTimes() <= buyTimes && tmp.getOldTreasureId().equals(troveAward.getId())).collect(Collectors.toList());
            if (ListUtil.isEmpty(probDecays)) {
                continue;
            }
            int probDecayValue = probDecays.stream().mapToInt(ProbDecay::getProb).sum();
            troveAward.deductProb(probDecayValue);
            TroveAward newtroveAward = newAwardPool.stream().filter(tmp -> tmp.getId().equals(probDecays.get(0).getNewTreasureId())).findFirst().orElse(null);
            newtroveAward.addProb(probDecayValue);
        }
        return newAwardPool;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
