package com.bbw.god.gameuser.businessgang.cfg;

import lombok.Data;

import java.io.Serializable;


/**
 * 运送任务特产规则
 *
 * @author fzj
 * @date 2022/1/17 10:35
 */
@Data
public class CfgSpecialsRules implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    /** 特产id */
    private Integer specialId;
    /** 特产等级 */
    private Integer grade;
}
