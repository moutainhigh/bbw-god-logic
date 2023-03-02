package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 生肖对碰 redis
 *
 * @author: huanghb
 * @date: 2022/12/13 17:32
 */
@Service
public class HolidayChineseZodiacConllisionService extends AbstractTmpDataRedisService<UserChineseZodiacConllision, Long> {

    /**
     * 获取业务数据类型
     *
     * @return
     */
    @Override
    protected Class<UserChineseZodiacConllision> getDataClazz() {
        return UserChineseZodiacConllision.class;
    }

    /**
     * 获取数据归属
     *
     * @param data
     * @return
     */
    @Override
    protected Long getDataBelong(UserChineseZodiacConllision data) {
        return data.getGameUserId();
    }

    /**
     * 获取循环数据的循环标识
     *
     * @param data
     * @return
     */
    @Override
    protected String getDataLoop(UserChineseZodiacConllision data) {
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
    protected Long getField(UserChineseZodiacConllision data) {
        return data.getId();
    }

    /**
     * 获取过期时间（ms）
     *
     * @param data
     * @return
     */
    @Override
    protected long getExpiredMillis(UserChineseZodiacConllision data) {
        return DateUtil.SECOND_ONE_DAY * 7 * 1000L;
    }

    /**
     * 获得玩家生肖对碰信息
     *
     * @param uid
     * @return
     */
    protected UserChineseZodiacConllision getUserHolidayChineseZodiacConllision(long uid) {
        //获得生肖对碰信息
        UserChineseZodiacConllision userChineseZodiacConllision = getSingleData(uid);
        //生肖对碰信息为空初始化
        if (null != userChineseZodiacConllision) {
            Boolean isNotNeedFixdata = userChineseZodiacConllision.checkAndHandleLastData();
            if (!isNotNeedFixdata) {
                updateData(userChineseZodiacConllision);
            }
            return userChineseZodiacConllision;
        }
        UserChineseZodiacConllision instance = UserChineseZodiacConllision.instance(uid);
        addData(userChineseZodiacConllision);
        return instance;
    }
}
