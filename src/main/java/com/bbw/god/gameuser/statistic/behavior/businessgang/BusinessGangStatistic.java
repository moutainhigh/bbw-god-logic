package com.bbw.god.gameuser.statistic.behavior.businessgang;

import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangNpcEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessNpcTypeEnum;
import com.bbw.god.gameuser.businessgang.cfg.CfgNpcInfo;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;
import static com.bbw.god.gameuser.statistic.StatisticConst.TOTAL;

/**
 * 商帮相关统计
 *
 * @author fzj
 * @date 2022/2/1 10:22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessGangStatistic extends BehaviorStatistic {
    public static String USE_GANG_TOKEN_NUM = "useGangTokenNum";
    public static String FINISH_GANG_TASK_NUM = "finishGangTaskNum";
    public static String DIG_FOR_TREASURE_NUM = "digForTreasureNum";
    /** 使用商帮令牌数量 */
    private Integer useGangTokenNum;
    /** 完成商帮任务数量统计 */
    private Integer finishGangTaskNum;
    /** 挖宝次数统计 */
    private Integer digForTreasureNum;
    /** 商帮声望统计 */
    private Map<String,Integer> businessGangPrestige;
    /** npc好感统计 */
    private Map<String,Integer> npcFavorability;
    /** 完成不同种类周常商帮任务数量统计 */
    private Map<String,Integer> finishWeeklyTaskNum;

    public BusinessGangStatistic() {
        super(BehaviorType.BUSINESS_GANG);
    }

    public BusinessGangStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.BUSINESS_GANG);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.getOrDefault(dateNumStr, 0);
        Integer total = redisMap.getOrDefault(TOTAL, 0);
        setToday(today);
        setTotal(total);
        //使用令牌统计
        useGangTokenNum = redisMap.getOrDefault(USE_GANG_TOKEN_NUM, 0);
        //完成商帮任务数量统计
        finishGangTaskNum = redisMap.getOrDefault(FINISH_GANG_TASK_NUM, 0);
        //挖宝次数统计
        digForTreasureNum = redisMap.getOrDefault(DIG_FOR_TREASURE_NUM, 0);
        //完成商帮周常任务统计
        List<CfgTaskEntity> tasks = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK);
        List<Integer> taskIds = tasks.stream().map(CfgTaskEntity::getId).collect(Collectors.toList());
        Map<String, Integer> finishWeeklyTask = new HashMap<>();
        for (Integer taskId : taskIds){
            String task = String.valueOf(taskId);
            finishWeeklyTask.put(task, redisMap.getOrDefault(task, 0));
        }
        setFinishWeeklyTaskNum(finishWeeklyTask);
        //商帮声望统计
        Map<String, Integer> businessGangPrestige = new HashMap<>();
        for (BusinessGangEnum businessGang : BusinessGangEnum.values()) {
            String name = businessGang.getName();
            businessGangPrestige.put(name, redisMap.getOrDefault(name, 0));
        }
        setBusinessGangPrestige(businessGangPrestige);
        //npc好感统计
        Map<String, Integer> npcFavorability = new HashMap<>();
        for (BusinessGangNpcEnum gangNpc : BusinessGangNpcEnum.values()) {
            CfgNpcInfo cfgNpcInfo = BusinessGangCfgTool.getNpcInfo(gangNpc.getId());
            if (cfgNpcInfo.getType() != BusinessNpcTypeEnum.ZHANG_DUO_REN.getType()){
                continue;
            }
            String name = gangNpc.getName();
            npcFavorability.put(name, redisMap.getOrDefault(name, 0));
        }
        setNpcFavorability(npcFavorability);
    }
}
