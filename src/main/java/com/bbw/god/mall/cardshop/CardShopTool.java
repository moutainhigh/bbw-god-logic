package com.bbw.god.mall.cardshop;

import com.bbw.god.game.config.treasure.TreasureEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌屋工具类
 *
 * @author suhq
 * @date 2020-04-22 11:20
 **/
public class CardShopTool {
    /**
     * 获取抽卡池需要的源晶类型
     *
     * @param poolType
     * @return
     */
    public static TreasureEnum getNeedYJAsDraw(CardPoolEnum poolType) {
        switch (poolType) {
            case GOLD_CP:
                return TreasureEnum.JZY;
            case WOOD_CP:
                return TreasureEnum.MZY;
            case WATER_CP:
                return TreasureEnum.SZY;
            case FIRE_CP:
                return TreasureEnum.HZY;
            case EARTH_CP:
                return TreasureEnum.TZY;
            default:
                return TreasureEnum.XZY;
        }
    }

    /**
     * 获取激活卡池需要的源晶类型
     *
     * @param poolType
     * @return
     */
    public static TreasureEnum getNeedYJAsActive(CardPoolEnum poolType) {
        switch (poolType) {
            case WATER_CP:
                return TreasureEnum.SYJ;
            case WOOD_CP:
                return TreasureEnum.MYJ;
            case FIRE_CP:
                return TreasureEnum.HYJ;
            case EARTH_CP:
                return TreasureEnum.TYJ;
            case WANWU_CP:
                return TreasureEnum.WWYJ;
            default:
                return null;
        }
    }

    /**
     * 获取奖励的源晶
     *
     * @param poolType
     * @return
     */
    public static TreasureEnum getAwardYJ(CardPoolEnum poolType) {
        switch (poolType) {
            case GOLD_CP:
                return TreasureEnum.SYJ;
            case WATER_CP:
                return TreasureEnum.MYJ;
            case WOOD_CP:
                return TreasureEnum.HYJ;
            case FIRE_CP:
                return TreasureEnum.TYJ;
            case EARTH_CP:
                return TreasureEnum.WWYJ;
            default:
                return null;
        }
    }

    /**
     * 金水木火土万物
     *
     * @param poolType
     * @return
     */
    public static CardPoolEnum getPreCardPool(CardPoolEnum poolType) {
        switch (poolType) {
            case WATER_CP:
                return CardPoolEnum.GOLD_CP;
            case WOOD_CP:
                return CardPoolEnum.WATER_CP;
            case FIRE_CP:
                return CardPoolEnum.WOOD_CP;
            case EARTH_CP:
                return CardPoolEnum.FIRE_CP;
            case WANWU_CP:
                return CardPoolEnum.EARTH_CP;
            default:
                return CardPoolEnum.WANWU_CP;
        }
    }

    /**
     * 返回卡池的顺序列表
     *
     * @return
     */
    public static List<CardPoolEnum> getCardPoolEnum() {
        List<CardPoolEnum> list = new ArrayList<CardPoolEnum>();
        list.add(CardPoolEnum.GOLD_CP);
        list.add(CardPoolEnum.WATER_CP);
        list.add(CardPoolEnum.WOOD_CP);
        list.add(CardPoolEnum.FIRE_CP);
        list.add(CardPoolEnum.EARTH_CP);
        list.add(CardPoolEnum.WANWU_CP);
        return list;
    }
}
