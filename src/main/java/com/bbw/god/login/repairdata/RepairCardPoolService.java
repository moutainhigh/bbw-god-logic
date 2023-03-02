package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.mall.cardshop.CardPoolStatusEnum;
import com.bbw.god.mall.cardshop.CardShopService;
import com.bbw.god.mall.cardshop.UserCardPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_CARD_POOL;

/**
 * @author suchaobin
 * @description 修复卡池service
 * @date 2020/12/7 14:21
 **/
@Service
public class RepairCardPoolService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(REPAIR_CARD_POOL)) {
            UserCardPool cardPool = gameUserService.getMultiItems(gu.getId(), UserCardPool.class).stream().filter(tmp ->
                    tmp.getCardPool() == CardPoolEnum.JUX_CP.getValue()).findFirst().orElse(null);
            // 聚贤卡池未解锁
            if (null != cardPool && !cardPool.ifUnlock()) {
                cardPool.setIsUnlock(CardPoolStatusEnum.UNLOCK.getValue());
                gameUserService.updateItem(cardPool);
            }
        }
    }
}
