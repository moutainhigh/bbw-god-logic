package com.bbw.god.server.maou.alonemaou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 魔王被击杀事件
 *
 * @author suhq
 * @date 2019年2月28日 下午4:50:40
 */
public class AloneMaouKilledEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = -6677171809068070702L;

    public AloneMaouKilledEvent(EPAloneMaou param) {
        super(param);
    }

    @Override
    public EPAloneMaou getEP() {
        return (EPAloneMaou) getSource();
    }
}
