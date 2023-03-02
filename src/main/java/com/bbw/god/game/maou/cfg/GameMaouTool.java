package com.bbw.god.game.maou.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * 跨服工具类
 *
 * @author: suhq
 * @date: 2021/12/17 11:38 上午
 */
public class GameMaouTool {

    /**
     * 获取跨服魔王相关配置
     *
     * @return
     */
    public static CfgGameMaou getMaouConfig(GameMaouType maouType) {
        return Cfg.I.get(maouType.getValue(), CfgGameMaou.class);
    }

    /**
     * 获取击杀奖励所需要的等级
     *
     * @param maouType
     * @return
     */
    public static Integer getKillAwardsNeedLevel(GameMaouType maouType) {
        CfgGameMaou maouConfig = getMaouConfig(maouType);
        return maouConfig.getKillAwardsNeedLevel();
    }

    /**
     * 获取击杀奖励
     *
     * @param maouType
     * @param turn
     * @return
     */
    public static List<Award> getKillAwards(GameMaouType maouType, int turn) {
        CfgGameMaou.GameMaouInfo maou = getMaouConfig(maouType).getMaouInfo(turn);
        return maou.getKillAwards();
    }
}
