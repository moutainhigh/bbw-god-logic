package com.bbw.god.gameuser.businessgang.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 增加商帮npc好感度事件
 *
 * @author fzj
 * @date 2022/1/29 13:36
 */
public class AddGangNpcFavorabilityEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public AddGangNpcFavorabilityEvent(EPAddGangNpcFavorability dta) {
        super(dta);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPAddGangNpcFavorability getEP() {
        return (EPAddGangNpcFavorability) getSource();
    }
}
