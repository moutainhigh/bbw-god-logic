package com.bbw.god.activity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动类别
 *
 * @author suhq
 * @date 2019年3月2日 下午6:45:56
 */
@Getter
@AllArgsConstructor
public enum ActivityEnum {

    FIRST_R("首冲大礼", 10),
    LIMIT_CARD("限定卡牌", 11),
    MULTIPLE_REBATE("3倍返利", 12),
    GOD_BLESS("仙人祝福", 13),
    ACC_R("累计充值", 20),
    TODAY_ACC_R("今日累充", 23),
    TODAY_ACC_R_2("今日累充-周一至周六", 24),
    TODAY_ACC_R_3("今日累充-周日", 25),
    ACC_R_DAYS_7("累天充值", 26),
    SEVEN_LOGIN("七日之约", 30),
    MULTI_DAY_ACC_R("多日累充（周一至周二）", 31),
    MULTI_DAY_ACC_R2("多日累充（周三至周四）", 32),
    MULTI_DAY_ACC_R3("多日累充（周五至周六）", 33),
    SUNDAY_ACC("周日充值", 34),
    MONTH_LOGIN("月签到", 40),
    DICE("补充体力", 50),
    DOUBLE_DICE("补充体力翻倍", 55),
    INVITE("邀请好友", 60),
    // HOLIDAY_BAG("商城限时礼包", 110),
    LOGIN_AWARD("登录之礼", 120),
    DOUBLE_FIGHT_EXP("经验加倍", 130),
    DOUBLE_FIGHT_COPPER("铜钱加倍", 140),
    RESET_FIRST_DOUBLE_R("首冲翻倍重置", 165),
    PER_DAY_DOUBLE_FIRST_R("每日首冲翻倍", 180),
    HOLIDAY_ACC_R("节日累计充值", 200),
    HOLIDAY_PER_DAY_ACC_R("节日每日累充", 205),
    MALL_DISCOUNT("商品八折", 210),
    RECHARGE_CARD("充值卡", 1010),

    NEWER_SPECIAL_OFFER("新手特惠", 9010),
    SERVER_OPEN_NECESSARY("开服必备", 9013),
    GongCLD("攻城略地", 9020),
    PER_DAY_GOLD_CONSUME("每日消费", 9030),
    XingJBK("星君宝库", 9040),
    DRAW_CARD_TH("招募特惠", 9050),
    GOLD_CONSUME("元宝消费福利", 9060),
    GOD_POWER_SWEEP("神力横扫", 9070),
    BORN("仙师献礼", 9080),
    WEEK_BAG("周度礼包", 9090),
    MONTH_BAG("月度礼包", 9095),
    HERO_BACK_GIFT("英雄回归-回归大礼", 10001),
    HERO_BACK_SIGIN("英雄回归-回归签到", 10002),
    HERO_BACK_TASK("英雄回归-重拾荣光", 10003),
    HERO_BACK_RECHARGE("英雄回归-特惠福利", 10004),

