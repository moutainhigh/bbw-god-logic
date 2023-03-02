package com.bbw.god.mall;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.HolidayDiscountChangeProcessor;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.TreasureTroveService;
import com.bbw.god.activity.processor.CombinedServiceDiscountChangeProcessor;
import com.bbw.god.detail.async.MallDetailAsyncHandler;
import com.bbw.god.detail.async.MallDetailEventParam;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.FavorableBagEnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.HonorCurrencyService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.event.MallEventPublisher;
import com.bbw.god.mall.processor.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MallLogic {
    @Autowired
    private MallProcessorFactory mallProcessorFactory;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MysteriousMallProcessor mysteriousMallProcessor;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private MallService mallService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private HolidayDiscountChangeProcessor holidayBfDiscountProcessor;
    @Autowired
    private CombinedServiceDiscountChangeProcessor combinedServiceDiscountChangeProcessor;
    @Autowired
    private MallDetailAsyncHandler mallDetailAsyncHandler;
    @Autowired
    private HolidayTreasureTroveMallProcessor holidayTreasureSecretMallProcessor;
    @Autowired
    private TreasureTroveService treasureTroveService;
    @Autowired
    private HonorCurrencyService honorCurrencyService;
    @Autowired
    private HolidaySkyLanternWorkShopMallProcessor holidaySkyLanternWorkShopMallProcessor;
    /** 荣耀货币 */
    private final static List<Integer> HONOR_CURRENCY = Arrays.asList(
            TreasureEnum.HONOR_SILVER_COIN.getValue(),
            TreasureEnum.HONOR_COPPER_COIN.getValue());

    /**
     * @param guId
     * @param type 10-道具；20-神秘；30-卡包；40-礼包
     * @return
     */
    public RDMallList getProducts(long guId, int type) {
        AbstractMallProcessor mallProcessor = mallProcessorFactory.getMallProcessor(type);
        if (mallProcessor == null) {
            throw new ExceptionForClientTip("mall.not.valid.type");
        }
        return mallProcessor.getGoods(guId);
    }

    public RDCommon buy(long guId, int mallId, int buyNum) {
        CfgMallEntity mall = MallTool.getMall(mallId);
        if (mall == null) {
            //诛仙令需要特殊处理，客户端传的是诛仙令的ID不是商品ID
            if (mallId == TreasureEnum.ZXL.getValue()) {
                mall = MallTool.getMall(1327);
            } else {
                throw new ExceptionForClientTip("mall.not.valid");
            }
        }
        if (buyNum <= 0 || buyNum > MallTool.getMallConfig().getMaxBuyNum()) {
            throw new ExceptionForClientTip("mall.not.valid.num");
        }

        // 检查权限
        AbstractMallProcessor mallProcessor = mallProcessorFactory.getMallProcessor(mall.getType());
        mallProcessor.checkAuth(guId, mall);

        RDCommon rd = new RDCommon();
        // 检查购买记录
        UserMallRecord userMallRecord = mallProcessor.checkRecord(guId, mall, buyNum);
        GameUser gu = gameUserService.getGameUser(guId);
        if (mall.getGoodsId() == FavorableBagEnum.TiLLB.getValue() && gu.ifMaxDice()) {
            throw new ExceptionForClientTip("gu.dice.outOfLimit");
        }
        // 检查和扣除资源
        checkAndHandlePrice(gu, mall, buyNum, rd);
        // 发放
        mallProcessor.deliver(guId, mall, buyNum, rd);
        // 限购的处理购买纪录
        if (mall.getLimit() != 0) {
            if (userMallRecord == null) {
                userMallRecord = UserMallRecord.instance(guId, mallId, mall.getType(), buyNum);
                mallService.addRecord(userMallRecord);
            } else {
                userMallRecord.addNum(buyNum);
                gameUserService.updateItem(userMallRecord);
            }
        }
        BaseEventParam bep = new BaseEventParam(guId, WayEnum.MALL_BUY, rd);
        MallEventPublisher.pubMallbuySendEvent(mall.getGoodsId(), mall.getType(), buyNum, bep);
        return rd;
    }

    /**
     * 刷新神秘物品
     *
     * @param guId
     * @return
     */
    public RDMallList refreshMysterious(long guId) {
        return mysteriousMallProcessor.refreshMysterious(guId);
    }

    /**
     * 刷新藏宝秘境
     *
     * @param guId
     * @return
     */
    public RDMallList refreshMyTreasureTrove(long guId) {
        return holidayTreasureSecretMallProcessor.refreshMyTreasureTrove(guId);
    }
    
    /**
     * 藏宝秘境购买
     *
     * @param guId
     * @return
     */
    public RDCommon buyMyTreasureTrove(long guId, Integer mallIndex) {
        return holidayTreasureSecretMallProcessor.buyMyTreasureTrove(guId, mallIndex);
    }

    /**
     * 获取物品信息，当前只支持助力礼包
     *
     * @param uid
     * @param goodsId
     * @return
     */
    public RDMallInfo getMallBagInfo(long uid, int goodsId) {
        FavorableBagEnum favorableBag = FavorableBagEnum.fromValue(goodsId);
        CfgMallEntity mall = MallTool.getMall(favorableBag.getType(), goodsId);
        if (mall == null) {
            throw new ExceptionForClientTip("mall.not.valid");
        }
        RDMallInfo rd = new RDMallInfo();
        MallEnum mallType = MallEnum.fromValue(mall.getType());
        if (mallType == MallEnum.ZLLB) {
            List<UserMallRecord> umRecords = mallService.getUserMallRecord(uid, mallType);
            Optional<UserMallRecord> optionalUmr = umRecords.stream().filter(r -> r.getBaseId().equals(mall.getId())).findFirst();
            if (optionalUmr.isPresent()) {
                UserMallRecord umr = optionalUmr.get();
                rd.setRemainTimes(mall.getLimit() - umr.getNum());
                rd.setRemainTime(umr.getDateTime().getTime() - System.currentTimeMillis());
                rd.setPrice(mall.getPrice());
                rd.setUnit(mall.getUnit());
                rd.setRechargeId(MallTool.getRechargeId(mall.getGoodsId()));
            }
        } else if (mallType == MallEnum.ACTIVITY_BAG) {
            int remainTimes = mallService.getActivityMallRemainTimes(uid, goodsId);
            rd = RDMallInfo.fromMall(mall, mall.getPrice(), remainTimes);
            IActivity a = mallService.getMallBagActivity(uid, goodsId);
            long remainTime = 0L;
            if (a != null) {
                remainTime = a.gainEnd().getTime() - System.currentTimeMillis();
            }
            rd.setRemainTime(remainTime);
        }
        return rd;
    }

    /**
     * 检查和扣除资源
     *
     * @param gu
     * @param mall
     * @param buyNum
     * @param rd
     */
    private void checkAndHandlePrice(GameUser gu, CfgMallEntity mall, int buyNum, RDCommon rd) {
        long guId = gu.getId();
        int price = mall.getPrice();
        MallEnum mallType = MallEnum.fromValue(mall.getType());
        // 参与折扣的商品
        List<MallEnum> discountMalllTypes = Arrays.asList(MallEnum.NOT_SHOWED, MallEnum.DJ);
        if (discountMalllTypes.contains(mallType)) {
            boolean isDiscount = activityService.isActive(gu.getServerId(), ActivityEnum.MALL_DISCOUNT);
            price = mall.getPrice(isDiscount);
        }
        //藏宝秘境价格
        if (mallType == MallEnum.TREASURE_SECRET) {
            price = treasureTroveService.getMallPrice(guId, mall);
        }
        //黑五特惠（折扣变化商店）
        if (mallType == MallEnum.DISCOUNT_CHANGER) {
            price *= holidayBfDiscountProcessor.getLimitedTimeDiscount(guId);
        }
        //合服特惠折扣
        if (mallType == MallEnum.SPECIAL_DISCOUNT) {
            price *= combinedServiceDiscountChangeProcessor.getLimitedTimeDiscount(guId);
        }
        ConsumeType cType = mall.gainConsumeType();
        int needPay = price * buyNum;
        // 扣除铜钱、元宝
        switch (cType) {
            case GOLD:
                ResChecker.checkGold(gu, needPay);
                ResEventPublisher.pubGoldDeductEvent(guId, needPay, WayEnum.MALL_BUY, rd);
                mallDetailAsyncHandler.log(new MallDetailEventParam(guId, mall, buyNum, needPay, gu.getGold()));
                break;
            case COPPER:
                ResChecker.checkCopper(gu, needPay);
                ResEventPublisher.pubCopperDeductEvent(guId, (long) (needPay), WayEnum.MALL_BUY, rd);
                mallDetailAsyncHandler.log(new MallDetailEventParam(guId, mall, buyNum, needPay, gu.getCopper()));
                break;
            case DIAMOND:
                ResChecker.checkDiamond(gu, needPay);
                ResEventPublisher.pubDiamondDeductEvent(guId, needPay, WayEnum.MALL_BUY, rd);
                mallDetailAsyncHandler.log(new MallDetailEventParam(guId, mall, buyNum, needPay, gu.getDiamond()));
                break;
            default:

                TreasureEnum treasureEnum = TreasureEnum.fromValue(cType.getGoodsId());
                if (null == treasureEnum) {
                    throw new ExceptionForClientTip("mall.not.support.unit");
                }
                //是否礼包形式发放
                boolean isPriceAsPackage = cType.isPriceAsPackage();
                if (isPriceAsPackage) {
                    checkAndHandlePriceAsPackage(guId, treasureEnum, needPay, rd);
                    break;
                }
                checkAndHandlePriceAsTreasure(guId, treasureEnum, needPay, rd);
                break;
        }
    }

    /**
     * 处理以道具礼包形式为单位的
     *
     * @param uid
     * @param treasure
     * @param needPay
     * @param rd
     */
    private void checkAndHandlePriceAsPackage(long uid, TreasureEnum treasure, int needPay, RDCommon rd) {
        UserTreasure uTreasure = userTreasureService.getUserTreasure(uid, treasure.getValue());
        if (uTreasure != null && uTreasure.gainTotalNum() >= needPay) {
            TreasureEventPublisher.pubTDeductEvent(uid, treasure.getValue(), needPay, WayEnum.EXCHANGE_XJBK, rd);
            return;
        }
        // 礼包不足时，判断道具数量能否兑换成足额礼包
        Integer packageOwnNum = uTreasure == null ? 0 : uTreasure.gainTotalNum();
        String packageName = treasure.getName();
        String[] split = packageName.split("、");
        Map<Integer, Integer> treasureMap = new HashMap<>(16);
        for (int i = 0; i < split.length; i++) {
            int index = split[i].indexOf("*");
            String treasureName = split[i].substring(0, index);
            Integer num = Integer.parseInt(split[i].substring(index + 1));
            Integer treasureId = TreasureTool.getTreasureByName(treasureName).getId();
            Integer ownNum = userTreasureService.getTreasureNum(uid, treasureId);
            if (ownNum < (needPay - packageOwnNum) * num) {
                throw new ExceptionForClientTip("treasure.not.enough", treasureName);
            }
            treasureMap.put(treasureId, (needPay - packageOwnNum) * num);
        }
        // 兑换道具
        Set<Integer> keySet = treasureMap.keySet();
        for (Integer key : keySet) {
            TreasureEventPublisher.pubTDeductEvent(uid, key, treasureMap.get(key), WayEnum.HOLIDAY_EXCHANGE, rd);
        }
        TreasureEventPublisher.pubTDeductEvent(uid, treasure.getValue(), packageOwnNum, WayEnum.HOLIDAY_EXCHANGE, rd);
    }

    /**
     * 处理以道具形式为单位的
     *
     * @param uid
     * @param treasure
     * @param needPay
     * @param rd
     */
    private void checkAndHandlePriceAsTreasure(long uid, TreasureEnum treasure, int needPay, RDCommon rd) {
        int treasureId = treasure.getValue();
//        if (HONOR_CURRENCY.contains(treasureId)){
//            honorCurrencyService.honorCurrencyDeductConvert(uid, treasureId, needPay, rd);
//            return;
//        }
        TreasureChecker.checkIsEnough(treasureId, needPay, uid);
        TreasureEventPublisher.pubTDeductEvent(uid, treasureId, needPay, WayEnum.HOLIDAY_EXCHANGE, rd);
    }

    /**
     * 返回 某个商品的信息和购买次数
     *
     * @param uid
     * @param type
     * @param goodsId
     * @return
     */
    public RDMallInfo getGoodsInfoByTypeID(long uid, int type, int goodsId) {
        CfgMallEntity mall = MallTool.getMall(type, goodsId);
        if (mall == null) {
            throw new ExceptionForClientTip("mall.not.valid");
        }
        RDMallInfo info = RDMallInfo.fromMall(mall);
        AbstractMallProcessor mallProcessor = mallProcessorFactory.getMallProcessor(mall.getType());
        info.setBoughtTimes(mallProcessor.boughtTimes(uid, mall));
        info.setLimit(mall.getLimit());
        return info;
    }
}
