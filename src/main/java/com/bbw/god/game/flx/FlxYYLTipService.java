package com.bbw.god.game.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.data.GameDayDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年1月6日 上午10:01:09
 * 类说明  redis中 game:flxYaYaLeTip:月份  存储每日随机事件压压乐的提示元素，存储有效期40天
 */
@Service
@Slf4j
public class FlxYYLTipService {

    @Autowired
    private GameDayDataService dataService;

    @Autowired
    private RedisHashUtil<Integer, Integer> redisHashUtil;

    /**
     * 检查 生成随机事件压压乐提示的元素
     *
     * @param date
     */
    public void checkAndGenerateTips() {
        log.info(DateUtil.nowToString() + "开始检查押押乐元素提示-------------");
        long begin = System.currentTimeMillis();
        Integer today = generateBeginDate();
        if (today < 0) {
            log.info("-------------福临轩压压乐结果提示已是最新！-------------");
            return;
        }
        List<FlxDayResult> results = dataService.getGameData(FlxDayResult.class);
        if (results.isEmpty()) {
            return;
        }
        Map<Integer, Integer> tips = new HashMap<Integer, Integer>();
        int monthOffset = 1;
        Integer newMonth = DateUtil.toDateInt(DateUtil.getMonthBegin(DateUtil.fromDateInt(today), monthOffset));
        results = results.stream().filter(p -> p.getDateInt() >= today).sorted(Comparator.comparing(FlxDayResult::getDateInt)).collect(Collectors.toList());
        for (FlxDayResult result : results) {
            if (result.getDateInt() >= today) {
                if (result.getDateInt() >= newMonth) {
                    String key = getkey(DateUtil.toDateInt(DateUtil.getMonthBegin(DateUtil.fromDateInt(today), monthOffset - 1)) / 100);
                    redisHashUtil.putAllField(key, tips);
                    redisHashUtil.expire(key, 40, TimeUnit.DAYS);
                    tips.clear();
                    monthOffset++;
                    newMonth = DateUtil.toDateInt(DateUtil.getMonthBegin(DateUtil.fromDateInt(today), monthOffset));
                }
                int index = PowerRandom.getRandomBetween(1, 30);
                //1~10选择第一个
                int tip = result.getYsgBet1();
                if (10 < index && index <= 20) {
                    //11~20选择第二个
                    tip = result.getYsgBet2();
                } else if (20 < index) {
                    //21~30选择第三个
                    tip = result.getYsgBet3();
                }
                tips.put(result.getDateInt(), tip);
            }
        }
        if (!tips.isEmpty()) {
            String key = getkey(DateUtil.toDateInt(DateUtil.getMonthBegin(DateUtil.fromDateInt(today), monthOffset - 1)) / 100);
            redisHashUtil.putAllField(key, tips);
            redisHashUtil.expire(key, 40, TimeUnit.DAYS);
        }
        long end = System.currentTimeMillis();
        log.info(DateUtil.nowToString() + "本次生成押押乐提示 耗时：" + (end - begin));
    }

    /**
     * 获取今日提示的压压乐元素
     *
     * @return
     */
    public Integer getTodayTip() {
        int begin = DateUtil.toDateInt(DateUtil.getMonthBegin(new Date(), 0));
        return redisHashUtil.getField(getkey(begin / 100), DateUtil.getTodayInt());
    }

    private Integer generateBeginDate() {
        int today = DateUtil.getTodayInt();
        String key = getkey(today / 100);
        Map<Integer, Integer> map = redisHashUtil.get(key);
        if (map.isEmpty()) {
            //当前月缺数据 ,则只生成今日以后缺少的数据
            return today;
        }
        //当前月份的数据不为空
        int monthDays = DateUtil.getMonthDays(new Date());
        if (map.size() < monthDays) {
            //当前月份的数据量 小于 该月的天数，说明本月有缺少数据，则检查今日以及以后的日期是否有缺少数据
            int end = DateUtil.toDateInt(DateUtil.getMonthEnd(new Date(), 0));
            for (int j = today; j <= end; j++) {
                if (map.get(j) == null) {
                    return j;
                }
            }
        }
        return -1;
    }

    //例：game:flxYaYaLeTip:202001表示2020年1月份数据
    private String getkey(Integer date) {
        return "game:flxYaYaLeTip:" + date;
    }

    /**
     * 仅支持重新生成当月数据！！！
     *
     * @param beginDate
     */
    public void regenerateData(int beginDate) {
        String key = getkey(beginDate / 100);
        Map<Integer, Integer> map = redisHashUtil.get(key);
        // 删除数据
        Set<Map.Entry<Integer, Integer>> entries = map.entrySet();
        List<Integer> removeKeys = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : entries) {
            if (entry.getKey() >= beginDate) {
                removeKeys.add(entry.getKey());
            }
        }
        removeKeys.forEach(map::remove);
        redisHashUtil.putAllField(key, map);
        // 生成数据
        checkAndGenerateTips();
    }
}
