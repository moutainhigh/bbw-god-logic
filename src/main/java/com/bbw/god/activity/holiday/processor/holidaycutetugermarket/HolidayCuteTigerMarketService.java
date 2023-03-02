package com.bbw.god.activity.holiday.processor.holidaycutetugermarket;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.activity.cfg.CfgCuteTigerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 萌虎集市数据储存服务类
 *
 * @author fzj
 * @date 2022/3/7 16:46
 */
@Service
public class HolidayCuteTigerMarketService {
    /** 特殊事件效果 */
    @Autowired
    private RedisHashUtil<String, Integer> specialEventEffects;
    /** 特殊事件次数 */
    @Autowired
    private RedisValueUtil<Integer> specialEventTimes;
    /** 糕点数量 */
    @Autowired
    private RedisHashUtil<Integer, Integer> pastryNum;
    /** 小虎商店 */
    @Autowired
    private RedisHashUtil<Integer, Award> littleTigerStore;
    /** 刷新次数 */
    @Autowired
    private RedisValueUtil<Integer> refreshTimes;
    /** 累计刷新次数奖励状态 */
    @Autowired
    private RedisValueUtil<Integer> grandTotalAwardStatus;
    /** 小虎商店奖励状态 */
    @Autowired
    private RedisValueUtil<Integer> littleTigerStoreAwardStatus;

