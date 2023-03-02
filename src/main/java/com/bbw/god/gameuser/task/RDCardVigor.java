package com.bbw.god.gameuser.task;

import lombok.Data;

/**
 * 返回卡牌精力
 *
 * @author: suhq
 * @date: 2021/8/10 9:31 上午
 */
@Data
public class RDCardVigor {
    private Integer cardId;
    /** 当前精力  */
    private Integer vigor;
    /** 最大精力 */
    private Integer maxVigor;

    public RDCardVigor(int cardId, int vigor, int maxCardVigor) {
        this.cardId = cardId;
        this.vigor = vigor;
        this.maxVigor = maxCardVigor;
    }
}
