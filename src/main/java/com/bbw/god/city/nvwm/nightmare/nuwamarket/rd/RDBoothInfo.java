package com.bbw.god.city.nvwm.nightmare.nuwamarket.rd;

import com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaBooth;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GoodsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 摊位详情
 *
 * @author fzj
 * @date 2022/5/9 10:56
 */
@Data
public class RDBoothInfo extends RDSuccess {
    /** 摊主信息 */
    private String ownerInfo;
    /** 摊位号 */
    private Integer boothNo;
    /** 头像 */
    private Integer head;
    /** 剩余租赁时间 */
    private Long remainTime;
    /** 摊位状态 */
    private Integer status;
    /** 商品信息 */
    private List<RDProductInfo> productInfos;
    /** 标语 */
    private String message;

    /**
     * 返还给客户端的商品信息
     */
    @Data
    public static class RDProductInfo {
        /** id */
        private Long productId;
        /** 商品Id */
        private Integer goodsId;
        /** 组数 */
        private Integer groupNum;
        /** 每组商品数量 */
        private Integer groupProductNum;
        /** 价格 */
        private List<RDProductPrice> prices;
        /** 是否有还价 */
        private boolean isHasBargain = false;
    }

    @Data
    public static class RDProductPrice {
        /** 价格编号 */
        private Integer priceId;
        /** 交易状态 */
        private Integer tradeStatus;
        /** 价格 */
        private List<RDAward> price;
    }

    public static RDProductInfo getInstance(GameNvWaBooth.ProductInfo productInfo) {
        RDProductInfo rd = new RDProductInfo();
        rd.setProductId(productInfo.getProductId());
        rd.setGoodsId(productInfo.getGoods().getId());
        Integer num = productInfo.getGoods().getNum();
        Integer minSellNum = productInfo.getMinSellNum();
        rd.setGroupNum(num / minSellNum);
        rd.setGroupProductNum(minSellNum);
        List<RDProductPrice> rdProductPrices = new ArrayList<>();
        List<GameNvWaBooth.ProductPrice> productPrices = productInfo.getProductPrices();
        for (int i = 0; i < productPrices.size(); i++) {
            RDProductPrice rdProductPrice = new RDProductPrice();
            rdProductPrice.setPriceId(i);
            rdProductPrice.setTradeStatus(productPrices.get(i).getTradeStatus());
            String price = productPrices.get(i).getPrice();
            rdProductPrice.setPrice(getRDAwards(GoodsInfo.getGoods(price)));
            rdProductPrices.add(rdProductPrice);
        }
        rd.setPrices(rdProductPrices);
        return rd;
    }


    private static List<RDAward> getRDAwards(List<GoodsInfo> goods) {
        List<Award> awards = GoodsInfo.getAwards(goods, AwardEnum.FB);
        return RDAward.getInstances(awards);
    }
}
