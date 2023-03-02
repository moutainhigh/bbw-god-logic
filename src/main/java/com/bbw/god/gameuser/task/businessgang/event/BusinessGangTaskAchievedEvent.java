package com.bbw.god.gameuser.task.businessgang.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 商帮任务事件
 *
 * @author fzj
 * @date 2022/1/29 13:36
 */
public class BusinessGangTaskAchievedEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public BusinessGangTaskAchievedEvent(EPBusinessGangTask dta) {
        super(dta);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPBusinessGangTask getEP() {
        return (EPBusinessGangTask) getSource();
    }
}
