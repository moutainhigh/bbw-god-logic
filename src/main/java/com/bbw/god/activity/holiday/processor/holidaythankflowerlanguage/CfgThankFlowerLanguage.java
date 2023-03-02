package com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 感恩花语活动配置类
 *
 * @author: huanghb
 * @date: 2022/11/16 10:42
 */
@Data
public class CfgThankFlowerLanguage implements CfgInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 花盆数量 */
    private Integer flowerpotNum;
    /** 种子包 */
    private List<Integer> seedBags;
    /** 种子包信息 */
    private List<SeedBagInfo> seedBagInfos;
    /** 成花信息 */
    private List<FlowerInfo> flowerInfos;
    /** 肥料 */
    private List<Integer> fertilizers;
    /** 花束信息 */
    private List<BouquetInfo> bouquetInfos;
    /** 花朵=》等级 */
    private Map<Integer, Integer> flowerLevels;
    /** 每花影响花束概率 */
    private Integer perFlowerEfferBouquetPro;
    /** 每花影响花束概率 */
    private List<Integer> flowers;

    @Data
    public static class SeedBagInfo implements Serializable {
        private static final long serialVersionUID = -8102266118862887471L;
        /** 种子包等级 */
        private Integer id;
        /** 种子信息 */
        private List<Integer> seeds;
    }

    @Data
    public static class FlowerInfo implements Serializable {
        private static final long serialVersionUID = -4589450579679794700L;
        /** 编号 */
        private Integer id;
        /** 种子id */
        private Integer seedId;
        /** 成花数量 */
        private Integer flowerNum;
        /** 需要肥料 */
        public Integer needFertilizer;
        /** 成花id */
        private Integer flowerId;

        public boolean isMatch(int flowerpotInfo) {
            return this.id / 100 == flowerpotInfo / 100;
        }
    }

    @Data
    public static class BouquetInfo implements Serializable {
        private static final long serialVersionUID = 8796475464809202539L;
        /** 编号 */
        private Integer id;
        /** 花束等级 */
        private Integer bouquetLevel;
        /** 成花id(仅所有花朵id一至才有id) */
        private Integer flowerId;
        /** 花束id */
        private List<Integer> bouquets;
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
