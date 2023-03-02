package com.bbw.god.game.combat.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明： 主角卡事件
 *
 * @author lwb
 * date 2021-04-25
 */

public class CombatLeaderCardEvent extends ApplicationEvent implements IEventParam {

    public CombatLeaderCardEvent(EPCombatLeaderCardParam source) {
        super(source);
    }

    @Override
    public EPCombatLeaderCardParam getEP() {
        return (EPCombatLeaderCardParam)getSource();
    }
}
