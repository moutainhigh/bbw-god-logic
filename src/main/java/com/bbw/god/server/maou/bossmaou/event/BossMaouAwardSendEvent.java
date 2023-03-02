package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 魔王发送奖励事件，传的值缓存的魔王的key
 *
 * @author suhq
 * @date 2019年2月28日 下午4:50:40
 */
public class BossMaouAwardSendEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public BossMaouAwardSendEvent(EPBossMaou param) {
        super(param);
    }

    @Override
    public EPBossMaou getEP() {
        return (EPBossMaou) getSource();
    }
}
