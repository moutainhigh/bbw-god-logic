package com.bbw.god.gm.admin;

import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.game.config.card.CfgDeifyCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存枚举
 *
 * @author lwb
 */

@Getter
@AllArgsConstructor
public enum CfgAdminType {
    SERVER(5, "区服", "CfgServerEntity", CfgServerEntity.class),
    CARD(10, "卡牌", "CfgCardEntity", CfgCardEntity.class),
    TREASURE(20, "法宝", "CfgTreasureEntity", CfgTreasureEntity.class),
    SKILL(30, "卡牌技能", "CfgCardSkill", CfgCardSkill.class),
    CITY(40, "城市", "CfgCityEntity", CfgCityEntity.class),
    CfgDeifyCardEntity(50, "封神卡", "CfgDeifyCardEntity", CfgDeifyCardEntity.class),
    ;
    private final int type;
    private final String memo;
    private final String key;
    private final Class<? extends CfgEntityInterface> entityClass;

    public static CfgAdminType fromVal(Integer type) {
        if (type == null) {
            return null;
        }
        for (CfgAdminType cacheEnum : values()) {
            if (cacheEnum.getType() == type) {
                return cacheEnum;
            }
        }
        return null;
    }

    /**
     * 根据key，获取数据类型
     *
     * @param key
     * @return
     */
    public static CfgAdminType fromClass(String key) {
        for (CfgAdminType item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
