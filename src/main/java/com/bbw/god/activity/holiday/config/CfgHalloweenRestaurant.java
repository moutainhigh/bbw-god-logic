package com.bbw.god.activity.holiday.config;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 万圣餐厅
 *
 * @author: huanghb
 * @date: 2022/10/11 15:16
 */
@Data
public class CfgHalloweenRestaurant implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 接受订单上限 */
    private Integer acceptOrderLimit;
    /** 盘子初始化 */
    private List<Integer> plateInits;
    /** 解锁需要法宝（糖果）盘子个数=》糖果消耗数量 */
    private Map<Integer, Integer> unlockPlateNeedTreasureNums;
    /** 解锁需要法宝（糖果） */
    private Integer unlockPlateNeedTreasure;
    /** 食物合成 食物=》下一级食物 */
    private Map<Integer, Integer> foodCompounds;
    /** 食物商品id 食物=》商品id */
    private Map<Integer, Integer> foodMallIds;
    /** 食物合成需要数量 */
    private Integer foodCompoundNeedNum;
    /** 盘子产出法宝 */
    private Integer plateOutPutTreasure;
    /** 盘子每小时产出 */
    private Integer plateOutPutNumPerHour;
    /** 订单有效时间 */
    private Integer orderValidTime;


    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
