package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明： 麒麟击败召唤师事件
 *
 * @author lwb
 * date 2021-04-25
 */

public class CombatQiLinKillZhsEvent extends ApplicationEvent implements IEventParam {

    public CombatQiLinKillZhsEvent(BaseEventParam source) {
        super(source);
    }

    @Override
    public BaseEventParam getEP() {
        return (BaseEventParam)getSource();
    }
}
