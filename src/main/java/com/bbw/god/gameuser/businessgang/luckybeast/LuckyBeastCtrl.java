package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 招财兽相关入口
 *
 * @author: huanghb
 * @date: 2022/1/17 16:58
 */
@RestController
public class LuckyBeastCtrl extends AbstractController {
    @Autowired
    private LuckyBeastLogic luckyBeastLogic;

    /**
     * 获得招财兽本次奖励信息
     *
     * @return
     */
    @GetMapping(CR.LuckyBeast.LUCKY_BEAST_GET_AWARDS_INFO)
    public RDSuccess getLuckyBeastAwardsInfo() {
        return luckyBeastLogic.getAwardsToShow(getUserId());
    }

    /**
     * 购买攻打次数
     *
     * @return
     */
    @GetMapping(CR.LuckyBeast.LUCKY_BEAST_BUY_ATTACK_TIMES)
    public RDSuccess buyAttackTimes(Integer buyNum) {
        return luckyBeastLogic.buyAttackTimes(getUserId(), buyNum);
    }

    /**
     * 设置攻打招财兽的卡牌
     *
     * @return
     */
    @GetMapping(CR.LuckyBeast.SET_LUCKY_BEAST_CARD)
    public RDSuccess setLuckyBeastCard(Integer luckyBeastCardId) {
        return luckyBeastLogic.setLuckyBeastCards(getUserId(), luckyBeastCardId);
    }

    /**
     * （攻打）招财
     *
     * @return
     */
    @GetMapping(CR.LuckyBeast.ATTACK_LUCKY_BEAST)
    public RDSuccess attack() {
        return luckyBeastLogic.attack(getUserId());
    }


    /**
     * 刷新招财兽
     *
     * @param
     * @return
     */
    @GetMapping(CR.LuckyBeast.REFRESH_MY_LUCKY_BEAST)
    public RDRefreshLuckyBeastPos refreshMyLuckyBeast() {
        return luckyBeastLogic.refreshMyLuckyBeast(getUserId());
    }

}
