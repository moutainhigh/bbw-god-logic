package com.bbw.god.game.zxz.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 区域攻防比例
 * @author: hzf
 * @create: 2022-09-19 09:01
 **/
@Data
public class CfgRegionDefenseProport implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;

    /** 难度类型 */
    private Integer difficulty;
    /**  添加的攻防比例 %  */
    private Integer addDefenseProport;
    /** 减少的攻防比例 */
    private Integer reduceDefenseProport;
}
