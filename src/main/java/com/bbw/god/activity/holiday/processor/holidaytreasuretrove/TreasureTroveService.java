package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.ConsumeType;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 藏宝秘境逻辑
 *
 * @author: huanghb
 * @date: 2021/12/21 18:14
 */
@Service
public class TreasureTroveService {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private TroveGrandPrizeBuyTimesCacheService troveGrandPrizeBuyTimesCacheService;
    @Autowired
    private TroveGrandPrizebGuaranteeInfoCacheService troveGrandPrizebGuaranteeInfoCacheService;
    

    /**
     * 获得藏宝秘境价格
     *
     * @param guId
     * @param mall
     * @return
     */
    public Integer getMallPrice(long guId, CfgMallEntity mall) {
        //购买单位为宝藏令，直接返回价格
        troveGrandPrizeBuyTimesCacheService.addBuyTimes(guId, mall.getId());
        if (mall.getUnit() == ConsumeType.SHUANG_D_DH_3.getValue()) {
            return mall.getPrice();
        }
        UserTreasureTrove userTreasureTrove = getUserTreasureTrove(guId);
        int purchaseNumber = userTreasureTrove.gainBoughtNum();
        //第一次免费
        if (purchaseNumber == 0) {
            return 0;
        }
        int price = mall.getPrice() + (purchaseNumber - 1) * 5;
        return price;
    }

    /**
     * 获得用户藏宝秘境信息
     *
     * @param uid
     * @return
     */
    public UserTreasureTrove getUserTreasureTrove(long uid) {
        UserTreasureTrove userTrove = getTroveFromCache(uid);
        //缓存为空初始化
        if (null != userTrove) {
            return userTrove;
        }
        //数据缓存到本地和redis
        userTrove = UserTreasureTrove.instance(new ArrayList<>());
        updateTroveToCache(uid, userTrove);
        return userTrove;
    }


    /**
     * 获得刷新藏宝秘境需要的法宝
     *
     * @param isOpenGrandPrize
     * @return
     */
    public int getRefreshNeedGold(boolean isOpenGrandPrize) {
        if (isOpenGrandPrize) {
            return 0;
        }
        int needGold = TreasureTroveTool.getTroveCfg().getRefreshNeedGold();
        return needGold;
    }

    /**
     * 获得宝藏令数量
     *
     * @param uid
     * @return
     */
    public int getTresureOrderNum(long uid) {
        return userTreasureService.getTreasureNum(uid, TreasureEnum.BAO_ZHANG_LING.getValue());
    }

    /**
     * 生成奖池
     *
     * @param isToBuildBigAwards
     * @return
     */
    public List<CfgTreasureTrove.TroveAward> buildTroveAwards(long uid, boolean isToBuildBigAwards) {
        List<CfgTreasureTrove.TroveAward> troveAwards = new ArrayList<>();
        int awardNum = isToBuildBigAwards ? TreasureTroveEnum.GRAND_PRIZES.getAwardNum() : TreasureTroveEnum.ORDINARY_PRIZES.getAwardNum();
        //获得藏宝秘境配置
        CfgTreasureTrove troveCfg = TreasureTroveTool.getTroveCfg();
        CfgTreasureTrove cfgTreasureTrove = TreasureTroveTool.getTroveCfg();
        //获得概率衰减后的奖池
        List<CfgTreasureTrove.TroveAward> bigAwardPool = cfgTreasureTrove.probDecays(troveGrandPrizeBuyTimesCacheService.getAllBuyTimes(uid));
        //大奖池概率
        List<Integer> bigAwardProps = bigAwardPool.stream().map(CfgTreasureTrove.TroveAward::getProbability).collect(Collectors.toList());
        //总购买次数
        int sumBuyTimes = troveGrandPrizeBuyTimesCacheService.getBuyTimes(uid, TroveGrandPrizeBuyTimesCacheService.TOTAL_BUY_TIMES);
        //保底信息
        List<CfgTreasureTrove.GuaranteeInformation> guaranteeInformations = cfgTreasureTrove.getGuaranteeInformations();
        //保底判定
        for (CfgTreasureTrove.GuaranteeInformation guaranteeInformation : guaranteeInformations) {
            if (sumBuyTimes < guaranteeInformation.getGuaranteedTimes()) {
                continue;
            }
            int buyTimes = troveGrandPrizeBuyTimesCacheService.getBuyTimes(uid, guaranteeInformation.getAwardPoolId());
            if (0 != buyTimes) {
                continue;
            }
            int isGuaranteed = troveGrandPrizebGuaranteeInfoCacheService.getGuaranteeInfo(uid, guaranteeInformation.getAwardPoolId());
            if (0 != isGuaranteed) {
                continue;
            }
            troveGrandPrizebGuaranteeInfoCacheService.renewGuaranteeInfo(uid, guaranteeInformation.getAwardPoolId());
            troveAwards.add(bigAwardPool.stream().filter(tmp -> tmp.getId().equals(guaranteeInformation.getAwardPoolId())).findFirst().orElse(null));
            awardNum--;
        }
        //是否触发大奖
        if (isToBuildBigAwards) {
            for (int i = 0; i < awardNum; i++) {
                int awardIndex = PowerRandom.getIndexByProbs(bigAwardProps, troveCfg.getPropSum());
                troveAwards.add(bigAwardPool.get(awardIndex));
            }
            return troveAwards;
        }
        //生成普通奖池
        List<CfgTreasureTrove.TroveAward> normalAwardPool = TreasureTroveTool.getNormalAwardPool();
        List<Integer> normalProps = normalAwardPool.stream().map(CfgTreasureTrove.TroveAward::getProbability).collect(Collectors.toList());
        for (int i = 0; i < awardNum; i++) {
            int random = PowerRandom.getRandomBySeed(troveCfg.getPropSum());
            if (random <= troveCfg.getBigAwardPoolProbability()) {
                int awardIndex = PowerRandom.getIndexByProbs(bigAwardProps, troveCfg.getPropSum());
                troveAwards.add(bigAwardPool.get(awardIndex));
                continue;
            }
            int normalAwardIndex = PowerRandom.getIndexByProbs(normalProps, troveCfg.getPropSum() - troveCfg.getBigAwardPoolProbability());
            troveAwards.add(normalAwardPool.get(normalAwardIndex));
        }
        return troveAwards;
    }


    /**
     * 更新用户藏宝秘境
     *
     * @param uid
     * @param userTreasureTrove
     */
    public void updateTroveToCache(long uid, UserTreasureTrove userTreasureTrove) {
        String cacheKey = getTroveCacheKey();
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, cacheKey, userTreasureTrove, DateUtil.SECOND_ONE_DAY * 11);
    }

    /**
     * 读取藏宝秘境信息（从缓存）
     *
     * @param uid
     * @return
     */
    public UserTreasureTrove getTroveFromCache(long uid) {
        String cacheKey = getTroveCacheKey();
        UserTreasureTrove userTreasureTrove = TimeLimitCacheUtil.getFromCache(uid, cacheKey, UserTreasureTrove.class);
        return userTreasureTrove;
    }


    /**
     * 藏宝秘境缓存key
     *
     * @return
     */
    private String getTroveCacheKey() {
        return "treasure" + SPLIT + "trove";
    }
}
