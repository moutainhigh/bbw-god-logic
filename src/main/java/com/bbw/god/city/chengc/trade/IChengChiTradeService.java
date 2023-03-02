package com.bbw.god.city.chengc.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 城池交易接口
 *
 * @author: suhq
 * @date: 2022/7/4 9:23 上午
 */
public interface IChengChiTradeService {
    /**
     * 获取子服务可购买的物品
     *
     * @return
     */
    List<Integer> getAbleTradeGoodIds();

    /**
     * 获取要购买的物品价格
     *
     * @param goodId
     * @return
     */
    int getTradeBuyPrice(int goodId);


    /**
     * 获取某个批次中某个子服务的物品购买信息
     *
     * @return
     */
    default List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        List<BuyGoodInfo> buyGoodInfos = new ArrayList<>();
        List<Integer> goodIds = getAbleTradeGoodIds();
        List<Integer> goodIdsToBuy = specialIds.stream().filter(goodIds::contains).collect(Collectors.toList());
        if (goodIdsToBuy.isEmpty()) {
            return buyGoodInfos;
        }
        //发放礼物
        for (int goodIdToBuy : goodIdsToBuy) {
            int price = getTradeBuyPrice(goodIdToBuy);
            BuyGoodInfo buyGoodInfo = new BuyGoodInfo(goodIdToBuy, price);
            buyGoodInfos.add(buyGoodInfo);
        }
        return buyGoodInfos;
    }

}
