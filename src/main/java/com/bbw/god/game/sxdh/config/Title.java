package com.bbw.god.game.sxdh.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 称号
 *
 * @author suhq
 * @date 2019-06-21 11:33:21
 */
@Deprecated
@Getter
@AllArgsConstructor
public enum Title {

    FAN_REN("凡人", 1),
    DAO_TONG("道童", 2),
    DAO_REN("道人", 3),
    DAO_SI("道士", 4),
    DAO_ZHANG("道长", 5),
    JIAO_ZONG("教众", 6),
    JIAO_SHOU("教首", 7),
    ZHEN_REN("真人", 8),
    SAN_XIAN("散仙", 9),
    ZHEN_XIAN("真仙", 10),
    DA_SHI("大师", 11),
    SHANG_SHI("上师", 12),
    DAO_SHI("道师", 13),
    DU_JIE("渡劫", 14),
    SHEN_XIAN("神仙", 15),
    SHANG_XIAN("上仙", 16),
    FEI_XIAN("飞仙", 17),
    ZHANG_LAO("长老", 18),
    XING_JUN("星君", 19),
    YUAN_SHEN("元神", 20),
    JIAO_ZHU("教主", 21),
    TIAN_ZUN("天尊", 22);

    private String name;
    private int value;

    public static Title fromValue(int value) {
        for (Title item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static Title fromName(String name) {
        for (Title item : values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
