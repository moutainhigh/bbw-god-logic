package com.bbw.god.pay;

import com.bbw.common.IpUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.*;
import com.bbw.god.game.config.CfgProductChannelMap.ChannelMap;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.CfgProductGroup.ProductAward;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.pay.RDProductList.RDProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 产品服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-15 14:16
 */
@Slf4j
@Service
public class ProductService {
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private WxPublicReceiptService wxPublicReceiptService;

    /**
     * 根据APP运行模式下，区服产品列表
     *
     * @param sid
     * @return
     */
    public CfgProductGroup getAppProductGroup(int sid) {
        Optional<CfgServerGroup> cfgServerGroup = getCfgServerGroupBySid(sid);
        if (!cfgServerGroup.isPresent()) {
            throw CoderException.high("区服(sid=" + sid + ")未配置到指定的服务器组。");
        }
        CfgProductGroup cfgProductGroup = Cfg.I.get(cfgServerGroup.get().getAppProductGroupId(), CfgProductGroup.class);
        if (null == cfgProductGroup) {
            throw CoderException.high("区服(sid=" + sid + ")未配置到指定的产品组。");
        }
        return cfgProductGroup;
    }

    /**
     * 获取微信公众号充值产品列表
     *
     * @param sid
     * @return
     */
    public CfgProductGroup getWechatProductGroup(int sid) {
        Optional<CfgServerGroup> cfgServerGroup = getCfgServerGroupBySid(sid);
        if (!cfgServerGroup.isPresent()) {
            throw CoderException.high("区服(sid=" + sid + ")未配置到指定的服务器组。");
        }
        CfgProductGroup cfgProductGroup = Cfg.I.get(cfgServerGroup.get().getWechatProductGroupId(), CfgProductGroup.class);
        return cfgProductGroup;
    }

    /**
     * 根据产品Id获取产品
     *
     * @param productId
     * @param payType
     * @return
     */
    public CfgProduct getCfgProduct(int productId, int payType) {
        ProductGroup productGroup = getProductGroup(PayType.fromValue(payType));
        CfgProductGroup group = Cfg.I.get(productGroup.getValue(), CfgProductGroup.class);
        if (group == null) {
            group = Cfg.I.get(ProductGroup.Normal.getValue(), CfgProductGroup.class);
        }
        Optional<CfgProduct> match = group.getProducts().stream().filter(product -> product.getId().intValue() == productId).findFirst();
        if (match.isPresent()) {
            return match.get();
        }
        throw CoderException.normal("不存在id=[" + productId + "]的充值产品！");
    }

    /**
     * 根据产品Id获取产品
     *
     * @param productId
     * @return
     */
    public CfgProduct getCfgProduct(int productId) {
        List<CfgProductGroup> groups = Cfg.I.get(CfgProductGroup.class);
        for (CfgProductGroup group : groups) {
            Optional<CfgProduct> match = group.getProducts().stream().filter(product -> product.getId().intValue() == productId).findFirst();
            if (match.isPresent()) {
                return match.get();
            }
        }
        throw CoderException.normal("不存在id=[" + productId + "]的充值产品！");
    }

    /**
     * 获取直冲产品的奖励内容
     *
     * @param productId
     * @return
     */
    public ProductAward getProductAward(int productId) {
        Optional<ProductAward> optional = getProductAwardOptional(productId);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw CoderException.normal("不存在id=[" + productId + "]的充值产品！");
    }

    public Optional<ProductAward> getProductAwardOptional(int productId) {
        List<CfgProductGroup> groups = Cfg.I.get(CfgProductGroup.class);
        for (CfgProductGroup group : groups) {
            Optional<ProductAward> match = group.getProductAward().stream().filter(award -> award.getProductId() == productId).findFirst();
            if (match.isPresent()) {
                return match;
            }
        }
        return Optional.empty();
    }

    private Optional<CfgServerGroup> getCfgServerGroupBySid(int sid) {
        CfgServerEntity server = Cfg.I.get(sid, CfgServerEntity.class);

        List<CfgServerGroup> settings = Cfg.I.get(CfgServerGroup.class);
        for (CfgServerGroup group : settings) {
            if (group.getId().intValue() == server.getGroupId()) {
                return Optional.of(group);
            }
        }
        return Optional.empty();
    }

