package com.bbw.god.gameuser.guide.v2;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 新手引导
 *
 * @author suhq 2018年9月30日 下午5:07:04
 */
@Getter
@AllArgsConstructor
public enum NewerGuideEnum implements Serializable {
    NONE(0, 0, 0, 0),
    START(110, 3346, 3, 2), // 起点+2
    CUNZHUANG(120, 3146, 3, 1), // 村庄+1
    XIANRENDONG(130, 3046, 3, 5), // 仙人洞+5
    KZ_BUY(140, 2546, 3, 0), // 客栈+3
    BIANZHU(150, 2546, 3, 3), // 编组
    YEGUAI(160, 2246, 3, 1), // 野地+1
    ATTACK(170, 2146, 3, 0), // 涂山-攻城
    LDF_LEVEL_UP(180, 2146, 3, 0),// 涂山-升级炼丹房
    CARD_EXP(190, 2146, 3, 0),// 涂山-收取卡牌经验
    FIRST_FIGHT_ACHIEVEMENT(200, 2146, 3, 0),// 领取首战告捷成就
    QKT_USE(210, 2146, 3, 0),// 使用乾坤图
    DFZ_USE(220, 2146, 3, 0),// 使用定风珠
    JXZ_BUY(230, 2146, 3, 0),// 聚贤庄购买卡牌
    TCP_LEVEL_UP(240, 2146, 3, 0),// 升级特产铺
    SPECIAL_BUY(250, 2146, 3, 0),// 购买特产
    CARD_LEVEL_UP(260, 2146, 3, 0);// 升级卡牌

    private Integer step;
    private Integer pos;
    private Integer dir;
    private Integer nextStepNum;

    public static NewerGuideEnum fromValue(int step) {
        for (NewerGuideEnum item : values()) {
            if (item.getStep() == step) {
                return item;
            }
        }
        return NONE;
    }
}
