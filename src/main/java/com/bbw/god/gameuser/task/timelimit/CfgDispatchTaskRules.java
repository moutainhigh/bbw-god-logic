package com.bbw.god.gameuser.task.timelimit;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 派遣规则配置
 *
 * @author: suhq
 * @date: 2021/8/6 3:48 下午
 */
@Slf4j
@Data
public class CfgDispatchTaskRules implements Serializable {
    private static final long serialVersionUID = -2194371713246181712L;
    private Integer needDice;
    /** 卡牌精力 */
    private Integer needCardVigor;
    private Integer needStar;
    private List<Integer> skillLevels1;
    private List<Integer> skillLevels2;
    private List<Integer> skillLevels3;
    private List<Integer> skillPool1;
    private List<Integer> skillPool2;
    private List<Integer> skillPool3;
    /** 派遣时间(分) */
    private Integer dispatchTime;
    /** 等待时间(分) */
    private Integer waitTime;
}
