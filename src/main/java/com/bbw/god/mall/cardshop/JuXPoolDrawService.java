package com.bbw.god.mall.cardshop;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.detail.async.CardDrawDetailAsyncHandler;
import com.bbw.god.detail.async.CardDrawDetailEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardGroup;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.guide.GuideConfig;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.card.DrawCardStatistic;
import com.bbw.god.gameuser.statistic.behavior.card.DrawCardStatisticService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.cardshop.event.CardShopEventPublisher;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 聚贤卡池抽卡service
 * @date 2020/10/23 14:49
 **/
@Service
public class JuXPoolDrawService extends AbstractDrawService {
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private DrawCardStatisticService drawCardStatisticService;
    @Autowired
    private GuideConfig guideConfig;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private CardDrawDetailAsyncHandler cardDrawDetailAsyncHandler;

    public static final String JU_XIAN_CARD_POOL_STRATEGY_1 = "聚贤卡池_聚贤总等级[1,10]";
    public static final String JU_XIAN_CARD_POOL_STRATEGY_2 = "聚贤卡池_聚贤总等级[11,30]";
    public static final String JU_XIAN_CARD_POOL_STRATEGY_3 = "聚贤卡池_聚贤总等级[31,180]";
    public static final String JU_XIAN_CARD_POOL_STRATEGY_4 = "聚贤卡池_聚贤总等级[181,340]";
    public static final String JU_XIAN_CARD_POOL_STRATEGY_5 = "聚贤卡池_聚贤总等级[341,510]";
    public static final String JU_XIAN_CARD_POOL_STRATEGY_6 = "聚贤卡池_聚贤总等级[511,680]";
    public static final String JU_XIAN_CARD_POOL_STRATEGY_7 = "聚贤卡池_聚贤总等级[681,850]";

    public static final String RANDOM_JLSS = "随机九龙岛四圣";
    public static final String RANDOM_SDTW = "随机四大天王";
    public static final List<Integer> SDTW = Arrays.asList(208, 308, 407, 506);
    public static final List<Integer> JLSS = Arrays.asList(210, 314, 409, 512);

    public static final List<String> STRATEGIES = Arrays.asList(JU_XIAN_CARD_POOL_STRATEGY_1, JU_XIAN_CARD_POOL_STRATEGY_2,
            JU_XIAN_CARD_POOL_STRATEGY_3, JU_XIAN_CARD_POOL_STRATEGY_4, JU_XIAN_CARD_POOL_STRATEGY_5,
            JU_XIAN_CARD_POOL_STRATEGY_6, JU_XIAN_CARD_POOL_STRATEGY_7);

    /**
     * 抽卡
     *
     * @param guId
     * @param type      卡池类型
     * @param drawTimes 抽卡次数
     * @return
     */
    @Override
    public RDCardDraw draw(long guId, int type, int drawTimes) {
        // 防止客户端传除非1和10的值
        drawTimes = drawTimes != 1 ? 10 : 1;
        int need = drawTimes == 10 ? 95 : 10;
        UserCardPool ucPool = cardShopService.getCardPoolRecords(guId, type);
        // 卡池是否解锁
        if (ucPool.getIsUnlock() == CardPoolStatusEnum.LOCK.getValue()) {
            throw new ExceptionForClientTip("cardpool.lock");
        }
        RDCardDraw rd = new RDCardDraw();
        int treasureNum = userTreasureService.getTreasureNum(guId, TreasureEnum.JU_XIAN_LING.getValue());
        if (treasureNum < need) {
            throw new ExceptionForClientTip("treasure.not.enough", TreasureEnum.JU_XIAN_LING.getName());
        }
        TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.JU_XIAN_LING.getValue(), need, WayEnum.OPEN_JU_XIAN_CARD_POOL, rd);
        ConsumeType costType = ConsumeType.JU_XIAN_LING;
        Integer wishCard = ucPool.getWishCard();
        Integer wishValue = ucPool.getWishValue();
        // 开卡池
        CardDrawResult resultData = drawPool(ucPool, costType, drawTimes, rd);
        ActionStatisticTool.addUserActionStatistic(guId, drawTimes, CardPoolEnum.JUX_CP.getName());
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
    @Override
    CardDrawResult drawPool(UserCardPool cardPool, ConsumeType costType, int num, RDCardDraw rd) {
        long uid = cardPool.getGameUserId();
        List<CfgCardEntity> cards = drawByStrategy(cardPool, num, rd);
        List<Integer> cardIds = cards.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
        CardDrawResult cardDrawResult = new CardDrawResult(num, WayEnum.OPEN_JU_XIAN_CARD_POOL);
        // 发放卡牌
        List<EPCardAdd.CardAddInfo> cardAddInfos = CardEventPublisher.getCardAddInfos(uid, cardIds);
        cardDrawResult.setAddCards(cardAddInfos);
        CardEventPublisher.pubCardAddEvent(uid, cardAddInfos, WayEnum.OPEN_JU_XIAN_CARD_POOL, "开聚贤卡池", rd);
        return cardDrawResult;
    }

