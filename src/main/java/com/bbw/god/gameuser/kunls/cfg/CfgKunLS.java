package com.bbw.god.gameuser.kunls.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 昆仑山配置
 *
 * @author: huanghb
 * @date: 2022/9/15 10:48
 */
@Data
public class CfgKunLS implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 8194020774360554882L;
    private String key;
    /** 注灵次数上限 */
    private Integer infusionTimesLimit;
    /** 属性偏向概率 */
    private List<Integer> propertyTrendProbs;
    /** 图纸的法宝id集合 图纸品质=》法宝id */
    private Map<Integer, Integer> mapTreasureIds;
    /** 至宝胚对应id集合 至宝id=》至宝胚法宝id */
    private Map<Integer, Integer> embryoTreasureIds;
    /** 属性类型上限(如 数值为2 即只能选择 金木，木水这样只有两种属性) */
    private Integer propertyTypeLimit;
    /** 注灵需要法宝id */
    private Integer infusionNeedTreasureId;
    /** 属性偏向材料数量上限 */
    private Integer propertyTrendMaterialNumLimit;
    /** 注灵需要法宝数量 */
    private List<Integer> infusionNeedTreasureNum;
    /** 灵宝炼制信息 */
    private List<MakingInfo> makingInfos;
    /** 注灵信息 */
    private List<Property> infusions;
    /** 提炼 */
    private List<Refine> refines;

    @Data
    public static class MakingInfo {
        private Integer quality;
        /** 初始概率 */
        private Integer initProb;
        /** 名称 */
        private String name;
        /** 是否需要上一级灵宝献祭 */
        private Boolean isNeedSacrifice;
        /** 献祭至宝的品质，0表示没有 */
        private Integer sacrificeZhiBaoQuality;
        /** 是否随机材料(仅在必选材料中随机) */
        private Boolean isRandomEssential;
        /** 必选材料 */
        private List<Integer> essentials;
        /** 材料数量 */
        private Integer essentialNum;
        /** 可选材料 */
        private List<Integer> optionals = null;
        /** 可选材料数量 */
        private List<Integer> optionalNum;
        /** 升品材料 */
        private List<Integer> upgradeMaterials = null;
        /** 生品材料数量 */
        private List<Integer> upgradeMaterialNums;
        /** 产出法宝 */
        private List<Integer> outPuts;
    }


    /**
     * 获取配置项到ID值
     *
     * @return
     */
    @Override
    public Serializable getId() {
        return key;
    }

    /**
     * 获取排序号
     *
     * @return
     */
    @Override
    public int getSortId() {
        return 1;
    }


    @Data
    public static class Property {
        /** 数据id */
        private Integer id;
        /** 名称 */
        private String name;
        /** 最小攻击 */
        private Integer minAttack;
        /** 最大攻击 */
        private Integer maxAttack;
        /** 最小防御 */
        private Integer minDefense;
        /** 最大防御 */
        private Integer maxDefense;
        /** 最小韧度 */
        private Integer minTenacity;
        /** 最大韧度 */
        private Integer maxTenacity;
        /** 最小强度 */
        private Integer minStrength;
        /** 最大强度 */
        private Integer maxStrength;
        /** 技能组一 */
        private List<Integer> skillGroupOne;
        /** 技能组二 */
        private List<Integer> skillGroupTwo;

    }

    @Data
    public static class Refine {
        /** 数据id */
        private Integer id;
        /** 至宝类型 */
        private Integer type;
        /** 名称 */
        private String name;
        /** 返还材料 */
        private List<ReturnMaterial> returnMaterials;


    }

    @Data
    public static class ReturnMaterial {
        /** 返还材料法宝id */
        private Integer treasureId;
        /** 材料最小数量 */
        private Integer minNum;
        /** 材料最大数量 */
        private Integer maxNum;
    }
}
