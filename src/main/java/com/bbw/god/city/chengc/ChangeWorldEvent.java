package com.bbw.god.city.chengc;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 世界跳转事件
 * @date 2020/9/24 11:21
 **/
public class ChangeWorldEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 8626817667527628235L;

    public ChangeWorldEvent(EPChangeWorld source) {
        super(source);
    }

    /**
     * 获取事件参数
     *
     * @return
     */
    @Override
    public EPChangeWorld getEP() {
        return (EPChangeWorld) getSource();
    }
}
