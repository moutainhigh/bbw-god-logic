package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 魔王被击杀事件
 *
 * @author suhq
 * @date 2019年2月28日 下午4:50:40
 */
public class BossMaouKilledEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = -6677171809068070702L;

    public BossMaouKilledEvent(EPBossMaou param) {
        super(param);
    }

    @Override
    public EPBossMaou getEP() {
        return (EPBossMaou) getSource();
    }
}
