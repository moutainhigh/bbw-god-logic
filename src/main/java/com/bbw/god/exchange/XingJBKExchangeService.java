package com.bbw.god.exchange;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.exchange.RDExchangeList.RDExchangeGoodInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.exchangegood.CfgExchangeGoodEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 星君宝库兑换
 *
 * @author suhq
 * @date 2019年3月27日 上午11:06:51
 */
@Deprecated
@Service
public class XingJBKExchangeService extends ExchangeService {

    @Autowired
    private UserTreasureService userTreasureService;

    public XingJBKExchangeService() {
        this.exchangeWay = ExchangeWayEnum.XJBK;
    }

    @Override
    RDExchangeList toRdExchangeList(long guId, List<CfgExchangeGoodEntity> goods) {
        List<RDExchangeGoodInfo> rdGoodsInfo = goods.stream().map(good -> RDExchangeGoodInfo.fromExchangeGood(good)).collect(Collectors.toList());
        RDExchangeList rd = new RDExchangeList();
        rd.setGoods(rdGoodsInfo);
        int shenShaNum = userTreasureService.getTreasureNum(guId, TreasureEnum.SS.getValue());
        rd.setOwnSsNum(shenShaNum);
        // 设置魂源数量
        int hunYuanNum = userTreasureService.getTreasureNum(guId, TreasureEnum.HY.getValue());
        rd.setOwnHyNum(hunYuanNum);
        return rd;
    }

    @Override
    RDCommon toDeliver(long guId, int sid, CfgExchangeGoodEntity exchangeGood, int exchangeNum) {
        int needNum = exchangeGood.getPrice() * exchangeNum;
        RDCommon rd = new RDCommon();
        int unit = exchangeGood.getUnit();// 兑换需要的道具单位 具体见ConsumeType
        TreasureEnum treasureEnum = TreasureEnum.HY;
        switch (ConsumeType.fromValue(unit)) {
            case SHEN_SHA:
                treasureEnum = TreasureEnum.SS;
            case HUN_YUAN:
                TreasureChecker.checkIsEnough(treasureEnum.getValue(), needNum, guId);
                TreasureEventPublisher.pubTDeductEvent(guId, treasureEnum.getValue(), needNum, WayEnum.EXCHANGE_XJBK, rd);
                break;
            case XIAN_YU:
                TreasureChecker.checkIsEnough(TreasureEnum.XY.getValue(), needNum, guId);
                TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.XY.getValue(), needNum, WayEnum.EXCHANGE_XJBK, rd);
                break;
            default:
                // 不是符合的兑换道具单位
                throw new ExceptionForClientTip("exchange.not.define.unit");
        }
        deliver(guId, exchangeGood, exchangeNum, WayEnum.EXCHANGE_XJBK, rd);
        return rd;
    }

}
