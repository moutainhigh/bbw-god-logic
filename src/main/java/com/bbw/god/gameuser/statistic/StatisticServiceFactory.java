package com.bbw.god.gameuser.statistic;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.InsUserStatistic;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 统计工厂类
 * @date 2020/4/16 9:15
 */
@Component
@Slf4j
public class StatisticServiceFactory {
    @Autowired
    @Lazy
    private List<BaseStatisticService> statisticServiceList;
    @Autowired
    @Lazy
    private List<ResourceStatisticService> resourceStatisticServices;
    @Autowired
    @Lazy
    private List<BehaviorStatisticService> behaviorStatisticServices;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedisHashUtil<String, Object> redisHashUtil;
    @Autowired
    private RedisValueUtil<String> redisValueUtil;

    /**
     * 初始化统计数据，除非是修复统计数据，否则禁止直接调用某个service的init方法！
     * 必须使用工程类调用！
     *
     * @param uid 玩家id
     */
    public void init(long uid) {
        statisticServiceList.forEach(service -> service.init(uid));
        redisValueUtil.set(getLoadKey(uid), "1");
    }

    /**
     * 从redis删除单个玩家数据
     *
     * @param uid
     */
    public void delFromRedis(long uid) {
        Set<String> keys = getKeys(uid);
        redisHashUtil.delete(keys);
    }

    /**
     * 从redis批量删除玩家数据
     *
     * @param uids
     */
    public void delFromRedis(List<Long> uids) {
        Set<String> keys = new HashSet<>();
        uids.forEach(u -> keys.addAll(getKeys(u)));
        redisHashUtil.delete(keys);
    }

    /**
     * 清理统计数据
     *
     * @param uid 玩家id
     */
    public void clean(long uid) {
        statisticServiceList.forEach(service -> service.clean(uid));
    }

    public ResourceStatisticService getByAwardEnum(AwardEnum awardEnum) {
        for (ResourceStatisticService service : resourceStatisticServices) {
            if (awardEnum == service.getMyAwardEnum()) {
                return service;
            }
        }
        throw new CoderException(String.format("程序员没有编写AwardEnum=%s的服务", awardEnum));
    }

    public BehaviorStatisticService getByBehaviorType(BehaviorType behaviorType) {
        for (BehaviorStatisticService service : behaviorStatisticServices) {
            if (behaviorType == service.getMyBehaviorType()) {
                return service;
            }
        }
        throw new CoderException(String.format("程序员没有编写BehaviorType=%s的服务", behaviorType));
    }

    /**
     * 获取所有统计的key
     *
     * @param uid 玩家id
     * @return 所有统计的key
     */
    public Set<String> getKeys(long uid) {
        Set<String> keys = new HashSet<>();
        resourceStatisticServices.forEach(service -> {
            keys.add(service.getKey(uid, StatisticTypeEnum.GAIN));
            keys.add(service.getKey(uid, StatisticTypeEnum.CONSUME));
        });
        behaviorStatisticServices.forEach(service -> keys.add(service.getKey(uid, StatisticTypeEnum.NONE)));
        return keys;
    }

    /**
     * 从数据库加载数据
     *
     * @param uid 玩家id
     */
    public void loadStatistic(long uid) {
        long start = System.currentTimeMillis();
        resourceStatisticServices.forEach(service -> {
            service.loadFromDb(uid, StatisticTypeEnum.GAIN);
            service.loadFromDb(uid, StatisticTypeEnum.CONSUME);
        });
        behaviorStatisticServices.forEach(service -> {
            service.loadFromDb(uid, StatisticTypeEnum.NONE);
        });
        redisValueUtil.set(getLoadKey(uid), "1");
        long end = System.currentTimeMillis();
        long usedTime = end - start;
        if (usedTime > 200) {
            log.warn("从数据库加载数据缓存到redis中，本次耗时：{}毫秒", (end - start));
        }
    }

