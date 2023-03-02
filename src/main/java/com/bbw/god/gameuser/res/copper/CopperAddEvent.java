package com.bbw.god.gameuser.res.copper;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:26
 */
public class CopperAddEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public CopperAddEvent(EPCopperAdd eventParam) {
        super(eventParam);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPCopperAdd getEP() {
        EPCopperAdd ep = (EPCopperAdd) getSource();
//        System.out.println(ep.getGuId());
        return ep;
    }

}
