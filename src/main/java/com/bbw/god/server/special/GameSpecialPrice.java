package com.bbw.god.server.special;

import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 全服合成特产价格
 * @date 2020/11/11 17:24
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class GameSpecialPrice extends GameData {
    private Integer specialId;
    private Integer serverGroup;
    private Integer highPriceCountry;// 特产高价出售区域
    private Integer minPrice;// 最低价格
    private Integer maxPrice;// 最高价格

    public static GameSpecialPrice getInstance(int specialId, int serverGroup, int highPriceCountry, int minPrice, int maxPrice) {
        GameSpecialPrice gameSpecialPrice = new GameSpecialPrice();
        gameSpecialPrice.setId(ID.INSTANCE.nextId());
        gameSpecialPrice.setServerGroup(serverGroup);
        gameSpecialPrice.setSpecialId(specialId);
        gameSpecialPrice.setHighPriceCountry(highPriceCountry);
        gameSpecialPrice.setMinPrice(minPrice);
        gameSpecialPrice.setMaxPrice(maxPrice);
        return gameSpecialPrice;
    }

    /**
     * 资源类型的字符串
     *
     * @return
     */
    @Override
    public GameDataType gainDataType() {
        return GameDataType.GAME_SPECIAL_PRICE;
    }
}
