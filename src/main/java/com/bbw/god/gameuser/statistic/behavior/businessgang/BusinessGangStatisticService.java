package com.bbw.god.gameuser.statistic.behavior.businessgang;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.transmigration.event.EPTransmigrationSuccess;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.cfg.CfgNpcInfo;
import com.bbw.god.gameuser.businessgang.event.EPAddGangNpcFavorability;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.Transmigration.TransmigrationStatistic;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 商帮统计服务
 *
 * @author fzj
 * @date 2022/2/7 21:08
 */
@Service
public class BusinessGangStatisticService extends BehaviorStatisticService {
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.BUSINESS_GANG;
    }

    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof BusinessGangStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        BusinessGangStatistic businessGangStatistic = (BusinessGangStatistic) statistic;
        Integer date = businessGangStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, businessGangStatistic.getToday());
        map.put(TOTAL, businessGangStatistic.getTotal());
        map.put(BusinessGangStatistic.USE_GANG_TOKEN_NUM, businessGangStatistic.getUseGangTokenNum());
        map.put(BusinessGangStatistic.FINISH_GANG_TASK_NUM, businessGangStatistic.getFinishGangTaskNum());
        map.put(BusinessGangStatistic.DIG_FOR_TREASURE_NUM, businessGangStatistic.getDigForTreasureNum());
        Map<String, Integer> businessGangPrestige = businessGangStatistic.getBusinessGangPrestige();
        if (null != businessGangPrestige) {
            for (String typeField : businessGangPrestige.keySet()) {
                map.put(typeField, businessGangPrestige.get(typeField));
            }
        }
        Map<String, Integer> finishWeeklyTaskNum = businessGangStatistic.getFinishWeeklyTaskNum();
        if (null != finishWeeklyTaskNum) {
            for (String typeField : finishWeeklyTaskNum.keySet()) {
                map.put(typeField, finishWeeklyTaskNum.get(typeField));
            }
        }
        Map<String, Integer> npcFavorability = businessGangStatistic.getNpcFavorability();
        if (null != npcFavorability) {
            for (String typeField : npcFavorability.keySet()) {
                map.put(typeField, npcFavorability.get(typeField));
            }
        }
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    @Override
    public BusinessGangStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return new BusinessGangStatistic(date, redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new BusinessGangStatistic(date, redisMap);
    }

    /**
     * 执行统计
     *
     * @param uid
     * @param num
     */
    public void doStatistic(long uid, int num) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //执行统计
        redisHashUtil.increment(key, BusinessGangStatistic.USE_GANG_TOKEN_NUM, num);
    }

    /**
     * 声望统计增加
     *
     * @param uid
     * @param prestige
     */
    public void doAddPrestigeStatistic(long uid, List<EVTreasure> prestige) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //执行统计
        for (EVTreasure treasure : prestige) {
            Integer gang = BusinessGangCfgTool.getGangByPrestigeId(treasure.getId());
            if (null == gang) {
                continue;
            }
            String gangName = BusinessGangEnum.fromValue(gang).getName();
            redisHashUtil.increment(key, gangName, treasure.getNum());
        }
    }

    /**
     * 声望扣除
     *
     * @param uid
     * @param deductPrestige
     */
    public void doDelPrestigeStatistic(long uid, EVTreasure deductPrestige) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //执行统计
        Integer gang = BusinessGangCfgTool.getGangByPrestigeId(deductPrestige.getId());
        String gangName = BusinessGangEnum.fromValue(gang).getName();
        redisHashUtil.increment(key, gangName, -deductPrestige.getNum());
    }

    /**
     * 完成商帮任务统计
     *
     * @param ep
     */
    public void doFinishGangTaskStatistic(EPBusinessGangTask ep) {
        Long uid = ep.getGuId();
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        int taskId = ep.getTaskId();
        //周常任务统计
        TaskGroupEnum taskGroup = ep.getTaskGroup();
        if (taskGroup == TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK) {
            redisHashUtil.increment(key, String.valueOf(taskId), 1);
        } else {
            //普通任务统计
            redisHashUtil.increment(key, BusinessGangStatistic.FINISH_GANG_TASK_NUM, 1);
        }
    }

    /**
     * npc好感度增加统计
     *
     * @param ep
     */
    public void doAddGangNpcFavorabilityStatistic(EPAddGangNpcFavorability ep) {
        Long uid = ep.getGuId();
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        CfgNpcInfo cfgNpcInfo = BusinessGangCfgTool.getNpcInfo(ep.getNpcId());
        //执行统计
        redisHashUtil.increment(key, cfgNpcInfo.getName(), ep.getAddFavorability());
    }

    /**
     * 挖宝统计
     *
     * @param uid
     */
    public void doDigTreasureStatistic(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //执行统计
        redisHashUtil.increment(key, BusinessGangStatistic.DIG_FOR_TREASURE_NUM, 1);
    }

    @Override
    public void init(long uid) {

    }
}
