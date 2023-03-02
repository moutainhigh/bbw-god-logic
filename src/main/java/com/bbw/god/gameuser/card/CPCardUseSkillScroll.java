package com.bbw.god.gameuser.card;

import lombok.Data;

/**
 * 卡牌使用卷轴
 *
 * @author suhq
 * @date 2019-09-30 14:31:03
 */
@Data
public class CPCardUseSkillScroll {
    /** 数据ID（非必传） **/
    private Long dataId;
    /** 卡牌基础ID（必传） **/
    private int cardId;
    private int skillLevel;// 0,5,10
    private int skillScroll;// 技能卷轴
}
