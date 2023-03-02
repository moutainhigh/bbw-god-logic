package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.task.RDCardVigorsList;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 派遣类任务精力业务类
 *
 * @author: huanghb
 * @date: 2022/12/8 16:03
 */
@Service
public class UserDispatchCardVigorLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserDispatchCardVigorService dispatchCardVigorService;

    /**
     * 获取卡牌精力集合
     *
     * @param uid
     * @param taskGroup
     * @return
     */
    public RDCardVigorsList getCardVigors(long uid, TaskGroupEnum taskGroup) {
        RDCardVigorsList rd = new RDCardVigorsList();
        // 精力数据
        UserCardVigor userCardVigor = gameUserService.getSingleItem(uid, UserCardVigor.class);
        List<UserCard> userCards = userCardService.getUserCards(uid);
        for (UserCard userCard : userCards) {
            int cardVigor = dispatchCardVigorService.getCardVigor(taskGroup, userCard, userCardVigor);
            int maxCardVigor = TimeLimitTaskTool.getMaxCardVigor(userCard.getBaseId(), userCard.getHierarchy());
            rd.addVigors(userCard.getBaseId(), cardVigor, maxCardVigor);
        }
        return rd;
    }


    /**
     * 恢复卡牌精力
     *
     * @param uid
     * @param cardIds
     * @param beePulpNum
     */
    public RDCommon recoverCardVigor(long uid, String cardIds, Integer beePulpNum) {
        //检查蜂王浆数量
        TreasureChecker.checkIsEnough(TreasureEnum.BEE_PULP.getValue(), beePulpNum, uid);
        //检查卡牌
        List<Integer> cards = ListUtil.parseStrToInts(cardIds);
        //需要恢复精力的卡牌数量与使用蜂王浆数量不符
        if (cards.size() != beePulpNum) {
            throw ExceptionForClientTip.fromi18nKey("card.beePulp.num");
        }
        List<UserCard> userCards = userCardService.getUserCards(uid, cards);
        //移除精力满的卡牌
        userCards = removeMaxVigorCard(uid, userCards);
        //精力恢复
        cardVigorRecover(uid, userCards);
        RDCommon rd = new RDCommon();
        //扣除蜂王浆
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.BEE_PULP.getValue(), beePulpNum, WayEnum.CARD_RECOVER_VIGOR, rd);
        return rd;
    }

    /**
     * 卡牌精力恢复
     *
     * @param uid
     * @param userCards
     */
    private void cardVigorRecover(long uid, List<UserCard> userCards) {
        //精力重置
        UserCardVigor userCardVigor = gameUserService.getSingleItem(uid, UserCardVigor.class);
        if (null == userCardVigor) {
            throw ExceptionForClientTip.fromi18nKey("card.not.need.recovery");
        }
        userCards = removeMaxVigorCard(uid, userCards);
        for (UserCard userCard : userCards) {
            int maxCardVigor = TimeLimitTaskTool.getMaxCardVigor(userCard.getBaseId(), userCard.getHierarchy());
            userCardVigor.getCardVigors().put(userCard.getBaseId().toString(), maxCardVigor);
        }
        userCardVigor.setLastUpdate(DateUtil.now());
        gameUserService.updateItem(userCardVigor);
    }


    /**
     * 移除精力满的卡牌
     *
     * @param uid
     * @param userCards
     * @return
     */
    private List<UserCard> removeMaxVigorCard(long uid, List<UserCard> userCards) {
        List<UserCard> userCardList = new ArrayList<>();
        UserCardVigor userCardVigor = gameUserService.getSingleItem(uid, UserCardVigor.class);
        for (UserCard userCard : userCards) {
            int maxCardVigor = TimeLimitTaskTool.getMaxCardVigor(userCard.getBaseId(), userCard.getHierarchy());
            Integer vigor = userCardVigor.getCardVigors().getOrDefault(userCard.getBaseId().toString(), maxCardVigor);
            //如果卡牌精力已满
            if (vigor == maxCardVigor) {
                continue;
            }
            userCardList.add(userCard);
        }
        return userCardList;
    }
}
