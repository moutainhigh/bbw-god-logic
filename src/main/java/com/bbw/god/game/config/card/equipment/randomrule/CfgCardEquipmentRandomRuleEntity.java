package com.bbw.god.game.config.card.equipment.randomrule;

import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 卡牌装备随机规则配置
 * @author: hzf
 * @create: 2022-12-02 09:40
 **/
@Data
public class CfgCardEquipmentRandomRuleEntity implements CfgInterface, Serializable {
    private static final long serialVersionUID = 6283485026406890074L;

    /** 战斗类型 */
    private Integer fightType;

    /** 至宝规则 */
    private List<CfgZhiBao> zhiBaos;
    /** 仙决规则 */
    private List<CfgXianJue> xianJues;



    @Data
    public static class CfgZhiBao{
        /** 至宝 */
        private Integer zhiBaoId;
        /** 卡牌 属性 */
        private Integer property;
        /** 技能组 */
        private List<Integer> skillGroup;
        /** 属性加成  10:攻击，20防御，50：强度，60：韧度*/
        /** 类型====>加的值*/
        private Map<String,Integer> additions;
    }
    @Data
    public static class CfgXianJue{
        /**仙决类型 */
        private Integer xianJueType;
        /** 仙决等级 */
        private Integer level;
        /** 仙决  品质====>淬星 */
        private Integer quality;
        /** 仙决 星图进度 */
        private Integer starMapProgress;
        /** 属性加成  10:攻击，20防御，30：强度，40：韧度*/
        /** 类型====>加的值*/
        private Map<String,Integer> additions;
    }
    @Override
    public Serializable getId() {
        return fightType;
    }

    @Override
    public int getSortId() {
        return 0;
    }

}
