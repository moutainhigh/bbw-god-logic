package com.bbw.god.mall.cardshop;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.activity.processor.MonthLoginProcessor;
import com.bbw.god.detail.async.CardDrawDetailAsyncHandler;
import com.bbw.god.detail.async.CardDrawDetailEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.cardshop.event.CardShopEventPublisher;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡池抽取策略
 *
 * @author suhq
 * @date 2020-04-20 16:47
 **/
public abstract class AbstractDrawService {

    @Autowired
    protected UserCardService userCardService;
    @Autowired
    protected UserCardRandomService userCardRandomService;
    @Autowired
    protected MonthLoginProcessor monthLoginProcessor;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected CardShopService cardShopService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private CardDrawDetailAsyncHandler cardDrawDetailAsyncHandler;


    /**
     * 抽卡
     *
     * @param guId
     * @param type      卡池类型
     * @param drawTimes 抽卡次数
     * @return
     */
    public RDCardDraw draw(long guId, int type, int drawTimes) {
        // 防止客户端传除非1和10的值
        if (drawTimes != 1) {
            drawTimes = 10;
        }
        UserCardPool ucPool = cardShopService.getCardPoolRecords(guId, type);
        // 万物卡池直接解锁
        if (CardPoolEnum.WANWU_CP.getValue() == ucPool.getCardPool() && !ucPool.ifUnlock()) {
            ucPool.setIsUnlock(CardPoolStatusEnum.UNLOCK.getValue());
        }
        // 卡池是否解锁
        if (ucPool.getIsUnlock() == CardPoolStatusEnum.LOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.lock");
        }
        RDCardDraw rd = new RDCardDraw();
        CardPoolEnum typeEnum = CardPoolEnum.fromValue(type);
        // 获取需要扣除的资源的数量及id
        int need = drawTimes == 10 ? 9 : 1;
        TreasureEnum needTreasure = getNeedTreasure(guId, typeEnum, need);
        int needTreasureId = needTreasure.getValue();
        int treasureNum = userTreasureService.getTreasureNum(guId, needTreasureId);
        if (treasureNum < need) {
            throw new ExceptionForClientTip("treasure.not.enough", needTreasure.getName());
        }
        // 扣除资源
        TreasureEventPublisher.pubTDeductEvent(guId, needTreasureId, need, getWay(typeEnum), rd);
        // 资源消耗类型
        ConsumeType costType = needTreasureId == TreasureEnum.XZY.getValue() ? ConsumeType.XZY : ConsumeType.YUAN_JING;

        Integer wishCard = ucPool.getWishCard();
        Integer wishValue = ucPool.getWishValue();
        // 开卡池
        CardDrawResult resultData = drawPool(ucPool, costType, drawTimes, rd);
        ActionStatisticTool.addUserActionStatistic(guId, drawTimes, typeEnum.getName());
        // 触发抽卡事件
        List<Integer> cardIds = resultData.getAddCards().stream().map(EPCardAdd.CardAddInfo::getCardId).collect(Collectors.toList());
        CardShopEventPublisher.pubDrawEndEvent(guId, new EPDraw(drawTimes, costType, type, wishCard, wishValue, cardIds), rd);
        //抽卡明细
        cardDrawDetailAsyncHandler.log(new CardDrawDetailEventParam(guId, resultData.getDrawTimes(), resultData.getAddCards(), resultData.getTreasures(), resultData.getWay()));
        rd.setVowCardId(ucPool.getWishCard());
        return rd;
    }

    /**
     * 抽卡
     *
     * @param cardPool
     * @param costType
     * @param num
     * @param rd
     * @return
     */
    abstract CardDrawResult drawPool(UserCardPool cardPool, ConsumeType costType, int num, RDCardDraw rd);


    /**
     * 策略抽卡
     *
     * @param cardPool
     * @param num
     * @param rd
     * @return
     */
    abstract List<CfgCardEntity> drawByStrategy(UserCardPool cardPool, int num, RDCardDraw rd);

    abstract WayEnum getWay(CardPoolEnum type);

    /**
     * 获取许愿值
     *
     * @return
     */
    protected int getAddedWishValue() {
        return PowerRandom.getRandomBySeed(2);
    }

    /**
     * 获取策略参数
     *
     * @param cardPool
     * @param ownCards
     * @return
     */
    protected RandomParam getRandomParamForDraw(UserCardPool cardPool, List<UserCard> ownCards) {
        RandomParam randomParams = new RandomParam();
        List<String> cards = monthLoginProcessor.getWithinThreeMonthsCards().stream().map(tmp -> CardTool.getCardById(tmp).getName()).collect(Collectors.toList());
        randomParams.set("$排除卡牌", cards);//$排除卡牌必设
        randomParams.setExtraCardsToPool(ownCards);
        return randomParams;
    }

    /**
     * 抛出卡牌策略不存在的异常
     *
     * @param uid
     * @param strategyKey
     * @param cardPoolType
     */
    protected void thowExceptionAsNotExistStrategy(long uid, String strategyKey, int cardPoolType) {
        GameUser gu = gameUserService.getGameUser(uid);
        String title = "卡牌策略[" + strategyKey + "]错误!";
        String msg = "区服sid[" + gu.getServerId() + "]玩家[" + gu.getId() + "," + gu.getRoleInfo()
                .getNickname() + "]";
        msg += "未能从[" + cardPoolType + "]类型卡包中获得卡牌！";
        throw CoderException.high(title + msg);
    }

    private TreasureEnum getNeedTreasure(long uid, CardPoolEnum typeEnum, int need) {
        TreasureEnum yj = CardShopTool.getNeedYJAsDraw(typeEnum);
        int num = userTreasureService.getTreasureNum(uid, yj.getValue());
        // 源晶数量不足
        if (num < need) {
            return TreasureEnum.XZY;
        }
        return yj;
    }

    /**
     * x是否在[min. max]这个区间中
     *
     * @param min 区间最小值
     * @param max 区间最大值
     * @param x   判断的数
     * @return
     */
    protected boolean in(int min, int max, int x) {
        return x >= min && x <= max;
    }
}
