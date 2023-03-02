package com.bbw.god.gameuser.kunls;

import com.bbw.cache.UserCacheService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.kunls.data.UserInfusionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 昆仑山注灵室服务工具类
 *
 * @author: huanghb
 * @date: 2022/9/15 11:33
 */
@Slf4j
@Service
public class KunLSInfusionService {
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取玩家昆仑山注灵室信息，
     *
     * @param uid
     * @return
     */
    public UserInfusionInfo getUserInfusionInfo(long uid) {
        UserInfusionInfo userInfusionInfo = this.gameUserService.getSingleItem(uid, UserInfusionInfo.class);
        if (null != userInfusionInfo) {
            return userInfusionInfo;
        }
        return UserInfusionInfo.instance(uid);
    }

    /**
     * 清楚注灵信息
     *
     * @param userInfusionInfo
     */
    public void cleanInfusionInfo(UserInfusionInfo userInfusionInfo) {
        gameUserService.deleteItem(userInfusionInfo);
//        log.info("正在进行生成至宝的清除注灵信息阶段:" + userInfusionInfo.toString());
    }

    /**
     * 添加注灵信息
     *
     * @param uid
     * @param userInfusionInfo
     */
    public void addInfusionInfo(long uid, UserInfusionInfo userInfusionInfo) {
        gameUserService.addItem(uid, userInfusionInfo);
//        log.info("{}添加新的灵宝{}", uid, userInfusionInfo.toString());
    }

    /**
     * 缓存注灵信息
     *
     * @param userInfusionInfo
     */
    public void cacheInfusionInfo(UserInfusionInfo userInfusionInfo) {
        gameUserService.updateItem(userInfusionInfo);
    }

}
