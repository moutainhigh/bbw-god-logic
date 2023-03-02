package com.bbw.god.server.special;

import com.bbw.common.ID;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 特产城市价格
 *
 * @author suhq
 * @date 2018年10月23日 上午10:11:19
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ServerSpecialCityPrice extends ServerData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer specialId;
    private Integer specialCountry;// 特产出产区域
    private Integer cityId;
    private Integer cityCountry;// 城池所在区域
    private Integer initPrice;// 初始化时的价格
    private Integer price;// 玩家特产出售价格

    public static ServerSpecialCityPrice instance(CfgSpecialEntity special, CfgCityEntity city, int price, int sId) {
        ServerSpecialCityPrice sscPrice = new ServerSpecialCityPrice();
        sscPrice.setId(ID.INSTANCE.nextId());
        sscPrice.setSid(sId);
        sscPrice.setSpecialId(special.getId());
        sscPrice.setSpecialCountry(special.getCountry());
        sscPrice.setCityId(city.getId());
        sscPrice.setCityCountry(city.getCountry());
        sscPrice.setInitPrice(price);
        sscPrice.setPrice(price);

        return sscPrice;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.SPECIAL_PRICE;
    }
}