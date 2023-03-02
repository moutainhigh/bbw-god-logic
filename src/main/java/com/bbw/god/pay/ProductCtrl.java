package com.bbw.god.pay;

import com.bbw.common.IpUtil;
import com.bbw.common.LM;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 充值产品
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-14 18:08
 */
@RestController
public class ProductCtrl extends AbstractController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ZhiCongService zhiCongService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 免登录请求 微信公众内的充值产品列表.
     *
     * @return
     */
    @RequestMapping(CR.Product.WECHAT_PRODUCTS)
    public RDProductList wechatProducts(int sid, Long uid, int cid) {
        // 免登录的请求，获取服务器配置的微信公众内的充值产品列表
        CfgProductGroup productGroup = productService.getWechatProductGroup(sid);
        GameUser user = this.gameUserService.getGameUser(uid);
        RDProductList list = productService.getProductsList(productGroup, user, IpUtil.getIpAddr(request));
        list.setLv(user.getLevel());
        list.setMemo("以上充值计入活动充值额度，且可触发节日活动中的每日首笔充值效果。但每日首充效果与每周首充效果同时存在时，仅会获得一份奖励。");
        for (RDProductList.RDProduct rdProduct : list.getProducts()) {
            if (productService.ifPayForDiamond(rdProduct.getId()) && rdProduct.getDes().contains("首次")) {
                //微信公众号首次购买
                int extraNum = productService.getExtraDiamondForWXMP(rdProduct.getId());
                if (extraNum > 0) {
                    String des = "本周" + LM.I.getFormatMsg("product.des.shoucigoumai", extraNum);
                    des = des.replace("元宝", "钻石");
                    rdProduct.setDes(des);
                } else {
                    rdProduct.setDes("");
                }

                continue;
            }
            if (rdProduct.getDes().contains("首次")) {
                rdProduct.setDes("本周" + rdProduct.getDes());
            }
        }
        return list;
    }

    /**
     * app应用内的充值产品列表
     *
     * @return
     */
    @RequestMapping(CR.Product.LIST_PRODUCTS)
    public RDProductList listProducts() {
        // 1.获取服务器配置的App充值产品列表
        CfgProductGroup productGroup = productService.getAppProductGroup(this.getServerId());
        RDProductList pl = getProductsList(productGroup);
        return pl;
    }

    private RDProductList getProductsList(CfgProductGroup productGroup) {
        RDProductList list = productService.getProductsList(productGroup, this.getGameUser(), IpUtil.getIpAddr(request));
        return list;
    }

    /**
     * 直冲产品是否可以购买
     *
     * @param uid
     * @param pid
     * @return
     */
    @GetMapping(CR.Product.CAN_BUG)
    public Rst zhiCongCanBuy(Long uid, int pid) {
        if (uid==null){
            uid=getUserId();
        }
        String result = zhiCongService.canBuy(uid, pid);
        if (StrUtil.isBlank(result)) {
            return Rst.businessOK().put("canBuy", "1");
        }
        return Rst.businessFAIL(result).put("canBuy", "0");
    }

}
