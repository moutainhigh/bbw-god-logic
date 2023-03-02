package com.bbw.god.game.online;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.server.redis.ServerRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 在线统计服务，每5分钟统计一次
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-08 17:14
 */
@Service
public class GameOnlineService {
    private static final long EXPIRE_TIME = 15 * 60;//15分钟
    @Autowired
    private RedisSetUtil<Long> redis;

    /**
     * 添加到在线统计
     *
     * @param sid
     * @param uid
     */
    public void addToOnline(int sid, Long uid) {
        Keys keys = getRedisKey(sid, DateUtil.now());
        //区服id列表
        //System.out.println("loopKey=" + loopKey);
        if (redis.exists(keys.sidListKey)) {
            redis.add(keys.sidListKey, sid + 0L);
        } else {
            redis.add(keys.sidListKey, sid + 0L);
            redis.expire(keys.sidListKey, EXPIRE_TIME);
        }
        if (redis.exists(keys.uidListKey)) {
            redis.add(keys.uidListKey, uid);
        } else {
            redis.add(keys.uidListKey, uid);
            redis.expire(keys.uidListKey, EXPIRE_TIME);
        }
    }

    /**
     * 获取在线人数
     *
     * @param minutesAgo
     * @return
     */
    public int getOnlineNum(int minutesAgo) {
        return getOnlineUids(minutesAgo).size();
    }

    /**
     * 获取某个时间的所有在线玩家
     *
     * @param minutesAgo
     * @return
     */
    public List<Long> getOnlineUids(int minutesAgo) {
        List<Long> onlineUids = new ArrayList<>();
        Date dateMinutesAgo = DateUtil.addMinutes(DateUtil.now(), -minutesAgo);
        Keys keys = getRedisKey(0, dateMinutesAgo);
        Set<Long> sids = redis.members(keys.sidListKey);
        for (Long sid : sids) {
            keys = getRedisKey(sid.intValue(), dateMinutesAgo);
            Set<Long> uids = redis.members(keys.uidListKey);
            if (SetUtil.isNotEmpty(uids)) {
                onlineUids.addAll(uids);
            }
        }
        return onlineUids;
    }

    /**
     * 获取上一个统计周期里的区服ID列表
     *
     * @return
     */
    public Set<Long> getLastSidList() {
        Date fiveMinutesAgo = DateUtil.addMinutes(DateUtil.now(), -5);
        Keys keys = getRedisKey(0, fiveMinutesAgo);
        return redis.members(keys.sidListKey);
    }

    /**
     * 上一个统计周期里的玩家ID
     *
     * @param sid
     * @return
     */
    public Set<Long> getLastUidList(int sid) {
        Date fiveMinutesAgo = DateUtil.addMinutes(DateUtil.now(), -5);
        Keys keys = getRedisKey(sid, fiveMinutesAgo);
        return redis.members(keys.uidListKey);
    }


    /**
     * 上一个统计周期里的区服的在线人数
     *
     * @param sid
     * @return
     */
    public int getLastUidCount(int sid) {
        Date fiveMinutesAgo = DateUtil.addMinutes(DateUtil.now(), -5);
        Keys keys = getRedisKey(sid, fiveMinutesAgo);
        if (redis.exists(keys.uidListKey)) {
            Long l = redis.size(keys.uidListKey);
            return l.intValue();
        }
        return 0;
    }

    /**
     * 查看玩家是否在当前周期或者上个周期内在线
     * <br>每10分钟的尾数0~4 和 5~9各为一个周期
     *
     * @param sid
     * @param uid
     * @return
     */
    public boolean isOnlineWithinFiveMinute(int sid, long uid) {
        Keys keys = getRedisKey(sid, DateUtil.now());
        if (redis.isMember(keys.uidListKey, uid)) {
            return true;
        }
        Date fiveMinutesAgo = DateUtil.addMinutes(DateUtil.now(), -5);
        keys = getRedisKey(sid, fiveMinutesAgo);
        if (redis.isMember(keys.uidListKey, uid)) {
            return true;
        }
        return false;
    }

    private Keys getRedisKey(int sid, Date date) {
        String yyyyMMddHHmm = DateUtil.toString(date, "yyyyMMddHHmm");
        String loopKey = yyyyMMddHHmm.substring(0, yyyyMMddHHmm.length() - 1);
        if (Long.valueOf(yyyyMMddHHmm) % 10 <= 4) {
            loopKey += "0";
        } else {
            loopKey += "5";
        }
        String baseKey = GameRedisKey.getRunTimeVarKey("online") + ServerRedisKey.SPLIT + loopKey;
        Keys keys = new Keys();
        keys.sidListKey = baseKey + ServerRedisKey.SPLIT + "sids";
        keys.uidListKey = baseKey + ServerRedisKey.SPLIT + "server" + ServerRedisKey.SPLIT + sid;
        return keys;
    }

    private class Keys {
        //区服id列表
        private String sidListKey;
        //具体区服在线列表
        private String uidListKey;
    }
}
