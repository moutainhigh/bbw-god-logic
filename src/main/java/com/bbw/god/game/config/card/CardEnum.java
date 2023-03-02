package com.bbw.god.game.config.card;

import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 说明：部分卡牌枚举
 *
 * @author lwb
 * date 2021-04-25
 */
@Getter
@AllArgsConstructor
public enum CardEnum {
    YANG_JIAN(102, "杨戬"),
    HONG_YAO(154, "闳夭"),
    YU_YI_XIAN(225, "羽翼仙"),
    GOD_JIANG_ZY(10101, "神·姜子牙"),
    GOD_YANG_JIAN(10102, "神·杨戬"),
    GOD_ZHAO_GM(10302, "神·赵公明"),
    GOD_CHONG_HOU_HU(10325, "神·崇侯虎"),
    ZHOU_WANG(525, "纣王"),
    XIAO_TIAN(551, "哮天犬"),
    QI_LIN(547, "麒麟"),
    WEN_DAO_REN(257, "蚊道人"),
    FEI_WEN(258, "飞蚊"),
    JU_WEN(259, "巨蚊"),
    LONG_WEN(260, "龙蚊"),
    XIAN_THH(360, "玄坛黑虎"),
    TAI_YXJ(362, "太阴星君"),
    LEADER_CARD(LeaderCardTool.getLeaderCardId(), "主角卡"),
    GUI_BING(424, "鬼兵"),
    JIN_LING_SHENG_MU(461, "金灵圣母"),
    GAO_JI_NENG(462, "高继能"),
    CHI_JING_ZI(464, "赤精子"),
    JINJING_TIGER(142, "金睛白虎"),
    TENG_SNAKE(249, "滕蛇"),
    ZHAO_GONG_MING(302, "赵公明"),
    ZHONG_SI(264, "螽斯"),
    QIU_YU(561, "犰狳");
    private int cardId;
    private String name;
}