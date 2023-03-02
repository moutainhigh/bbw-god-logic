package com.bbw.god.gameuser.card.equipment.cfg;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import com.bbw.god.gameuser.card.equipment.CfgXianJueTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 卡牌仙诀
 *
 * @author: huanghb
 * @date: 2022/9/14 16:06
 */
@Slf4j
@Data
public class CfgXianJue implements CfgInterface, CfgPrepareListInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    /** 最大等级 */
    private Integer maxLevel;
    /** 最好品质 */
    private Integer maxQuality;
    /** 星图星级数 */
    private Integer starNum;
    /** 基本装备加成 */
    private List<CfgXianJueEntity> xianJues;
    /** 强化数据 */
    private List<CfgXianJueStrengthen> xianJueStrengthens;
    /** 品质等级上限  仙诀品质=》等级上限 */
    private Map<Integer, Integer> qualityLevelLimits;
    /** 星图配置  星图进度=》星图配置 当前星图进度101 为第一个星图第一阶段 value为星图配置 */
    private Map<Integer, CfgXianJueStarMap> xianjueStarMaps;
    /** 仙诀等级对应的加成 等级=》加成信息 */
    private Map<Integer, List<CardEquipmentAddition>> xianJueLevelAdditions = new HashMap<>();
    /** 参悟限制  品质，=》参悟上限 */
    private Map<Integer, Integer> comprehendLimits;
    /** 参悟需要消耗法宝 参悟类别=》为法宝id */
    private Map<Integer, Integer> comprehendNeedTreasureId;
    /** 参悟概率  加成数值=》加成概率 */
    private Map<Integer, Integer> comprehendProbs;

    @Override
    public void prepare() {
        int maxLevel = CfgXianJueTool.getMaxLevelLimit();
        for (int i = 1; i <= maxLevel; i++) {
            final int level = i;
            CfgXianJueStrengthen cfgXianJueStrengthen = CfgXianJueTool.getXianJueStrengthen(level);
            List<CardEquipmentAddition> cardEquipmentAdditions = new ArrayList<>();
            cardEquipmentAdditions.addAll(cfgXianJueStrengthen.getAdditions());
            if (level > 1) {
                List<CardEquipmentAddition> preCardEquipmentAdditions = xianJueLevelAdditions.get(level - 1);
                cardEquipmentAdditions.addAll(preCardEquipmentAdditions);
                Map<Integer, Integer> group = cardEquipmentAdditions.stream().collect(Collectors.groupingBy(CardEquipmentAddition::getType, Collectors.summingInt(CardEquipmentAddition::getValue)));
                cardEquipmentAdditions.clear();
                for (Integer type : group.keySet()) {
                    cardEquipmentAdditions.add(new CardEquipmentAddition(type, group.get(type)));
                }
            }
            xianJueLevelAdditions.put(level, cardEquipmentAdditions);
        }
//        log.info("仙诀预准备完成");

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
