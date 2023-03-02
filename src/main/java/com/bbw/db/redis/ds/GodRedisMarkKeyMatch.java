package com.bbw.db.redis.ds;

import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 富甲Redis访问分配器
 *
 * @author suhq
 * @date 2020-12-14 14:54
 **/
@Service
public class GodRedisMarkKeyMatch implements IRedisMarkKeyMatch {
    @Autowired
    private GameUserService gameUserService;

    @Override
    public String getRedisMark(String key) {
        if (StrUtil.isBlank(key)) {
            throw CoderException.normal("key 不能为null或者\"\"");
        }
        int sid = getServer(key);
        if (sid <= 0) {
            return MultiRedis.COMMON_REDIS_DS;
        }
        CfgServerEntity server = ServerTool.getServer(sid);
        if (null == server) {
            return MultiRedis.COMMON_REDIS_DS;
        }
        return server.getRedisDs();
    }

    private int getServer(String key) {
        if (key.startsWith("usr")) {
            String uidPart = key.split(":")[1];
            if (uidPart.contains("?")) {
                return Integer.valueOf(uidPart.substring(6, 10));
            }
            return gameUserService.getActiveSid(Long.parseLong(uidPart));
        }
        if (key.startsWith("server")) {
            return Integer.parseInt(key.split(":")[1]);
        }
        return -1;
    }
}
