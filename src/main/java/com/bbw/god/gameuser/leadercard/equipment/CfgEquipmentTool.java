package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.common.CloneUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * 装备工具类
 *
 * @author suhq
 * @date 2021-03-26 14:34
 **/
public class CfgEquipmentTool {

    /**
     * 获取装备配置
     *
     * @return
     */
    private static CfgEquipment getEquipmentConfig() {
        return Cfg.I.getUniqueConfig(CfgEquipment.class);
    }

    public static CfgEquipmentEntity getEquipment(int equipmentId) {
        CfgEquipmentEntity equipmentEntity = getEquipments().stream().filter(tmp -> tmp.getEquipmentId() == equipmentId).findFirst().orElse(null);
        return equipmentEntity;
    }

    public static List<CfgEquipmentEntity> getEquipments() {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        return CloneUtil.cloneList(equipmentConfig.getEquipments());
    }

    public static List<Addition> getLevelAddition(int level, int quality, int starProgress) {
        // 等级加成
        List<Addition> levelAdditions = CfgEquipmentTool.getEquipmentLevelAddition(level);
        //星图加成处理
        double starAddition = getStarAddition(quality, starProgress);
        levelAdditions.forEach(levelAddition -> levelAddition.setValue((int) (levelAddition.getValue() * (1 + starAddition))));
        return levelAdditions;
    }

    private static double getStarAddition(int quality, int starProgress) {
        int qualityParam = quality;
        int starParam = starProgress;
        if (quality == CfgEquipmentTool.getMaxQuality() || (quality > 10 && starProgress == 0)) {
            qualityParam -= 10;
            starParam = CfgEquipmentTool.getNeedStarNum();
        }

        CfgEquipmentStarMap starMap = CfgEquipmentTool.getEquipmentStarMap(qualityParam, starParam);
        if (starMap == null) {
            return 0;
        }
        double starAddition = starMap.getAdditionRatio();
        return starAddition;
    }

    /**
     * 获取装备等级限制
     *
     * @param quality
     * @return
     */
    public static int getLevelLimit(int quality) {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        return equipmentConfig.getQualityLevelLimits().get(quality);
    }

    /**
     * 获取最大等级限制
     *
     * @return
     */
    public static int getMaxLevelLimit() {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        return equipmentConfig.getMaxLevel();
    }

    /**
     * 获得最好的品质
     *
     * @return
     */
    public static int getMaxQuality() {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        return equipmentConfig.getMaxQuality();
    }

    /**
     * 获取所需的星图进度
     *
     * @return
     */
    public static int getNeedStarNum() {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        return equipmentConfig.getStarNum();
    }

    /**
     * 获取等级对应的强化配置信息
     *
     * @param level
     * @return
     */
    public static CfgEquipmentStrengthen getEquipmentStrengthen(int level) {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        CfgEquipmentStrengthen equipmentStrengthen = equipmentConfig.getEquipmentStrengthens().stream().filter(tmp -> tmp.isMatch(level)).findFirst().orElse(null);
        if (null == equipmentStrengthen) {
            throw CoderException.high("装备等级" + level + "强化信息没有配置到 装备属性配置.yml");
        }
        return CloneUtil.clone(equipmentStrengthen);
    }

    /**
     * 获取装备位置
     *
     * @param equipmentId
     * @return
     */
    public static int getEquipmentPosition(int equipmentId) {
        CfgEquipmentEntity equipment = getEquipment(equipmentId);
        return equipment.getPosition();
    }

    /**
     * 获得装备基本加成
     *
     * @param equipmentId
     * @return
     */
    public static List<Addition> getBaseEquipmentAddition(int equipmentId) {
        CfgEquipmentEntity equipment = getEquipment(equipmentId);
        return CloneUtil.cloneList(equipment.getAdditions());
    }

    /**
     * 获得装备等级加成
     *
     * @param level
     * @return
     */
    public static List<Addition> getEquipmentLevelAddition(int level) {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        return CloneUtil.cloneList(equipmentConfig.getEquipmentLevelAdditions().get(level));
    }

    /**
     * 获取星图配置
     *
     * @param quality
     * @param star
     * @return
     */
    public static CfgEquipmentStarMap getEquipmentStarMap(int quality, int star) {
        CfgEquipment equipmentConfig = getEquipmentConfig();
        int index = quality * 10 + star;
        return CloneUtil.clone(equipmentConfig.getEquipmentStarMaps().get(index));
    }
}
