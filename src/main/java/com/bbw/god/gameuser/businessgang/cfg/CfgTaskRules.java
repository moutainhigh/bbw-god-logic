package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 任务规则
 *
 * @author fzj
 * @date 2022/1/17 10:36
 */
@Data
public class CfgTaskRules implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 任务难度 */
    private Integer difficulty;
    /** 特产数量 */
    private Integer specialtyNum;
    /** 运送数量 */
    private Integer shippingNum;
    /** 可运送城池等级分布 */
    private List<Integer> cityLvRange;
    /** 特产分级 */
    private List<SpecialtyGrading> specialtyGrading;

    @Data
    public static class SpecialtyGrading{
        /** 特产等级 */
        private Integer specialtyGrade;
        /** 概率 */
        private Integer probability;
    }
}
