package com.bbw.god.game.combat.runes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 部分特殊判定的护身符枚举
 * @author lwb
 * @date 2020/9/18 10:28
 */
@Getter
@AllArgsConstructor
public enum RunesEnum {
    //天官赐福
    TGCF(131000),
    //一鼓作气
    YGZQ(131001),
    //天道轮回
    TDLH(131002),
    //法外分身
    FWFS(131003),
    //鬼蜮伎俩
    GYJL(131004),
    //妖族血脉
    YZXM(131005),
    //天道轮回(妖族来犯专用)
    TDLHI(131006),
    //五行之气
    WXZQ(131007),
    //灭魄符
    MIE_PO(131160),
    //挑离符
    TIAO_LI(131200),
    //天斩符
    TIAN_ZHAN(131310),
    //群嘲
    QUN_CHAO(131260),
    //震慑符
    ZHNE_SHE(131510),
    //威慑符
    WEI_SHE(131210),
    //天劫符
    TIAN_JIE(131430),
    //天赋符
    TIAN_FU(131360),
    //天魔符
    TIAN_MO(131580),
    //地魔符
    DI_MO(131590),
    //人魔符
    REN_MO(131600),
    //哼哈符
    HENG_HA(131610),
    //雷纹符
    LEI_WEN(131620),
    //毒纹符
    DU_WEN(131630),
    //火纹符
    HUO_WEN(131640),
    //刃纹符
    REN_WEN(131650),
    //激将符
    JI_JIANG(131660),
    //军师符
    JUN_SHI(131670),
    //不动符
    BU_DONG(131680),
    //净火符
    JING_HUO(131690),

    //神针符图
    SHEN_ZHEN_PLAYER_1(232001),
    SHEN_ZHEN_PLAYER_2(232002),
    SHEN_ZHEN_PLAYER_3(232003),
    SHEN_ZHEN_PLAYER_4(232004),
    SHEN_ZHEN_PLAYER_5(232005),
    // 革土符图
    GE_TU_PLAYER_5(232015),
    // 革木符图
    GE_MU_PLAYER_5(232025),

    //飞花符图
    FEI_HUA_PLAYER_1(232101),
    FEI_HUA_PLAYER_2(232102),
    FEI_HUA_PLAYER_3(232103),
    FEI_HUA_PLAYER_4(232104),
    FEI_HUA_PLAYER_5(232105),
    // 枝水符图
    ZHI_SHUI_PLAYER_5(232115),
    // 枝土符图
    ZHI_TU_PLAYER_5(232125),

    //凝冰符图
    NING_BING_PLAYER_1(232201),
    NING_BING_PLAYER_2(232202),
    NING_BING_PLAYER_3(232203),
    NING_BING_PLAYER_4(232204),
    NING_BING_PLAYER_5(232205),
    // 冰金符图
    BING_JIN_PLAYER_5(232215),
    // 冰火符图
    BING_HUO_PLAYER_5(232225),
    //炽炎符图
    CHI_YAN_PLAYER_1(232301),
    CHI_YAN_PLAYER_2(232302),
    CHI_YAN_PLAYER_3(232303),
    CHI_YAN_PLAYER_4(232304),
    CHI_YAN_PLAYER_5(232305),
    // 炎木符图
    YAN_MU_PLAYER_5(232315),
    // 炎金符图
    YAN_JIN_PLAYER_5(232325),
    //烈石符图
    LIE_SHI_PLAYER_1(232401),
    LIE_SHI_PLAYER_2(232402),
    LIE_SHI_PLAYER_3(232403),
    LIE_SHI_PLAYER_4(232404),
    LIE_SHI_PLAYER_5(232405),
    // 石火符图
    SHI_HUO_PLAYER_5(232415),
    // 石水符图
    SHI_SHUI_PLAYER_5(232425),
    //鎏金符图
    LIU_JIN_PLAYER_1(233001),
    LIU_JIN_PLAYER_2(233002),
    LIU_JIN_PLAYER_3(233003),
    LIU_JIN_PLAYER_4(233004),
    LIU_JIN_PLAYER_5(233005),
    // 鎏土符图
    LIU_TU_PLAYER_5(233015),
    // 鎏木符图
    LIU_MU_PLAYER_5(233025),
    //落木符图
    LUO_MU_PLAYER_1(233101),
    LUO_MU_PLAYER_2(233102),
    LUO_MU_PLAYER_3(233103),
    LUO_MU_PLAYER_4(233104),
    LUO_MU_PLAYER_5(233105),
    // 叶水符图
    YE_SHUI_PLAYER_5(233115),
    // 叶土符图
    YE_TU_PLAYER_5(233125),
    //甘霖符图
    GAN_LIN_PLAYER_1(233201),
    GAN_LIN_PLAYER_2(233202),
    GAN_LIN_PLAYER_3(233203),
    GAN_LIN_PLAYER_4(233204),
    GAN_LIN_PLAYER_5(233205),
    // 霖金符图
    LIN_JIN_PLAYER_5(233215),
    // 霖火符图
    LIN_HUO_PLAYER_5(233225),
    //燎原符图
    LIAO_YUAN_PLAYER_1(233301),
    LIAO_YUAN_PLAYER_2(233302),
    LIAO_YUAN_PLAYER_3(233303),
    LIAO_YUAN_PLAYER_4(233304),
    LIAO_YUAN_PLAYER_5(233305),
    // 燎木符图
    LIAO_MU_PLAYER_5(233315),
    // 燎金符图
    LIAO_JIN_PLAYER_5(233325),
    //净土符图
    JING_TU_PLAYER_1(233401),
    JING_TU_PLAYER_2(233402),
    JING_TU_PLAYER_3(233403),
    JING_TU_PLAYER_4(233404),
    JING_TU_PLAYER_5(233405),
    // 山火符图
    SHAN_HUO_PLAYER_5(233415),
    // 山水符图
    SHAN_SHUI_PLAYER_5(233425),
    //固本符图
    GU_BEN_PLAYER_1(234001),
    GU_BEN_PLAYER_2(234002),
    GU_BEN_PLAYER_3(234003),
    GU_BEN_PLAYER_4(234004),
    GU_BEN_PLAYER_5(234005),

