package com.bbw.god.gameuser.bag;

import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 玩家背包服务
 * @date 2020/11/27 14:52
 **/
@Service
public class UserBagService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SyncLockUtil syncLockUtil;

    /**
     * 获取玩家背包购买记录
     *
     * @param uid 玩家id
     * @return
     */
    public UserBagBuyRecord getCurBuyRecord(long uid) {
        UserBagBuyRecord buyRecord = gameUserService.getSingleItem(uid, UserBagBuyRecord.class);
        if (null == buyRecord) {
            buyRecord = (UserBagBuyRecord) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserBagBuyRecord record = gameUserService.getSingleItem(uid, UserBagBuyRecord.class);
                if (null == record) {
                    record = UserBagBuyRecord.getInstance(uid);
                }
                return record;
            });
        }
        return buyRecord;
    }

    /**
     * 购买背包格子
     *
     * @param uid 玩家id
     * @param num 购买数量
     */
    public RDCommon buyBag(long uid, int num) {
        UserBagBuyRecord buyRecord = getCurBuyRecord(uid);
        Integer alreadyBuyTimes = buyRecord.getBuyTimes();
        // 超过购买上限
        if (alreadyBuyTimes + num > UserBagBuyConfig.BUY_LIMIT) {
            throw new ExceptionForClientTip("bag.buy.times.limit");
        }
        // 检查元宝
        int needPay = 0;
        for (int i = 1; i <= num; i++) {
            int buyTimes = alreadyBuyTimes + i;
            needPay += UserBagBuyConfig.getNeedPayGold(buyTimes);
        }
        GameUser gu = gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needPay);
        // 扣除资源
        RDCommon rd = new RDCommon();
        ResEventPublisher.pubGoldDeductEvent(uid, needPay, WayEnum.BUY_BAG, rd);
        // 增加格子
        buyRecord.addBuyTimes(num);
        gameUserService.updateItem(buyRecord);
        return rd;
    }

}
