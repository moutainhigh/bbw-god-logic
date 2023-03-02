package com.bbw.god.gameuser.yaozu;

import com.bbw.cache.UserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 妖族来犯service
 *
 * @author fzj
 * @date 2021/9/7 14:47
 */
@Service
public class UserYaoZuInfoService {
    @Autowired
    public UserCacheService userCacheService;

    /**
     * 获取用户所有妖族来犯的数据
     *
     * @param uid
     * @return
     */
    public List<UserYaoZuInfo> getUserYaoZu(long uid) {
        List<UserYaoZuInfo> UserYaoZuInfo = userCacheService.getUserDatas(uid, UserYaoZuInfo.class);
        return UserYaoZuInfo;
    }

    /**
     * 根据妖族id获取数据
     *
     * @param uid
     * @param yaoZuId
     * @return
     */
    public UserYaoZuInfo getUserYaoZuInfo(long uid, int yaoZuId) {
        return userCacheService.getCfgItem(uid, yaoZuId, UserYaoZuInfo.class);
    }

    /**
     * 根据妖族位置获取数据
     *
     * @param uid
     * @param pos
     * @return
     */
    public UserYaoZuInfo getUserYaoZuInfoByPos(long uid, int pos) {
        Optional<UserYaoZuInfo> userYaoZuInfo = getUserYaoZu(uid).stream().filter(yaoZuInfo -> yaoZuInfo.getPosition() == pos).findFirst();
        return userYaoZuInfo.orElse(null);
    }


}
