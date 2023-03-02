package com.bbw.god.gameuser.nightmarenvwam.pinchpeople;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玩家捏土造人service
 *
 * @author fzj
 * @date 2022/5/4 14:03
 */
@Service
public class UserKneadSoilService {
    @Autowired
    GameUserService gameUserService;

    /**
     * 获得或创建玩家捏土造人信息
     *
     * @param uid
     * @return
     */
    public UserPinchPeopleInfo getOrCreatUserKneadSoilInFo(long uid) {
        UserPinchPeopleInfo pinchPeopleInfo = gameUserService.getSingleItem(uid, UserPinchPeopleInfo.class);
        if (null != pinchPeopleInfo) {
            return pinchPeopleInfo;
        }
        pinchPeopleInfo = UserPinchPeopleInfo.getInstance(uid);
        gameUserService.addItem(uid, pinchPeopleInfo);
        return pinchPeopleInfo;
    }

    /**
     * 获得玩家捏土造人信息
     *
     * @param uid
     * @return
     */
    public UserPinchPeopleInfo getUserKneadSoilInFo(long uid) {
        return gameUserService.getSingleItem(uid, UserPinchPeopleInfo.class);
    }
}
