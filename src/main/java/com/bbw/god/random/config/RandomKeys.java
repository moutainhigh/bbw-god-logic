package com.bbw.god.random.config;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌随机策略key集合
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-10 22:08
 */
public class RandomKeys {
    // 常数-----------------------
    public static final int NO_LIMIT = -1;// 不限制
    public static final String NO_LIMIT_STRING = "-1";// 不限制
    /**
     * 参数引导标识
     */
    public static final String PARAM_PREFIX = "$";
    public static final String POWER_STAR_1 = "1星灵石";
    public static final String POWER_STAR_2 = "2星灵石";
    public static final String POWER_STAR_3 = "3星灵石";
    public static final String POWER_STAR_4 = "4星灵石";
    public static final String POWER_STAR_5 = "5星灵石";
    /**
     * 万能灵石对应的卡牌ID与在法宝中定义的ID一致。[810,820,830,840,850]
     */
    public static final int[] POWER_STAR_IDS = {TreasureEnum.WNLS1.getValue(), TreasureEnum.WNLS2
            .getValue(), TreasureEnum.WNLS3.getValue(), TreasureEnum.WNLS4.getValue(), TreasureEnum.WNLS5.getValue()};
    public static final String[] POWER_STAR_NAMES = {"1星灵石", "2星灵石", "3星灵石", "4星灵石", "5星灵石"};
    // ------策略名
    public static final String NvWM = "女娲庙";
    public static final String TAIYF_5 = "太一府_5个";
    public static final String TAIYF_10 = "太一府_10个";
    public static final String TAIYF_15 = "太一府_15个";
    public static final String TAIYF_20 = "太一府_20个";
    public static final String TAIYF_25 = "太一府_25个";
    public static final String CITY_CARD_1 = "1级城池_攻城振兴";
    public static final String CITY_CARD_2 = "2级城池_攻城振兴";
    public static final String CITY_CARD_3 = "3级城池_攻城振兴";
    public static final String CITY_CARD_4 = "4级城池_攻城振兴";
    public static final String CITY_CARD_5 = "5级城池_攻城振兴";

    public static final String YEGUAI_FRIEND_CARD_1 = "普通友怪_玩家等级[1,9]";
    public static final String YEGUAI_FRIEND_CARD_2 = "普通友怪_玩家等级[10,19]";
    public static final String YEGUAI_FRIEND_CARD_3 = "普通友怪_玩家等级[20,29]";
    public static final String YEGUAI_FRIEND_CARD_4 = "普通友怪_玩家等级[30,39]";
    public static final String YEGUAI_FRIEND_CARD_5 = "普通友怪_玩家等级[40,999]";

    public static final String YEGUAI_ELITE__FRIEND_CARD_1 = "精英友怪_玩家等级[20,29]";
    public static final String YEGUAI_ELITE__FRIEND_CARD_2 = "精英友怪_玩家等级[30,39]";
    public static final String YEGUAI_ELITE__FRIEND_CARD_3 = "精英友怪_玩家等级[40,999]";

    public static final String RECHARGE_RANK_ADDITION_CARD = "充值榜第一名加奖";
//    public static final String MENGXIN_PACKAGE = "萌新礼包";
//    public static final String GOLD_CONSUME_CARD = "消费福利可选卡牌";

    public static final String CUN_ZHUANG_NORMAL = "村庄_一般情况";
    public static final String CUN_ZHUANG_NEWER_GUIDE = "村庄_新手引导";
    public static final String KE_ZHAN_NORMAL = "客栈_一般情况";
    public static final String KE_ZHAN_NEWER_GUIDE = "客栈_新手引导";
    //    public static final String JU_XIAN_ZHUANG = "聚贤庄";
    public static final String JU_XIAN_ZHUANG_PRE_TWO = "聚贤庄_前两张卡";
    public static final String JU_XIAN_ZHUANG_THIRD = "聚贤庄_第三张卡";
    //    public static final String JU_XIAN_ZHUANG_NEW_GUIDER = "聚贤庄_新手引导";
    public static final String SZGY = "神仙_送子观音";
    public static final String DFS = "神仙_大福神";
    public static final String DFS_NEW_GUIDER = "神仙_大福神_新手引导";
    public static final String MiaoY_UPUP = "庙宇_上上签";
    public static final String MiaoY_UP = "庙宇_上签";


    /**
     * 万能灵石设置为卡牌。万能灵石对应的卡牌ID与在法宝中定义的ID一致。[810,820,830,840,850]
     *
     * @return
     */
    public static List<CfgCardEntity> getPowerStarCards() {
        List<CfgCardEntity> stars = new ArrayList<>();
        for (int i = 0; i < POWER_STAR_IDS.length; i++) {
            CfgCardEntity card = new CfgCardEntity();
            card.setId(POWER_STAR_IDS[i]);
            card.setName(POWER_STAR_NAMES[i]);
            card.setStar(i + 1);
            card.setType(0);
            card.setWay(5);
            stars.add(card);
        }
        return stars;
    }

    /**
     * 是否是灵石卡牌
     *
     * @param cardId
     * @return
     */
    public static boolean isPowerStarCard(int cardId) {
        for (int i = 0; i < POWER_STAR_IDS.length; i++) {
            if (cardId == POWER_STAR_IDS[i]) {
                return true;
            }
        }
        return false;
    }
}
