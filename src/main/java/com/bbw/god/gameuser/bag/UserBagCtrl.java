package com.bbw.god.gameuser.bag;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 玩家背包接口
 * @date 2020/11/27 15:38
 **/
@RestController
public class UserBagCtrl extends AbstractController {
    @Autowired
    private UserBagService userBagService;

    /**
     * 购买背包格子
     *
     * @param num 购买数量
     * @return
     */
    @RequestMapping(CR.Bag.BUY_BAG)
    public RDCommon buyBag(int num) {
        return userBagService.buyBag(getUserId(), num);
    }
}
