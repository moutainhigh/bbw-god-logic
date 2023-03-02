package com.bbw.god.activity.holiday.processor.holidaycutetugermarket;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.cfg.CfgCuteTigerEntity;
import com.bbw.god.activity.cfg.CfgLittleTigerStoreEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 萌虎集市工具类
 *
 * @author fzj
 * @date 2022/3/7 13:48
 */
public class HolidayCuteTigerMarketTool {

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgCuteTigerEntity getCuteTigerCfg() {
        return Cfg.I.getUniqueConfig(CfgCuteTigerEntity.class);
    }

    /**
     * 开启最后一天邮件通知等级
     *
     * @return
     */
    public static Integer getLevelToMailNotice() {
        CfgCuteTigerEntity cuteTigerCfg = getCuteTigerCfg();
        return cuteTigerCfg.getLevelToMailNotice();
    }

    /**
     * 获得特殊野地事件
     *
     * @return
     */
    public static List<CfgCuteTigerEntity.SpecialYeDiEvent> getSpecialYeDiEvents() {
        CfgCuteTigerEntity cuteTigerCfg = getCuteTigerCfg();
        return cuteTigerCfg.getSpecialYeDiEvent();
    }

    /**
     * 根据概率随机获得
     *
     * @return
     */
    public static CfgCuteTigerEntity.SpecialYeDiEvent getRandomSpecialYeDiEvent() {
        List<CfgCuteTigerEntity.SpecialYeDiEvent> specialYeDiEvents = getSpecialYeDiEvents();
        //所有概率
        List<Integer> pro = specialYeDiEvents.stream().map(CfgCuteTigerEntity.SpecialYeDiEvent::getPro).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pro);
        return specialYeDiEvents.get(index);
    }

    /**
     * 根据概率随机获得野地好事件id
     *
     * @return
     */
    public static CfgCuteTigerEntity.SpecialYeDiEvent getRandomSpecialYeDiGoodEvent(int pastryId) {
        List<CfgCuteTigerEntity.SpecialYeDiEvent> specialYeDiEvents = getSpecialYeDiEvents().stream()
                .filter(tmp -> tmp.getTreasureId() == pastryId && tmp.getEffect() > 0).collect(Collectors.toList());
        //所有概率
        List<Integer> pro = specialYeDiEvents.stream().map(CfgCuteTigerEntity.SpecialYeDiEvent::getPro).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pro);
        return specialYeDiEvents.get(index);
    }

    /**
     * 根据概率随机获得野地坏事件id
     *
     * @return
     */
    public static CfgCuteTigerEntity.SpecialYeDiEvent getRandomSpecialYeDiBadEvent(Integer pastryId) {
        List<CfgCuteTigerEntity.SpecialYeDiEvent> specialYeDiEvents = getSpecialYeDiEvents().stream()
                .filter(tmp -> tmp.getTreasureId() == pastryId && tmp.getEffect() < 0).collect(Collectors.toList());
        ;
        //所有概率
        List<Integer> pro = specialYeDiEvents.stream().map(CfgCuteTigerEntity.SpecialYeDiEvent::getPro).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pro);
        return specialYeDiEvents.get(index);
    }


    /**
     * 根据概率获得奖励
     *
     * @param awards
     * @return
     */
    public static List<Award> getRandomAwardsByPro(List<Award> awards) {
        List<Award> awardList = new ArrayList<>();
        //所有概率
        List<Integer> pro = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pro);
        awardList.add(awards.get(index));
        return awardList;
    }

    /**
     * 获得糕点初始价格
     *
     * @param pastryId
     * @return
     */
    public static Integer getPastryInitialPrice(int pastryId) {
        CfgCuteTigerEntity cuteTigerCfg = getCuteTigerCfg();
        List<CfgCuteTigerEntity.InitialSellingPrice> initialSellingPrice = cuteTigerCfg.getInitialSellingPrice();
        Integer price = initialSellingPrice.stream().filter(i -> pastryId == i.getTreasureId())
                .map(CfgCuteTigerEntity.InitialSellingPrice::getPrice).findFirst().orElse(null);
        if (null == price) {
            throw new ExceptionForClientTip("mall.not.valid");
        }
        return price;
    }

    /**
     * 获得野地事件
     *
     * @param eventId
     * @return
     */
    public static CfgCuteTigerEntity.SpecialYeDiEvent getSpecialYeDiEvent(int eventId) {
        return getSpecialYeDiEvents().stream().filter(s -> eventId == s.getId()).findFirst().orElse(null);
    }

    /**
     * 小虎集市配置
     *
     * @return
     */
    public static CfgLittleTigerStoreEntity getLittleTigerStoreEntity() {
        return Cfg.I.getUniqueConfig(CfgLittleTigerStoreEntity.class);
    }

    /**
     * 根据刷新次数返回对应奖池
     *
     * @param refreshTimes
     * @return
     */
    public static int getPoolByRefreshTimes(int refreshTimes) {
        CfgLittleTigerStoreEntity littleTigerStoreEntity = getLittleTigerStoreEntity();
        //根据刷新次数获得奖池
        List<CfgLittleTigerStoreEntity.PoolPro> poolPros = littleTigerStoreEntity.getRefreshAndPoolPro().stream()
                .filter(r -> r.getMinRefreshTimes() <= refreshTimes && refreshTimes <= r.getMaxRefreshTimes())
                .map(CfgLittleTigerStoreEntity.RefreshAndPoolPro::getPoolPro).findFirst().orElse(null);
        if (null == poolPros) {
            return 10;
        }
        //所有概率
        List<Integer> pros = poolPros.stream().map(CfgLittleTigerStoreEntity.PoolPro::getPro).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pros);
        return poolPros.get(index).getId();
    }

    /**
     * 获得随机奖励
     *
     * @param poolId
     * @return
     */
    public static Award getAwardByPoolId(int poolId) {
        CfgLittleTigerStoreEntity littleTigerStoreEntity = getLittleTigerStoreEntity();
        CfgLittleTigerStoreEntity.PoolAndAwards poolAndAwards = littleTigerStoreEntity.getPoolAndAwards()
                .stream().filter(p -> p.getPoolId() == poolId).findFirst().orElse(null);
        if (null == poolAndAwards) {
            throw new ExceptionForClientTip("activity.cuteTigerMarket.pool.not");
        }
        //所有概率
        List<Award> awards = poolAndAwards.getAwards();
        List<Integer> pros = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        int index = PowerRandom.hitProbabilityIndex(pros);
        return awards.get(index);
    }

    /**
     * 获得某个卡池的所有卡牌
     *
     * @param poolId
     * @return
     */
    public static List<Integer> getPoolAllCards(int poolId) {
        CfgLittleTigerStoreEntity littleTigerStoreEntity = getLittleTigerStoreEntity();
        CfgLittleTigerStoreEntity.CardsPool cardsPool = littleTigerStoreEntity.getCardsPool()
                .stream().filter(c -> c.getPoolId() == poolId).findFirst().orElse(null);
        if (null == cardsPool) {
            throw new ExceptionForClientTip("activity.cuteTigerMarket.pool.not");
        }
        return cardsPool.getCards().stream().map(Award::gainAwardId).collect(Collectors.toList());
    }

    /**
     * 获得累计刷新奖励
     *
     * @param refreshTimes
     * @return
     */
    public static List<Award> getGrandTotalAward(int refreshTimes) {
        CfgLittleTigerStoreEntity littleTigerStoreEntity = getLittleTigerStoreEntity();
        return littleTigerStoreEntity.getCumulativeRefreshAwards().stream().filter(r -> r.getRefreshTimes() == refreshTimes)
                .map(CfgLittleTigerStoreEntity.CumulativeRefreshAwards::getAwards).findFirst().orElse(new ArrayList<>());
    }

    /**
     * 获得累计刷新奖励
     *
     * @return
     */
    public static List<CfgLittleTigerStoreEntity.CumulativeRefreshAwards> getGrandTotalAward() {
        CfgLittleTigerStoreEntity littleTigerStoreEntity = getLittleTigerStoreEntity();
        return littleTigerStoreEntity.getCumulativeRefreshAwards();
    }

}
