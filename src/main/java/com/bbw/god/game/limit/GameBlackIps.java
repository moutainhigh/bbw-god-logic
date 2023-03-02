package com.bbw.god.game.limit;

import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏黑名单ip
 *
 * @author: suhq
 * @date: 2022/3/30 3:15 下午
 */
@Data
@ToString(callSuper = true)
public class GameBlackIps extends GameData {
    private Map<String, Integer> blackIps;

    public static GameBlackIps instance() {
        GameBlackIps instance = new GameBlackIps();
        instance.setId(ID.INSTANCE.nextId());
        instance.setBlackIps(new HashMap<>());
        return instance;
    }

    /**
     * 添加黑名单
     *
     * @param ip
     */
    public void addBlackIp(String ip) {
        blackIps.put(ip, 1);
    }

    /**
     * 移除黑名单
     *
     * @param ip
     */
    public void removeBlackIp(String ip) {
        blackIps.remove(ip);
    }

    /**
     * 是否是黑名单ip
     *
     * @param ip
     * @return
     */
    public boolean ifBlackIp(String ip) {
        Integer value = blackIps.getOrDefault(ip, 0);
        return 1 == value;
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.BLACK_IPS;
    }

}
