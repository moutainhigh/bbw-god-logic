package com.bbw.god.mall.store;

import com.bbw.exception.ExceptionForClientTip;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lwb
 * @date 2020/3/24 9:59
 */
@Getter
@AllArgsConstructor
public enum StoreEnum {
    // 商店
    FST("封神台", 60, false),
    ZXZ("诛仙阵", 70, false),
    MAOU("魔王商店", 130, false),
    COC("商会商店", 150, true),
    GUILD("行会商店", 160, true),
    SXDH("神仙大会商店", 170, true),
    DFDJ("巅峰对决商店", 155, true),
    HORSE_RACING("赛马商店", 300, false),
    WAR_TOKEN("战令商店", 400, false),
    TRANSMIGRATION_MALL("轮回商店", 600, false),
    GLORY_COIN_STORE("荣耀币商店", 770, false),
    CHINESE_ZODIAC_COLLISION("生肖对碰", 790, false),

    ;
    private String name;
    private int type;
    private boolean indep;//是否独立于商城配置

    public static boolean cfgByMallConfig(int type) {
        for (StoreEnum storeEnum : values()) {
            if (storeEnum.getType() == type) {
                return !storeEnum.indep;
            }
        }
        throw new ExceptionForClientTip("store.goods.no.exists");
    }
}
