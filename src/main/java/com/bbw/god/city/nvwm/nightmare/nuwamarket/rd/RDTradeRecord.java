package com.bbw.god.city.nvwm.nightmare.nuwamarket.rd;

import com.bbw.god.city.nvwm.nightmare.nuwamarket.GoodsInfo;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.UserNvWaTradeRecord;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 交易记录
 *
 * @author fzj
 * @date 2022/5/24 15:02
 */
@Data
public class RDTradeRecord extends RDSuccess {
    /** 交易类型 10买入 20卖出 */
    private Integer tradeType;
    /** 对方信息 */
    private String counterparty;
    /** 交易时间 */
    private Date tradeDate;
    /** 货品 */
    private RDProductInfo productInfos;
    /** 出价 */
    private List<RDProductPrice> productPrices;

    @Data
    public static class RDProductInfo {
        /** 商品id */
        private Integer productId;
        /** 商品数量 */
        private Integer productNum;
    }

    @Data
    public static class RDProductPrice {
        /** 价格id */
        private Integer priceId;
        /** 数量 */
        private Integer priceNum;
    }

    public static RDTradeRecord getInstance(UserNvWaTradeRecord userNvWaMarket) {
        RDTradeRecord rd = new RDTradeRecord();
        rd.setTradeType(userNvWaMarket.getTradeType());
        rd.setCounterparty(userNvWaMarket.getCounterparty());
        rd.setTradeDate(userNvWaMarket.getTradeDate());
        GoodsInfo product = userNvWaMarket.getProduct();
        RDProductInfo rdProductInfo = new RDProductInfo();
        rdProductInfo.setProductId(product.getId());
        rdProductInfo.setProductNum(product.getNum());
        rd.setProductInfos(rdProductInfo);

        List<GoodsInfo> prices = userNvWaMarket.getPrices();
        List<RDProductPrice> rdProductPrices = new ArrayList<>();
        for (GoodsInfo productPrice : prices) {
            RDProductPrice rdProductPrice = new RDProductPrice();
            rdProductPrice.setPriceId(productPrice.getId());
            rdProductPrice.setPriceNum(productPrice.getNum());
            rdProductPrices.add(rdProductPrice);
        }
        rd.setProductPrices(rdProductPrices);
        return rd;
    }
}
