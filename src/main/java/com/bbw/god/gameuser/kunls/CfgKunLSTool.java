package com.bbw.god.gameuser.kunls;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.kunls.Enum.InfusionPositionEnum;
import com.bbw.god.gameuser.kunls.cfg.CfgKunLS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 昆仑山工具类
 *
 * @author: huanghb
 * @date: 2022/9/22 13:58
 */
public class CfgKunLSTool {
    /**
     * 获取昆仑山配置
     *
     * @return
     */
    public static CfgKunLS getKunLSConfig() {
        return Cfg.I.getUniqueConfig(CfgKunLS.class);
    }

    /**
     * 获取昆仑山图纸品质对应的法宝id
     *
     * @return
     */
    public static Integer getMapTreasureIdByQuality(Integer quality) {
        return getKunLSConfig().getMapTreasureIds().get(quality);
    }

    /**
     * 获取至宝id对应的至宝胚法宝id
     *
     * @return
     */
    public static Integer getEmbryoTreasureIds(Integer zhiBaoId) {
        return getKunLSConfig().getEmbryoTreasureIds().get(zhiBaoId);
    }

    /**
     * 获取所有至宝类型
     *
     * @return
     */
    public static List<Integer> getAllZhiBaoType() {
        return getKunLSConfig().getEmbryoTreasureIds().keySet().stream().collect(Collectors.toList());
    }

    /**
     * 获取注灵需要法宝id
     *
     * @return
     */
    public static Integer getInfusionNeedTreasureId() {
        return getKunLSConfig().getInfusionNeedTreasureId();
    }

    /**
     * 获取提炼信息
     *
     * @return
     */
    public static CfgKunLS.Refine getRefine(Integer embryoType) {
        CfgKunLS cfgKunLS = getKunLSConfig();
        List<CfgKunLS.Refine> refines = cfgKunLS.getRefines();
        CfgKunLS.Refine refine = refines.stream().filter(tmp -> tmp.getId().equals(embryoType)).findFirst().orElse(null);
        return refine;
    }

    /**
     * 获取属性偏向材料数量上限
     *
     * @return
     */
    public static Integer getPropertyTrendMaterialNumLimit() {
        return getKunLSConfig().getPropertyTrendMaterialNumLimit();
    }

    /**
     * 获取注灵需要法宝id
     *
     * @return
     */
    public static Integer getInfusionNeedTreasureNum(int infusionTimes) {
        return getKunLSConfig().getInfusionNeedTreasureNum().get(infusionTimes);
    }

    /**
     * 获取属性偏向概率
     *
     * @return
     */
    public static List<Integer> getPropertyTrendProbs() {
        return getKunLSConfig().getPropertyTrendProbs();
    }

    /**
     * 根据材料随机获得至宝属性类别
     *
     * @param optionalsMap 材料id=》材料数量   本源材料 都没有传空,否则按金木水火土传递参数，没有材料传0
     * @return
     */
    public static Integer getPropertyTrendProbs(Map<Integer, Integer> optionalsMap) {
        //获得材料类别上限
        int typeLimit = getPropertyTypeLimit();
        int propertyTypeNum = (int) optionalsMap.values().stream().filter(tmp -> tmp.intValue() != 0).count();
        //如果材料类别超过上限
        if (typeLimit < propertyTypeNum) {
            throw new ExceptionForClientTip("kunLs.infusion.propertyType.limit");
        }
        //获得属性倾向概率
        List<Integer> propertyTrendProbs = CloneUtil.cloneList(getKunLSConfig().getPropertyTrendProbs());
        //未使用材料
        if (0 == propertyTypeNum) {
            int propertyIndex = PowerRandom.hitProbabilityIndex(propertyTrendProbs);
            return TypeEnum.fromValue((propertyIndex + 1) * 10).getValue();
        }
        //总使用材料数量
        List<Integer> optionalsMapKeys = optionalsMap.keySet().stream().collect(Collectors.toList());
        int treasureNum = optionalsMap.values().stream().mapToInt(Integer::intValue).sum();
        //根据使用材料数量执行方法
        for (int i = 0; i < optionalsMapKeys.size(); i++) {
            //获得材料数量
            int prob = propertyTrendProbs.get(i);
            int materialNum = optionalsMap.get(optionalsMapKeys.get(i));
            //没有使用材料（报空其他属性有使用，但该属性未使用）
            if (0 == materialNum) {
                prob = prob - 5 * treasureNum;
                propertyTrendProbs.set(i, prob);
                continue;
            }
            //使用材料且只使用一种材料
            boolean isUserOneMaterial = 0 != materialNum && 1 == propertyTypeNum;
            if (isUserOneMaterial) {
                prob = prob + 20 * treasureNum;
                propertyTrendProbs.set(i, prob);
                continue;
            }
            //使用两种以上材料
            prob = prob + 15 * treasureNum;
            propertyTrendProbs.set(i, prob);
            continue;
        }
        int propertyIndex = PowerRandom.hitProbabilityIndex(propertyTrendProbs);
        return TypeEnum.fromValue((propertyIndex + 1) * 10).getValue();
    }

    /**
     * 属性类型上限(如 数值为2 即只能选择 金木，木水这样只有两种属性)
     *
     * @return
     */
    public static Integer getPropertyTypeLimit() {
        return getKunLSConfig().getPropertyTypeLimit();
    }

