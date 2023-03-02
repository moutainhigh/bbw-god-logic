package com.bbw.god.game.config;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-04-22
 */
public class GameTool {

    /**
     * 获取游戏全局相关配置
     * @return
     */
    public static CfgGame getGameConfig(){
        return Cfg.I.getUniqueConfig(CfgGame.class);
    }

    /**
     * 获取游戏允许的最大名称长度
     * @return
     */
    public static int maxNicknameLength(){
        return getGameConfig().getMaxNicknameLength();
    }
}