    public RDProductList getProductsList(CfgProductGroup productGroup, GameUser user, String ip) {
        int channelId = user.getRoleInfo().getChannelId();
        if (79000 == channelId) {
            channelId = 78000;
        }
        CfgChannelEntity channel = Cfg.I.get(channelId, CfgChannelEntity.class);
        List<CfgProduct> productsToShow = getProductsToShow(productGroup, channel, ip);
        productsToShow.sort(Comparator.comparing(CfgProduct::getSerial));
        RDProductList list = RDProductList.fromCfgProducts(productsToShow);
        // 2.根据玩家的渠道 设置innerId
        // 多数渠道不需要innerid，仅从缓存中获取，避免缓存击穿
        CfgProductChannelMap map = Cfg.I.getFromLocalCache(user.getRoleInfo().getChannelId(), CfgProductChannelMap.class);
        if (null != map) {
            for (RDProduct rdProduct : list.getProducts()) {
                rdProduct.setInnerId(getCustomInnerId(map, rdProduct.getId()));
            }
        }
        // 3.获取本阶段的有效购买记录，自定义描述信息
        List<UserReceipt> receipts = null;
        if (ProductGroup.WxPayJSAPI.getValue() != productGroup.getGroupId()) {
            receipts = receiptService.getValidUserReceipt(user.getId(), user.getServerId());
        } else {
            receipts = wxPublicReceiptService.getValidUserReceipt(user.getId(), user.getServerId());
        }
        for (RDProduct product : list.getProducts()) {
            String des = getProductDes(receipts, product);
            product.setDes(des);
            boolean hasBought = bought(receipts, product.getId());
            product.setIsBought(hasBought ? 1 : 0);
        }

        // 4.已经购买了速战卡则不再显示此产品
        boolean suzhan = this.receiptService.suzhanHasBuy(user.getId());
        if (suzhan) {
            // 已经购买了速战卡
            for (RDProduct product : list.getProducts()) {
                if (product.getId() == CfgProduct.SUZHANKA_ID) {
                    list.getProducts().remove(product);
                    break;
                }
            }
        }
        // 5.设置是否首充。
        boolean b = this.receiptService.isFirstBought(user.getId());
        // TODO:兼容旧代码，只能设置0或者100
        list.setFirstBought(b ? 100 : 0);
        return list;
    }

    /**
     * 微信公众号充值钻石额外赠送
     *
     * @param productId
     * @return
     */
    public int getExtraDiamondForWXMP(int productId) {
        switch (productId) {
            case 41:
                return 6;
            case 42:
                return 14;
            case 43:
                return 20;
            case 44:
                return 40;
            case 45:
                return 66;
            case 46:
                return 130;
            default:
                return 0;
        }
    }

    /**
     * 是否充值钻石
     *
     * @param productId
     * @return
     */
    public boolean ifPayForDiamond(int productId) {
        return productId >= 40 && productId <= 49;
    }

    private String getCustomInnerId(CfgProductChannelMap map, int zfProductId) {
        for (ChannelMap channelMap : map.getChannelMap()) {
            if (channelMap.getZfProductId() == zfProductId) {
                return channelMap.getInnerId();
            }
        }
        return "noset";
        // throw CoderException.fatal("渠道ID[" + map.getChannelId() + "]没有配置与竹风产品[" +
        // zfProductId + "]对应的渠道innerId!");
    }

    /**
     * @param receipts
     * @param product
     */
    private String getProductDes(List<UserReceipt> receipts, RDProduct product) {
        if (product.getId() == CfgProduct.SUZHANKA_ID) {// 速战卡
            return LM.I.getMsg("product.des.suzhanka");
        }
        if (product.getId() == CfgProduct.YUEKA_ID) {// 月卡
            return LM.I.getMsg("product.des.yueka");
        }
        if (product.getId() == CfgProduct.JIKA_ID) {// 季卡
            return LM.I.getMsg("product.des.jika");
        }
        if (product.getFirstRate() == 1 && product.getExtraNum() > 0) {// 额外赠送数量
            return LM.I.getFormatMsg("product.des.zengsong", product.getExtraNum());
        }
        boolean hasBounght = bought(receipts, product.getId());
        if (product.getFirstRate() > 1 && !hasBounght) {// 首次购买
            return LM.I.getFormatMsg("product.des.shoucigoumai", (product.getFirstRate() - 1) * product.getQuantity());
        }

        if (product.getFirstRate() > 1 && hasBounght && product.getExtraNum() > 0) {// 重复购买
            return LM.I.getFormatMsg("product.des.zengsong", product.getExtraNum());
        }
        return "";
    }

    private boolean bought(List<UserReceipt> receipts, int productId) {
        // TODO:首购重置
        if (ListUtil.isEmpty(receipts)) {
            return false;
        }
        for (UserReceipt entity : receipts) {
            if (entity.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得要展示的产品基础数据
     *
     * @param productGroup
     * @param channel
     * @param ip
     * @return
     */
    private List<CfgProduct> getProductsToShow(CfgProductGroup productGroup, CfgChannelEntity channel, String ip) {
        List<CfgProduct> products = new ArrayList<>();
        CfgUSAIps cfgUSAIps = Cfg.I.getUniqueConfig(CfgUSAIps.class);
        boolean isIosAndUsaIp = channel.isIos() && IpUtil.ipInRange(ip, cfgUSAIps.getIpRanges());
        for (CfgProduct cfgProduct : productGroup.getProducts()) {
            if (!cfgProduct.getIsShow()) {
                continue;
            }
            //TODO 苹果审核包80000，新增产品300元宝
            if (channel.getId() == 80000 && cfgProduct.getId() == 20) {
                products.add(cfgProduct);
                continue;
            }
            // iOS包的美国ip或者审核包不显示没有在苹果上注册的商品
            if ((isIosAndUsaIp || channel.getId() == 80000) && cfgProduct.isNotRegisteredOnIos()) {
                continue;
            }
            //TODO 非iOS审核包，不显示这两个产品。包通过后，配合客户端去掉该代码
            if (cfgProduct.getId() == 22) {
                continue;
            }
            products.add(cfgProduct);
        }
        return products;
    }

    /**
     * 获得产品分组
     *
     * @param payType
     * @return
     */
    private ProductGroup getProductGroup(PayType payType) {
        switch (payType) {
            case WxPayJSAPI:
                return ProductGroup.WxPayJSAPI;
            default:
                return ProductGroup.Normal;
        }
    }
}
