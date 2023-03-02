package com.bbw.sys.session;

import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 唯一登录
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-01 21:48
 */
@Slf4j
@Service
public class SingleUserService {
    private static final String BASE_KEY = RedisKeyConst.RUNTIME_KEY + RedisKeyConst.SPLIT + "uidMapSessionId" + RedisKeyConst.SPLIT;
    @Autowired
    private RedisValueUtil<String> uidMapSessionId;
    @Autowired
    private RedisOperationsSessionRepository redisOperationsSessionRepository;

    public void login(Long uid, String newSessionId) {
        String mapkey = BASE_KEY + uid;
        String oldSessionId = uidMapSessionId.get(mapkey);
        if (StrUtil.isNotNull(oldSessionId) && !oldSessionId.equals(newSessionId)) {
            redisOperationsSessionRepository.deleteById(oldSessionId);
        }
        uidMapSessionId.set(mapkey, newSessionId, 60 * 60 * 8L);
    }

    public String getSessionId(Long uid) {
        String mapkey = BASE_KEY + uid;
        String sessionId = uidMapSessionId.get(mapkey);
        return sessionId;
    }

    public List<String> getSessionIds(List<Long> uids) {
        List<String> keys = new ArrayList<>();
        for (Long uid : uids) {
            keys.add(BASE_KEY + uid);
        }
        List<String> sessionIds = uidMapSessionId.getBatch(keys);
        return sessionIds;
    }

    public void removeSessionId(Long uid) {
        try {
            String mapkey = BASE_KEY + uid;
            String sessionId = uidMapSessionId.get(mapkey);
            if (null != sessionId) {
                redisOperationsSessionRepository.deleteById(sessionId);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
