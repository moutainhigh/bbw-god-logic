package com.bbw.god.gameuser.pay;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玩家体力信息服务
 *
 * @author suhq
 * @date 2021-05-17 15:40
 **/
@Service
public class UserPayInfoService {
    @Autowired
    private GameUserService gameUserService;

    public UserPayInfo getUserPayInfo(long uid) {
        UserPayInfo userPayInfo = gameUserService.getSingleItem(uid, UserPayInfo.class);
        if (null == userPayInfo) {
            GameUser user = gameUserService.getGameUser(uid);
            userPayInfo = UserPayInfo.getInstance(user);
            gameUserService.addItem(uid, userPayInfo);
        }
        return userPayInfo;
    }
}
