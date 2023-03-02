package com.bbw.god.server.monster.event;

import com.bbw.god.event.IEventParam;
import com.bbw.god.server.guild.event.EPGuildEightDiagramsTask;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 友怪新增事件
 * @date 2019/12/20 15:48
 */
public class FriendMonsterAddEvent  extends ApplicationEvent implements IEventParam {
    public FriendMonsterAddEvent(EPFriendMonsterAdd source) {
        super(source);
    }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public EPFriendMonsterAdd getEP() {
        return (EPFriendMonsterAdd) getSource();
    }
}
