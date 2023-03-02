package com.bbw.god.pay;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.IpUtil;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.CfgUSAIps;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值产品
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-03 16:43
 */
@Data
public class RDProductList extends RDSuccess {
    private List<RDProduct> products = new ArrayList<>();
    private int firstBought = 0;// 旧代码只能两个值0或者100
    private int lv = 0;// 玩家等级
    /** 公众号产品列表页底部提示 */
    private String memo = "";

    public static RDProductList fromCfgProductGroup(CfgProductGroup productGroup, CfgChannelEntity channel, String ip) {
        RDProductList productList = new RDProductList();
        CfgUSAIps cfgUSAIps = Cfg.I.getUniqueConfig(CfgUSAIps.class);
        boolean isIosAndUsaIp = channel.isIos() && IpUtil.ipInRange(ip, cfgUSAIps.getIpRanges());
        for (CfgProduct cfgProduct : productGroup.getProducts()) {
            if (!cfgProduct.getIsShow()) {
                continue;
            }
            // iOS包的美国ip不显示没有在苹果上注册的商品
            if (isIosAndUsaIp && cfgProduct.isNotRegisteredOnIos()) {
                continue;
            }
            RDProduct rd = RDProduct.fromCfgProduct(cfgProduct);
            productList.getProducts().add(rd);
        }
        return productList;
    }

    public static RDProductList fromCfgProducts(List<CfgProduct> products) {
        RDProductList productList = new RDProductList();
        for (CfgProduct cfgProduct : products) {
            RDProduct rd = RDProduct.fromCfgProduct(cfgProduct);
            productList.getProducts().add(rd);
        }
        return productList;
    }

    @Data
    public static class RDProduct {
        private int id;
        private int mallId;
        private String innerId;
        private String name;
        private int quantity;
        @JSONField(serialize = false)
        private int firstRate = 1;// 首次购买翻倍比例
        private int price;
        private int recommend;
        private String des;
        private int extraNum;
        private int isBought;
        private int hasFirstBoughtAward = 1;

        public static RDProduct fromCfgProduct(CfgProduct cfg) {
            RDProduct rd = new RDProduct();
            rd.setId(cfg.getId());
            rd.setMallId(rd.getId());
            rd.setInnerId(String.valueOf(cfg.getId()));
            rd.setName(cfg.getName());
            rd.setPrice(cfg.getPrice());
            rd.setQuantity(cfg.getQuantity());
            rd.setExtraNum(cfg.getExtraNum());
            rd.setRecommend(cfg.getRecommend() ? 1 : 0);
            rd.setFirstRate(cfg.getFirstRate());
            rd.setHasFirstBoughtAward(cfg.getFirstRate() > 1 ? 1 : 0);
            return rd;
        }
    }
}