    //火盾符图
    HUO_DUN_PLAYER(231001),
    //雷盾符图
    LEI_DUN_PLAYER(231002),
    //毒盾符图
    DU_DUN_PLAYER(231003),
    //护盾符图
    HU_DUN_PLAYER(231004),
    //雷纹符图
    LEI_WEN_PLAYER(231005),
    //毒纹符图
    DU_WEN_PLAYER(231006),
    //火纹符图
    HUO_WEN_PLAYER(231007),
    //刃纹符图
    REN_WEN_PLAYER(231008),
    //禁法符图
    JIN_FA_PLAYER(231009),
    //升仙符图
    SHENG_XIAN_PLAYER(231010),
    //禁天符图
    JIN_TIAN_PLAYER(231011),

    //健体符图
    JIAN_TI_PLAYER(231101),
    //克火符图
    KE_HUO_PLAYER(231102),
    //震慑符图
    ZHEN_SHE_PLAYER(231103),
    //鼓舞符图
    GU_WU_PLAYER(231104),
    //激将符图
    JI_JIANG_PLAYER(231105),
    //穿刺符图
    CHUAN_CI_PLAYER(231106),
    //蛊惑符图
    GU_HUO_PLAYER(231107),
    //得道符图
    DE_DAO_PLAYER(231108),
    //飞天符图
    FEI_TIAN_PLAYER(231109),
    //无相符图
    WU_XIANG_PLAYER(231110),
    //掩杀符图
    YAN_SHA_PLAYER(231111),

    //灵动符图
    LING_DONG_PLAYER(231201),
    //自愈符图
    ZI_YU_PLAYER(231202),
    //军师符图
    JUN_SHI_PLAYER(231203),
    //神剑符图
    SHEN_JIAN_PLAYER(231204),
    //固灵符图
    GU_LING_PLAYER(231205),
    //固定符图
    GU_DING_PLAYER(231206),
    //治愈符图
    ZHI_YU_PLAYER(231207),
    //解禁符图
    JIE_JIN_PLAYER(231208),

    //不动符图
    BU_DONG_PLAYER(231301),
    //红砂符图
    HONG_SHA_PLAYER(231302),
    //固地符图
    GU_DI_PLAYER(231303),
    //侵蚀符图
    QIN_SHI_PLAYER(231304),
    //晦星符图
//    HUI_XING_PLAYER(231305),
    //辟土符图
    PI_TU_PLAYER(231306),
    // 冷箭符图
    LENG_JIAN_PLAYER(231307),

    //挑离符图
    TIAO_LI_PLAYER(231401),
    //斩魂符图
    ZHAN_HUN_PLAYER(231402),
    //长生符图
    CHANG_SHENG_PLAYER(231403),
    //疾军符图
    JI_JUN_PLAYER(231404),
    //召魂符图
    ZHAO_HUN_PLAYER(231405),
    //金刚符图
    JIN_GANG_PLAYER(231406),

    //毒火词条
    DU_HUO_ENTRY(331001),
    //毒雷词条
    DU_LEI_ENTRY(331002),
    //火毒词条
    HUO_DU_ENTRY(331003),
    //火雷词条
    HUO_LEI_ENTRY(331004),
    //雷毒词条
    LEI_DU_ENTRY(331005),
    //雷火词条
    LEI_HUO_ENTRY(331006),
    //毒箭词条
    DU_JIAN_ENTRY(331007),
    //火箭词条
    HUO_JIAN_ENTRY(331008),
    //雷箭词条
    LEI_JIAN_ENTRY(331009),

    //劫血词条
    JIE_XUE_ENTRY(331101),
    //藤甲词条
    TENG_JIA_ENTRY(331102),
    //入骨词条
    RU_GU_ENTRY(331103),
    //续航词条
    XU_HANG_ENTRY(331104),
    //巧击词条
    QIAO_JI_ENTRY(331105),
    //顺风词条
    SHUN_FENG_ENTRY(331106),
    //勇猛词条
    YONG_MENG_ENTRY(331107),
    //坚守词条
    JIAN_SHOU_ENTRY(331108),

