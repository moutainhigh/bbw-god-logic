package com.bbw.god.gameuser.statistic.behavior.yuxg;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.yuxg.Enum.FuTuEnum;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.NUM;
import static com.bbw.god.gameuser.statistic.StatisticConst.UNDERLINE;

/**
 * 玉虚宫行为统计
 *
 * @author fzj
 * @date 2021/11/1 18:17
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class YuXGStatistic extends BehaviorStatistic {
    /** 技能符图获取数量 */
    private Map<String, Integer> skillsFuTuNums;
    /** 攻击符图获取数量 */
    private Map<String, Integer> attackFuTuNums;
    /** 防御符图获取数量 */
    private Map<String, Integer> defenseFuTuNums;
    /** 血量符图获取数量 */
    private Map<String, Integer> bloodFuTuNums;

    public YuXGStatistic() {
        super(BehaviorType.YU_XG);
    }

    public YuXGStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.YU_XG);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.getOrDefault(dateNumStr, 0);
        setToday(today);
        //获得技能符图统计
        Map<String, Integer> skillsFuTuMap = new HashMap<>();
        for (FuTuEnum fuTuEnum : FuTuEnum.values()){
            String skillsFuTuField = FuTuTypeEnum.SKILLS_FU_TU.getName() + fuTuEnum.getName();
            skillsFuTuMap.put(skillsFuTuField, redisMap.getOrDefault(skillsFuTuField, 0));
        }
        setSkillsFuTuNums(skillsFuTuMap);
       //获得攻击符图统计
        Map<String, Integer> attackFuTuMap = new HashMap<>();
        for (FuTuEnum fuTuEnum : FuTuEnum.values()){
            String attackFuTuField = FuTuTypeEnum.ATTACK_FU_TU.getName() + fuTuEnum.getName();
            attackFuTuMap.put(attackFuTuField, redisMap.getOrDefault(attackFuTuField, 0));
        }
        setAttackFuTuNums(attackFuTuMap);
       //获得防御符图统计
        Map<String, Integer> defenseFuTuMap = new HashMap<>();
        for (FuTuEnum fuTuEnum : FuTuEnum.values()){
            String defenseFuTuField = FuTuTypeEnum.DEFENSE_FU_TU.getName() + fuTuEnum.getName();
            defenseFuTuMap.put(defenseFuTuField, redisMap.getOrDefault(defenseFuTuField, 0));
        }
        setDefenseFuTuNums(defenseFuTuMap);
       //获得血量符图统计
        Map<String, Integer> bloodFuTuMap = new HashMap<>();
        for (FuTuEnum fuTuEnum : FuTuEnum.values()){
            String bloodFuTuField = FuTuTypeEnum.BLOOD_FU_TU.getName() + fuTuEnum.getName();
            bloodFuTuMap.put(bloodFuTuField, redisMap.getOrDefault(bloodFuTuField, 0));
        }
        setBloodFuTuNums(bloodFuTuMap);
    }
}