    /**
     * 增加全服特殊事件效果
     *
     * @param uid
     * @param eventId
     */
    public void addSpecialEventEffects(long uid, int eventId) {
        String key = "game" + SPLIT + "specialEventEffects";
        String filed = uid + SPLIT + DateUtil.toDateTimeLong() + SPLIT + DateUtil.toDateInt(DateUtil.now());
        specialEventEffects.putField(key, filed, eventId, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得某个日期之前已经触发的效果
     *
     * @param date
     * @return
     */
    public List<Integer> getSpecifyDateBeforeEffects(int date, int pastryId) {
        List<Integer> eventsId = new ArrayList<>();
        Map<String, Integer> specialEvents = getSpecialEvents();
        for (Map.Entry<String, Integer> specialEvent : specialEvents.entrySet()) {
            int key = Integer.parseInt(specialEvent.getKey().split(SPLIT)[2]);
            if (key > date) {
                continue;
            }
            eventsId.add(specialEvent.getValue());
        }
        eventsId.removeIf(e -> {
            CfgCuteTigerEntity.SpecialYeDiEvent specialYeDiEvent = HolidayCuteTigerMarketTool.getSpecialYeDiEvent(e);
            return specialYeDiEvent.getTreasureId() != pastryId;
        });
        return eventsId;
    }

    /**
     * 获得所有已触发事件
     * 玩家触发事件的key，事件id
     *
     * @return
     */
    public Map<String, Integer> getSpecialEvents() {
        String key = "game" + SPLIT + "specialEventEffects";
        return specialEventEffects.get(key);
    }

    /**
     * 获得事件Id
     *
     * @return
     */
    public List<Integer> getSpecialEventsId(int pastryId) {
        List<Integer> specialEvents = new ArrayList<>(getSpecialEvents().values());
        specialEvents.removeIf(e -> {
            CfgCuteTigerEntity.SpecialYeDiEvent specialYeDiEvent = HolidayCuteTigerMarketTool.getSpecialYeDiEvent(e);
            return specialYeDiEvent.getTreasureId() != pastryId;
        });
        return specialEvents;
    }

    /**
     * 增加特殊事件触发次数
     */
    public void addSpecialEventTimes() {
        int sign = getSign();
        String key = "game" + SPLIT + "specialEventTimes" + SPLIT + DateUtil.toDateInt(DateUtil.now()) + SPLIT + sign;
        specialEventTimes.increment(key, 1);
        specialEventTimes.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得事件触发次数
     *
     * @return
     */
    public Integer getSpecialEventTimes() {
        int sign = getSign();
        String key = "game" + SPLIT + "specialEventTimes" + SPLIT + DateUtil.toDateInt(DateUtil.now()) + SPLIT + sign;
        Integer times = specialEventTimes.get(key);
        return times == null ? 0 : times;
    }

    /**
     * 获得标识
     *
     * @return
     */
    private int getSign() {
        int toHInt = DateUtil.toHInt(DateUtil.now());
        int sign = 1;
        if (toHInt >= 0 && toHInt <= 13) {
            sign = 0;
        }
        return sign;
    }

    /**
     * 增加玩家糕点数量
     *
     * @param uid
     * @param pastryId
     * @param num
     */
    public void addPastryNum(long uid, int pastryId, int num) {
        String key = uid + SPLIT + "pastry" + SPLIT + pastryId;
        pastryNum.increment(key, DateUtil.toDateInt(DateUtil.now()), num);
        pastryNum.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 扣除玩家糕点数量
     *
     * @param uid
     * @param pastryId
     * @param num
     */
    public void delPastryNum(long uid, int pastryId, int num) {
        String key = uid + SPLIT + "pastry" + SPLIT + pastryId;
        pastryNum.decrement(key, DateUtil.toDateInt(DateUtil.now()), num);
        pastryNum.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }


    /**
     * 扣除玩家糕点数量
     *
     * @param uid
     * @param pastryId
     * @param num
     */
    public void delDatePastryNum(long uid, int pastryId, int date, int num) {
        String key = uid + SPLIT + "pastry" + SPLIT + pastryId;
        pastryNum.decrement(key, date, num);
        pastryNum.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得非今日糕点数量
     *
     * @param uid
     * @param pastryId
     * @return
     */
    public Map<Integer, Integer> getNotTodayPastryNum(long uid, int pastryId) {
        String key = uid + SPLIT + "pastry" + SPLIT + pastryId;
        Map<Integer, Integer> integerIntegerMap = pastryNum.get(key);
        integerIntegerMap.remove(DateUtil.toDateInt(DateUtil.now()));
        return integerIntegerMap;
    }

    /**
     * 替换奖励
     *
     * @param uid
     * @param number
     * @param award
     */
    public void replaceAward(long uid, int number, Award award) {
        String key = uid + SPLIT + "littleTigerStore";
        littleTigerStore.putField(key, number, award, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得小虎商店奖励
     *
     * @param uid
     * @param number
     * @return
     */
    public Award getLittleTigerStoreAward(long uid, int number) {
        String key = uid + SPLIT + "littleTigerStore";
        Award award = littleTigerStore.get(key).get(number);
        return award == null ? new Award() : award;
    }

    /**
     * 获得小虎商店奖励状态
     *
     * @param uid
     * @param number
     * @return
     */
    public Integer getLittleTigerStoreAwardStatus(long uid, int number) {
        String key = uid + SPLIT + "littleTigerStore" + SPLIT + number;
        Integer status = littleTigerStoreAwardStatus.get(key);
        return status == null ? AwardStatus.UNAWARD.getValue() : status;
    }

    /**
     * 更新小虎商店奖励状态
     *
     * @param uid
     * @param number
     * @param status
     */
    public void updateLittleTigerStoreAwardStatus(long uid, int number, int status) {
        String key = uid + SPLIT + "littleTigerStore" + SPLIT + number;
        littleTigerStoreAwardStatus.set(key, status);
        littleTigerStoreAwardStatus.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 增加刷新次数
     *
     * @param uid
     */
    public void addRefreshTimes(long uid) {
        String key = uid + SPLIT + "refreshTimes";
        refreshTimes.increment(key, 1);
        refreshTimes.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得刷新次数
     *
     * @param uid
     * @return
     */
    public int getRefreshTimes(long uid) {
        String key = uid + SPLIT + "refreshTimes";
        Integer refreshTime = refreshTimes.get(key);
        return refreshTime == null ? 0 : refreshTime;
    }

    /**
     * 更新奖励状态
     *
     * @param uid
     * @param refreshTimes
     */
    public void updateGrandTotalAwardStatus(long uid, int refreshTimes, int status) {
        String key = uid + SPLIT + "grandTotalAwardStatus" + SPLIT + refreshTimes;
        grandTotalAwardStatus.set(key, status);
        grandTotalAwardStatus.expire(key, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获得奖励状态
     *
     * @param uid
     * @return
     */
    public Integer getGrandTotalAwardStatus(long uid, int refreshTimes) {
        String key = uid + SPLIT + "grandTotalAwardStatus" + SPLIT + refreshTimes;
        Integer status = grandTotalAwardStatus.get(key);
        return status == null ? AwardStatus.UNAWARD.getValue() : status;
    }
}
