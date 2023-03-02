package com.bbw.god.game.transmigration;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 本轮轮回世界进入记录服务
 *
 * @author: suhq
 * @date: 2021/9/16 10:12 上午
 */
@Service
@Slf4j
public class TransmigrationEnterService {
    /** 参与本轮的玩家ID集合 */
    @Autowired
    private RedisSetUtil<Long> enterUidUtil;
    @Autowired
    private TransmigrationGlobalEnterService globalEnterService;

    /**
     * 获取本轮进入轮回世界的所有玩家
     *
     * @param transmigration
     * @return
     */
    public Set<Long> getUids(GameTransmigration transmigration) {
        String key = getKey(transmigration);
        Set<Long> uids = enterUidUtil.members(key);
        return uids;
    }

    /**
     * 参与本轮轮回世界
     *
     * @param transmigration
     * @param uid
     */
    public void enter(GameTransmigration transmigration, long uid) {
        // 轮回不存在返回
        if (null == transmigration) {
            return;
        }
        String key = getKey(transmigration);
        if (enterUidUtil.isMember(key, uid)) {
            return;
        }
        enterUidUtil.add(key, uid);
        globalEnterService.enter(uid);
    }

    /**
     * 是否参加本轮轮回世界
     *
     * @param transmigration
     * @param uid
     */
    public boolean isEnter(GameTransmigration transmigration, long uid) {
        // 轮回不存在返回
        if (null == transmigration) {
            return false;
        }
        String key = getKey(transmigration);
        return enterUidUtil.isMember(key, uid);
    }

    /**
     * 进入轮回的玩家：game:transmigration:区服组:开始日期:enterUids
     *
     * @param transmigration
     * @return
     */
    private static String getKey(GameTransmigration transmigration) {
        String key = TransmigrationKey.getBaseKey(transmigration);
        return key + RedisKeyConst.SPLIT + "enterUids";
    }
}