    //蛇毒词条
    SHE_DU_ENTRY(331201),
    //烈火词条
    LIE_HUO_ENTRY(331202),
    //闪雷词条
    SHAN_LEI_ENTRY(331203),
    //箭阵词条
    JIAN_ZHEN_ENTRY(331204),
    //压制词条
    YA_ZHI_ENTRY(331205),
    //联合词条
    LIAN_HE_ENTRY(331206),
    //强攻词条
    QIANG_GONG_ENTRY(331207),
    //吸取词条
    XI_QU_ENTRY(331208),

    //偏移词条
    PIAN_YI_ENTRY(331301),
    //灵敏词条
    LING_MIN_ENTRY(331302),
    //反甲词条
    FAN_JIA_ENTRY(331303),
    //金水词条
    JIN_SHUI_ENTRY(331304),
    //水木词条
    SHUI_MU_ENTRY(331305),
    //木火词条
    MU_HUO_ENTRY(331306),
    //火土词条
    HUO_TU_ENTRY(331307),
    //土金词条
    TU_JIN_ENTRY(331308),

    //溅射词条
    JIAN_SHE_ENTRY(331401),
    //硬甲词条
    YING_JIA_ENTRY(331402),
    //残影词条
    CAN_YING_ENTRY(331403),
    //撤离词条
    CHE_LI_ENTRY(331404),
    //易位词条
    YI_WEI_ENTRY(331405),
    //轮转词条
    LUN_ZHUAN_ENTRY(331406),
    //陨星词条
    YUN_XING_ENTRY(331407),
    //奋战词条
    FEN_ZHAN_ENTRY(331408),
    //助威词条
    ZHU_WEI_ENTRY(331409),

    //号令词条
    HAO_LING_ENTRY(332001),
    //健体词条
    JIAN_TI_ENTRY(332002),
    //防守词条
    FANG_SHOU_ENTRY(332003),
    //益毒词条
    YI_DU_ENTRY(332004),
    //益火词条
    YI_HUO_ENTRY(332005),
    //益雷词条
    YI_LEI_ENTRY(332006),
    //益狙词条
    YI_JU_ENTRY(332007),
    //星术词条
    XING_SHU_ENTRY(332008),
    //气魄词条
    QI_PO_ENTRY(332009),
    //胆识词条
    DAN_SHI_ENTRY(332010),
    //调理词条
    TIAO_LI_ENTRY(332011),
    //灵装词条
    LING_ZHUANG_ENTRY(331209),
    //狂暴词条
    KUANG_BAO_ERTRY(333001),
    // 疾风词条
    JI_FENG_ENTRY(333101),
    // 骤雨词条
    ZHOU_YU_ENTRY(333102),
    // 引雷词条
    YIN_LEI_ENTRY(333103),
    // 百草词条
    BAI_CAO_ENTRY(333104),
    // 咒血词条
    ZHOU_XUE_ENTRY(333105),
    // 夜袭词条
    YE_XI_ENTRY(333106),
    // 豁免词条
    HUO_MIAN_ENTRY(333107),
    // 深入词条
    SHEN_RU_ENTRY(333108),
    // 同命词条
    TONG_MING_ENTRY(333109),
    // 策反词条
    CE_FAN_ENTRY(333110),
    // 反噬词条
    FAN_SHI_ENTRY(333201),
    // 离间词条
    LI_JIAN_ENTRY(333202),
    // 僵直词条
    JIANG_ZHI_ENTRY(333203),
    // 禁言词条
    JIN_YAN_ENTRY(333204),
    // 沉默词条
    CHEN_MO_ENTRY(333205),
    // 墓守词条
    MU_SHOU_ENTRY(333206),
    // 同步词条
    TONG_BU_ENTRY(333207),
    // 镜甲词条
    JING_JIA_ENTRY(333208),
    // 乱流词条
    LUAN_LIU_ENTRY(333209),
    // 刺骨词条
    CI_GU_ENTRY(333210),
    // 衰弱词条
    SHUAI_RUO_ENTRY(333211),
    // 致命词条
    ZHI_MING_ENTRY(333212),
    // 烧伤词条
    SHAO_SHANG_ENTRY(333213),
    // 导电词条
    DAO_DIAN_ENTRY(333214),
    // 毒爆词条
    DU_BAO_ENTRY(333215),
    // 血代词条
    XUE_DAI_ENTRY(333301),
    // 气流词条
    QI_LIU_ENTRY(333302),
    // 分离词条
    FEN_LI_ENTRY(333303),
    // 集中词条
    JI_ZHONG_ENTRY(333304),
    // 追击词条
    ZHUI_JI_ENTRY(333305),
    //卡牌装备效果（不包含主角卡）
    CARD_EQUIPMENT(900001),
    //长生词条:诛仙阵专用,用来给敌方召唤师复活
    CHANG_SHENG_ENTRY(900002),
    ;
    private int runesId;
}