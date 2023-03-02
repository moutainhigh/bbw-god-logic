package com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 感恩花语数据储存服务类
 *
 * @author: huanghb
 * @date: 2022/11/16 11:59
 */
@Slf4j
@Service
public class HolidayThankFlowerLanguageDateService extends AbstractTmpDataRedisService<UserThankFlowerLanguage, Long> {
    /** 缓存过期时间 */
    private final static Integer CACHE_TIME = 10;

    /**
     * 获取业务数据类型
     *
     * @return
     */
    @Override
    protected Class<UserThankFlowerLanguage> getDataClazz() {
        return UserThankFlowerLanguage.class;
    }

    /**
     * 获取数据归属
     *
     * @param data
     * @return
     */
    @Override
    protected Long getDataBelong(UserThankFlowerLanguage data) {
        return data.getId();
    }

    /**
     * 获取循环数据的循环标识
     *
     * @param data
     * @return
     */
    @Override
    protected String getDataLoop(UserThankFlowerLanguage data) {
        return "";
    }

    /**
     * 获取Redis key
     *
     * @param belong
     * @param dataType
     * @param loop
     * @return
     */
    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        return "usr" + SPLIT + belong + SPLIT + dataType.getRedisKey();
    }

    /**
     * 获取Redis hash field
     *
     * @param data
     * @return
     */
    @Override
    protected Long getField(UserThankFlowerLanguage data) {
        return data.getId();
    }

    /**
     * 获取过期时间（ms）
     *
     * @param data
     * @return
     */
    @Override
    protected long getExpiredMillis(UserThankFlowerLanguage data) {
        return DateUtil.SECOND_ONE_DAY * CACHE_TIME * 1000L;
    }
}
