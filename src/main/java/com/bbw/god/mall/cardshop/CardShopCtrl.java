package com.bbw.god.mall.cardshop;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 卡牌屋相关接口
 *
 * @author suhq
 * @date 2018年11月1日 上午10:17:11
 */
@RestController
public class CardShopCtrl extends AbstractController {

    @Autowired
    private CardShopLogic cardShopLogic;

    /**
     * 卡牌屋信息
     *
     * @return
     */
    @GetMapping(CR.CardShop.GET_CARD_SHOP_INFO)
    public RDCardShop getCardShopInfo() {
        return cardShopLogic.getCardShopInfo(getUserId());
    }

    /**
     * 卡池信息
     *
     * @param type
     * @return
     */
    @GetMapping(CR.CardShop.GET_CARD_POOL_INFO)
    public RDCardPool getCardPoolInfo(int type) {
        RDCardPool rd = cardShopLogic.getCardPoolInfo(getUserId(), type);
        int serverGroupId = gameUserService.getActiveGid(getUserId());
        // 小游戏临时处理
//        if (80 == serverGroupId && CardPoolEnum.LIMIT_TIME_CP.getValue() == type) {
//            rd.setCards(new ArrayList<>());
//            rd.setVowCardId(-1);
//        }
        return rd;
    }

    /**
     * 许愿池信息
     *
     * @return
     */
    @GetMapping(CR.CardShop.GET_WISH_POOL_INFO)
    public RDWishCardPool getWishPoolInfo() {
        return cardShopLogic.getWishPoolInfo(getUserId());
    }

    /**
     * 添加到卡池
     *
     * @param cardId
     * @return
     */
    @GetMapping(CR.CardShop.ADD_TO_POOL)
    public RDSuccess addToPool(int cardId) {
        return cardShopLogic.addToPool(getUserId(), cardId);
    }

    /**
     * 激活卡池
     *
     * @param type
     * @return
     */
    @GetMapping(CR.CardShop.ACTIVE_CARD_POOL)
    public RDCardShop activeCardPool(int type) {
        return cardShopLogic.activeCardPool(getUserId(), type);
    }

    /**
     * 抽卡
     *
     * @param type      卡池类型
     * @param drawTimes 抽卡次数
     * @return
     */
    @GetMapping(CR.CardShop.DRAW)
    public RDCardDraw draw(int type, int drawTimes, Integer newerGuide) {
        /*if (null != newerGuide) {
            return newerGuideService.drawCard(getUserId(), newerGuide);
        }*/
        return cardShopLogic.draw(getUserId(), type, drawTimes);
    }

}
