package com.bbw.god.db.pool;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author suchaobin
 * @description 统计缓存池
 * @date 2020/4/27 11:53
 */
@Slf4j
@Service
public class StatisticPool extends BasePool {
    private static Long idSeq = 1L;
    @Autowired
    protected RedisSetUtil<String> dataPoolKeySet;// Data缓存池
    @Autowired
    private StatisticServiceFactory statisticServiceFactory;
    private static final String UPDATE_KEY = RedisKeyConst.SPLIT + "update" + RedisKeyConst.SPLIT;// 修改
    // 用户key
    private static final String USER_UPDATE = RedisKeyConst.SPLIT + "update" + RedisKeyConst.SPLIT;

    /**
     * 返回基础键值
     */
    @Override
    protected String getBaseKey() {
        return BASE_KEY + RedisKeyConst.SPLIT + "userStatistic";
    }

    /**
     * 获取数据类型
     */
    @Override
    protected String getDataType() {
        return "UserStatistic";
    }

    /**
     * 获取当前缓冲池
     *
     * @return
     */
    private String getCurrentUpdatePoolKey() {
        return this.getBaseKey() + USER_UPDATE + getCurrentPoolSeq();
    }

    /**
     * 获取上一个缓冲池key
     *
     * @return
     */
    private String getLastUpdatePoolKey() {
        return this.getBaseKey() + USER_UPDATE + getLastPoolSeq();
    }

    /**
     * 保存数据
     */
    @Override
    protected void save() {
        long start = System.currentTimeMillis();
        String lastUpdatePoolKey = getLastUpdatePoolKey();
        List<String> keys = new ArrayList<>(getUpdateKeys(lastUpdatePoolKey));
        dataPoolKeySet.delete(lastUpdatePoolKey);
        statisticServiceFactory.saveToDb(keys, DateUtil.getTodayInt());
        long end = System.currentTimeMillis();
        log.info("本次更新数据耗时:{}毫秒,更新key总数:{}", (end - start), keys.size());
    }

    /**
     * 将数据池的对象持久化到数据库
     */
    @Override
    public void saveToDB() {
        save();
    }

    /**
     * 获取当前等待【更新】数据池的key
     *
     * @return
     */
    private String getUpdatePoolKey() {
        return this.getBaseKey() + UPDATE_KEY + getCurrentPoolSeq();
    }

    /**
     * 添加到数据池，等待【更新】到数据库
     *
     * @param updateObjKey
     */
    public void toUpdatePool(String... updateObjKey) {
        // 玩家ID.资源类型.资源ID
        dataPoolKeySet.add(this.getUpdatePoolKey(), updateObjKey);
    }

    private Set<String> getUpdateKeys(String userUpdatePoolKey) {
        Set<String> keys = dataPoolKeySet.members(userUpdatePoolKey);
        if (SetUtil.isEmpty(keys)) {
            return new HashSet<>();
        }
        return keys;
    }
}