    /**
     * 策略抽卡
     *
     * @param cardPool
     * @param num
     * @param rd
     * @return
     */
    @Override
    public List<CfgCardEntity> drawByStrategy(UserCardPool cardPool, int num, RDCardDraw rd) {
        long uid = cardPool.getGameUserId();
        String strategyKey = getStrategy(uid);
        List<CfgCardEntity> cards = new ArrayList<>(num);
        List<UserCard> ownCards = userCardService.getUserCards(uid);
        Optional<CfgCardEntity> matchCardResult = null;
        DrawCardStatistic statistic = drawCardStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        Integer jux = statistic.getJux();
        int country = gameUserService.getGameUser(uid).getRoleInfo().getCountry();
        for (int i = 0; i < num; i++) {
            // 策略抽卡
            RandomParam randomParams = getRandomParamForDraw(cardPool, ownCards);
            randomParams.setExtraCardsToMap(ownCards);
            matchCardResult = userCardRandomService.getRandomCard(uid, strategyKey, randomParams);
            if (matchCardResult.isPresent()) {
                jux += 1;
                MatchCardParam param = MatchCardParam.getInstance(uid, jux, country, randomParams, matchCardResult.get(), cards, ownCards);
                CfgCardEntity matchCard = getMatchCard(param);
                cards.add(matchCard);
            } else {
                thowExceptionAsNotExistStrategy(uid, strategyKey, cardPool.getCardPool());
            }
        }
        return cards;
    }

    private CfgCardEntity getMatchCard(MatchCardParam param) {
        // 根据抽卡次数来替换抽到的结果
        CfgCardEntity card = changeMatchCardByDrawTimes(param);
        if (null != card) {
            param.setMatchCard(card);
            return card;
        }
        // 根据抽到的星级来替换抽到的结果
        CfgCardEntity matchCard = param.getMatchCard();
        switch (matchCard.getStar()) {
            case 2:
                doStar2(param);
                break;
            case 4:
                doStar4(param);
                break;
        }
        return param.getMatchCard();
    }

    private CfgCardEntity changeMatchCardByDrawTimes(MatchCardParam param) {
        // 抽卡总次数超过50次，直接返回
        if (param.getTotalDrawTimes() > 50) {
            return null;
        }
        // 获取从聚贤卡池抽出的九龙岛四圣数量
        boolean ownAllJLSS = ifOwnAll(JLSS, param.getOwnCards(), param.getCards());
        // 如果九龙岛四圣还没集齐，抽到第10次，本次替换为随机九龙岛四圣
        if (!ownAllJLSS && 10 == param.getTotalDrawTimes()) {
            return changeMatchCardByStrategy(param, RANDOM_JLSS);
        }
        // 第20次送对应属性三星卡牌
        if (20 == param.getTotalDrawTimes()) {
            int index = param.getCountry() / 10 - 1;
            Integer cardId = guideConfig.getDrawCardsAsJxPool().get(index);
            return CardTool.getCardById(cardId);
        }
        // 如果九龙岛四圣还没集齐，抽到第30次，本次替换为随机九龙岛四圣
        if (!ownAllJLSS && 30 == param.getTotalDrawTimes()) {
            return changeMatchCardByStrategy(param, RANDOM_JLSS);
        }
        // 获取从聚贤卡池抽出的四星卡数量
        long count = getDrawCount(4, param.getOwnCards(), param.getCards());
        boolean ifOwnAll = ifOwnAll(SDTW, param.getOwnCards(), param.getCards());
        // 如果四大天王还没集齐，抽到第50次，聚贤卡池还没抽出过四大天王的话，本次替换为随机四大天王
        if (!ifOwnAll && 50 == param.getTotalDrawTimes() && 0 == count) {
            return changeMatchCardByStrategy(param, RANDOM_SDTW);
        }
        return null;
    }

    private CfgCardEntity changeMatchCardByStrategy(MatchCardParam param, String strategy) {
        Optional<CfgCardEntity> optional = userCardRandomService.getRandomCard(param.getUid(), strategy, param.getRandomParam());
        return optional.orElse(null);
    }

    private void doStar2(MatchCardParam param) {
        Long uid = param.getUid();
        Integer level = gameUserService.getGameUser(uid).getLevel();
        if (level > 22) {
            return;
        }
        List<Integer> cards = param.getCards().stream().map(CfgCardEntity::getId).collect(Collectors.toList());
        UserCardGroup cardGroup = userCardGroupService.getUsingGroup(uid, CardGroupWay.Normal_Fight);
        List<Integer> cardIds = cardGroup.getCards().stream().filter(tmp ->
                CardTool.getCardById(tmp).getStar() == 2).collect(Collectors.toList());
        // 默认卡组没有2星卡，不进行替换
        if (ListUtil.isEmpty(cardIds)) {
            return;
        }
        // 当前抽到的二星卡是默认卡组中的
        if (cardIds.contains(param.getMatchCard().getId())) {
            return;
        }
        // 当前已经抽到了3张及以上默认卡组的2星卡
        long count = cards.stream().filter(cardIds::contains).count();
        if (count >= 3) {
            return;
        }
        // 替换成默认卡组中的2星卡牌
        Integer cardId = PowerRandom.getRandomFromList(cardIds);
        param.setMatchCard(CardTool.getCardById(cardId));
    }

