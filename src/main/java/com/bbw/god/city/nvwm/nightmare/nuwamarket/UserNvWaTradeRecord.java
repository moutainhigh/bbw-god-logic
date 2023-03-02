package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 女娲集市
 *
 * @author fzj
 * @date 2022/5/6 16:34
 */
@Data
public class UserNvWaTradeRecord extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 交易类型 10买入 20卖出 */
    private Integer tradeType;
    /** 对方信息 */
    private String counterparty;
    /** 交易时间 */
    private Date tradeDate = DateUtil.now();
    /** 货品 */
    private GoodsInfo product;
    /** 出价 */
    private List<GoodsInfo> prices;

    public static UserNvWaTradeRecord getInstance(long uid, int tradeType, String counterparty, GoodsInfo product, List<GoodsInfo> prices) {
        UserNvWaTradeRecord userNvWaMarket = new UserNvWaTradeRecord();
        userNvWaMarket.setId(ID.INSTANCE.nextId());
        userNvWaMarket.setGameUserId(uid);
        userNvWaMarket.setTradeType(tradeType);
        userNvWaMarket.setCounterparty(counterparty);
        userNvWaMarket.setProduct(product);
        userNvWaMarket.setPrices(prices);
        return userNvWaMarket;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_NV_WA_TRADE_RECORD;
    }
}
