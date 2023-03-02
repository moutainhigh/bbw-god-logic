package com.bbw.god.login.strategy;

import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录策略
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月29日 下午2:52:37
 */
public interface LoginCheckStrategy {
    /**
     * 获取UAC的基本路径
     *
     * @return
     */
    default String getUacBaseUrl() {
        CfgGame gameConfig = Cfg.I.getUniqueConfig(CfgGame.class);
        String baseUrl = gameConfig.getUacBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl;
    }

    boolean support(int loginType);

    LoginResult check(HttpServletRequest request, CfgChannelEntity channel);
}
