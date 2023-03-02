package com.bbw.god.game.combat.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
public class CombatSxdhAddPointEvent extends ApplicationEvent implements IEventParam {

    public CombatSxdhAddPointEvent(EPCombatSxdhAddPoint source) {
        super(source);
    }

    @Override
    public EPCombatSxdhAddPoint getEP() {
        return (EPCombatSxdhAddPoint)getSource();
    }
}
