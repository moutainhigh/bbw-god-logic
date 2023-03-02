package com.bbw.god.activity.holiday.processor.holidaychristmaswish;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 圣诞心愿 redis
 *
 * @author: huanghb
 * @date: 2022/12/13 17:32
 */
@Service
public class HolidayChristmasWishService extends AbstractTmpDataRedisService<UserHolidayChristmasWish, Long> {

    /**
     * 获取业务数据类型
     *
     * @return
     */
    @Override
    protected Class<UserHolidayChristmasWish> getDataClazz() {
        return UserHolidayChristmasWish.class;
    }

    /**
     * 获取数据归属
     *
     * @param data
     * @return
     */
    @Override
    protected Long getDataBelong(UserHolidayChristmasWish data) {
        return data.getGameUserId();
    }

    /**
     * 获取循环数据的循环标识
     *
     * @param data
     * @return
     */
    @Override
    protected String getDataLoop(UserHolidayChristmasWish data) {
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
    protected Long getField(UserHolidayChristmasWish data) {
        return data.getId();
    }

    /**
     * 获取过期时间（ms）
     *
     * @param data
     * @return
     */
    @Override
    protected long getExpiredMillis(UserHolidayChristmasWish data) {
        return DateUtil.SECOND_ONE_DAY * 7 * 1000L;
    }

    /**
     * 获得玩家圣诞心愿信息
     *
     * @param uid
     * @return
     */
    protected List<UserHolidayChristmasWish> getUserHolidayChristmasWish(long uid) {
        //获得心愿信息
        List<UserHolidayChristmasWish> userHolidayChristmasWishs = getDatas(uid);
        //心愿信息为空初始化
        if (ListUtil.isEmpty(userHolidayChristmasWishs)) {
            userHolidayChristmasWishs = new ArrayList<>();
        }
        //获得进行中心愿数量
        List<UserHolidayChristmasWish> doingWish = userHolidayChristmasWishs.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
        Integer doingWishNum = CloneUtil.clone(doingWish.size());
        //最大心愿任务数量
        Integer maxWishNum = HolidayChristmasWishTool.getMaxWishNum();
        //心愿数达到上限
        if (doingWishNum >= maxWishNum) {
            return doingWish;
        }
        //新增心愿任务到达上限
        for (int i = doingWishNum; i < maxWishNum; i++) {
            doingWish.add(UserHolidayChristmasWish.instance(uid));
        }
        //更新心愿信息
        updateDatas(uid, doingWish);
        return doingWish;
    }
}
