package com.bbw.god.gameuser.statistic.behavior.yuxg;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.treasure.event.EPFuTuAdd;
import com.bbw.god.gameuser.yuxg.Enum.FuTuEnum;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.UserFuTuInfo;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 玉虚宫统计服务
 *
 * @author fzj
 * @date 2021/9/17 17:25
 */
@Service
public class YuXGStatisticService extends BehaviorStatisticService {
    @Autowired
    GameUserService gameUserService;
    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.YU_XG;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof YuXGStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        YuXGStatistic yuXGStatistic = (YuXGStatistic) statistic;
        Integer date = yuXGStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, yuXGStatistic.getToday());
        map.put(TOTAL, yuXGStatistic.getTotal());
        Map<String, Integer> attackFuTuNums = yuXGStatistic.getAttackFuTuNums();
        if (null != attackFuTuNums) {
            for (String typeField : attackFuTuNums.keySet()) {
                map.put(typeField, attackFuTuNums.get(typeField));
            }
        }
        Map<String, Integer> bloodFuTuNums = yuXGStatistic.getBloodFuTuNums();
        if (null != bloodFuTuNums) {
            for (String typeField : attackFuTuNums.keySet()) {
                map.put(typeField, attackFuTuNums.get(typeField));
            }
        }
        Map<String, Integer> defenseFuTuNums = yuXGStatistic.getDefenseFuTuNums();
        if (null != defenseFuTuNums) {
            for (String typeField : attackFuTuNums.keySet()) {
                map.put(typeField, attackFuTuNums.get(typeField));
            }
        }
        Map<String, Integer> skillsFuTuNums = yuXGStatistic.getSkillsFuTuNums();
        if (null != skillsFuTuNums) {
            for (String typeField : attackFuTuNums.keySet()) {
                map.put(typeField, attackFuTuNums.get(typeField));
            }
        }
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }
    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return 统计对象
     */
    @Override
    public YuXGStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new YuXGStatistic(date,redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new YuXGStatistic(date, redisMap);
    }

    /**
     * 执行统计
     *
     * @param ep
     */
    public void doStatistic(EPFuTuAdd ep, List<Integer> fuTuIds) {
        long uid = ep.getGuId();
        String key = getKey(uid, StatisticTypeEnum.NONE);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        for (int fuTuId : fuTuIds) {
            UserFuTuInfo userFuTuInfo = gameUserService.getSingleItem(uid, UserFuTuInfo.class);
            CfgFuTuEntity fuTuInFo = YuXGTool.getFuTuInFo(fuTuId);
            Integer fuTuType = fuTuInFo.getType();
            Integer quality = fuTuInFo.getQuality();
            boolean isFirstGain = userFuTuInfo.getFuTuBaseIds().contains(fuTuId);
            String fuTuKey = FuTuTypeEnum.fromValue(fuTuType).getName() + FuTuEnum.fromValue(quality).getName();
            Integer futuNum = redisMap.getOrDefault(fuTuKey, 0);
            if (!isFirstGain && futuNum > 0) {
                continue;
            }
            //全量统计
            increment(uid, DateUtil.getTodayInt(), 1);
            //获得符图统计
            redisHashUtil.increment(key, FuTuTypeEnum.fromValue(fuTuType).getName() + FuTuEnum.fromValue(quality).getName(), 1);
        }
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {

    }
}
