package com.bbw.god.activity.holiday.processor.holidaychristmaswish;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 圣诞心愿配置
 *
 * @author: huanghb
 * @date: 2022/12/14 17:55
 */
@Data
public class CfgChristmasWish implements CfgInterface, Serializable {
    private static final long serialVersionUID = 1341975239179802282L;
    /** 默认配置的key值 */
    private String key;
    /** 最大心愿数 */
    private Integer maxWishNum;
    /** 圣诞心愿 */
    private List<ChristmasWish> christmasWishs;
    /** npcId集合 */
    private List<Integer> npcIds;

    @Data
    public static class ChristmasWish {
        /** 任务id */
        private Integer id;
        /** 任务类别 */
        private Integer type;
        /** 礼物心愿 */
        private Integer giftWish;
        /** 最喜欢奖励 */
        private List<Award> favoriteAwards;
        /** 喜欢奖励 */
        private List<Award> likeAwards;
        /** 普通奖励 */
        private List<Award> ordinaryAwards;
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
        return 0;
    }


}
