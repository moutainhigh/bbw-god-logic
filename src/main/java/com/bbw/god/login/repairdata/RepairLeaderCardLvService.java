package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.LEADER_CARD_LV_REPAIR_TIME;

/**
 * 修复玩家主角卡等级
 *
 * @author fzj
 * @date 2022/2/7 8:28
 */
public class RepairLeaderCardLvService implements BaseRepairDataService {
    @Autowired
    LeaderCardService leaderCardService;
    @Autowired
    GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (!lastLoginDate.before(LEADER_CARD_LV_REPAIR_TIME)) {
            return;
        }
        Long uid = gu.getId();
        UserLeaderCard leaderCard=leaderCardService.getUserLeaderCard(uid);
        Integer leaderCardLv = leaderCard.getLv();
        if (leaderCardLv <= 30){
            return;
        }
        leaderCard.setLv(30);
        gameUserService.updateItem(leaderCard);
    }
}
