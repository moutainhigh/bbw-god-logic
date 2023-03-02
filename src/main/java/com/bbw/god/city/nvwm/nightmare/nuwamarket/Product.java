package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import lombok.Data;

/**
 * 商品信息对象
 *
 * @author fzj
 * @date 2022/5/26 16:48
 */
@Data
public class Product {
    /** 商品id */
    private Integer goodsId;
    /** 商品数量 */
    private Integer num;
    /** 最低出售数量 */
    private Integer minSellNum;

    public static Product getInstance(String productInfo) {
        Product pro = new Product();
        String product = productInfo.split(":")[0];
        String[] proInfo = product.split("_");
        int proNum = Integer.parseInt(proInfo[1]) * Integer.parseInt(proInfo[2]);
        int proId = Integer.parseInt(proInfo[0]);
        pro.setGoodsId(proId);
        pro.setNum(proNum);
        pro.setMinSellNum(Integer.parseInt(proInfo[1]));
        return pro;
    }

    public static Product getProduct(String productInfo) {
        Product pro = new Product();
        String[] proInfo = productInfo.split("_");
        int proNum = Integer.parseInt(proInfo[1]) * Integer.parseInt(proInfo[2]);
        int proId = Integer.parseInt(proInfo[0]);
        pro.setGoodsId(proId);
        pro.setNum(proNum);
        pro.setMinSellNum(Integer.parseInt(proInfo[1]));
        return pro;
    }
}
