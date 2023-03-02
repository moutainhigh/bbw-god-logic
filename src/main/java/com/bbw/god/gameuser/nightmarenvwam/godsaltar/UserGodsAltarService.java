package com.bbw.god.gameuser.nightmarenvwam.godsaltar;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玩家封神祭坛服务类
 *
 * @author fzj
 * @date 2022/5/10 14:54
 */
@Service
public class UserGodsAltarService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取活创建玩家神格信息
     *
     * @param uid
     * @return
     */
    public UserGodsAltarInfo getOrCreatGodsAltarInfo(long uid) {
        UserGodsAltarInfo userGodsAltarInfo = gameUserService.getSingleItem(uid, UserGodsAltarInfo.class);
        if (null != userGodsAltarInfo) {
            return userGodsAltarInfo;
        }
        userGodsAltarInfo = UserGodsAltarInfo.getInstance(uid);
        gameUserService.addItem(uid, userGodsAltarInfo);
        return userGodsAltarInfo;
    }
}
