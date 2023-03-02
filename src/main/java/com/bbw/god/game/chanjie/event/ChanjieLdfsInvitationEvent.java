package com.bbw.god.game.chanjie.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
public class ChanjieLdfsInvitationEvent extends ApplicationEvent implements IEventParam {
    public ChanjieLdfsInvitationEvent(EPChanjieLdfsInvitation source) {
        super(source);
    }

    @Override
    public EPChanjieLdfsInvitation getEP() {
        return (EPChanjieLdfsInvitation)getSource();
    }
}
