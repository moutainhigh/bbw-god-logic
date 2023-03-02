package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum.BoothStatusEnum;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum.TradeStatusEnum;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 女娲集市摊位数据
 *
 * @author fzj
 * @date 2022/5/6 10:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GameNvWaBooth extends GameData implements Serializable {
    private static final long serialVersionUID = 3352313347452828250L;
    /** 玩家 */
    private Long uid;
    /** 摊位编号 */
    private Integer boothNo;
    /** 标语 */
    private String slogan;
    /** 过期时间 */
    private Date leaseEndTime;
    /** 摊位状态 */
    private Integer boothStatus = BoothStatusEnum.CLOSE_BOOTH.getValue();
    /** 商品信息 */
    private List<ProductInfo> productInfos = new ArrayList<>();
    /** 还价信息 */
    private List<BargainInfo> bargainInfos = new ArrayList<>();

    /**
     * 还价信息
     */
    @Data
    public static class BargainInfo {
        /** 还价编号 */
        private Long bargainId;
        /** 过期时间 */
        private Date expireTime;
    }

    /**
     * 商品信息
     */
    @Data
    public static class ProductInfo {
        /** 商品编号 */
        private Long productId;
        /** 商品 */
        private GoodsInfo goods;
        /** 商品最低出售数量 */
        private Integer minSellNum;
        /** 价格 */
        private List<ProductPrice> productPrices = new ArrayList<>();
    }

    /**
     * 商品价格
     */
    @Data
    public static class ProductPrice {
        /** 交易状态 */
        private Integer tradeStatus;
        /** 价格 goodId_2,good1_1*/
        private String price;
    }

    public static GameNvWaBooth getInstance(long boothId, int boothNo, Date expiredDate, long userId) {
        GameNvWaBooth gameNvWaMarket = new GameNvWaBooth();
        gameNvWaMarket.setId(boothId);
        gameNvWaMarket.setBoothNo(boothNo);
        gameNvWaMarket.setLeaseEndTime(expiredDate);
        gameNvWaMarket.setUid(userId);
        return gameNvWaMarket;
    }

    /**
     * 添加商品信息
     *
     * @param goodsId
     * @param goodsNum
     * @param minSellNum
     */
    public ProductInfo addProductInfo(int goodsId, int goodsNum, int minSellNum,  List<ProductPrice> productPrices) {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId(System.currentTimeMillis());
        GoodsInfo goods = GoodsInfo.getInstance(goodsId, goodsNum);
        productInfo.setGoods(goods);
        productInfo.setMinSellNum(minSellNum);
        productInfo.setProductPrices(productPrices);
        this.productInfos.add(productInfo);
        return productInfo;
    }

    /**
     * 更新商品
     *
     * @param productId
     */
    public void updateProduct(long productId, Product pro) {
        ProductInfo product = this.productInfos.stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        if (null == product) {
            return;
        }
        Integer goodsId = pro.getGoodsId();
        Integer num = pro.getNum();
        Integer minSellNum = pro.getMinSellNum();
        GoodsInfo goodsInfo = GoodsInfo.getInstance(goodsId, num);
        product.setMinSellNum(minSellNum);
        product.setGoods(goodsInfo);
    }

    /**
     * 更新出价
     *
     * @param productId
     * @param productPrices
     */
    public void updatePrice(long productId, List<ProductPrice> productPrices) {
        ProductInfo product = this.productInfos.stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        if (null == product) {
            return;
        }
        product.setProductPrices(productPrices);
    }

    /**
     * 下架商品
     *
     * @param productNo
     */
    public void delProductInfo(long productNo) {
        //清除数据
        this.productInfos.removeIf(p -> productNo == p.getProductId());
    }

    /**
     * 设置摊位状态
     */
    public void updateBoothStatus() {
        Integer boothStatus = this.boothStatus;
        if (boothStatus == BoothStatusEnum.CLOSE_BOOTH.getValue()) {
            this.setBoothStatus(BoothStatusEnum.OPEN_BOOTH.getValue());
            return;
        }
        this.setBoothStatus(BoothStatusEnum.CLOSE_BOOTH.getValue());
    }

    /**
     * 商品数量是否足够
     *
     * @param productId
     * @return
     */
    public boolean isEnoughProductNum(long productId) {
        ProductInfo productInfo = this.productInfos.stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        if (null == productInfo) {
            return false;
        }
        Integer productNum = productInfo.getGoods().getNum();
        Integer minSellNum = productInfo.getMinSellNum();
        return productNum >= minSellNum;
    }

    /**
     * 扣除商品
     *
     * @param productId
     */
    public void delProduct(long productId) {
        ProductInfo productInfo = this.productInfos.stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        Integer productNum = productInfo.getGoods().getNum();
        productInfo.getGoods().setNum(productNum - productInfo.getMinSellNum());
    }

    /**
     * 更新出价状态
     *
     * @param productId
     * @param priceNo
     */
    public void updatePriceWayStatus(long productId, int priceNo) {
        ProductInfo productInfo = this.productInfos.stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
        if (null == productInfo) {
            return;
        }
        List<ProductPrice> productPrices = productInfo.getProductPrices();
        ProductPrice productPrice = productPrices.get(priceNo);
        if (null == productPrice) {
            return;
        }
        productPrice.setTradeStatus(TradeStatusEnum.ALREADY_TRADED.getValue());
    }

    /**
     * 添加还价信息
     *
     * @param expireTime
     */
    public void addBargainInfo(long bargainId, Date expireTime) {
        BargainInfo bargainInfo = new BargainInfo();
        bargainInfo.setBargainId(bargainId);
        bargainInfo.setExpireTime(expireTime);
        this.bargainInfos.add(bargainInfo);
    }

    /**
     * 检查是否过期
     *
     * @return
     */
    public boolean isExpired(){
        return DateUtil.now().after(this.leaseEndTime);
    }

    /**
     * 删除还价
     *
     * @param bargainIds
     */
    public void delBargain(List<Long> bargainIds) {
        this.bargainInfos.removeIf(b -> bargainIds.contains(b.bargainId));
    }

    /**
     * 获得商品信息
     *
     * @param productId
     * @return
     */
    public ProductInfo getProduct(long productId) {
        return this.productInfos.stream().filter(p -> productId == p.getProductId()).findFirst().orElse(null);
    }


    @Override
    public GameDataType gainDataType() {
        return GameDataType.NV_WA_MARKET;
    }
}
