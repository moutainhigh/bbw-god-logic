package com.bbw.god.gameuser.treasure;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 荣耀货币服务类
 *
 * @author fzj
 * @date 2022/1/29 16:40
 */
@Service
public class HonorCurrencyService {
    @Autowired
    UserTreasureService userTreasureService;
    @Autowired
    GameUserService gameUserService;


    /**
     * 荣耀货币增加转化
     *
     * @param uid
     * @param treasureId
     * @param num
     * @param rd
     */
    @Deprecated
    public void honorCurrencyAddConversion(long uid, int treasureId, int num, RDCommon rd) {
        TreasureEnum treasureEnum = TreasureEnum.fromValue(treasureId);
        switch (treasureEnum){
            case HONOR_COPPER_COIN:
                copperConversion(uid, num, rd);
                break;
            case HONOR_SILVER_COIN:
                silverConversion(uid, num, 0, rd);
                break;
            case HONOR_GOLD_COIN:
                goldConversion(uid, num, 0, rd);
            default:
        }
    }

    /**
     * 铜币转换
     *
     * @param uid
     * @param addCopperNum
     * @param rd
     */
    @Deprecated
    private void copperConversion(long uid, int addCopperNum, RDCommon rd) {
        UserTreasure ut = getOrCreatHonorCurrency(uid, TreasureEnum.HONOR_COPPER_COIN);
        int copperCoin = ut.gainTotalNum();
        //获取货币转化汇率
        Integer rate = BusinessGangCfgTool.getBusinessGangInfo().getGloryCurrencyRate();
        //获取总数的荣耀铜币
        int totalCopperCoinNum = copperCoin + addCopperNum;
        //如果小于转化率
        if (totalCopperCoinNum < rate) {
            addHonorCurrency(addCopperNum, ut);
            rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_COPPER_COIN.getValue(), addCopperNum, AwardEnum.FB.getValue(), 0, addCopperNum));
            return;
        }
        //获得可以增加的银币
        int addSilverCoinNum = totalCopperCoinNum / rate;
        //获得剩余铜币
        int remainCopperCoinNum = totalCopperCoinNum - (addSilverCoinNum * rate);
        //变化的铜币值
        int varietyCopperNum = remainCopperCoinNum - copperCoin;
        if (varietyCopperNum == copperCoin) {
            silverConversion(uid, addSilverCoinNum, 1, rd);
            return;
        }
        if (varietyCopperNum < 0) {
            rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_COPPER_COIN.getValue(), 0, AwardEnum.FB.getValue(), 0, addCopperNum));
            //发布扣除事件
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), -varietyCopperNum, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
        } else {
            addHonorCurrency(addCopperNum, ut);
            rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_COPPER_COIN.getValue(), varietyCopperNum, AwardEnum.FB.getValue(), 0, addCopperNum));
        }
        silverConversion(uid, addSilverCoinNum, 1, rd);
    }

    /**
     * 银币转换
     *
     * @param uid
     * @param addSilverCoinNum
     * @param rd
     */
    @Deprecated
    private void silverConversion(long uid, int addSilverCoinNum, int isShow, RDCommon rd) {
        UserTreasure ut = getOrCreatHonorCurrency(uid, TreasureEnum.HONOR_SILVER_COIN);
        int silverCoin = ut.gainTotalNum();
        //获取货币转化汇率
        Integer rate = BusinessGangCfgTool.getBusinessGangInfo().getGloryCurrencyRate();
        //获取总数的荣耀银币
        int totalSilverCoinNum = silverCoin + addSilverCoinNum;
        if (totalSilverCoinNum < rate) {
            addHonorCurrency(addSilverCoinNum, ut);
            rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_SILVER_COIN.getValue(), addSilverCoinNum, AwardEnum.FB.getValue(), isShow, addSilverCoinNum));
            return;
        }
        //获得可以增加的金币
        int addGoldCoinNum = totalSilverCoinNum / rate;
        //获得剩余银币
        int remainSilverCoinNum = totalSilverCoinNum - (addGoldCoinNum * rate);
        //变化的银币值
        int varietySilverNum = remainSilverCoinNum - silverCoin;
        if (varietySilverNum == silverCoin) {
            goldConversion(uid, addGoldCoinNum, 1, rd);
            return;
        }
        if (varietySilverNum < 0) {
            rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_SILVER_COIN.getValue(), 0, AwardEnum.FB.getValue(), isShow, addSilverCoinNum));
            //发布扣除事件
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_SILVER_COIN.getValue(), -varietySilverNum, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
        } else {
            addHonorCurrency(addSilverCoinNum, ut);
            rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_SILVER_COIN.getValue(), varietySilverNum, AwardEnum.FB.getValue(), isShow, addSilverCoinNum));
        }
        goldConversion(uid, addGoldCoinNum, 1, rd);
    }

    /**
     * 金币转换
     *
     * @param uid
     * @param addGoldCoinNum
     * @param rd
     */
    @Deprecated
    private void goldConversion(long uid, int addGoldCoinNum, int isShow, RDCommon rd) {
        UserTreasure ut = getOrCreatHonorCurrency(uid, TreasureEnum.HONOR_GOLD_COIN);
        addHonorCurrency(addGoldCoinNum, ut);
        rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.HONOR_GOLD_COIN.getValue(), addGoldCoinNum, AwardEnum.FB.getValue(), isShow, addGoldCoinNum));
    }

    /**
     * 获取或者创建法宝信息
     *
     * @param uid
     * @param treasureEnum
     * @return
     */
    private UserTreasure getOrCreatHonorCurrency(long uid, TreasureEnum treasureEnum) {
        UserTreasure ut = userTreasureService.getUserTreasure(uid, treasureEnum.getValue());
        CfgTreasureEntity cfgTreasure = TreasureTool.getTreasureById(treasureEnum.getValue());
        if (null == ut) {
            ut = UserTreasure.instance(uid, cfgTreasure, 0);
            gameUserService.addItem(uid, ut);
        }
        return ut;
    }

    /**
     * 增加荣耀货币
     *
     * @param addNum
     */
    @Deprecated
    private void addHonorCurrency(int addNum, UserTreasure ut) {
        ut.addNum(addNum);
        gameUserService.updateItem(ut);
    }

    /**
     * 荣耀货币扣除转化
     *
     * @param uid
     * @param treasureId
     * @param needNum
     */
    @Deprecated
    public void honorCurrencyDeductConvert(long uid, int treasureId, int needNum, RDCommon rd) {
        if (treasureId == TreasureEnum.HONOR_COPPER_COIN.getValue()) {
            deductCopperConversion(uid, needNum, rd);
            return;
        }
        if (treasureId == TreasureEnum.HONOR_SILVER_COIN.getValue()) {
            deductSilverConversion(uid, needNum, 0, rd);
            return;
        }
        if (treasureId == TreasureEnum.HONOR_GOLD_COIN.getValue()) {
            deductColdConversion(uid, needNum, 0, 0, rd);
        }
    }

    /**
     * 扣除铜币转换
     *
     * @param uid
     * @param needNum
     * @param rd
     */
    @Deprecated
    private void deductCopperConversion(long uid, int needNum, RDCommon rd) {
        //获取货币转化汇率
        Integer rate = BusinessGangCfgTool.getBusinessGangInfo().getGloryCurrencyRate();
        //获取已拥有的铜币数
        int copperCoin = userTreasureService.getTreasureNum(uid, TreasureEnum.HONOR_COPPER_COIN.getValue());
        //缺少的铜币数
        int lackCopperCoin = needNum - copperCoin;
        //如果铜币足够
        if (lackCopperCoin <= 0) {
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), needNum, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
            return;
        }
        //需要的银币数
        int needSilverCoin = lackCopperCoin / rate;
        needSilverCoin = needSilverCoin + 1;
        //变化后的铜币
        int varietyCopper = needSilverCoin * rate - needNum;
        deductSilverConversion(uid, needSilverCoin, varietyCopper, rd);
    }

    /**
     * 扣除银币转换
     *
     * @param uid
     * @param needNum
     * @param varietyCopper
     * @param rd
     */
    @Deprecated
    private void deductSilverConversion(long uid, int needNum, int varietyCopper, RDCommon rd) {
        //获取货币转化汇率
        Integer rate = BusinessGangCfgTool.getBusinessGangInfo().getGloryCurrencyRate();
        //获取已拥有的银币数
        int silverCoin = userTreasureService.getTreasureNum(uid, TreasureEnum.HONOR_SILVER_COIN.getValue());
        //缺少的银币数
        int lackSilverCoin = needNum - silverCoin;
        //如果银币足够
        if (lackSilverCoin <= 0) {
            //铜币处理
            copperDeal(uid, varietyCopper, rd);
            //银币扣除
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_SILVER_COIN.getValue(), needNum, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
            return;
        }
        //需要的金币数
        int needGoldCoin = lackSilverCoin / rate;
        needGoldCoin = needGoldCoin + 1;
        //变化后的银币
        int varietySilver = needGoldCoin * rate - needNum;
        deductColdConversion(uid, needGoldCoin, varietyCopper, varietySilver, rd);
    }

    /**
     * 扣除金币转换
     *
     * @param uid
     * @param needGoldNum
     * @param varietyCopper
     * @param varietySilver
     * @param rd
     */
    @Deprecated
    private void deductColdConversion(long uid, int needGoldNum, int varietyCopper, int varietySilver, RDCommon rd) {
        //检查金币是否足够
        TreasureChecker.checkIsEnough(TreasureEnum.HONOR_GOLD_COIN.getValue(), needGoldNum, uid);
        //铜币处理
        copperDeal(uid, varietyCopper, rd);
        //银币处理
        silverDeal(uid, varietySilver, rd);
        //金币处理
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_GOLD_COIN.getValue(), needGoldNum, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
    }

    /**
     * 铜币处理
     *
     * @param uid
     * @param varietyCopper
     * @param rd
     */
    @Deprecated
    private void copperDeal(long uid, int varietyCopper, RDCommon rd) {
        if (varietyCopper == 0) {
            return;
        }
        //铜币处理
        if (varietyCopper > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), varietyCopper, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
            return;
        }
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), -varietyCopper, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);

    }

    /**
     * 银币处理
     *
     * @param uid
     * @param varietySilver
     * @param rd
     */
    @Deprecated
    private void silverDeal(long uid, int varietySilver, RDCommon rd) {
        if (varietySilver == 0) {
            return;
        }
        //银币处理
        if (varietySilver > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.HONOR_SILVER_COIN.getValue(), varietySilver, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
            return;
        }
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_SILVER_COIN.getValue(), -varietySilver, WayEnum.HONOR_CURRENCY_EXCHANGE, rd);
    }
}
