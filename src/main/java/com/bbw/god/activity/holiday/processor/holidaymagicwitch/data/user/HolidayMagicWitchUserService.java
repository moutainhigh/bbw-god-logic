package com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.user;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 魔法女巫个人 redis 临时数据
 *
 * @author: huanghb
 * @date: 2022/12/13 9:16
 */
@Service
public class HolidayMagicWitchUserService extends AbstractTmpDataRedisService<UserHolidayMagicWitch, Long> {
    public final static String REDIS_KEY = "usr:";

    /**
     * 获取业务数据类型
     *
     * @return
     */
    @Override
    protected Class<UserHolidayMagicWitch> getDataClazz() {
        return UserHolidayMagicWitch.class;
    }

    /**
     * 获取数据归属
     *
     * @param data
     * @return
     */
    @Override
    protected Long getDataBelong(UserHolidayMagicWitch data) {
        return data.getGameUserId();
    }

    /**
     * 获取循环数据的循环标识
     *
     * @param data
     * @return
     */
    @Override
    protected String getDataLoop(UserHolidayMagicWitch data) {
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
        return REDIS_KEY + belong + SPLIT + dataType.getRedisKey() + loop;
    }

    /**
     * 获取Redis hash field
     *
     * @param data
     * @return
     */
    @Override
    protected Long getField(UserHolidayMagicWitch data) {
        return data.getGameUserId();
    }

    /**
     * 获取过期时间（ms）
     *
     * @param data
     * @return
     */
    @Override
    protected long getExpiredMillis(UserHolidayMagicWitch data) {
        return DateUtil.SECOND_ONE_DAY * 7 * 1000L;
    }

    /**
     * 获取当前魔法女巫信息
     *
     * @param uid
     * @return
     */
    public UserHolidayMagicWitch getCurUserHolidayWagicWoman(long uid) {
        Optional<UserHolidayMagicWitch> optional = getData(uid, uid);
        if (optional.isPresent()) {
            return optional.get();
        }
        UserHolidayMagicWitch userHolidayMagicWitch = UserHolidayMagicWitch.getInstance(uid);
        return userHolidayMagicWitch;
    }
}
