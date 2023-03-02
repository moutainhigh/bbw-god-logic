package com.bbw.god.game.config.city;

import com.bbw.god.game.config.Cfg;

/**
 * 法坛工具类
 *
 * @author fzj
 * @date 2021/11/11 10:10
 */
public class FaTanTool {
    /**
     * 获取法坛配置类
     *
     * @return
     */
    public static CfgFaTanEntity getFaTanInFo() {
        return Cfg.I.getUniqueConfig(CfgFaTanEntity.class);
    }
}
