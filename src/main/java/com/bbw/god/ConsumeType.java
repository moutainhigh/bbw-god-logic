package com.bbw.god;

import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消费类别
 *
 * @author suhq
 * @date 2019-05-31 14:27:17
 */
@Getter
@AllArgsConstructor
public enum ConsumeType {
    GOLD("元宝", 1, 0),
    COPPER("铜钱", 2, 0),
    DIAMOND("钻石", 10000, 0),
    RMB("人民币", 3, 0),
    BEAN("神仙大会仙豆", 4, TreasureEnum.XIAN_DOU.getValue()),
    GUILD_CONTRIBUTION("行会贡献", 5, TreasureEnum.GUILD_CONTRIBUTE.getValue()),
    SHANG_HUI_GOLD("商会金币", 6, TreasureEnum.SHJB.getValue()),
    /** 抽卡业务专用 */
    YUAN_JING("源晶", 7, 0),
    SHEN_SHA("神砂", 8, TreasureEnum.SS.getValue()),
    HUN_YUAN("魂源", 9, TreasureEnum.HY.getValue()),
    FST_POINT("封神台积分", 10, TreasureEnum.FST_POINT.getValue()),
    ZXZ_POINT("诛仙阵积分", 11, TreasureEnum.ZXZ_POINT.getValue()),
    GOLD_CONSUME_POINT("元宝消费积分", 12, TreasureEnum.GOLD_CONSUME_POINT.getValue()),
    MAOU_SOUL("魔王魂", 13, TreasureEnum.MoWH.getValue()),
    ETJ("二踢脚", 14, TreasureEnum.ETJ.getValue()),
    ZTX("震天响", 15, TreasureEnum.ZTX.getValue()),
    MDH("满地红", 16, TreasureEnum.MDH.getValue()),
    CJYX_PACKAGE_1("二踢脚*3、窜天猴*1", 17, TreasureEnum.CJYX_NEED_TREASURE_1.getValue()),
    CJYX_PACKAGE_2("二踢脚*10、窜天猴*3、冲天炮*1", 18,TreasureEnum.CJYX_NEED_TREASURE_2.getValue()),
    CJYX_PACKAGE_3("震天响*5、满地红*1", 19,TreasureEnum.CJYX_NEED_TREASURE_3.getValue()),
    MEI("梅", 20,TreasureEnum.MEI.getValue()),
    LIAN_HUA("莲花", 21,TreasureEnum.LIAN_HUA.getValue()),
    MU_DAN("牡丹", 22,TreasureEnum.MU_DAN.getValue()),
    ZNQ_PACKAGE_1("梅*3、兰*1", 23,TreasureEnum.ZNQ_NEED_TREASURE_1.getValue()),
    ZNQ_PACKAGE_2("梅*10、兰*3、竹*1", 24,TreasureEnum.ZNQ_NEED_TREASURE_2.getValue()),
    ZNQ_PACKAGE_3("兰*15、竹*5、菊*1", 25,TreasureEnum.ZNQ_NEED_TREASURE_3.getValue()),
    ZNQ_PACKAGE_4("莲花*5、牡丹*1", 26,TreasureEnum.ZNQ_NEED_TREASURE_4.getValue()),
    LZ("龙舟", 27,TreasureEnum.LZ.getValue()),
    WSSX("五色丝线", 28,TreasureEnum.WSSX.getValue()),
    DWXN("端午香囊", 29,TreasureEnum.DWXN.getValue()),
    DW_PACKAGE_1("龙舟*3、粽子*1", 30,TreasureEnum.DW_PACKAGE_1.getValue()),
    DW_PACKAGE_2("龙舟*10、粽子*3、艾菖*1", 31,TreasureEnum.DW_PACKAGE_2.getValue()),
    DW_PACKAGE_3("粽子*15、艾菖*5、风筝*1", 32,TreasureEnum.DW_PACKAGE_3.getValue()),
    DW_PACKAGE_4("五色丝线*5、端午香囊*1", 33,TreasureEnum.DW_PACKAGE_4.getValue()),
    SNATCH_TREASURE_FU("夺宝符", 34,TreasureEnum.SNATCH_TREASURE_FU.getValue()),
    YE_MING_ZHU("夜明珠", 35,TreasureEnum.YE_MING_ZHU.getValue()),
    PEACE_GOLDEN_DOVE("和平金鸽", 36,TreasureEnum.PEACE_GOLDEN_DOVE.getValue()),
    JU_XIAN_LING("纳贤令", 37,TreasureEnum.JU_XIAN_LING.getValue()),
    XZY("仙之源", 38,TreasureEnum.XZY.getValue()),
    SMALL_GOLD_BRICK("小金砖", 39,TreasureEnum.SMALL_GOLD_BRICK.getValue()),
    GOLD_BEAN("巅峰对决金豆", 40,TreasureEnum.GOLD_BEAN.getValue()),
    HORSE_RACING_POINT("赛马积分", 50,TreasureEnum.HORSE_RACING_POINT.getValue()),
    SAI_ZHOU_POINT("赛舟积分", 60,TreasureEnum.SAI_ZHOU_POINT.getValue()),
    ZNQ_ZT_1("庆*1", 41,TreasureEnum.ZNQ_ZT_1.getValue()),
    ZNQ_ZT_2("竹*1、风*1", 42,TreasureEnum.ZNQ_ZT_2.getValue()),
    ZNQ_ZT_3("周*5、年*5、庆*1", 43,TreasureEnum.ZNQ_ZT_3.getValue()),
    ZNQ_ZT_4("竹*2、风*2、周*1、年*1", 44,TreasureEnum.ZNQ_ZT_4.getValue()),
    ZNQ_ZT_5("竹*10、风*10、周*5、年*5、庆*1", 45,TreasureEnum.ZNQ_ZT_5.getValue()),
    LDJ_ZT_1("最*1", 46,TreasureEnum.LDJ_ZT_1.getValue()),
    LDJ_ZT_2("劳*1、动*1", 47,TreasureEnum.LDJ_ZT_2.getValue()),
    LDJ_ZT_3("最*1、光*5、荣*5", 48,TreasureEnum.LDJ_ZT_3.getValue()),
    LDJ_ZT_4("劳*2、动*2、光*1、荣*1", 49,TreasureEnum.LDJ_ZT_4.getValue()),
    LDJ_ZT_5("劳*10、动*10、最*1、光*5、荣*5", 51,TreasureEnum.LDJ_ZT_5.getValue()),
    LDJ_ZT_6("最*1、光*8、荣*8", 52,TreasureEnum.LDJ_ZT_6.getValue()),
    LDJ_ZT_7("劳*3、动*3、光*2、荣*2", 53,TreasureEnum.LDJ_ZT_7.getValue()),
    LDJ_ZT_8("劳*20、动*20、最*1、光*10、荣*10", 54,TreasureEnum.LDJ_ZT_8.getValue()),
    XIA_T_ZT_1("字帖-啊*1", 61,TreasureEnum.XIA_T_ZT_1.getValue()),
    XIA_T_ZT_2("字帖-夏*1、字帖-天*1", 62,TreasureEnum.XIA_T_ZT_2.getValue()),
    XIA_T_ZT_3("字帖-夏*5、字帖-天*5、字帖-啊*1", 63,TreasureEnum.XIA_T_ZT_3.getValue()),
    XIA_T_ZT_4("字帖-你*2、字帖-好*2、字帖-夏*1、字帖-天*1", 64,TreasureEnum.XIA_T_ZT_4.getValue()),
    XIA_T_ZT_5("字帖-你*10、字帖-好*10、字帖-啊*1、字帖-夏*5、字帖-天*5", 65,TreasureEnum.XIA_T_ZT_5.getValue()),
    ZHONG_Q_ZT_1("字帖-家*1", 66,TreasureEnum.ZHONG_Q_ZT_1.getValue()),
    ZHONG_Q_ZT_2("字帖-团*1、字帖-圆*1", 67,TreasureEnum.ZHONG_Q_ZT_2.getValue()),
    ZHONG_Q_ZT_3("字帖-家*1、字帖-团*5、字帖-圆*5", 68,TreasureEnum.ZHONG_Q_ZT_3.getValue()),
    ZHONG_Q_ZT_4("字帖-中*2、字帖-秋*2、字帖-团*1、字帖-圆*1", 69,TreasureEnum.ZHONG_Q_ZT_4.getValue()),
    ZHONG_Q_ZT_5("字帖-中*10、字帖-秋*10、字帖-家*1、字帖-团*5、字帖-圆*5", 70,TreasureEnum.ZHONG_Q_ZT_5.getValue()),
    WAN_S_DH_1("熊熊软糖*15、糖霜饼干*10", 71,TreasureEnum.WAN_S_DH_1.getValue()),
    WAN_S_DH_2("熊熊软糖*30、糖霜饼干*20", 72,TreasureEnum.WAN_S_DH_2.getValue()),
    WAN_S_DH_3("熊熊软糖*60、糖霜饼干*40", 73,TreasureEnum.WAN_S_DH_3.getValue()),
    WAN_S_DH_4("熊熊软糖*150、糖霜饼干*100", 74,TreasureEnum.WAN_S_DH_4.getValue()),
    WAN_S_DH_5("熊熊软糖*1", 75,TreasureEnum.WAN_S_DH_5.getValue()),
    WAN_S_DH_6("糖霜饼干*1", 76,TreasureEnum.WAN_S_DH_6.getValue()),
//    WAN_S_DH_7("怪味糖豆*1", 77),
    WAN_S_DH_8("怪味糖豆*1", 78,TreasureEnum.WAN_S_DH_8.getValue()),
    WAN_S_DH_9("怪味糖豆*2", 79,TreasureEnum.WAN_S_DH_9.getValue()),
    WAN_S_DH_10("怪味糖豆*3", 80,TreasureEnum.WAN_S_DH_10.getValue()),
    WAN_S_DH_11("巫师帽*1", 81,TreasureEnum.WAN_S_DH_11.getValue()),
    WAN_S_DH_12("巫师帽*2", 82,TreasureEnum.WAN_S_DH_12.getValue()),
    WAN_S_DH_13("巫师帽*3", 83,TreasureEnum.WAN_S_DH_13.getValue()),
    WAN_S_DH_14("飞天扫帚*1", 84,TreasureEnum.WAN_S_DH_14.getValue()),
    WAN_S_DH_15("飞天扫帚*2", 85,TreasureEnum.WAN_S_DH_15.getValue()),
    WAN_S_DH_16("飞天扫帚*3", 86,TreasureEnum.WAN_S_DH_16.getValue()),
    THANKSGIVING_STAR_1("感恩星*1", 87,TreasureEnum.THANKSGIVING_STAR_1.getValue()),
    THANKSGIVING_STAR_2("感恩星*2", 88, TreasureEnum.THANKSGIVING_STAR_2.getValue()),
    THANKSGIVING_STAR_5("感恩星*5", 89, TreasureEnum.THANKSGIVING_STAR_5.getValue()),
    SHUANG_D_DH_1("村庄银币", 91, TreasureEnum.CUNZ_COIN.getValue()),
    SHUANG_D_DH_2("双旦星", 92, TreasureEnum.SD_STAR.getValue()),
    SHUANG_D_DH_3("宝藏令", 93, TreasureEnum.BAO_ZHANG_LING.getValue()),
    XIAN_YU("仙玉", 90, TreasureEnum.XY.getValue()),
    WAR_TOKEN("军符", 11700, TreasureEnum.WAR_TOKEN.getValue()),
    FST_MEDAL("封神台竞技勋章", 11770, TreasureEnum.FST_MEDAL.getValue()),
    HONOR_GOLD_COIN("荣耀金币", 50011, TreasureEnum.HONOR_GOLD_COIN.getValue()),
    HONOR_SILVER_COIN("荣耀银币", 50012, TreasureEnum.HONOR_SILVER_COIN.getValue()),
    HONOR_COPPER_COIN("荣耀铜币", 50198, TreasureEnum.HONOR_COPPER_COIN.getValue()),
    SMALL_FIRECRACKER_PACK_NEED_TREASURE("仙女棒*2", 94, TreasureEnum.SMALL_FIRECRACKER_PACK_NEED_TREASURE.getValue()),
    MEDIUM_FIRECRACKER_PACK_NEED_TREASURE("仙女棒*1、窜天猴*1", 95, TreasureEnum.MEDIUM_FIRECRACKER_PACK_NEED_TREASURE.getValue()),
    LARGE_FIRECRACKER_PACK_NEED_TREASURE("窜天猴*2", 96, TreasureEnum.LARGE_FIRECRACKER_PACK_NEED_TREASURE.getValue()),
    SKY_LANTERN_NEED_TREASURE("薄纸*1、木条*1、许愿蜡烛*1", 97, TreasureEnum.SKY_LANTERN_NEED_TREASURE.getValue()),
    HEFU_DH_1("大仙令牌", 98, TreasureEnum.HEFU_DH_1.getValue()),
    HEFU_DH_2("大仙令牌*50、老仙令牌*1", 99, TreasureEnum.HEFU_DH_2.getValue()),
    HEFU_DH_3("老仙令牌", 100, TreasureEnum.HEFU_DH_3.getValue()),
    FLOWER_GOD_ENDOW("花神赋", 101, TreasureEnum.FLOWER_GOD_ENDOW.getValue()),
    SMALL_PEACE_PACK_NEED_TREASURE("符•百毒不侵*2", 102, TreasureEnum.SMALL_PEACE_PACK_NEED_TREASURE.getValue()),
    MEDIUM_PEACE_PACK_NEED_TREASURE("符•百毒不侵*1、符•诸邪退散*1", 103, TreasureEnum.MEDIUM_PEACE_PACK_NEED_TREASURE.getValue()),
    LARGE_PEACE_PACK_NEED_TREASURE("符•诸邪退散*2", 104, TreasureEnum.LARGE_PEACE_PACK_NEED_TREASURE.getValue()),
    GODS_ALTAR_1("韦护神格*1、降魔杵*30", 105, TreasureEnum.GODS_ALTAR_1.getValue()),
    GODS_ALTAR_2("袁洪神格*1、水火棍*30", 106, TreasureEnum.GODS_ALTAR_2.getValue()),
    GODS_ALTAR_3("无当圣母神格*1、日月珠*30", 107, TreasureEnum.GODS_ALTAR_3.getValue()),
    GODS_ALTAR_4("天花娘娘神格*1、天花结*30", 108, TreasureEnum.GODS_ALTAR_4.getValue()),
    GODS_ALTAR_5("龟灵圣母神格*1、敕令印*30", 109, TreasureEnum.GODS_ALTAR_5.getValue()),
    GODS_ALTAR_6("韦护神格*1、衍金残页*30", 110, TreasureEnum.GODS_ALTAR_6.getValue()),
    GODS_ALTAR_7("袁洪神格*1、衍木残页*30", 111, TreasureEnum.GODS_ALTAR_7.getValue()),
    GODS_ALTAR_8("无当圣母神格*1、衍水残页*30", 112, TreasureEnum.GODS_ALTAR_8.getValue()),
    GODS_ALTAR_9("天花娘娘神格*1、衍火残页*30", 113, TreasureEnum.GODS_ALTAR_9.getValue()),
    GODS_ALTAR_10("龟灵圣母神格*1、衍土残页*30", 114, TreasureEnum.GODS_ALTAR_10.getValue()),
    SMALL_MIDSUMMER_GIFT_PACK("小扇子*2", 115, TreasureEnum.SMALL_MIDSUMMER_GIFT_PACK.getValue()),
    MID_SUMMER_GIFT_PACK("小扇子*1、喷水手枪*1", 116, TreasureEnum.MID_SUMMER_GIFT_PACK.getValue()),
    LARGE_MIDSUMMER_GIFT_PACK("喷水手枪*2", 117, TreasureEnum.LARGE_MIDSUMMER_GIFT_PACK.getValue()),
    SHIMEI_XINWU_1("思思的信物*1、玫瑰*200", 118, TreasureEnum.SHIMEI_XINWU_1.getValue()),
    SHIMEI_XINWU_2("欣欣的信物*1、玫瑰*150", 119, TreasureEnum.SHIMEI_XINWU_2.getValue()),
    SHIMEI_XINWU_3("莹莹的信物*1、玫瑰*100", 120, TreasureEnum.SHIMEI_XINWU_3.getValue()),
    SHIMEI_XINWU_4("绵绵的信物*1、玫瑰*70", 121, TreasureEnum.SHIMEI_XINWU_4.getValue()),
    SHIMEI_XINWU_5("布布的信物*1、玫瑰*50", 122, TreasureEnum.SHIMEI_XINWU_5.getValue()),
    ROSE("玫瑰", 123, TreasureEnum.ROSE.getValue()),
    JADE_BRAND("破阵玉牌", 124, TreasureEnum.JADE_BRAND.getValue()),
    CANDY("糖果", 125, TreasureEnum.CANDY.getValue()),
    CHEESE("芝士奶酪", 126, TreasureEnum.CHEESE.getValue()),
    CHICKEN_TACO("鸡肉Taco", 127, TreasureEnum.CHICKEN_TACO.getValue()),
    CREAMY_SLICES("奶香切片", 128, TreasureEnum.CREAMY_SLICES.getValue()),
    CRISPY_COOKIES("酥脆曲奇", 129, TreasureEnum.CRISPY_COOKIES.getValue()),
    HONEY_SHUFFLE("蜜糖舒芙蕾", 130, TreasureEnum.HONEY_SHUFFLE.getValue()),
    STRAWBERRY_TART("草莓挞挞", 131, TreasureEnum.STRAWBERRY_TART.getValue()),
    MELLOW_TEA("醇香酥茶", 132, TreasureEnum.MELLOW_TEA.getValue()),
    CANDY_PUMPKIN_BUCKET("糖果南瓜桶", 133, TreasureEnum.CANDY_PUMPKIN_BUCKET.getValue()),
    HALLOWEEN_PUMPKIN("万圣南瓜", 134, TreasureEnum.HALLOWEEN_PUMPKIN.getValue()),
    GUESS_COIN("竞猜币", 135, TreasureEnum.GUESS_COIN.getValue()),
    DOG_TAG("小狗牌", 136, TreasureEnum.PUPPY_BRAND.getValue()),
    DIAMOND_CONSUME_POINT("钻石消费积分", 137, TreasureEnum.DIAMOND_CONSUMPTION_POINTS.getValue()),
    DZODIAC_POINT("生效挑战积分", 138, TreasureEnum.ZODIAC_POINT.getValue()),

    ;
    /** 消费类别名称 */
    private final String name;
    /** 消费类别编号 */
    private final int value;
    /** 消费类别对应法宝的编号（Id）goodsId中0代表无对应法宝 */
    private final int goodsId;

    public static ConsumeType fromValue(int value) {
        for (ConsumeType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
    /**
     * 是否以礼包形式发放
     *
     * @return
     */
    public boolean isPriceAsPackage() {
        return this.getName().contains("*");
    }
}
