package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 攻击魔王事件
 *
 * @author suhq
 * @date 2019年2月28日 下午4:50:40
 */
public class BossMaouAttackEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = -6677171809068070702L;

    public BossMaouAttackEvent(EPAttackMaou param) {
        super(param);
    }

    @Override
    public EPAttackMaou getEP() {
        return (EPAttackMaou) getSource();
    }
}
