package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 装备配置
 *
 * @author suhq
 * @date 2021-03-26 13:51
 **/
@Slf4j
@Data
public class CfgEquipment implements CfgInterface, CfgPrepareListInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    /** 最大等级 */
    private Integer maxLevel;
    /** 最好品质 */
    private Integer maxQuality;
    /** 星图星级数 */
    private Integer starNum;
    /** 基本装备加成 */
    private List<CfgEquipmentEntity> equipments;
    /** 强化数据 */
    private List<CfgEquipmentStrengthen> equipmentStrengthens;
    /** 品质等级上限 */
    private Map<Integer, Integer> qualityLevelLimits;
    /** 星图配置 */
    private Map<Integer, CfgEquipmentStarMap> equipmentStarMaps;
    /** 装备等级对应的加成 */
    private Map<Integer, List<Addition>> equipmentLevelAdditions = new HashMap<>();

    @Override
    public void prepare() {
        int maxLevel = CfgEquipmentTool.getMaxLevelLimit();
        for (int i = 1; i <= maxLevel; i++) {
            final int level = i;
            CfgEquipmentStrengthen equipmentStrengthen = CfgEquipmentTool.getEquipmentStrengthen(level);
            List<Addition> additions = new ArrayList<>();
            additions.addAll(equipmentStrengthen.getAdditions());
            if (level > 1) {
                List<Addition> preAdditions = equipmentLevelAdditions.get(level - 1);
                additions.addAll(preAdditions);
                Map<Integer, Integer> group = additions.stream().collect(Collectors.groupingBy(Addition::getType, Collectors.summingInt(Addition::getValue)));
                additions.clear();
                for (Integer type : group.keySet()) {
                    additions.add(new Addition(type, group.get(type)));
                }
            }
            equipmentLevelAdditions.put(level, additions);
        }
        log.info("装备预准备完成");

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
