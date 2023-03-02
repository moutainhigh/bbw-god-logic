package com.bbw.god.login.repairdata;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashion;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 主角卡修复
 *
 * @author: suhq
 * @date: 2021/8/5 10:42 上午
 */
@Service
public class RepairLeaserService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserLeaderFashionService userLeaderFashionService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        UserLeaderFashion fashion = userLeaderFashionService.getFashion(gu.getId(), TreasureEnum.FASHION_FaSFS.getValue());
        if (null == fashion) {
            fashion = UserLeaderFashion.getInstance(gu.getId(), TreasureEnum.FASHION_FaSFS.getValue());
            userLeaderFashionService.addUserLeaderFashion(fashion);
        }
    }
}
