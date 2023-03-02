package com.bbw.god.game.award.giveback;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 数据池的状态服务
 *
 * @author: suhq
 * @date: 2022/4/28 9:36 上午
 */
@Slf4j
@Service
public class GiveBackPoolStatusService {
    /** 保存动作状态。1保存中。0保存完毕。 */
    private static final String SAVING_KEY = RedisKeyConst.SPLIT + "saving";
    /** 保存中 */
    private static final Integer SAVING = 1;
    /** 非保存 */
    private static final Integer NOT_SAVING = 0;

    /** 保存返还处理状态 */
    @Autowired
    private RedisValueUtil<Integer> valueRedis;

    /**
     * 是否保存中
     *
     * @param poolBaseKey 池基础key
     * @return
     */
    public boolean isSaving(String poolBaseKey) {
        Integer saving = Optional.ofNullable(valueRedis.get(getSavingKey(poolBaseKey))).orElse(NOT_SAVING);
        return saving > 0;
    }

    /**
     * 标记保存中
     *
     * @param poolBaseKey
     */
    public void setSaving(String poolBaseKey) {
        valueRedis.set(getSavingKey(poolBaseKey), SAVING);
    }

    /**
     * 清除保存中的标记
     *
     * @param poolBaseKey
     */
    public void setNotSaving(String poolBaseKey) {
        valueRedis.set(getSavingKey(poolBaseKey), NOT_SAVING);
    }

    /**
     * 保存动作状态的KEY
     *
     * @param poolBaseKey
     * @return
     */
    private String getSavingKey(String poolBaseKey) {
        return poolBaseKey + SAVING_KEY;
    }
}
