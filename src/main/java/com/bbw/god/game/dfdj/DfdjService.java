package com.bbw.god.game.dfdj;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 巅峰对决service
 * @date 2021/1/5 14:32
 **/
@Service
@Slf4j
public class DfdjService {
    @Autowired
    private RedisSetUtil<String> redisSetUtil;

    /**
     * 加入巅峰对决
     *
     * @param fighterKey
     * @param serverGroup
     */
    public void joinDfdj(String fighterKey, int serverGroup) {
        redisSetUtil.add(getKey(serverGroup), fighterKey);
    }

    /**
     * 是否加入巅峰对决
     *
     * @param fighterKey
     * @param serverGroup
     * @return
     */
    public boolean isJoinedDfdj(String fighterKey, int serverGroup) {
        return redisSetUtil.isMember(getKey(serverGroup), fighterKey);
    }

    /**
     * 获取排行的key
     *
     * @param group
     * @return
     */
    private String getKey(int group) {
        return "game" + GameRedisKey.SPLIT + "dfdj" + GameRedisKey.SPLIT + group;
    }
}
