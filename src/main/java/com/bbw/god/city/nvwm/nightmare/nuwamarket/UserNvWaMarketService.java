package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 女娲集市服务类
 *
 * @author fzj
 * @date 2022/5/9 16:00
 */
@Service
public class UserNvWaMarketService {
    @Autowired
    GameUserService gameUserService;

    /**
     * 获得女娲集市交易记录信息
     *
     * @param uid
     * @return
     */
    public List<UserNvWaTradeRecord> getUserNvWaMarketTaredRecord(long uid) {
        return gameUserService.getMultiItems(uid, UserNvWaTradeRecord.class);
    }

    /**
     * 获得价格模板信息
     *
     * @param uid
     * @return
     */
    public UserNvWaPriceModel getOrCreatUserNvWaPriceModel(long uid) {
        UserNvWaPriceModel priceModel = gameUserService.getSingleItem(uid, UserNvWaPriceModel.class);
        if (null == priceModel) {
            priceModel = UserNvWaPriceModel.getInstance(uid);
            gameUserService.addItem(uid, priceModel);
        }
        return priceModel;
    }
}
