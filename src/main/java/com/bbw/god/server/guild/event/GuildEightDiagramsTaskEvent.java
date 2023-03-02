package com.bbw.god.server.guild.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 八卦任务事件
 * @date 2019/12/19 16:49
 */
public class GuildEightDiagramsTaskEvent extends ApplicationEvent implements IEventParam {
    public GuildEightDiagramsTaskEvent(EPGuildEightDiagramsTask source) {
        super(source);
    }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public EPGuildEightDiagramsTask getEP() {
        return (EPGuildEightDiagramsTask) getSource();
    }
}
