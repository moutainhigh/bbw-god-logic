package com.bbw.god.game.config.mall;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum FavorableBagEnum implements Serializable {
    YuanSLB("每日元素包", 1010, 40),
    // KaBLB("每日卡包", 1020),
    TiLLB("每日体力", 1030, 40),
    ShangHLB("每日商会", 1040, 40),
    ShangLLB("每日商令", 1050, 40),
    // XianYLB("每日仙缘", 1060),
    SSDLB("神沙大礼包", 1070, 0),
    SMALL_TTCJ_LB("小通天残卷礼包", 1081, 42),
    MIDDLE_TTCJ_LB("中通天残卷礼包", 1082, 42),
    BIG_TTCJ_LB("大通天残卷礼包", 1083, 42),
    SUPER_BIG_TTCJ_LB("超大通天残卷礼包", 1084, 42),

    XSLB("每周仙石礼包", 1000, 40),
    SXLB("神仙大礼包", 1001, 40),
    PaoSLB("每周跑商", 1110, 40),
    ShenSLB("每周神沙", 1120, 40),
    LingSLB("每周灵石", 1130, 40),
    ChengJLB("每周城建", 1140, 40),
    JuLLB("每周聚灵", 1150, 40),
    ShangMLB("每周商贸", 1160, 40),

    //	DayLB6("6元日礼包",1006,105),
//	DayLB30("30元日礼包",1031,105),
    DayFuLiLB("每日福利礼包", 1040, 105),
    DayDuoBaoLB("每日夺宝礼包", 1041, 105),
    DayShengJieLB("每日升阶礼包", 1042, 105),
    DayZhaoMuLB("每日招募礼包", 1043, 105),

    //	WeekLB68("68元周礼包", 1170, 100),
//	WeekLB198("198元周礼包", 1172, 100),
//	WeekLB328("328元周礼包", 1174, 100),
//	WeekLB468("668元周礼包", 1176, 100),
    WeekFuLiLB("每周福利礼包", 1101, 100),
    WeekLianJiLB("每周炼技礼包", 1105, 100),
    WeekXiuTiLB("每周修体礼包", 1103, 100),
    WeekBaoHuLB("每周保护礼包", 1102, 100),
    WeekDuoBaoLB("每周夺宝礼包", 1104, 100),
    WeekXiuDaoLB("每周修道礼包", 1106, 100),
    WeekBaoHuLB2("每周保护礼包2", 1107, 100),
    WeekXingTuLB("每周星图包", 1108, 100),

    TeHuiChaoZhiLB("特惠超值礼包", 1400, 180),
    TeHuiLB1("特惠1元礼包", 1401, 180),
    TeHuiLB3("特惠3元礼包", 1402, 180),
    TeHuiLB6("特惠6元礼包", 1403, 180),
    TeHuiLB60("特惠包周礼包", 1404, 180),

//	MonthLB30("30元月礼包", 1310, 110),
//	MonthLB68("68元月礼包", 1315, 110),
//	MonthLB198("198元月礼包", 1320, 110),
//	MonthLB328("328元月礼包", 1325, 110),
//	MonthLB468("468元月礼包", 1330, 110),
//	MonthLB648("648元月礼包", 1335, 110),

    MengXLB("萌新礼包", 1210, 44),
    MengXLB_1("1元萌新礼包", 1211, 44),
    ShangXLB("上仙礼包", 1220, 40),
    ShangXLB2("上仙礼包2", 1221, 40),
    ZhanDZL("战斗助力", 1281, 50),
    JinJZL("进阶助力", 1282, 50),
    FaBZL("法宝助力", 1283, 50),
    BORN("魔童降临礼包", 1291, 120),
    GOD_POWER_SWEEP_LB("神力横扫礼包", 1292, 120),

    CJYX_LB_1("辞旧迎新礼包1", 1510, 140),
    CJYX_LB_2("辞旧迎新礼包2", 1520, 140),

    WAR_TOKEN_68("进阶令牌", 1600, 500),
    WAR_TOKEN_98("高级进阶令牌", 1610, 500),
    XSFL1("新手福利礼包1", 1603, 185),
    XSFL2("新手福利礼包2", 1604, 185),
    XLTE3("修炼特惠礼包3", 1605, 185),
    XLTH4("修炼特惠礼包4", 1606, 185),
    ZMJX1("招募惊喜礼包1", 1607, 185),
    ZMJX2("招募惊喜礼包2", 1608, 185),
    GongCLD_GOLD("攻城略地礼包-金", 1701, 185),
    GongCLD_WOOD("攻城略地礼包-木", 1702, 185),
    GongCLD_WATER("攻城略地礼包-水", 1703, 185),
    GongCLD_FIRE("攻城略地礼包-火", 1704, 185),
    GongCLD_EARTH("攻城略地礼包-土", 1705, 185),
    HOLIDAY_GIFT_PACK_51_6("节日礼包6元", 1801, 780),
    HOLIDAY_GIFT_PACK_51_30("节日礼包30元", 1802, 780),
    HOLIDAY_GIFT_PACK_51_98("节日礼包98元", 1803, 780),
    HOLIDAY_GIFT_PACK_51_198("节日礼包198元", 1804, 780)
    ;
    private String name;
    private int value;
    private int type;

    public static FavorableBagEnum fromValue(int value) {
        for (FavorableBagEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }

        return null;
    }
}