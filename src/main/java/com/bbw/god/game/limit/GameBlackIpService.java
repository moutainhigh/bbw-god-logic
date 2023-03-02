package com.bbw.god.game.limit;

import com.bbw.common.ListUtil;
import com.bbw.god.game.data.GameDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 游戏黑名单ip
 *
 * @author: suhq
 * @date: 2022/3/30 3:15 下午
 */
@Service
public class GameBlackIpService {
    @Autowired
    private GameDataService gameDataService;

    /**
     * 添加黑名单
     *
     * @param ip
     */
    public void addBlackIp(String ip) {
        GameBlackIps gameBlackIps = getGameBlackIps();
        gameBlackIps.addBlackIp(ip);
        gameDataService.updateGameData(gameBlackIps);
    }

    /**
     * 移除黑名单
     *
     * @param ip
     */
    public void removeBlackIp(String ip) {
        GameBlackIps gameBlackIps = getGameBlackIps();
        gameBlackIps.removeBlackIp(ip);
        gameDataService.updateGameData(gameBlackIps);
    }

    /**
     * 是否是黑名单ip
     *
     * @param ip
     * @return
     */
    public boolean ifBlackIp(String ip) {
        List<GameBlackIps> gameDatas = gameDataService.getGameDatas(GameBlackIps.class);
        if (ListUtil.isEmpty(gameDatas)) {
            return false;
        }
        GameBlackIps gameBlackIps = gameDatas.get(0);
        return gameBlackIps.ifBlackIp(ip);
    }

    private GameBlackIps getGameBlackIps() {
        List<GameBlackIps> gameDatas = gameDataService.getGameDatas(GameBlackIps.class);
        GameBlackIps gameBlackIps;
        if (ListUtil.isEmpty(gameDatas)) {
            gameBlackIps = GameBlackIps.instance();
            gameDataService.addGameData(gameBlackIps);
        } else {
            gameBlackIps = gameDatas.get(0);
        }
        return gameBlackIps;
    }

}
