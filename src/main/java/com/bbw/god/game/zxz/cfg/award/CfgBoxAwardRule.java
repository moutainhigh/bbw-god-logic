package com.bbw.god.game.zxz.cfg.award;

import lombok.Data;

import java.io.Serializable;

/**
 * 宝箱奖励规则
 * @author: hzf
 * @create: 2022-09-22 11:49
 **/
@Data
public class CfgBoxAwardRule implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;
    /** 难度类型 */
    private  Integer difficulty;
    /** 关卡种类 */
    private  Integer defenderKind;
    /** 宝箱Id */
    private Integer boxId;
    /** 固定奖励Id */
    private Integer fixedId;


}
