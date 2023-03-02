package com.bbw.god.gm;

import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.Rst;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsReceiptEntity;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.pay.DispatchProduct;
import com.bbw.god.pay.PayCallBackCtrl;
import com.bbw.god.pay.PayType;
import com.bbw.god.pay.ProductService;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 玩家充值相关的操作
 *
 * @author: suhq
 * @date: 2022/1/5 4:51 下午
 */
@RestController
@RequestMapping("/gm")
public class GMUserPayCtrl extends AbstractController {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PayCallBackCtrl payCallBackCtrl;
    @Autowired
    private App app;

    @RequestMapping("user!simulatePay")
    public Rst simulatePay(int sid, String nickname, int productId) {
        return doPay(sid, nickname, 0, productId);
    }

    @RequestMapping("user!simulateWxPublicPay")
    public Rst simulateWxPublicPay(int sid, String nickname, int productId) {
        return doPay(sid, nickname, PayType.WxPayJSAPI.getValue(), productId);
    }

    private Rst doPay(int sid, String nickname, int payType, int productId) {
        Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sid, nickname);
        if (!uidOptional.isPresent()) {
            return Rst.businessFAIL("不存在该角色");
        }
        long uid = uidOptional.get();
        CfgServerEntity server = ServerTool.getServer(sid);
        if (!app.runAsDev() && server.getGroupId() != 1) {
            return Rst.businessFAIL("仅限于给测试服的角色模拟支付");
        }
        CfgProduct product = this.productService.getCfgProduct(productId);
        if (product == null) {
            return Rst.businessFAIL("无效的商品ID");
        }
        // 插入订单
        InsReceiptEntity r = new InsReceiptEntity();
        r.setId(ID.INSTANCE.nextId());
        r.setPayType(payType);
        r.setPid(productId);
        r.setPrice(product.getPrice());
        r.setProductName(product.getName());
        r.setPurchaseDate(DateUtil.now());
        r.setQuantity(product.getQuantity());
        r.setSid(sid);
        r.setStatus(1);
        r.setUid(uid);
        DispatchProduct dispatch = SpringContextUtil.getBean(DispatchProduct.class, sid);
        dispatch.dbInsertInsReceiptEntity(r);
        // 发放
        this.payCallBackCtrl.notify(r.getId(), sid, 1);
        return Rst.businessOK();
    }

}