    public void saveToDb(List<String> keys, int date) {
        try {
            doSaveToDb(keys, date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void doSaveToDb(List<String> keys, int date) {
        Map<Long, List<String>> uidKeysMap = new HashMap<>();
        for (String key : keys) {
            Long uid = Long.valueOf(key.split(SPLIT)[1]);
            if (!uidKeysMap.containsKey(uid)) {
                uidKeysMap.put(uid, new ArrayList<>());
            }
            uidKeysMap.get(uid).add(key);
        }
        List<InsUserStatistic> updateList = new ArrayList<>();
        long start = System.currentTimeMillis();
        log.info("本次统计更新玩家数量：{}", uidKeysMap.keySet().size());
        Map<String, Map<String, Object>> allPreparedData = redisHashUtil.getBatch(keys);
        for (Long uid : uidKeysMap.keySet()) {
            List<String> myUpdateKeys = uidKeysMap.get(uid);
            Map<String, Map<String, Object>> myPreparedData = new HashMap<>();
            for (String myUpdateKey : myUpdateKeys) {
                myPreparedData.put(myUpdateKey, allPreparedData.get(myUpdateKey));
            }
            int sid = gameUserService.getOriServer(uid).getMergeSid();

            // 更新资源统计
            for (ResourceStatisticService service : resourceStatisticServices) {
                int typeCount = service.getMyTypeCount();
                for (int i = 0; i < typeCount; i++) {
                    String key = service.getKey(uid, StatisticTypeEnum.fromValue(i));
                    if (!myUpdateKeys.contains(key)) {
                        continue;
                    }
                    long s = System.currentTimeMillis();
                    try {
                        StatisticTypeEnum statisticTypeEnum = StatisticTypeEnum.fromValue(i);
                        BaseStatistic baseStatistic = service.buildStatisticFromPreparedData(uid, statisticTypeEnum, date, myPreparedData);
                        InsUserStatistic insUserStatistic = InsUserStatistic.fromBaseStatistic(uid, sid, key,
                                baseStatistic);
                        updateList.add(insUserStatistic);
                    } catch (Exception e) {
                        log.error("添加key={}的数据到数据库时出错", key);
                    }
                    long e = System.currentTimeMillis();
                    if (e - s >= 5) {
                        log.warn("添加key={}到updateList中耗时:{}", key, e - s);
                    }
                }
            }
            // 更新行为统计
            for (BehaviorStatisticService service : behaviorStatisticServices) {
                String key = service.getKey(uid, StatisticTypeEnum.NONE);
                if (!myUpdateKeys.contains(key)) {
                    continue;
                }
                long s = System.currentTimeMillis();
                try {
                    BaseStatistic baseStatistic = service.buildStatisticFromPreparedData(uid, StatisticTypeEnum.NONE, date, myPreparedData);
                    InsUserStatistic insUserStatistic = InsUserStatistic.fromBaseStatistic(uid, sid, key,
                            baseStatistic);
                    updateList.add(insUserStatistic);
                } catch (Exception e) {
                    log.error("添加key=" + key + "的数据到数据库时出错", e);
                }
                long e = System.currentTimeMillis();
                if (e - s >= 5) {
                    log.warn("添加key={}到updateList中耗时:{}", key, e - s);
                }
            }
        }
        long end = System.currentTimeMillis();
        log.info("updateList添加数据量{},耗时：{}毫秒", updateList.size(), end - start);
        // 按区服分组
        start = System.currentTimeMillis();
        Map<Integer, List<InsUserStatistic>> updateMap = updateList.stream()
                .collect(Collectors.groupingBy(InsUserStatistic::getSid));
        Set<Map.Entry<Integer, List<InsUserStatistic>>> entries = updateMap.entrySet();
        log.info("updateList数据分组耗时：{}毫秒", end - start);
        start = System.currentTimeMillis();
        // 保存数据
        for (Map.Entry<Integer, List<InsUserStatistic>> entry : entries) {
            try {
                if (ListUtil.isEmpty(entry.getValue())) {
                    continue;
                }
                Integer sid = entry.getKey();
                PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
                pdd.dbInsertUserStatisticBatch(entry.getValue());
            } catch (Exception e) {
                List<Long> errorUids = entry.getValue().stream().map(InsUserStatistic::getUid)
                        .collect(Collectors.toList());
                log.error("区服id={}的区服，统计错误的key集合={}", entry.getKey(), errorUids);
            }
        }
        end = System.currentTimeMillis();
        log.info("更新或插入数据耗时：{}毫秒", end - start);
    }

    /**
     * 是否需要加载数据
     *
     * @param uid
     * @return
     */
    public boolean isNeedLoad(long uid) {
        String value = redisValueUtil.get(getLoadKey(uid));
        return null == value;
    }

    public Map<String, Map<String, Object>> getAllKeyMap(long uid) {
        Map<String, Map<String, Object>> keyMap = new HashMap<>();
        Set<String> keys = getKeys(uid);
        for (String key : keys) {
            Map<String, Object> redisMap = redisHashUtil.get(key);
            keyMap.put(key, redisMap);
        }
        return keyMap;
    }

    /**
     * 获取用来判断加载数据的key
     *
     * @param uid
     * @return
     */
    public String getLoadKey(long uid) {
        return "usr" + SPLIT + uid + SPLIT + "0statistic" + SPLIT + "load";
    }
}
