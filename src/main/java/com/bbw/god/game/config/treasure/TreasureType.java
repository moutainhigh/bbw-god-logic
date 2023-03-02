package com.bbw.god.game.config.treasure;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物品类别
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:04:07
 */
@Getter
@AllArgsConstructor
public enum TreasureType {

    MAP_TREASURE("地图法宝", 10),
    FIGHT_TREASURE("战斗法宝", 20),
    BOX("宝箱、宝袋", 30),
    CARD_PACKAGE("卡牌礼包", 33),

    WNLS("万能灵石", 40),
    CARD_UPLEVEL("特殊卡牌升级材料", 41),

    HEAD_BOX("头像框", 45),

    HEAD("头像", 46),
    EMOTICON("表情", 47),

    Common("通用", 50),
    POINTS("积分", 51),
    SYMBOL("符箓", 55),

    SKILL_SCROLL("技能卷轴", 56),

    DAILY_BOX("每日任务宝箱", 60),
    YUSUI("玉髓", 70),
    FUTU("符图", 71),
    SPAR("晶石", 72),
    FUSHOU("符首", 73),
    DEIFYS("群体封神令", 98),
    DEIFY("封神令", 99),
    EQUIPMENT("装备", 100),
    BEAST_FTXS("神兽-飞天仙兽", 110),
    BEAST_XJLS("神兽-迅捷灵兽", 111),
    FASHION("时装", 120),
    ;

    private String name;
    private int value;

    public static TreasureType fromValue(int value) {
        for (TreasureType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static TreasureType fromName(String name) {
        for (TreasureType model : values()) {
            if (model.getName().equals(name)) {
                return model;
            }
        }
        return null;
    }
}
