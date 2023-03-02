package com.bbw.god.game.config.treasure;

import com.bbw.god.game.config.Cfg;

/**
 * @author lwb
 * @date 2020/6/29 10:01
 */
public class DeifyTokenTool {

    public static CfgDeifyToken getCfgDeifyToken(int tokenId) {
        return Cfg.I.get(tokenId, CfgDeifyToken.class);
    }

    /**
     * 获得群体封神令配置
     *
     * @param tokenId
     * @return
     */
    public static CfgDeifysToken getCfgDeifyTokens(int tokenId) {
        return Cfg.I.get(tokenId, CfgDeifysToken.class);
    }
}
