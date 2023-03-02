package com.bbw.god.mall.store;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分商店相关接口
 */
@RestController
public class StoreController extends AbstractController {
    @Autowired
    private StoreLogic storeLogic;

    /**
     * @param type storeEnum
     * @return
     */
    @GetMapping(CR.Store.LIST_MALLS)
    public RDCommon listMalls(int type) {
        return storeLogic.getGoodsList(getUserId(), type);
    }


    @GetMapping(CR.Store.BUY)
    public RDCommon buy(int proId, int type, Integer buyNum, Integer consume) {
        if (buyNum == null) {
            buyNum = 1;
        }
        if (buyNum <= 0) {
            throw ExceptionForClientTip.fromi18nKey("buy.num.unvalid");
        }
        Integer value = Math.abs(Integer.valueOf(buyNum));
        return storeLogic.buyGoods(getUserId(), proId, value, type, consume);
    }
}
