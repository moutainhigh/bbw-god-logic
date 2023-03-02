package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.*;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.mall.MallLogic;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 藏宝秘境
 *
 * @author: huanghb
 * @date: 2021/12/20 15:38
 */
@Service
public class HolidayTreasureTroveMallProcessor extends AbstractMallProcessor {
    @Autowired
    private TreasureTroveService treasureTroveService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private HolidayTreasureTroveProcessor holidayTreasureTroveProcessor;
    @Autowired
    private MallLogic mallLogic;

    HolidayTreasureTroveMallProcessor() {
        this.mallType = MallEnum.TREASURE_SECRET;
    }

    @Override
    public RDMallList getGoods(long guId) {
        int sid = this.gameUserService.getActiveSid(guId);
        //获得用户藏宝秘境信息
        UserTreasureTrove userTreasureTrove = treasureTroveService.getUserTreasureTrove(guId);
        //获得宝藏令数量
        int treasureTroveOrderNum = treasureTroveService.getTresureOrderNum(guId);
        //购买次数
        Integer purchaseNum = userTreasureTrove.gainBoughtNum();
        //宝藏值
        int treasureTroveValue = userTreasureTrove.getTroveValue();

        RDTreasureSectetInfos rd = RDTreasureSectetInfos.instance(userTreasureTrove);
        rd.setOpenBigTreasure(userTreasureTrove.getIsBuildBigAward());
        rd.setTreasureTroveOrderNum(treasureTroveOrderNum);
        rd.setPurchaseNum(purchaseNum);
        rd.setTreasureTroveValue(treasureTroveValue);
        return rd;
    }

    /**
     * 更新藏宝秘境
     *
     * @param uid
     * @return
     */
    public RDMallList refreshMyTreasureTrove(long uid) {
        UserTreasureTrove userTreasureTrove = treasureTroveService.getUserTreasureTrove(uid);
        //是否开启大奖池
        boolean isToBuildBigAward = userTreasureTrove.ifToBuildBigAward();
        // 是否元宝足够
        GameUser gameUser = gameUserService.getGameUser(uid);
        int needGold = treasureTroveService.getRefreshNeedGold(isToBuildBigAward);
        ResChecker.checkGold(gameUser, needGold);
        List<CfgTreasureTrove.TroveAward> troveAwards = treasureTroveService.buildTroveAwards(uid, isToBuildBigAward);
        userTreasureTrove.refreshMallIds(troveAwards);
        userTreasureTrove.addTroveValue(1);
        treasureTroveService.updateTroveToCache(uid, userTreasureTrove);
        //返回信息
        RDTreasureSectetInfos rd = RDTreasureSectetInfos.instance(userTreasureTrove);
        rd.setOpenBigTreasure(userTreasureTrove.getIsBuildBigAward());
        rd.setTreasureTroveValue(userTreasureTrove.getTroveValue());
        rd.setPurchaseNum(userTreasureTrove.gainBoughtNum());
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.TREASURE_TROVE, rd);
        return rd;
    }

    /**
     * 购买藏宝秘境商品
     *
     * @param uid
     * @param mallIndex
     * @return
     */
    public RDCommon buyMyTreasureTrove(long uid, Integer mallIndex) {
        UserTreasureTrove userTreasureTrove = treasureTroveService.getUserTreasureTrove(uid);
        boolean isNotBougt = userTreasureTrove.ifNotBought(mallIndex);
        if (!isNotBougt) {
            throw new ExceptionForClientTip("store.goods.limit");
        }
        //购买藏宝秘境商品
        int mallId = userTreasureTrove.getMallIds()[mallIndex];
        RDCommon rd = mallLogic.buy(uid, mallId, 1);
        CfgTreasureTrove.TroveAward troveAward = TreasureTroveTool.getTroveAward(mallId);
        userTreasureTrove.updateMallToBought(mallIndex, troveAward.getBigAward());
        treasureTroveService.updateTroveToCache(uid, userTreasureTrove);
        return rd;
    }


    /**
     * 发放奖励
     *
     * @param guId
     * @param mall
     * @param buyNum
     * @param rd
     */
    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        //获得奖品
        List<Award> awards = new ArrayList<>();
        Award award = Award.instance(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), mall.getNum());
        awards.add(award);
        //发放奖品
        awardService.fetchAward(guId, awards, WayEnum.TREASURE_TROVE, WayEnum.TREASURE_TROVE.getName(), rd);
    }

    /**
     * 获得用户奖励
     *
     * @param guId
     * @return
     */
    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        return null;
    }


}