    /**
     * 获得注灵上限
     *
     * @return
     */
    public static Integer getInfusionTimesLimit() {
        return getKunLSConfig().getInfusionTimesLimit();
    }


    /**
     * 获得炼制室所有炼制信息配置
     *
     * @return
     */
    private static List<CfgKunLS.MakingInfo> getMakingInfos() {
        CfgKunLS cfgKunLS = getKunLSConfig();
        return cfgKunLS.getMakingInfos();
    }

    /**
     * 根据图纸品质和牺牲法宝的品质获得炼制信息
     *
     * @param quality
     * @param sacrificeZhiBaoQuality
     * @return
     */
    public static CfgKunLS.MakingInfo getMakingInfo(Integer quality, Integer sacrificeZhiBaoQuality) {
        List<CfgKunLS.MakingInfo> makingInfos = getMakingInfos();
        //根据图纸品质和献祭法宝的品质获得炼制信息 图纸品质and 献祭法宝品质=》炼制信息
        CfgKunLS.MakingInfo makingInfo = makingInfos.stream()
                .filter(tmp -> tmp.getQuality().equals(quality) && tmp.getSacrificeZhiBaoQuality().equals(sacrificeZhiBaoQuality))
                .findFirst().orElse(null);
        if (null != makingInfo) {
            return makingInfo;
        }
        throw CoderException.high(String.format("没有配置图纸品质={}且牺牲至宝品质={}的配置", quality, sacrificeZhiBaoQuality));
    }

    /**
     * 根据至宝胚类型获得属性
     *
     * @param embryoType
     * @return
     */
    public static List<CardEquipmentAddition> getInfusionProperty(Integer embryoType) {
        CfgKunLS.Property property = getProperty(embryoType);
        if (null == property) {
            throw CoderException.high(String.format("没有配置embryoType={}的至宝配置", embryoType));
        }
        return randomInfusionProperty(property, embryoType);
    }

    /**
     * 根据至宝类型获得注灵属性
     *
     * @param embryoType
     * @return
     */
    public static CfgKunLS.Property getProperty(Integer embryoType) {
        List<CfgKunLS.Property> properties = getKunLSConfig().getInfusions();
        //获得至宝id对应的攻防技能信息
        return properties.stream().filter(tmp -> tmp.getId().equals(embryoType)).findFirst().orElse(null);
    }

    /**
     * 随机生成装备技能
     *
     * @param zhiBaoId
     * @return
     */
    public static Integer randomInfusionSkill(Integer zhiBaoId, Integer infusionPosition) {
        CfgKunLS.Property property = getProperty(zhiBaoId);
        if (null == property) {
            throw CoderException.high(String.format("没有配置id={}的至宝配置", zhiBaoId));
        }
        //注灵位置2生成技能1
        if (InfusionPositionEnum.INFUSION_POSITION_TWO.getValue() == infusionPosition) {
            List<Integer> skillGroup = property.getSkillGroupOne();
            return PowerRandom.getRandomFromList(skillGroup);
        }
        //注灵位置三生成技能2
        if (InfusionPositionEnum.INFUSION_POSITION_THREE.getValue() == infusionPosition) {
            List<Integer> skillGroup = property.getSkillGroupTwo();
            return PowerRandom.getRandomFromList(skillGroup);
        }
        throw new ExceptionForClientTip("skill.is.not.exist");
    }

    /**
     * 随机生成至宝属性
     *
     * @param property
     * @param zhiBaoId
     * @return
     */
    private static List<CardEquipmentAddition> randomInfusionProperty(CfgKunLS.Property property, Integer zhiBaoId) {
        List<CardEquipmentAddition> additions = new ArrayList<>();
        //攻击信息不为空，添加攻击值
        if (null != property.getMinAttack() && null != property.getMaxAttack()) {
            int attackValue = PowerRandom.getRandomBetween(property.getMinAttack(), property.getMaxAttack());
            additions.add(new CardEquipmentAddition(CardEquipmentAdditionEnum.ATTACK.getValue(), attackValue));
        }
        //防御值不为空，添加防御值
        if (null != property.getMinDefense() && null != property.getMinDefense()) {
            int defenseValue = PowerRandom.getRandomBetween(property.getMinDefense(), property.getMaxDefense());
            additions.add(new CardEquipmentAddition(CardEquipmentAdditionEnum.DEFENSE.getValue(), defenseValue));
        }
        //凡品至宝只有攻防，直接返回
        Integer quality = zhiBaoId % 100;
        if (QualityEnum.FAN_PIN.getValue() == quality) {
            return additions;
        }
        //强度值不为空，添加强度值
        if (null != property.getMinStrength() && null != property.getMaxStrength()) {
            int strengthValue = PowerRandom.getRandomBetween(property.getMinStrength(), property.getMaxStrength());
            additions.add(new CardEquipmentAddition(CardEquipmentAdditionEnum.STRENGTH.getValue(), strengthValue));
        }
        //韧度值不为空，添加韧度值
        if (null != property.getMinTenacity() && null != property.getMaxTenacity()) {
            int tenacityValue = PowerRandom.getRandomBetween(property.getMinTenacity(), property.getMaxTenacity());
            additions.add(new CardEquipmentAddition(CardEquipmentAdditionEnum.TENACITY.getValue(), tenacityValue));
        }
        return additions;
    }
}
