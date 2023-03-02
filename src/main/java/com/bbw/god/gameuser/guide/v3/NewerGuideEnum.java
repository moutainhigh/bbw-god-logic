package com.bbw.god.gameuser.guide.v3;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 新手引导
 * @date 2020年12月08日 下午2:43:04
 */
@Getter
@AllArgsConstructor
public enum NewerGuideEnum implements Serializable {
    START(0, "起点", 3046, 3, 5), // 起点仙人洞+5
    KZ_BUY(1010, "客栈买卡", 2546, 3, 0), // 客栈+3
    BIAN_ZHU_1(1020, "编组1", 2546, 3, 4), // 编组1
    ATTACK_1(1030, "涂山攻城", 2146, 3, 0), // 涂山-攻城
    JXZ(1040, "聚贤庄收取", 2146, 3, 0), // 涂山-聚贤庄收取+2
    DRAW_CARD(1050, "聚贤卡池抽卡", 2146, 3, 0), // 抽卡
    BIAN_ZHU_2(1060, "编组2", 2146, 3, 0), // 编组2
    CARD_LEVEL_UP_1(1070, "升级属性联功卡", 2146, 3, 2),// 升级卡牌1
    FD(1080, "到达福地", 1946, 3, 5), // 福地
    YOU_SHANG_GUAN(1090, "游商管买特产", 1941, 4, 6), // 游商管
    ATTACK_2(1100, "马邑攻城", 1935, 4, 0), // 马邑-攻城
    JIAOYI(1110, "特产铺交易", 1935, 4, 0),// 马邑-交易
    KC(1120, "矿场收取", 1935, 4, 0),// 马邑-升级矿产
    CARD_LEVEL_UP_2(1130, "升级卡牌方弼", 1935, 4, 0),// 升级卡牌-方弼
    CARD_LEVEL_UP_3(1140, "升级卡牌方相", 1935, 4, 6),// 升级卡牌-方相
    YE_GUAI(1150, "打野怪", 2333, 2, 0)// 野怪
    ;

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

    public static NewerGuideEnum getNextGuideEnum(NewerGuideEnum curGuideEnum) {
        List<NewerGuideEnum> list = Arrays.stream(values()).sorted(Comparator.comparing(NewerGuideEnum::getStep)).collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            NewerGuideEnum guideEnum = list.get(i);
            if (guideEnum == curGuideEnum && i != list.size() - 1) {
                return list.get(i + 1);
            }
        }
        return null;
    }
}
