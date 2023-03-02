package com.bbw.god.game.config.mall;

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
public enum MallEnum {
    NOT_SHOWED("商城不显示的物品", 0),
    DJ("道具", 10),
    EMOTICON("表情", 25),
    // KB("卡包", 30),
    THLB("特惠礼包", 40),
    NEWER_THLB("新手特惠礼包", 41),//已去除
    TTCJ_LB("通天残卷礼包", 42),
    NEWER_PACKAGE("新手礼包", 43),
    HOLIDAY_MALL_LIMIT_PACK("节日商铺限日礼包", 45),
    HOLIDAY_MALL_LIMIT_PACK_51("节日商铺限日礼包_51", 46),
    MXLB("萌新礼包", 44),
    ZLLB("助力礼包", 50),
    XJBK("星君宝库", 80),
    GOLD_CONSUME("消费福利", 90),
    DAILY_RECHARGE_BAG("日度充值礼包", 105),
    WEEK_RECHARGE_BAG("周度充值礼包", 100),
    MONTH_RECHARGE_BAG("月度充值礼包", 110),
    ACTIVITY_BAG("活动礼包", 120),
    HOLIDAY_EXCHANGE("节日活动兑换", 140),
    // 商店
    FST("封神台", 60),
    ZXZ("诛仙阵", 70),
    MAOU("魔王商店", 130),
    SXDH("神仙大会", 150),
    DFDJ("巅峰对决", 155),
    ADVENTURE("奇遇", 160),
    SNATCH_TREASURE("夺宝", 170),
    TE_HUI_RECHARGE_BAG("奇珍特惠礼包", 180),
    ROLE_TIME_LIMIT_BAG("角色限时礼包", 185),
    GOLD_RECHARGE_BAG("元宝礼包", 190),
    FIRST_RECHARGE_ITEM("首冲3倍档位", 195),
    HORSE_RACING("赛马商店", 300),
    WAR_TOKEN("战令商店", 400),
    JJLP_TOKEN("进阶令牌", 500),
    TRANSMIGRATION("轮回商店", 600),
    TREASURE_SECRET("藏宝秘境", 620),
    DISCOUNT_CHANGER("折扣变化商店", 610),
    RANDOM_EXCHANGE("活动随机兑换", 630),
    SKY_LANTERN_WORKSHOP("天灯工坊", 650),
    SPECIAL_DISCOUNT("合服-折扣特惠", 660),
    COMBINED_SERVICE_EXCHANGE("合服-兑换", 670),
    FLOWER_TO_GOD("花赋予神", 680),
    MATERIAL_STORE("材料商店", 690),
    GODS_ALTAR("封神祭坛", 700),
    DIAMOND_GIFT_PACK("钻石礼包", 710),
    SM("新神秘", 730),
    HALLOWEEN_RESTAURANT("万圣餐厅", 740),
    HALLOWEEN_HALLOWEEN_RESTAURANT_REDEEM("万圣餐厅-兑换", 750),
    WORLD_CUP_ACTIVITIE_GUESS_SHOP("竞猜商店", 760),
    GLORY_COIN_STORE("荣耀币商店", 770),
    HOLIDAY_GIFT_PACK_51("节日礼包-51", 780),
    CHINESE_ZODIAC_COLLISION("生肖对碰", 790),
    ;


    private String name;
    private int value;

    public static MallEnum fromValue(int value) {
        for (MallEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}