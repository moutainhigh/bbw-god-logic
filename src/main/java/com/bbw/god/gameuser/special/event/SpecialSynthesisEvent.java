package com.bbw.god.gameuser.special.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 特产合成事件
 * @date 2020/11/12 17:12
 **/
public class SpecialSynthesisEvent extends ApplicationEvent implements IEventParam {

    public SpecialSynthesisEvent(EPSpecialSynthesis source) {
        super(source);
    }

    /**
     * 获取事件参数
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public EPSpecialSynthesis getEP() {
        return (EPSpecialSynthesis) getSource();
    }
}
