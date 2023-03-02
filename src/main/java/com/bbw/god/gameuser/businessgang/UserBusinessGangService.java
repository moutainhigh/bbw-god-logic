package com.bbw.god.gameuser.businessgang;

import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 玩家商帮服务类
 *
 * @author fzj
 * @date 2022/1/17 11:55
 */
@Service
public class UserBusinessGangService {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    BusinessGangService businessGangService;

    /**
     * 获得玩家商帮信息
     *
     * @param uid
     * @return
     */
    public UserBusinessGangInfo getUserBusinessGang(long uid) {
        return gameUserService.getSingleItem(uid, UserBusinessGangInfo.class);
    }

    /**
     * 获得或者创建商帮信息
     *
     * @param uid
     * @return
     */
    public UserBusinessGangInfo getOrCreateUserBusinessGang(long uid) {
        UserBusinessGangInfo userBusinessGang = getUserBusinessGang(uid);
        if (null != userBusinessGang) {
            return userBusinessGang;
        }
        userBusinessGang = UserBusinessGangInfo.getInstance(uid);
        gameUserService.addItem(uid, userBusinessGang);
        return userBusinessGang;
    }

    /**
     * 获得商帮任务信息
     *
     * @param uid
     * @return
     */
    public UserBusinessGangTaskInfo getUserBusinessGangTask(long uid) {
        return gameUserService.getSingleItem(uid, UserBusinessGangTaskInfo.class);
    }

    /**
     * 获得或者创建商帮任务信息
     *
     * @param uid
     * @return
     */
    public UserBusinessGangTaskInfo getOrCreateUserBusinessGangTask(long uid) {
        UserBusinessGangTaskInfo item = gameUserService.getSingleItem(uid, UserBusinessGangTaskInfo.class);
        if (null != item) {
            return item;
        }
        item = UserBusinessGangTaskInfo.getInstance(uid);
        gameUserService.addItem(uid, item);
        return item;
    }
}
