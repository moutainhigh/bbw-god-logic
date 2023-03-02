package com.bbw.god.game.transmigration;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家在某座城的最近攻打时间
 *
 * @author: suhq
 * @date: 2021/9/13 3:29 下午
 */
@Service
@Slf4j
public class TransmigrationCityNewRecordTimeService {
    /** 某城池玩家最好记录 uid -> 新纪录的时间 */
    @Autowired
    private RedisHashUtil<Long, Long> userNewRecordTimeUtil;

    /**
     * 获取打下过某城的所有玩家
     *
     * @param transmigration
     * @return
     */
    public Map<Long, Long> getAllNewRecordTime(GameTransmigration transmigration) {
        // 榜单不存在返回
        if (null == transmigration) {
            return new HashMap<>();
        }
        String key = getKey(transmigration);
        return userNewRecordTimeUtil.get(key);
    }

    /**
     * 更新玩家新纪录的时间
     *
     * @param transmigration
     * @param uid
     */
    public void updateNewRecordTime(GameTransmigration transmigration, long uid) {
        // 榜单不存在返回
        if (null == transmigration) {
            return;
        }
        String key = getKey(transmigration);
        userNewRecordTimeUtil.putField(key, uid, DateUtil.toDateTimeLong());
    }

    /**
     * 城池挑战榜：game:transmigration:区服组:开始日期:newRecordTime
     *
     * @param transmigration
     * @return
     */
    private static String getKey(GameTransmigration transmigration) {
        String key = TransmigrationKey.getBaseKey(transmigration);
        return key + RedisKeyConst.SPLIT + "newRecordTime";
    }
}
