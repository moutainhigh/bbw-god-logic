package com.bbw.god.server.maou.alonemaou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;


/**
 * @author lwb
 * @description: 通关
 **/
public class AloneMaouPassEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = -6677171809068070702L;

    public AloneMaouPassEvent(EPPassAloneMaou param) {
        super(param);
    }

    @Override
    public EPPassAloneMaou getEP() {
        return (EPPassAloneMaou) getSource();
    }
}
