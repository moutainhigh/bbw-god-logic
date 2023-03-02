package com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.DrawResult;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.HolidayMagicWitchTool;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 魔法女巫 全服 redis 临时数据
 *
 * @author: huanghb
 * @date: 2022/12/13 9:16
 */
@Service
public class HolidayMagicWitchGameService extends AbstractTmpDataRedisService<GameHolidayMagicWitch, Long> {
    @Autowired
    private RedisLockUtil redisLockUtil;

    /**
     * 获取业务数据类型
     *
     * @return
     */
    @Override
    protected Class<GameHolidayMagicWitch> getDataClazz() {
        return GameHolidayMagicWitch.class;
    }

    /**
     * 获取数据归属
     *
     * @param data
     * @return
     */
    @Override
    protected Long getDataBelong(GameHolidayMagicWitch data) {
        return 0L;
    }

    /**
     * 获取循环数据的循环标识
     *
     * @param data
     * @return
     */
    @Override
    protected String getDataLoop(GameHolidayMagicWitch data) {
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
        return "game" + SPLIT + dataType.getRedisKey();
    }

    /**
     * 获取Redis hash field
     *
     * @param data
     * @return
     */
    @Override
    protected Long getField(GameHolidayMagicWitch data) {
        return data.getId();
    }

    /**
     * 获取过期时间（ms）
     *
     * @param data
     * @return
     */
    @Override
    protected long getExpiredMillis(GameHolidayMagicWitch data) {
        return DateUtil.SECOND_ONE_DAY * 7 * 1000L;
    }

    /**
     * 获取当前全服魔法女巫结果（排序）
     *
     * @param uid
     * @return
     */
    public List<DrawResult> getCurGameHolidayMagicWomanResult(long uid) {
        //获得全服魔法女巫信息
        GameHolidayMagicWitch gameHolidayMagicWomen = getSingleData(0L);
        if (null != gameHolidayMagicWomen && ListUtil.isNotEmpty(gameHolidayMagicWomen.getRecords())) {
            return gameHolidayMagicWomen.getRecords();
        }
        //初始化全服魔法女巫信息
        List<DrawResult> records = (List<DrawResult>) redisLockUtil.doSafe(String.valueOf(uid), tmp -> {
            GameHolidayMagicWitch gameHolidayMagicWomenList = getSingleData(0L);
            //懒汉单例 再判定一次
            if (null != gameHolidayMagicWomenList && ListUtil.isNotEmpty(gameHolidayMagicWomen.getRecords())) {
                return gameHolidayMagicWomenList.getRecords();
            }
            List<DrawResult> rewardResults = HolidayMagicWitchTool.getAwardResults();
            gameHolidayMagicWomenList = GameHolidayMagicWitch.getInstance(rewardResults);
            //保存信息
            addData(gameHolidayMagicWomenList);
            return rewardResults;
        });
        return records;
    }
}