    HOLIDAY_BAG("节日登录礼包", 10010),
    HOLIDAY_SIGN("节日签到", 10020),
    HOLIDAY_DAY_ACC_R("节日每日累充-1", 10030),
    HOLIDAY_DAY_ACC_R2("节日每日累充-2", 10035),
    HOLIDAY_ACC_R_2("节日累充(UI)", 10040),
    HOLIDAY_EXCHANGE("节日兑换|集字有礼", 10050),
    HOLIDAY_YEGUAI("节日特殊野怪|贼寇来袭", 10060),
    HOLIDAY_DAILY_TASK("节日每日任务", 10070),
    HOLIDAY_SPECIAL_TASK("节日特殊任务", 10080),
    HOLIDAY_TRAINING("节日练兵", 10090),
    HOLIDAY_COC("节日商会", 10100),
    HOLIDAY_LOTTERY("节日抽奖|雨露均沾", 10110),
    HOLIDAY_BO_BING("佳节博饼", 10115),
    HOLIDAY_JSWC("金鼠旺财", 10120),
    HOLIDAY_OVERVIEW("节日总览", 10130),
    HOLIDAY_PER_ACC_R_10("节日每日每充值10元", 10140),
    HOLIDAY_WISH_FEEDBACK("许愿回馈", 10150),
    HOLIDAY_SNATCH_TREASURE_FEEDBACK("夺宝回馈", 10160),
    HOLIDAY_DIG_FOR_TREASURE("节日野外挖宝", 10170),
    HOLIDAY_HORSE_RACING("节日赛马", 10180),
    LOTTERY_ACTIVITY("奖券", 11010),
    SNATCH_TREASURE("夺宝", 11020),
    NEWER_PACKAGE("新手礼包", 11030),
    GOD_FAVOR("仙人垂青", 11040),
    JU_XIAN_HUAN_SHEN("聚仙唤神", 11050),
    TRADE_WELFARE("交易福利", 11060),
    TCHC("特产合成", 11070),
    WQCY("五气朝元", 11080),
    ACC_GOLD_CONSUME("累计元宝消耗", 11090),
    TE_HUI_GIFT_PACK_1("特惠礼包限时", 11100),
    LIMIT_TIME_CARD_POOL("限时卡池", 11110),
    LIMIT_TIME_DRAW_FCJB("丰财聚宝", 11111),
    LIMIT_TIME_MALL_PACK("商铺限时节日礼包", 11120),
    NIGHTMARE_FIRST_R("梦魇世界首冲大礼", 12010),

    RECHARGE_SIGN("充值签到", 12020),

    NEWER_BOOST("新手助力", 12030),
    CARD_LEVEL_BOOST("卡牌等级助力", 12031),
    CARD_EXP_BOOST("卡牌经验助力", 12032),

    WAR_TOKEN("战令", 13000),

    NAO_GUI_NAN_GUA("闹鬼南瓜", 13010),
    CUN_Z_YI_YUN("村庄疑云", 13020),
    BU_GEI_TANG("不给糖就捣乱",13030),

    COOKING_FOOD("烹饪美食", 13040),
    GRATEFUL("感恩之举", 13050),
    BF_DISCOUNT("黑五特惠", 13060),

