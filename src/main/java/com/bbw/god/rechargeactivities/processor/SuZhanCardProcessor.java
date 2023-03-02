package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.IpUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.pay.RDProductList;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 速战卡购买
 *
 * @author lwb
 * @date 2020/7/2 11:35
 */
@Service
public class SuZhanCardProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserPayInfoService userPayInfoService;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.CARD_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.SU_ZHAN_CARD;
    }

    @Override
    public boolean isShow(long uid) {
        Date szk = userPayInfoService.getUserPayInfo(uid).getEndFightBuyTime();
        return null == szk;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        CfgProductGroup productGroup = productService.getAppProductGroup(gameUserService.getActiveSid(uid));
        RDProductList rdp = productService.getProductsList(productGroup, gameUserService.getGameUser(uid), IpUtil.getIpAddr(request));
        RDRechargeActivity rd = new RDRechargeActivity();
        //速战卡
        Optional<RDProductList.RDProduct> op = rdp.getProducts().stream().filter(p -> p.getId() == CfgProductGroup.CfgProduct.SUZHANKA_ID).findFirst();
        if (!op.isPresent()) {
            //已购买速战卡
            throw new ExceptionForClientTip("rechargeActivity.bought.szk");
        }
        List<RDRechargeActivity.GoldPackInfo> list = new ArrayList<>();
        list.add(RDRechargeActivity.GoldPackInfo.instance(op.get()));
        rd.setProducts(list);
        return rd;
    }
}