    private void doStar4(MatchCardParam param) {
        // 获取从聚贤卡池抽出的四星卡数量
        long count = getDrawCount(4, param.getOwnCards(), param.getCards());
        List<Integer> cardIds = Arrays.asList(208, 308, 407, 506);
        boolean ifOwnAll = ifOwnAll(cardIds, param.getOwnCards(), param.getCards());
        // 聚贤卡池抽到的四星卡次数小于2且未拥有四大天王
        if (!ifOwnAll && count < 2) {
            Optional<CfgCardEntity> result = userCardRandomService.getRandomCard(param.getUid(), RANDOM_SDTW, param.getRandomParam());
            // 用随机四大天王的策略获取并设置进去
            result.ifPresent(param::setMatchCard);
        }
    }

    /**
     * 获取聚贤卡池抽到star星级的卡牌数量，包括已经抽到但未发放的
     *
     * @param star     星级
     * @param ownCards 拥有的卡牌集合
     * @param cards    本次抽到的卡牌集合
     * @return
     */
    private long getDrawCount(int star, List<UserCard> ownCards, List<CfgCardEntity> cards) {
        long count = ownCards.stream().filter(tmp -> CardTool.getCardById(tmp.getBaseId()).getStar() == star
                && tmp.getGetWay() != null && tmp.getGetWay() == WayEnum.OPEN_JU_XIAN_CARD_POOL.getValue()).count();
        count += cards.stream().filter(tmp -> tmp.getStar() == star).count();
        return count;
    }

    private boolean ifOwnAll(List<Integer> needIds, List<UserCard> ownCards, List<CfgCardEntity> cards) {
        List<CfgCardEntity> cardList = ownCards.stream().map(tmp -> CardTool.getCardById(tmp.getBaseId())).collect(Collectors.toList());
        cardList.addAll(cards);
        List<Integer> ownIds = cardList.stream().map(CfgCardEntity::getId).distinct().collect(Collectors.toList());
        return ownIds.containsAll(needIds);
    }

    /**
     * 获取策略参数
     *
     * @param cardPool
     * @param ownCards
     * @return
     */
    @Override
    public RandomParam getRandomParamForDraw(UserCardPool cardPool, List<UserCard> ownCards) {
        RandomParam randomParams = super.getRandomParamForDraw(cardPool, ownCards);
        randomParams.setExtraCardsToMap(ownCards);
        randomParams.setRoleCards(ownCards);
        return randomParams;
    }

    public String getStrategy(long uid) {
        // 策略配置二维数组，例如{2, 11, 30} 表示聚贤总等级在[11, 30]的范围内使用2级策略
        int[][] strategyArr = {{1, 1, 10}, {2, 11, 30}, {3, 31, 180}, {4, 181, 340}, {5, 341, 510}, {6, 511, 680}, {7, 681, 850}};
        // 获取聚贤庄总等级
        List<UserCity> userCities = userCityService.getUserCities(uid);
        int totalLevel = userCities.stream().mapToInt(UserCity::getJxz).sum();
        // 根据总等级，返回对应使用的策略
        for (int[] arr : strategyArr) {
            if (in(arr[1], arr[2], totalLevel)) {
                int index = arr[0] - 1;
                return STRATEGIES.get(index);
            }
        }
        // 不在区间范围内的，默认使用最低级的策略
        return JU_XIAN_CARD_POOL_STRATEGY_1;
    }

    @Override
    WayEnum getWay(CardPoolEnum type) {
        return WayEnum.OPEN_JU_XIAN_CARD_POOL;
    }

    @Data
    private static class MatchCardParam {
        // 玩家id
        private Long uid;
        // 总的抽卡次数
        private Integer totalDrawTimes;
        // 玩家人物初始属性
        private Integer country;
        // 本次默认策略的命中卡牌
        private CfgCardEntity matchCard;
        // 本次抽卡到目前所有命中的卡牌集合（不包括matchCard）
        private List<CfgCardEntity> cards;
        // 玩家所拥有的卡牌
        private List<UserCard> ownCards;
        // 抽卡的策略参数
        private RandomParam randomParam;

        public static MatchCardParam getInstance(long uid, Integer jux, int country, RandomParam randomParam, CfgCardEntity matchCard, List<CfgCardEntity> cards, List<UserCard> ownCards) {
            MatchCardParam param = new MatchCardParam();
            param.setUid(uid);
            param.setTotalDrawTimes(jux);
            param.setRandomParam(randomParam);
            param.setCountry(country);
            param.setMatchCard(matchCard);
            param.setCards(cards);
            param.setOwnCards(ownCards);
            return param;
        }
    }
}
