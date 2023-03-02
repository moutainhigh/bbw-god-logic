package com.bbw.god.gameuser.card.equipment;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.cfg.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仙诀工具类
 *
 * @author: huanghb
 * @date: 2022/9/17 9:24
 */
public class CfgXianJueTool {

    /**
     * 获取仙诀配置
     *
     * @return
     */
    public static CfgXianJue getXianJueConfig() {
        return Cfg.I.getUniqueConfig(CfgXianJue.class);
    }

    /**
     * 获得仙诀信息
     *
     * @param xianJueType
     * @return
     */
    public static CfgXianJueEntity getXianJue(int xianJueType) {
        CfgXianJueEntity xianJueEntity = getXianJues().stream().filter(tmp -> tmp.getXianJueType() == xianJueType).findFirst().orElse(null);
        return xianJueEntity;
    }

    /**
     * 获得所有仙诀信息
     *
     * @return
     */
    public static List<CfgXianJueEntity> getXianJues() {
        CfgXianJue xianJueConfig = getXianJueConfig();
        return CloneUtil.cloneList(xianJueConfig.getXianJues());
    }

    /**
     * 获得等级加成
     *
     * @param studyLevel   研习等级
     * @param quality
     * @param starProgress
     * @return
     */
    public static List<CardEquipmentAddition> getLevelAddition(int studyLevel, int quality, int starProgress) {
        // 等级加成
        List<CardEquipmentAddition> levelCardEquipmentAdditions = CfgXianJueTool.getXianJueLevelAddition(studyLevel);
        //星图加成处理
        double starAddition = getStarAddition(quality, starProgress);
        levelCardEquipmentAdditions.forEach(levelAddition -> levelAddition.setValue((int) (levelAddition.getValue() * (1 + starAddition))));
        return levelCardEquipmentAdditions;
    }

    /**
     * 获得星级加成
     *
     * @param quality
     * @param starProgress
     * @return
     */
    private static double getStarAddition(int quality, int starProgress) {
        int qualityParam = quality;
        int starParam = starProgress;
        if (quality == CfgXianJueTool.getMaxQuality() || (quality > 10 && starProgress == 0)) {
            qualityParam -= 10;
            starParam = CfgXianJueTool.getNeedStarNum();
        }
        //获得星图配置
        CfgXianJueStarMap starMap = CfgXianJueTool.getXianJueStarMap(qualityParam, starParam);
        //没有对应星图配置,星图加成为0
        if (null == starMap) {
            return 0;
        }
        //获得星图加成
        double starAddition = starMap.getAdditionRatio();
        return starAddition;
    }

    /**
     * 获取仙诀等级限制
     *
     * @param quality
     * @return
     */
    public static int getLevelLimit(int quality) {
        CfgXianJue xianJueConfig = getXianJueConfig();
        return xianJueConfig.getQualityLevelLimits().get(quality);
    }

    /**
     * 获取最大等级限制
     *
     * @return
     */
    public static int getMaxLevelLimit() {
        CfgXianJue xianJueConfig = getXianJueConfig();
        return xianJueConfig.getMaxLevel();
    }

    /**
     * 获得最好的品质
     *
     * @return
     */
    public static int getMaxQuality() {
        CfgXianJue xianJueConfig = getXianJueConfig();
        return xianJueConfig.getMaxQuality();
    }

    /**
     * 获取所需的星图进度
     *
     * @return
     */
    public static int getNeedStarNum() {
        CfgXianJue xianJueConfig = getXianJueConfig();
        return xianJueConfig.getStarNum();
    }

    /**
     * 获取等级对应的强化配置信息
     *
     * @param level
     * @return
     */
    public static CfgXianJueStrengthen getXianJueStrengthen(int level) {
        CfgXianJue xianJueConfig = getXianJueConfig();
        CfgXianJueStrengthen xianJueStrengthen = xianJueConfig.getXianJueStrengthens().stream().filter(tmp -> tmp.isMatch(level)).findFirst().orElse(null);
        //仙诀强化信息为空
        if (null == xianJueStrengthen) {
            throw CoderException.high("仙诀等级" + level + "强化信息没有配置到 卡牌仙诀.yml");
        }
        return CloneUtil.clone(xianJueStrengthen);
    }

    /**
     * 获得仙诀基本加成
     *
     * @param xianJueDataId
     * @return
     */
    public static List<CardEquipmentAddition> getBaseXianJueAddition(int xianJueDataId) {
        CfgXianJueEntity xianJueEntity = getXianJue(xianJueDataId);
        return CloneUtil.cloneList(xianJueEntity.getAdditions());
    }

    /**
     * 获得仙诀等级加成
     *
     * @param level
     * @return
     */
    public static List<CardEquipmentAddition> getXianJueLevelAddition(int level) {
        CfgXianJue xianJueConfig = getXianJueConfig();
        return CloneUtil.cloneList(xianJueConfig.getXianJueLevelAdditions().get(level));
    }

    /**
     * 获取星图配置
     *
     * @param quality
     * @param star
     * @return
     */
    public static CfgXianJueStarMap getXianJueStarMap(int quality, int star) {
        CfgXianJue xianJueConfig = getXianJueConfig();
        int index = quality * 10 + star;
        return CloneUtil.clone(xianJueConfig.getXianjueStarMaps().get(index));
    }


    /**
     * 获得当前参悟上限
     *
     * @param quality
     * @return
     */
    public static Integer getComprehendLimitInfo(int quality) {
        CfgXianJue xianJueConfig = getXianJueConfig();
        Map<Integer, Integer> comprehendLimitInfo = xianJueConfig.getComprehendLimits();
        return comprehendLimitInfo.get(quality);
    }

    /**
     * 获得参悟上限
     *
     * @return
     */
    public static Integer getComprehendLimitInfo() {
        CfgXianJue xianJueConfig = getXianJueConfig();
        Map<Integer, Integer> comprehendLimitInfo = xianJueConfig.getComprehendLimits();
        return comprehendLimitInfo.get(QualityEnum.FAIRY.getValue());
    }

    /**
     * 获得参悟值
     *
     * @return
     */
    public static Integer getComprehendAddalue() {
        CfgXianJue xianJueConfig = getXianJueConfig();
        Map<Integer, Integer> comprehendLimitInfo = xianJueConfig.getComprehendProbs();
        List<Integer> probs = comprehendLimitInfo.values().stream().collect(Collectors.toList());
        Integer index = PowerRandom.hitProbabilityIndex((probs));
        List<Integer> keys = comprehendLimitInfo.keySet().stream().collect(Collectors.toList());
        Integer key = keys.get(index);
        return key;
    }

    /**
     * 获得参悟需要法宝
     *
     * @param comprehendType
     * @return
     */
    public static Integer getComprehendNeedTreasureId(Integer comprehendType) {
        CfgXianJue xianJueConfig = getXianJueConfig();
        Map<Integer, Integer> comprehendNeedTreasureIds = xianJueConfig.getComprehendNeedTreasureId();
        return comprehendNeedTreasureIds.get(comprehendType);

    }
}
