package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.ListUtil;
import com.bbw.god.fight.FighterInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import com.bbw.god.game.config.card.CardSkillTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商城相关常量配置
 *
 * @author suhq
 * @date 2019-07-29 11:17:36
 */
@Slf4j
@Data
public class CfgTimeLimitTaskRules implements CfgEntityInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 3288156423421447551L;
    private Integer groupId;
    /** 派遣模式 */
    private Integer dispatchMode = DispatchModeEnum.SUCCESS_RATE_MODE.getValue();
    /** 最大任务数 */
    private Integer maxTaskNum;
    /** 元宝加速(秒) */
    private Integer goldSpeedUpSeconds;
    /** 神行符加速(秒) */
    private Integer shenXSpeedUpSeconds;
    /** 常规任务有效时间(分) */
    private Integer normalTaskWait;
    /** 战斗任务有效时间(分) */
    private Integer fightTaskWait;
    /** 技能条件对应概率 */
    private Integer conditionRate;
    /** 星级成功率 */
    private List<Integer> starSuccessRate;
    /** 星级减少时间 */
    private List<Integer> starReduceTime;
    /** 卡牌等级间隔 */
    private Integer cardLevelSpacing;
    /** 卡牌等级成功率 */
    private Integer cardLevelRate;
    /** 精力重置时间 */
    private Integer cardVigorRestHour;
    /** 难度对应规则 */
    private Map<Integer, CfgDispatchTaskRules> dispatchTaskConfigs;
    /** 难度对应额外奖励 */
    private Map<Integer, ExtraAwards> dispatchTaskExtraAward;
    /** 技能分级 */
    private Map<Integer, List<String>> levelSkills;
    private Map<Integer, FighterInfo> fightersInfo;
    /** 进攻者信息 */
    private Map<Integer, FighterInfo> attackersInfo;
    /** 地灵印额外次数 */
    private Integer extraTimesForDiLY = 0;
    /** 天灵印额外次数 */
    private Integer extraTimesForTianLY = 1;
    /** 最大排队任务数 */
    private Integer maxQueuingTaskNum = 0;
    /** 派遣卡牌数量 */
    private Integer dispatchCardNum = 3;
    /** 最大等待任务数 0代表无限制 */
    private Integer maxWaitingTaskNum = 0;

    @Override
    public void prepare() {
        for (CfgDispatchTaskRules rule : dispatchTaskConfigs.values()) {
            //第一个技能位
            List<String> skillPools1 = new ArrayList<>();
            if (ListUtil.isNotEmpty(rule.getSkillLevels1())) {
                for (Integer level : rule.getSkillLevels1()) {
                    skillPools1.addAll(levelSkills.get(level));
                }
            }
            List<Integer> skillIdsPool1 = skillPools1.stream().map(tmp -> CardSkillTool.getSkillIdByName(tmp)).collect(Collectors.toList());
            rule.setSkillPool1(skillIdsPool1);
            //第二个技能位
            List<String> skillPools2 = new ArrayList<>();
            if (ListUtil.isNotEmpty(rule.getSkillLevels2())) {
                for (Integer level : rule.getSkillLevels2()) {
                    skillPools2.addAll(levelSkills.get(level));
                }
            }
            List<Integer> skillIdsPool2 = skillPools2.stream().map(tmp -> CardSkillTool.getSkillIdByName(tmp)).collect(Collectors.toList());
            rule.setSkillPool2(skillIdsPool2);
            //第三个技能位
            List<String> skillPools3 = new ArrayList<>();
            if (ListUtil.isNotEmpty(rule.getSkillLevels3())) {
                for (Integer level : rule.getSkillLevels3()) {
                    skillPools3.addAll(levelSkills.get(level));
                }
            }
            List<Integer> skillIdsPool3 = skillPools3.stream().map(tmp -> CardSkillTool.getSkillIdByName(tmp)).collect(Collectors.toList());
            rule.setSkillPool3(skillIdsPool3);
        }
    }

    @Data
    public static class ExtraAwards {
        private List<Award> awards;
    }

    @Override
    public Serializable getId() {
        return groupId;
    }

    @Override
    public int getSortId() {
        return groupId;
    }
}
