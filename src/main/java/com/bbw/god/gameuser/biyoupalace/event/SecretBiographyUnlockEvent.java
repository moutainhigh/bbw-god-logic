package com.bbw.god.gameuser.biyoupalace.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 游宫秘传解锁事件
 *
 * @author suhq
 * @date 2021/7/2 上午9:18
 **/
public class SecretBiographyUnlockEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 1L;

    public SecretBiographyUnlockEvent(EPSecretBiographyUnlock source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPSecretBiographyUnlock getEP() {
        return (EPSecretBiographyUnlock) getSource();
    }
}
