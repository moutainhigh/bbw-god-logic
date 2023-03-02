package com.bbw.god.mall;

import com.bbw.god.game.config.mall.CfgMallEntity;
import lombok.Data;

import java.util.List;

/**
 * 购物车信息类
 * @author longwh
 * @date 2022/9/20 16:09
 */
@Data
public class CartBuyInfo {

    /** 已购买商品列表 */
    private List<BuyMallInfo> buyMallInfos;

    @Data
    public static class BuyMallInfo{
        /** 原商品 */
        private CfgMallEntity mall;

        /** 记录id */
        private long recordId;

        /** 购买数量 */
        private Integer buyNum;
    }
}