    ABYSS_VISITORS("深渊来客", 13070),
    BUILDING_ALTAR("建造祭坛", 13080),
    CELEBRATION_INVITATION("庆典邀约", 13090),
    ALL_SERVICE_CELEBRATION("全服庆典", 13100),
    TREASURE_SECRET("藏宝秘境", 13110),
    RESIST_DEVIL("抵抗恶魔", 13120),
    ACTIVITY_OVERVIEW_MODEL_1("活动总览（模式1）", 13130),
    ACTIVITY_OVERVIEW_MODEL_2("活动总览（模式2）", 13140),
    HOLIDAY_WZJZ("威震九州", 13150),
    YEAR_BEAST("年兽来袭", 13160),
    HOLIDAY_JHNF("金虎纳福", 13170),
    SKY_LANTERN_WORKSHOP("天灯工坊", 13180),
    PRAYER_SKY_LANTERN("祈福天灯", 13190),
    FIND_TREASURE_MAP("寻藏宝图", 13200),
    LANTERN_FESTIVAL_GIFT("锦礼活动", 13210),
    THOUGHTS_OF_FLOWERS("花寄思语", 13220),
    COMBINED_SERVICE_LOGIN("合服登录", 13230),
    GUILD_TARGET("行会目标", 13240),
    BIG_GOD_PLAN("大仙计划", 13250),
    COMBINED_SERVICE_DISCOUNT("合服-折扣特惠", 13260),
    COMBINED_SERVICE_EXCHANGE("合服-兑换", 13270),
    COMBINED_SERVICE_PER_ACC_R_10("合服-每日累充10元", 13280),
    COMBINED_SERVICE_ACTIVITY_OVERVIEW_MODEL("合服活动总览", 13290),
    CUTE_TIGER_MARKET("萌虎集市", 13300),
    FLOWER_TO_GOD("花赋予神", 13310),
    GUAN_ZU("抵御冠族", 13320),
    LABOR_GLORIOUS("劳动光荣", 13330),
    MATERIAL_STORE("材料商店", 13340),
    COOL_SUMMER("清凉一夏", 13350),
    BUSINESS_GANG_YINGJIE("商帮英杰", 13360),
    KOI_PRAY("锦鲤祈愿", 13370),
    TREAT_OR_TRICK("不给糖就捣乱2", 13380),
    HALLOWEEN_RESTAURANT("万圣餐厅", 13390),
    HOLIDAY_ACTIVITY_ACC_R("节日活动-累计充值", 13410),
    WORLD_CUP_ACTIVITY_OVERVIEW("活动总览（世界杯活动）", 13420),
    WORLD_CUP_ACTIVITIE_SUPER_16("超强16强（世界杯活动）", 13430),
    WORLD_CUP_ACTIVITIE_FINALIZE_THE_TOP_8("决战8强（世界杯活动）", 13440),
    WORLD_CUP_ACTIVITIE_I_AM_PROPHET("我是预言家（世界杯活动）", 13450),
    WORLD_CUP_ACTIVITIE_I_AM_KING_OF_GUESS("我是竞猜王（世界杯活动）", 13460),
    WORLD_CUP_ACTIVITIE_GUESS_TASK("竞猜任务（世界杯活动）", 13470),
    WORLD_CUP_ACTIVITIE_GUESS_SHOP("竞猜商店（世界杯活动）", 13480),
    THANK_FLOWER_LANGUAGE("感恩花语", 13490),
    LITTLE_REINDEER("小小驯鹿", 13500),
    HOLIDAY_SIGN_51("登录有礼-51", 13510),
    PAI_LI_FAWN_51("派礼小鹿-51", 13520),
    MAGIC_WITCH_51("魔法女巫-51", 13530),
    CHRISTMAS_WISH_51("圣诞心愿-51", 13540),
    GROCERY_SHOP_51("杂货小铺-51", 13550),
    HOLIDAY_GIFT_PACK_51("节日礼包-51", 13560),
    LIMIT_TIME_DRAW_FCJB_51("丰财聚宝-51", 13570),
    LIMIT_TIME_MALL_PACK_51("商铺限时节日礼包-51", 13580),
    LIMIT_TIME_CARD_POOL_51("限时卡池-51", 13590),
    HOLIDAY_PER_ACC_R_10_51("节日每日每充值10元-51", 13600),
    HOLIDAY_DAY_ACC_R_1_51("(节日每日累充-1)-51", 13610),
    HOLIDAY_OVERVIEW_51("活动总览-51）", 13620),
    DOUBLE_GOLD("双倍元宝", 13630),
    CONSUMPTION_WELFARE("消费福利", 13640),
    HOLIDAY_OVERVIEW_52("活动总览-52）", 13670),
    HOLIDAY_SIGN_52("登录有礼-52", 13680),
    HOLIDAY_PER_ACC_R_10_52("节日每日每充值10元-51", 13690),
    HOLIDAY_DAY_ACC_R_1_52("(节日每日累充-1)-51", 13700),
    CHINESE_ZODIAC_COLLISION("生肖对碰", 13710),
    WINE_MEETS_A_BOSOM_FRIEND("酒逢知己", 13720),
    HAPPY_TOUCH_CUP("逍遥碰杯", 13730),

    ;

    private final String name;
    private final int value;

    public static ActivityEnum fromValue(int value) {
        for (ActivityEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static boolean isHerobackActivity(ActivityEnum tyEnum) {
    	switch (tyEnum) {
            case HERO_BACK_GIFT:
            case HERO_BACK_SIGIN:
            case HERO_BACK_TASK:
            case HERO_BACK_RECHARGE:
                return true;
            default:
                break;
        }
        return false;
    }

    public static boolean isMultiDayRechargeActivity(ActivityEnum tyEnum) {
        switch (tyEnum) {
            case MULTI_DAY_ACC_R:
            case MULTI_DAY_ACC_R2:
            case MULTI_DAY_ACC_R3:
            case SUNDAY_ACC:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是今日累冲
     * @param tyEnum
     * @return
     */
    public static boolean isTodayDayAccRechargeActivity(ActivityEnum tyEnum) {
        switch (tyEnum) {
            case TODAY_ACC_R:
            case TODAY_ACC_R_2:
            case TODAY_ACC_R_3:
                return true;
            default:
                return false;
        }
    }
}
