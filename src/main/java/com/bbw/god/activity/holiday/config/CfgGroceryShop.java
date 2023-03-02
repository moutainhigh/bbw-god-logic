package com.bbw.god.activity.holiday.config;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 杂货小铺配置类
 * @author: hzf
 * @create: 2022-12-08 17:57
 **/
@Data
public class CfgGroceryShop implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 盲盒的最大数量 */
    private Integer blindBoxMaxNum;
    /**至少需要打开多少个盲盒，才可以刷新*/
    private Integer blindBoxNeedNum;
    /**若前面都没有开出大奖，然第25次必出大奖*/
    private Integer needMustGetNum;
    /** 盲盒大奖配置 */
    private List<CfgBlindBoxGrandPrix> blindBoxGrandPrixs;
    /** 盲盒奖励配置 */
    private List<CfgBlindBoxAward> blindBoxAwards;

    /**
     * 大奖配置
     */
    @Data
    public static class CfgBlindBoxGrandPrix{
        /** 道具名称 */
        private String treasureName;
        /** 大奖道具id */
        private Integer treasureId;
        /** 需要消耗的道具id */
        private Integer needTreasure;
        /** 需要消耗的道具数量 */
        private Integer num;
        /** 奖励 */
        private Award award;
    }

    /**
     * 盲盒配置
     */
    @Data
    public static class CfgBlindBoxAward{
        /** 大奖的道具id */
        private Integer treasureId;
        /** 对应的盲盒奖励 */
        private List<Award> awards;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
