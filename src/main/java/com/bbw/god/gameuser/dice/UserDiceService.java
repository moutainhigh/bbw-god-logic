package com.bbw.god.gameuser.dice;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 玩家体力信息服务
 *
 * @author suhq
 * @date 2021-05-17 15:40
 **/
@Service
public class UserDiceService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 更新最近增长时间
     *
     * @param uid
     * @param date
     */
    public void updateLastIncTime(long uid, Date date) {
        UserDiceInfo userDiceInfo = getUserDiceInfo(uid);
        userDiceInfo.setDiceLastIncTime(date);
        gameUserService.updateItem(userDiceInfo);
    }

    public UserDiceInfo getUserDiceInfo(long uid) {
        UserDiceInfo userDiceInfo = gameUserService.getSingleItem(uid, UserDiceInfo.class);
        if (null == userDiceInfo) {
            GameUser user = gameUserService.getGameUser(uid);
            Date lastIncTime = user.getStatus().getDiceLastIncTime();
            Date lastBuyTime = user.getStatus().getDiceLastBuyTime();
            Integer buyTimes = user.getStatus().getDiceBuyTimes();
            if (lastBuyTime == null || !DateUtil.isToday(lastBuyTime)) {
                buyTimes = 0;
            }
            if (null == lastIncTime) {
                lastIncTime = user.getRoleInfo().getRegTime();
            }
            userDiceInfo = UserDiceInfo.getInstance(uid, lastIncTime, lastBuyTime, buyTimes);
            gameUserService.addItem(uid, userDiceInfo);
        }
        return userDiceInfo;
    }
}
