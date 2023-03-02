package com.bbw.god.gameuser.card;

import lombok.Data;

/**
 * 卡牌装配符箓
 *
 * @author suhq
 * @date 2019-09-30 14:31:03
 */
@Data
public class CPSymbol {
    /** 数据ID（非必传） **/
    private Long dataId;
    /** 卡牌基础ID（必传） **/
    private int cardId;
    /** 技能卷轴 */
    private int symbol;
}
