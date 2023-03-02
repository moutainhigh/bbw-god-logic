package com.bbw.god.game.transmigration;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.redis.RedisKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 轮回世界进入记录服务
 *
 * @author: suhq
 * @date: 2021/9/16 10:12 上午
 */
@Service
@Slf4j
public class TransmigrationGlobalEnterService {
    /** 进入过轮回世界的玩家集合 */
    @Autowired
    private RedisSetUtil<Long> enterUidUtil;

    /**
     * 获取玩家在某城的最好记录
     *
     * @param uid
     */
    public void enter(long uid) {
        String key = getKey();
        if (enterUidUtil.isMember(key, uid)) {
            return;
        }
        enterUidUtil.add(key, uid);
    }

    /**
     * 是否进入过轮回
     *
     * @param uid
     */
    public boolean isEnter(long uid) {
        String key = getKey();
        return enterUidUtil.isMember(key, uid);
    }

    /**
     * 进入过轮回的玩家：game:transmigration:0enterUids
     *
     * @return
     */
    private static String getKey() {
        return "game" + RedisKeyConst.SPLIT + GameDataType.TRANSMIGRATION.getRedisKey() + RedisKeyConst.SPLIT + "0enterUids";
    }
}
