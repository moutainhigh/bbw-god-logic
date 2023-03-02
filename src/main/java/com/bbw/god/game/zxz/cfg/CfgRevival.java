package com.bbw.god.game.zxz.cfg;

import lombok.Data;

import java.io.Serializable;

/**
 * 复活次数配置
 * @author: hzf
 * @create: 2022-09-15 17:19
 **/
@Data
public class CfgRevival implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 第几次 */
    private Integer frequency;
    /** 道具*/
    private Integer needTreasure;
    /** 数量 */
    private Integer num;
}
