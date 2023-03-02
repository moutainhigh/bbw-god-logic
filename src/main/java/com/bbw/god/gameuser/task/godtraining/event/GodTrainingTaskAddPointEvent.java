package com.bbw.god.gameuser.task.godtraining.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 上仙试炼增加试炼值事件
 * @date 2021/1/21 16:23
 **/
public class GodTrainingTaskAddPointEvent extends ApplicationEvent implements IEventParam {

    public GodTrainingTaskAddPointEvent(EPGodTrainingTaskAddPoint source) {
        super(source);
    }

    /**
     * 获取事件参数
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public EPGodTrainingTaskAddPoint getEP() {
        return (EPGodTrainingTaskAddPoint) getSource();
    }
}
