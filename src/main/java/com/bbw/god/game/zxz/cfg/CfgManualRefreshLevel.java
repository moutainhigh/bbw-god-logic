package com.bbw.god.game.zxz.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 手动刷新难度配置
 * @author: hzf
 * @create: 2022-09-15 17:16
 **/
@Data
public class CfgManualRefreshLevel implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** 道具id */
    private Integer needTreasure;
    /** 数量 */
    private Integer num;
}
