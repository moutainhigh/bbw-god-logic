package com.bbw.god.game.config.city;

import lombok.Data;

/**
 * @author：lwb
 * @date: 2020/12/24 16:00
 * @version: 1.0
 */
@Data
public class ChengC {
    // 城池ID
    private Integer id;
    // 城池名称
    private String name;
    private Integer buff;
    // 特产铺产出的特产
    private String specials;
    // 可掉落的卡牌
    private String dropCards;
    // 守将等级
    private Integer soliderLevel;
    // 守军
    private String soliders;//护卫军
    // 守军
    private String soliders2;//禁卫军
}
