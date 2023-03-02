package com.bbw.god.activity.holiday.processor.holidayprayerskylantern;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 祈福天灯数据保存服务类
 *
 * @author fzj
 * @date 2022/2/14 22:53
 */
@Service
public class HolidayPrayerSkyLanternService {
    /** 玩家寄语 */
    private final static String USER_MESSAGE = "userMessage";
    /** 活动奖励 */
    private final static String SKY_LANTERN_AWARD_STATUS = "skyLanternAwardStatus";
    /** 天灯数量 */
    private final static String SKY_LANTERN_NUM = "skyLanternNum";
    @Autowired
    private RedisHashUtil<Long, String> prayerSkyLanternUtil;
    @Autowired
    private RedisHashUtil<Integer, Integer> skyLanternAwardStatusUtil;
    @Autowired
    private RedisValueUtil<Integer> skyLanternNumUtil;

    /**
     * 保存寄语
     *
     * @param uid
     * @param message
     */
    public void saveMessage(long uid, String message) {
        prayerSkyLanternUtil.putField(USER_MESSAGE, uid, message, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获取所有玩家寄语
     *
     * @return
     */
    public List<String> getAllMessage() {
        List<String> allMessage = prayerSkyLanternUtil.getFieldValueList(USER_MESSAGE);
        if (allMessage.isEmpty()){
            return new ArrayList<>();
        }
        return allMessage;
    }

    /**
     * 获取玩家寄语
     *
     * @param uid
     * @return
     */
    public String getMessage(long uid) {
        return prayerSkyLanternUtil.getField(USER_MESSAGE, uid);
    }

    /**
     * 获取参与者
     *
     * @return
     */
    public List<Long> getJoiners() {
        return new ArrayList<>(prayerSkyLanternUtil.getFieldKeySet(USER_MESSAGE));
    }

    /**
     * 全部活动奖励及状态
     *
     * @return
     */
    public Map<Integer, Integer> getAllAwardStatus() {
        return skyLanternAwardStatusUtil.get(SKY_LANTERN_AWARD_STATUS);
    }

    /**
     * 活动对应目标奖励状态
     *
     * @param target
     * @return
     */
    public Integer getAwardStatus(Integer target) {
        return skyLanternAwardStatusUtil.getField(SKY_LANTERN_AWARD_STATUS, target);
    }

    /**
     * 更新目标奖励状态
     *
     * @param target
     * @param status
     */
    public void updateAwardStatus(Integer target, int status) {
        skyLanternAwardStatusUtil.putField(SKY_LANTERN_AWARD_STATUS, target, status);
    }

    /**
     * 增加放飞天灯次数
     *
     * @param skyLanternNum
     */
    public void addPutSkyLanternTimes(Integer skyLanternNum) {
        skyLanternNumUtil.increment(SKY_LANTERN_NUM, skyLanternNum);
        skyLanternNumUtil.expire(SKY_LANTERN_NUM, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获取天灯放飞次数
     *
     * @return
     */
    public Integer getPutSkyLanternTimes() {
        return skyLanternNumUtil.get(SKY_LANTERN_NUM);
    }
}
