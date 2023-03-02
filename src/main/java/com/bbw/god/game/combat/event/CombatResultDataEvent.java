package com.bbw.god.game.combat.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
public class CombatResultDataEvent extends ApplicationEvent implements IEventParam {
    public CombatResultDataEvent(EPCombatResultData source) {
        super(source);
    }

    @Override
    public EPCombatResultData getEP() {
        return (EPCombatResultData)getSource();
    }
}
