package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.common.lock.redis.annotation.RedisLockParam;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum.TradeStatusEnum;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 女娲集市redis锁
 *
 * @author fzj
 * @date 2022/6/8 9:18
 */
@Slf4j
@Component
public class NvWaMarketRedisLockService {
    @Autowired
    private GameNvWaMarketNumService gameNvWaMarketNumService;

    @RedisLock(key = "game:var:nvWaMarketTradeGoodsCheckLock")
    public boolean tradeGoodsCheckLock(@RedisLockParam long uid, long productNo, GameNvWaBooth booth) {
        return booth.isEnoughProductNum(productNo);
    }

    @RedisLock(key = "game:var:nvWaMarketTradeGoodsDelLock")
    public void tradeGoodsDelLock(@RedisLockParam long uid, long productNo, GameNvWaBooth booth, int priceNo) {
        //更新价格状态
        booth.updatePriceWayStatus(productNo, priceNo);
        //扣除商品数量
        booth.delProduct(productNo);
    }

    @RedisLock(key = "game:var:nvWaMarketRentalBoothLock")
    public Integer rentalBoothLock(@RedisLockParam long uid, Date expiredDate, long boothId) {
        return gameNvWaMarketNumService.addBoothNo(uid, DateUtil.toDateTimeLong(expiredDate), boothId);
    }

    @RedisLock(key = "game:var:nvWaMarketRentalBoothLock")
    public void bargainDealWithLock(@RedisLockParam long bargainId, GameNvWaMarketBargain nvWaMarketBargain) {
        if (nvWaMarketBargain.isDealWith()) {
            throw new ExceptionForClientTip("nightmareNvWaM.nvWaMarket.booth.bargain.deal_with");
        }
    }
}
