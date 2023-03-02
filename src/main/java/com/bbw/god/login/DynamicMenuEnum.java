package com.bbw.god.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月5日 上午10:16:10
 * 类说明 动态菜单枚举
 */
@Getter
@AllArgsConstructor
public enum DynamicMenuEnum {
    ZYNS(3001, "治愈女神"),
    ZDZL(3002, "战斗助力"),
    JJZL(3003, "进击助力"),
    FBZL(3004, "法宝助力"),
    ACHIEVE(3005, "成就"),
    JRLB(3006, "节日礼包"), // 如仙师9080 礼包id 1291
    SLHS(3007, "神力横扫"),
    YXGL(3009, "英雄归来"),
    FRIENDS(3010, "添加好友"),
    MAIL(3011, "邮件"),
    SALARY(3012, "俸禄"),
    MG(3013, "友怪"),
    MAOU_COMING(3014, "魔王"),
    MAOU_ATTACK(3015, "魔王"),
    DJT(3016, "点将台"),
    LYLB(3017, "灵印礼包"),
    JRHD(3018, "节日活动"),
    JIZHANG(3019, "激战"),
    TOTAL_RECHARGE(3020, "累计充值"),
    DAY_RECHARGE(3021, "累天充值"),
    ADVENTURE(3022, "奇遇"),
    WANXIAN_SIGN_UP(3101, "万仙阵报名图标"),
    WANXIAN_CHAMPION_PREDICTION(3102, "万仙阵冠军预测图标"),
    NEWER_WELFARE(3103, "新手福利"),
    CHAN_JIE(3104, "阐截斗法"),
    QUESTIONNAIRE(3105, "问卷调查"),
    CHAN_JIE2(3106, "阐截斗法2"),
    WANXIAN_LOG(3107, "万仙阵战报"),
    TRANSMIGRATION(3108, "轮回世界"),
    NEW_USER_SEVEN_LOGIN_1(3111, "七日之约1日"),
    NEW_USER_SEVEN_LOGIN_2(3112, "七日之约2日"),
    NEW_USER_SEVEN_LOGIN_3(3113, "七日之约3日"),
    NEW_USER_SEVEN_LOGIN_4(3114, "七日之约4日"),
    NEW_USER_SEVEN_LOGIN_5(3115, "七日之约5日"),
    NEW_USER_SEVEN_LOGIN_6(3116, "七日之约6日"),
    NEW_USER_SEVEN_LOGIN_7(3117, "七日之约7日"),
    DFDJ(3118, "巅峰对决"),
    GOD_TRAINING_TASK(3119, "上仙试炼"),
    DIG_FOR_TREASURE(3120, "挖宝"),
    SHEN_WU_XIAN_SHI(3130, "神物现世"),
    ROLE_TIME_LIMIT_BAG(3140, "限时礼包"),
    NORMAL_JRHD(3141, "普通节日活动"),
    COMBINED_SERVICE(3150, "合服活动"),
    SERIES_OF_ACTIVITIE(3160, "系列活动"),
    NORMAL_JRHD_51(3170, "普通节日活动_51"),
    NORMAL_JRHD_52(3180, "普通节日活动_52"),
    ;
    private final int val;
    private final String memo;

    public static int getGiftPack(int goodId) {
        switch (goodId) {
            case 1281:
            case 1282:
            case 1283:
                //助力礼包不在这
                return 0;
            case 1291:
                return JRLB.getVal();
            case 1292:
                //神力横扫主界面移除
                return 0;
            default:
                break;
        }
        return 0;
    }
}
