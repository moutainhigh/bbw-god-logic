package com.bbw.god.exchange;

import com.bbw.god.exchange.RDExchangeList.RDExchangeGoodInfo;
import com.bbw.god.exchange.RDExchangeList.RDOldExchangeGoodInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.exchangegood.CfgExchangeGoodEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 封神台兑换
 *
 * @author suhq
 * @date 2019年3月27日 上午11:06:51
 */
@Deprecated
@Service
public class FstExchangeService extends ExchangeService {

    public FstExchangeService() {
        this.exchangeWay = ExchangeWayEnum.FST;
    }

    @Override
    RDExchangeList toRdExchangeList(long guId, List<CfgExchangeGoodEntity> goods) {
        List<RDExchangeGoodInfo> rdGoodsInfo = goods.stream().map(good -> new RDOldExchangeGoodInfo(good.getId(), good.getPrice())).collect(Collectors.toList());
        RDExchangeList rd = new RDExchangeList();
        rd.setGoods(rdGoodsInfo);
        return rd;
    }

    @Override
    RDCommon toDeliver(long guId, int sid, CfgExchangeGoodEntity exchangeGood, int exchangeNum) {
        int needPoint = exchangeGood.getPrice() * exchangeNum;
        TreasureChecker.checkIsEnough(TreasureEnum.FST_POINT.getValue(), needPoint, guId);
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.FST_POINT.getValue(), needPoint, WayEnum.EXCHANGE_FST, rd);

        deliver(guId, exchangeGood, exchangeNum, WayEnum.EXCHANGE_FST, rd);
        return rd;
    }

}
