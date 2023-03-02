package com.bbw.god.gameuser.statistic;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.SetUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.InsUserStatistic;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.pool.StatisticPool;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 玩家统计相关的service
 *
 * @author suchaobin
 * @date 2020-03-28 16:08
 */
@Slf4j
@Service("userStatistic")
public abstract class BaseStatisticService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisHashUtil<String, Object> redisHashUtil;
    @Autowired
    protected StatisticPool statisticPool;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    private MailAction mailAction;
    @Autowired
    protected InsRoleInfoService insRoleInfoService;

    /**
     * 数据保存天数
     */
    private static final int REMAIN_DAYS = 3;

    /**
     * 获取redis的key
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @return redis的key
     */
    public abstract String getKey(long uid, StatisticTypeEnum typeEnum);

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     * @param <T>       统计对象
     */
    public abstract <T extends BaseStatistic> void toRedis(long uid, T statistic);

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @param <T>      统计对象
     * @return 统计对象
     */
    public abstract <T extends BaseStatistic> T fromRedis(long uid, StatisticTypeEnum typeEnum, int date);

    public abstract <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData);

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    public abstract void init(long uid);

    /**
     * 清理统计数据
     *
     * @param uid 玩家id
     */
    public abstract void clean(long uid);

    /**
     * 清理统计数据
     *
     * @param key redis的key
     */
    protected void cleanByKey(String key) {
        Set<String> fieldSet = redisHashUtil.getFieldKeySet(key);
        if (SetUtil.isEmpty(fieldSet)) {
            return;
        }
        List<Integer> dateList = new ArrayList<>();
        for (int i = 0; i <= REMAIN_DAYS; i++) {
            dateList.add(DateUtil.toDateInt(DateUtil.addDays(DateUtil.now(), -i)));
        }
        Set<String> delFields = new HashSet<>();
        String rex = "^20\\d{6}";
        Pattern p = Pattern.compile(rex);
        for (String field : fieldSet) {
            // 长度大于8，且前8位是纯数字的，不在规定日期范围内的删除
            if (field.length() >= 8 && p.matcher(field.substring(0, 8)).matches()
                    && !dateList.contains(Integer.parseInt(field.substring(0, 8)))) {
                delFields.add(field);
            }
        }
        if (SetUtil.isNotEmpty(delFields)) {
            String[] strArr = new String[delFields.size()];
            delFields.toArray(strArr);
            redisHashUtil.removeField(key, strArr);
        }
    }

    /**
     * 从数据库载入数据
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseStatistic> void loadFromDb(long uid, StatisticTypeEnum typeEnum) {
        try {
            GameUser gu = gameUserService.getGameUser(uid);
            PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, gu.getServerId());
            InsUserStatistic insUserStatistic = pdd.dbGetUserStatisticById(getKey(uid, typeEnum));
            if (null != insUserStatistic) {
                String dataJson = insUserStatistic.getDataJson();
                Integer clazzValue = insUserStatistic.getClazzValue();
                Class<T> clazz = (Class<T>) Class.forName(StatisticDataType.fromValue(clazzValue).getClazz());
                T statistic = JSONUtil.fromJson(dataJson, clazz);
                setFieldValueNotNull(statistic);
                toRedis(uid, statistic);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 保证所有属性都有值，防止统计类新增字段时导致可能产生的错误
     *
     * @param obj 统计类对象
     * @throws Exception 异常
     */
    private void setFieldValueNotNull(Object obj) throws Exception {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (null == field.get(obj)) {
                switch (field.getType().getName()) {
                    case "java.lang.String":
                        field.set(obj, "");
                        break;
                    case "java.lang.Integer":
                        field.set(obj, 0);
                        break;
                    case "java.lang.Double":
                        field.set(obj, 0.0);
                        break;
                    case "java.lang.Long":
                        field.set(obj, 0L);
                        break;
                    case "java.util.Map":
                        field.set(obj, new HashMap<>(16));
                        break;
                    case "java.util.List":
                        field.set(obj, new ArrayList<>());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    protected void checkUid(long uid) {
        if (uid <= 190000000000000L) {
            mailAction.notifyCoder("统计的玩家id异常", "uid=" + uid);
            throw new CoderException("统计的玩家id异常,uid=" + uid);
        }
    }
}
