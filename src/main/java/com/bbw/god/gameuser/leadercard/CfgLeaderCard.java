package com.bbw.god.gameuser.leadercard;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 配置路径是  主角卡初始化配置
 * @author：lwb
 * @date: 2021/3/22 14:30
 * @version: 1.0
 */
@Data
public class CfgLeaderCard implements CfgInterface {
    /**
     * 初始化技能列表
     */
    private List<InitSkill> initSkills;
    /**
     * 默认初始0级技能
     */
    private Integer initSkill0;
    /**
     * 初始攻击
     */
    private Integer initAtk;
    /**
     * 初始防御
     */
    private Integer initHp;
    /**
     * 初始阶级加成
     */
    private Integer initBaseHvAddition;
    /**
     * 重置 属性点数 消耗的元宝
     */
    private Integer resetAddPointNeedGod;
    /**
     * 解锁属性 消耗的元宝
     */
    private Integer unlockPropertyNeedGod;
    /**
     * 阶级上限
     */
    private Integer topLimitHv;
    /**
     * 等级上限
     */
    private Integer topLimitLv;
    /**
     * 升阶条件
     */
    private List<UpHvCondition> upHvConditions;
    /**
     * 等级经验配置
     */
    private List<ExpCondition> exps;
    /**
     * 解锁新的技能组所需元宝
     */
    private Integer unlockNewSkillsGroupNeedGod;
    @Data
    public static class InitSkill{
        /**
         * 技能id
         */
        private int skill;
        /**
         * 百分比
         */
        private int percentage;
    }

    @Data
    public static class UpHvCondition{
        private int hv;
        private int topLimit;
        /**
         * 每次消耗的数量
         */
        private int consume;
        /**
         * 每次加的百分比
         */
        private int add;
        /**
         * 总共需要消耗的数量
         */
        private int needLingZhi;
    }

    @Data
    public static class ExpCondition{
        private int lv;
        private long exp;
    }

    @Override
    public Serializable getId() {
        return "唯一";
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
