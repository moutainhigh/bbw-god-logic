package com.bbw.god.game.config.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 说明：
 * 主角卡 技海技能分类
 * @author lwb
 * date 2021-04-21
 */
@Getter
@AllArgsConstructor
public enum LeaderCardSkillGroupEnum {
    Other("特殊",0),
    INTO_PLAYING("上场",1),
    MAGIC("法术",2),
    ATTACK("攻击",3),
    DEFENSE("防御",4),
    DEAD("死亡",5);
    private String name;
    private int type;
}
