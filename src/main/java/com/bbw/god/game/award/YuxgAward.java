package com.bbw.god.game.award;

import lombok.Data;

import java.io.Serializable;

/**
 * 玉虚宫奖励
 *
 * @author fzj
 * @date 2021/11/1 10:37
 */
@Data
public class YuxgAward extends Award implements Serializable {
    private static final long serialVersionUID = -1L;
    /** 奖励类别 */
    private Integer awardType;
    /** 品阶 */
    private Integer quality;
}
