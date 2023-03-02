package com.bbw.god.game.sxdh;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 神仙大会，不依赖其他Sxdh***Service
 *
 * @author suhq
 * @date 2019-06-21 14:19:24
 */
@Service
public class SxdhService {
    @Autowired
    private RedisSetUtil<String> sxdhFighterUtil;

    /**
     * 加入神仙大会
     *
     * @param fighterKey
     * @param serverGroup
     */
    public void joinSxdh(String fighterKey, int serverGroup) {
        sxdhFighterUtil.add(getKey(serverGroup), fighterKey);
    }

    /**
     * 是否加入神仙大会
     *
     * @param fighterKey
     * @param serverGroup
     * @return
     */
    public boolean isJoinedSxdh(String fighterKey, int serverGroup) {
        return sxdhFighterUtil.isMember(getKey(serverGroup), fighterKey);
    }

    /**
     * 获取排行的key
     *
     * @param group
     * @return
     */
    private String getKey(int group) {
        return "game" + GameRedisKey.SPLIT + "sxdh" + GameRedisKey.SPLIT + group;
    }
}
