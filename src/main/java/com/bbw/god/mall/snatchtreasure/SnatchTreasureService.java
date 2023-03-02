package com.bbw.god.mall.snatchtreasure;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchTreasureStatistic;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchTreasureStatisticService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.snatchtreasure.event.SnatchTreasureEventPublisher;
import com.bbw.god.random.box.BoxGood;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;
import static com.bbw.god.mall.snatchtreasure.RDSnatchTreasureInfo.RDSnatchTreasureBox;

/**
 * @author suchaobin
 * @description 夺宝service
 * @date 2020/6/29 17:41
 **/
@Service
public class SnatchTreasureService {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private SnatchTreasureStatisticService statisticService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private ActivityService activityService;

    /**
     * 进入夺宝界面
     *
     * @param uid
     * @return
     */
    public RDSnatchTreasureInfo enterSnatchTreasure(long uid) {
        UserSnatchTreasure userSnatchTreasure = gameUserService.getSingleItem(uid, UserSnatchTreasure.class);
        if (null == userSnatchTreasure) {
            userSnatchTreasure = UserSnatchTreasure.getInstance(uid);
            gameUserService.addItem(uid, userSnatchTreasure);
        }
        // 重置玩家夺宝开箱
        resetUserSnatchTreasure(uid);
        Integer wishValue = userSnatchTreasure.getWishValue();
        List<BoxGood> goods = getBoxGoods();
        GameSnatchTreasureCard curGameSnatchTreasureCard = getCurGameSnatchTreasureCard();
        Date endTime = curGameSnatchTreasureCard.getEndTime();
        long remainTime = endTime.getTime() - DateUtil.now().getTime();
        List<Award> awards = boxService.toAwards(uid, goods);
        List<RDSnatchTreasureBox> weekBoxList = getWeekBoxList(uid);
        SnatchTreasureStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE,
                DateUtil.getTodayInt());
        Integer weekDrawTimes = statistic.getWeekDrawTimes();
        IActivity a = activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.HOLIDAY_SNATCH_TREASURE_FEEDBACK);
        int costByTicket1 = getNeedSnatchTreasureScoreNum(1, a);
        int costByTicket5 = getNeedSnatchTreasureScoreNum(5, a);
        boolean isDiscount = null != a;
        return new RDSnatchTreasureInfo(wishValue, remainTime, awards, weekDrawTimes, isDiscount,
                1, 5, costByTicket1, costByTicket5, weekBoxList);
    }

    /**
     * 获取本周夺宝次数宝箱信息
     *
     * @param uid
     * @return
     */
    private List<RDSnatchTreasureBox> getWeekBoxList(long uid) {
        UserSnatchTreasureBox snatchTreasureBox = gameUserService.getSingleItem(uid, UserSnatchTreasureBox.class);
        if (null == snatchTreasureBox) {
            snatchTreasureBox = UserSnatchTreasureBox.getInstance(uid);
            gameUserService.addItem(uid, snatchTreasureBox);
        }
        List<RDSnatchTreasureBox> boxList = new ArrayList<>();
        List<CfgSnatchTreasureBox> boxes = SnatchTreasureTool.getSnatchTreasureBoxes();
        for (CfgSnatchTreasureBox box : boxes) {
            SnatchTreasureBoxStatus status = getBoxStatus(snatchTreasureBox, box.getId());
            boxList.add(new RDSnatchTreasureBox(box.getId(), status.getValue()));
        }
        return boxList;
    }

    /**
     * 获取箱子状态
     *
     * @param snatchTreasureBox
     * @param boxId
     * @return
     */
    private SnatchTreasureBoxStatus getBoxStatus(UserSnatchTreasureBox snatchTreasureBox, int boxId) {
        if (null == snatchTreasureBox) {
            return SnatchTreasureBoxStatus.NO_ACCOMPLISHED;
        }
        List<Integer> accomplishedIds = snatchTreasureBox.getAccomplishedIds();
        if (accomplishedIds.contains(boxId)) {
            return SnatchTreasureBoxStatus.ACCOMPLISHED;
        }
        List<Integer> awardedIds = snatchTreasureBox.getAwardedIds();
        if (awardedIds.contains(boxId)) {
            return SnatchTreasureBoxStatus.AWARDED;
        }
        return SnatchTreasureBoxStatus.NO_ACCOMPLISHED;
    }

    /**
     * 夺宝抽奖
     *
     * @param uid
     * @param drawTimes
     * @return
     */
    public RDSnatchTreasureDraw draw(long uid, int drawTimes) {
        // 检查抽奖次数，不合格的报错提示
        if (1 != drawTimes && 5 != drawTimes) {
            throw new ExceptionForClientTip("snatch.treasure.error.drawTimes");
        }
        // 检查资源，并获得要扣除的法宝id
        IActivity a = activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.HOLIDAY_SNATCH_TREASURE_FEEDBACK);
        int treasureId = checkAndGetConsumeTreasureId(uid, drawTimes, a);
        // 扣除资源
        int deductTreasureNum = drawTimes;
        if (TreasureEnum.SNATCH_TREASURE_SCORE.getValue() == treasureId) {
            deductTreasureNum = getNeedSnatchTreasureScoreNum(drawTimes, a);
        }
        RDSnatchTreasureDraw rd = new RDSnatchTreasureDraw();
        TreasureEventPublisher.pubTDeductEvent(uid, treasureId, deductTreasureNum, WayEnum.SNATCH_TREASUER_DRAW, rd);
        // 获取抽奖结果集
        List<Award> drawResult = getDrawResult(uid, drawTimes);
        // 发放奖励
        awardService.fetchAward(uid, drawResult, WayEnum.SNATCH_TREASUER_DRAW, "", rd);
        // 发布事件
        SnatchTreasureEventPublisher.pubDrawEvent(uid, WayEnum.SNATCH_TREASUER_DRAW, rd, drawTimes, treasureId);
        // 返回许愿值
        UserSnatchTreasure userSnatchTreasure = gameUserService.getSingleItem(uid, UserSnatchTreasure.class);
        if (null == userSnatchTreasure) {
            userSnatchTreasure = UserSnatchTreasure.getInstance(uid);
            gameUserService.addItem(uid, userSnatchTreasure);
        }
        rd.setWishValue(userSnatchTreasure.getWishValue());
        return rd;
    }

    private int getNeedSnatchTreasureScoreNum(int drawTimes, IActivity a) {
        if (null == a) {
            return drawTimes == 5 ? 270 : 60;
        }
        return drawTimes == 5 ? 225 : 50;
    }

    /**
     * 检查资源，并返回要扣除的法宝id
     *
     * @param uid
     * @param drawTimes
     * @return
     */
    private int checkAndGetConsumeTreasureId(long uid, int drawTimes, IActivity a) {
        int treasureId = TreasureEnum.SNATCH_TREASURE_TICKET.getValue();
        int ticket = userTreasureService.getTreasureNum(uid, TreasureEnum.SNATCH_TREASURE_TICKET.getValue());
        if (ticket < drawTimes) {
            int score = userTreasureService.getTreasureNum(uid, TreasureEnum.SNATCH_TREASURE_SCORE.getValue());
            treasureId = TreasureEnum.SNATCH_TREASURE_SCORE.getValue();
            int needScore = getNeedSnatchTreasureScoreNum(drawTimes, a);
            if (score < needScore) {
                throw new ExceptionForClientTip("snatch.treasure.draw.resource.not.enough");
            }
        }
        return treasureId;
    }

    /**
     * 获取抽奖结果集
     *
     * @param uid
     * @param drawTimes
     * @return
     */
    private List<Award> getDrawResult(long uid, int drawTimes) {
        List<BoxGood> resultGoods = new ArrayList<>();
        UserSnatchTreasure userSnatchTreasure = gameUserService.getSingleItem(uid, UserSnatchTreasure.class);
        if (null == userSnatchTreasure) {
            userSnatchTreasure = UserSnatchTreasure.getInstance(uid);
            gameUserService.addItem(uid, userSnatchTreasure);
        }
        Integer wishValue = userSnatchTreasure.getWishValue();
        for (int i = 0; i < drawTimes; i++) {
            List<BoxGood> boxGoods = getBoxGoodsByWishValue(uid, wishValue);
            BoxGood boxGood = getRandomBoxGood(boxGoods);
            if ("夺宝符".equals(boxGood.getGood())) {
                userSnatchTreasure.setLastWishValue(wishValue);
                wishValue = 0;
            }
            resultGoods.add(boxGood);
            wishValue += 1;
        }
        userSnatchTreasure.setWishValue(wishValue);
        gameUserService.updateItem(userSnatchTreasure);
        return boxService.toAwards(uid, resultGoods);
    }

    /**
     * 根据概率随机抽取
     *
     * @param boxGoods
     * @return
     */
    private BoxGood getRandomBoxGood(List<BoxGood> boxGoods) {
        int random = PowerRandom.getRandomBySeed(10000);
        int sum = 0;
        for (BoxGood boxGood : boxGoods) {
            Integer prop = boxGood.getProp();
            sum += prop;
            if (random <= sum) {
                return boxGood;
            }
        }
        return boxGoods.get(0);
    }

    /**
     * 获取对应奖励及奖励对应概率
     *
     * @return
     */
    private List<BoxGood> getBoxGoods() {
        CfgSnatchTreasureAwards snatchTreasureAwards = SnatchTreasureTool.getSnatchTreasureAwards();
        CfgSnatchTreasureAwards clone = CloneUtil.clone(snatchTreasureAwards);
        GameSnatchTreasureCard snatchTreasureCard = getCurGameSnatchTreasureCard();
        List<BoxGood> goods = clone.getGoods();
        List<BoxGood> boxGoods = snatchTreasureCard.getBoxGoods();
        for (int i = 0; i < boxGoods.size(); i++) {
            BoxGood boxGood = boxGoods.get(i);
            int index = i == 0 ? 3 : 9;
            goods.add(index, boxGood);
        }
        return goods;
    }

    /**
     * 获取当前的全服夺宝卡牌对象，没有则生成
     *
     * @return
     */
    private GameSnatchTreasureCard getCurGameSnatchTreasureCard() {
        List<GameSnatchTreasureCard> snatchTreasureCards = gameDataService.getGameDatas(GameSnatchTreasureCard.class);
        Date now = DateUtil.now();
        GameSnatchTreasureCard gameSnatchTreasureCard = snatchTreasureCards.stream().filter(sc ->
                sc.getBeginTime().before(now) && sc.getEndTime().after(now)).findFirst().orElse(null);
        if (null == gameSnatchTreasureCard) {
            String instanceLockKey = "game" + SPLIT + "snatchTreasure" + SPLIT + "instance";
            gameSnatchTreasureCard = (GameSnatchTreasureCard) redisLockUtil.doSafe(instanceLockKey, tmp -> {
                GameSnatchTreasureCard data = snatchTreasureCards.stream().filter(sc -> sc.getBeginTime().before(now)
                        && sc.getEndTime().after(now)).findFirst().orElse(null);
                if (null == data) {
                    data = generateSnatchTreasureCard();
                }
                return data;
            });
        }
        return gameSnatchTreasureCard;
    }

    /**
     * 生成夺宝卡牌数据
     *
     * @return
     */
    private GameSnatchTreasureCard generateSnatchTreasureCard() {
        List<GameSnatchTreasureCard> snatchTreasureCards = gameDataService.getGameDatas(GameSnatchTreasureCard.class);
        // 第一次生成数据
        if (ListUtil.isEmpty(snatchTreasureCards)) {
            // 时间必须是周天晚上的结束时间
            Date date = DateUtil.fromDateTimeString("2020-09-06 23:59:59");
            return generateSnatchTreasureCard(date);
        }
        // 之前已经生成过数据了
        GameSnatchTreasureCard latestData = snatchTreasureCards.stream()
                .max(Comparator.comparing(GameSnatchTreasureCard::getEndTime)).get();
        return generateSnatchTreasureCard(latestData.getEndTime());
    }

    /**
     * 根据上次结束时间生成数据
     *
     * @param lastEndTime
     * @return
     */
    private GameSnatchTreasureCard generateSnatchTreasureCard(Date lastEndTime) {
        CfgSnatchTreasureCard cfgSnatchTreasureCards = SnatchTreasureTool.getSnatchTreasureCards();
        List<BoxGood> goods = cfgSnatchTreasureCards.getGoods();
        List<GameSnatchTreasureCard> toInsertDatas = new ArrayList<>();
        for (int i = 0; i < goods.size(); i = i + 2) {
            List<BoxGood> boxGoods = goods.subList(i, i + 2);
            Date beginTime = DateUtil.addSeconds(lastEndTime, 1 + 14 * (i / 2) * 24 * 60 * 60);
            // 每14天轮换一次
            Date endTime = DateUtil.addSeconds(beginTime, 2 * 7 * 24 * 60 * 60 - 1);
            toInsertDatas.add(GameSnatchTreasureCard.getInstance(boxGoods, beginTime, endTime));
        }
        gameDataService.addGameDatas(toInsertDatas);
        Date now = DateUtil.now();
        return toInsertDatas.stream().filter(sc -> sc.getBeginTime().before(now) &&
                sc.getEndTime().after(now)).findFirst().orElse(null);
    }

    /**
     * 根据幸运值和玩家id获取对应奖励及奖励对应概率
     *
     * @param uid
     * @param wishValue
     * @return
     */
    public List<BoxGood> getBoxGoodsByWishValue(long uid, Integer wishValue) {
        List<BoxGood> goods = getBoxGoods();
        BoxGood hdxs = goods.stream().filter(g -> "混沌仙石".equals(g.getGood())).findFirst().orElse(null);
        List<BoxGood> cardGoods = goods.stream().filter(g -> g.getItem() == 40).collect(Collectors.toList());
        // 卡牌已拥有的话，概率加到混沌仙石上面
        for (BoxGood cardGood : cardGoods) {
            String cardName = cardGood.getGood();
            Integer cardId = CardTool.getCardByName(cardName).getId();
            UserCard userCard = userCardService.getUserCard(uid, cardId);
            if (null != userCard) {
                hdxs.setProp(hdxs.getProp() + cardGood.getProp());
                cardGood.setProp(0);
            }
        }
        UserSnatchTreasure userSnatchTreasure = gameUserService.getSingleItem(uid, UserSnatchTreasure.class);
        if (null == userSnatchTreasure) {
            userSnatchTreasure = UserSnatchTreasure.getInstance(uid);
            gameUserService.addItem(uid, userSnatchTreasure);
        }
        // 幸运值满了
        if (userSnatchTreasure.getNeedWish().intValue() == wishValue) {
            BoxGood duoBaoFu = goods.stream().filter(g -> "夺宝符".equals(g.getGood())).findFirst().orElse(null);
            duoBaoFu.setProp(10000);
            goods = new ArrayList<>();
            goods.add(duoBaoFu);
        }
        return goods.stream().filter(g -> g.getProp() > 0).collect(Collectors.toList());
    }

    /**
     * 开启周累计宝箱
     *
     * @param uid
     * @param boxId
     * @return
     */
    public RDCommon openWeekBox(long uid, int boxId) {
        UserSnatchTreasureBox snatchTreasureBox = gameUserService.getSingleItem(uid, UserSnatchTreasureBox.class);
        if (null == snatchTreasureBox) {
            snatchTreasureBox = UserSnatchTreasureBox.getInstance(uid);
            gameUserService.addItem(uid, snatchTreasureBox);
        }
        List<Integer> accomplishedIds = snatchTreasureBox.getAccomplishedIds();
        List<Integer> awardedIds = snatchTreasureBox.getAwardedIds();
        // 已经领取过了
        if (awardedIds.contains(boxId)) {
            throw new ExceptionForClientTip("snatch.treasure.week.box.already.awarded");
        }
        // 还没达成，无法领取
        if (!accomplishedIds.contains(boxId)) {
            throw new ExceptionForClientTip("snatch.treasure.week.box.can.not.open");
        }
        // 发奖励
        RDCommon rd = new RDCommon();
        CfgSnatchTreasureBox box = SnatchTreasureTool.getSnatchTreasureBox(boxId);
        List<Award> awards = box.getAwards();
        awardService.fetchAward(uid, awards, WayEnum.SNATCH_TREASURE_BOX, "", rd);
        // 添加记录并保存
        snatchTreasureBox.openBox(boxId);
        gameUserService.updateItem(snatchTreasureBox);
        return rd;
    }

    /**
     * 周累计宝箱奖励预览
     *
     * @param boxId
     * @return
     */
    public RDSnatchTreasureBoxAward getBoxAward(int boxId) {
        CfgSnatchTreasureBox box = SnatchTreasureTool.getSnatchTreasureBox(boxId);
        List<Award> awards = box.getAwards();
        return new RDSnatchTreasureBoxAward(boxId, awards);
    }

    /**
     * 每周一8点重置
     *
     * @param uid
     */
    private void resetUserSnatchTreasure(long uid) {
        statisticService.resetPerWeek(uid);
        UserSnatchTreasureBox snatchTreasureBox = gameUserService.getSingleItem(uid, UserSnatchTreasureBox.class);
        if (null == snatchTreasureBox) {
            snatchTreasureBox = UserSnatchTreasureBox.getInstance(uid);
            gameUserService.addItem(uid, snatchTreasureBox);
        }
        // 需要重置玩家夺宝开箱，1、首次更新时间信息为null，2、校验最近一次重置的时间符合重置规则
        if (snatchTreasureBox.getLastRestDate() != null && !ifResetUserSnatchBox(snatchTreasureBox)) {
           return;
        }
        snatchTreasureBox.setLastRestDate(DateUtil.now());
        snatchTreasureBox.setAccomplishedIds(new ArrayList<>());
        snatchTreasureBox.setAwardedIds(new ArrayList<>());
        gameUserService.updateItem(snatchTreasureBox);
    }

    /**
     * 是否需要重置玩家夺宝开箱
     *
     * @param snatchTreasureBox 玩家夺宝开箱记录
     * @return true-重置，false-不重置
     */
    private boolean ifResetUserSnatchBox(UserSnatchTreasureBox snatchTreasureBox) {
        // 每周 周一
        int weekMonday = 1;
        // 8点（24小时制）
        int eightClock = 8;
        // 最近一次重置时间不为本周
        boolean isThisWeek = !DateUtil.isThisWeek(snatchTreasureBox.getLastRestDate());
        if (isThisWeek) {
            // 则当前时间大于周一：重置
            if (DateUtil.getWeekDay(DateUtil.now()) > weekMonday) {
                return true;
            }
            // 当前为周一且大于8点（24小时制）：重置
            return DateUtil.isWeekDay(weekMonday) && DateUtil.getHourOfDay(DateUtil.now()) >= eightClock;
        }
        return false;
    }
}