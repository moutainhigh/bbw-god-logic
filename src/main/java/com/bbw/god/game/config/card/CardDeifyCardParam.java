package com.bbw.god.game.config.card;

import lombok.Data;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-04-23
 */
@Data
public class CardDeifyCardParam {
    private int[] skills={0,0,0};
    /**
     * 是否是改变了技能
     */
    private boolean change=false;

    public static CardDeifyCardParam getInstance(int[] skills,boolean change){
        CardDeifyCardParam param=new CardDeifyCardParam();
        param.setSkills(skills);
        param.setChange(change);
        return param;
    }
}
