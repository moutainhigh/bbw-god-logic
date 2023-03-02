package com.bbw.god.gameuser.guide.v1;

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
    START(1, "起点", 3346, 3, 2), // 起点+2
    CUNZHUANG(2, "村庄", 3146, 3, 1), // 村庄+1
    XIANRENDONG(3, "仙人洞", 3046, 3, 5), // 仙人洞+5
    KZ_BUY(4, "客栈", 2546, 3, 0), // 客栈+3
    BIANZHU(5, "编组", 2546, 3, 3), // 编组
    YEGUAI(6, "野地", 2246, 3, 1), // 野地+1
    ATTACK(7, "涂山攻城", 2146, 3, 0), // 涂山-攻城
    QIANZHUANG(8, "钱庄收取", 2146, 3, 5), // 涂山-钱庄收取+2
    JIAOYI(9, "交易", 1942, 4, 0),// 青州交易
    CARD_LEVEL_UP(10, "升级卡牌", 1942, 4, 0);// 升级卡牌

    private final Integer step;
    private final String name;
    private final Integer pos;
    private final Integer dir;
    private final Integer nextStepNum;

    public static NewerGuideEnum fromValue(int step) {
        for (NewerGuideEnum item : values()) {
            if (item.getStep() == step) {
                return item;
            }
        }
        return null;
    }
}
