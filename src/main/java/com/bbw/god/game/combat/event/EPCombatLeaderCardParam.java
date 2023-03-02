package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import lombok.Data;

/**
 * 说明：主角卡相关事件的参数
 *
 * @author lwb
 * date 2021-04-25
 */
@Data
public class EPCombatLeaderCardParam extends BaseEventParam {
    private FightTypeEnum fightType;
    private boolean win=false;
    private boolean mainCity=false;
    private Integer cityId=null;
    /**
     * 主角卡击杀卡牌数量
     */
    private Integer killCards=0;
}
