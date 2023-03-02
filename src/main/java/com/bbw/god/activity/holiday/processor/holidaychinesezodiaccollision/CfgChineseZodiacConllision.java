package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 生肖对碰活动配置类
 *
 * @author: huanghb
 * @date: 2023/2/9 17:30
 */
@Data
public class CfgChineseZodiacConllision implements CfgInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 碰牌信息 */
    private List<Collision> collisions;
    /** 生肖地图 */
    private List<ChineseZodiacMap> chineseZodiacMaps;

    /**
     * 初始化
     *
     * @return
     */
    protected static CfgChineseZodiacConllision instance() {
        CfgChineseZodiacConllision cfgChineseZodiacConllision = new CfgChineseZodiacConllision();
        return cfgChineseZodiacConllision;
    }

    /**
     * 碰牌
     */
    @Data
    public static class Collision {
        /** 需要法宝 */
        private List<Award> needTreasures;
        /** 碰牌需要生肖牌数量 */
        private Integer collisionNeedChineseZodiacNum;
        /** 碰牌成功奖励 */
        private List<Award> sucessAwards;
        /** 是否有效 */
        private Boolean valid;
    }

    /**
     * 地图
     */
    @Data
    public static class ChineseZodiacMap {
        /** 地图id */
        private Integer id;
        /** 地图等级 */
        private Integer level;
        /** 地图大小 */
        private Integer size;
        /** 需要生肖数量 */
        private Integer needChineseZodiacNum;
        /** 奖励 */
        private List<Award> awards;

        /**
         * 是否匹配
         *
         * @param level
         * @return
         */
        public Boolean isMatch(int level) {
            return this.level == level;
        }
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
