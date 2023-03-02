package com.bbw.god.gm.coder;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.db.pool.UserDataPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis data手动同步
 *
 * @author: suhq
 * @date: 2022/11/7 10:16 上午
 */
@Slf4j
@RestController
@RequestMapping(value = "/coder")
public class CoderRedisSyncCtrl {
    @Autowired
    private RedisSetUtil<String> dataPoolKeySet;// Data缓存池
    @Autowired
    private UserDataPool userDataPool;

    @RequestMapping("/syncUserDeleteData")
    public Rst syncUserDeleteData(int deletePoolSeq, int limit) {
        // 删除数据
        try {
            Long begin = System.currentTimeMillis();
            String deletePoolKey = "appruntime:dbpool:userdata:delete:" + deletePoolSeq;
            List<String> keysInPool = dataPoolKeySet.randomMembers(deletePoolKey, limit);
            if (ListUtil.isEmpty(keysInPool)) {
                return Rst.businessOK("delete pool 没有需要同步的数据");
            }
            Set<String> keysToSync = keysInPool.stream().limit(limit).collect(Collectors.toSet());
            userDataPool.doDbDelete(keysToSync);
            dataPoolKeySet.remove(deletePoolKey, keysInPool);
            Long end = System.currentTimeMillis();
            String info = "delete缓冲池[" + deletePoolKey + "]拥有[" + keysInPool.size() + "]条key,同步[" + keysToSync.size() + "]条数据。耗时：" + (end - begin);
            info += "。样例：" + keysToSync.iterator().next();
            return Rst.businessOK(info);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Rst.businessFAIL(e.getMessage());
        }
    }
}
