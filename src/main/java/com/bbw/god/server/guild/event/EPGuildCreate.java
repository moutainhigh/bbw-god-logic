package com.bbw.god.server.guild.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 行会创建事件
 * @author lwb
 *
 */
@Getter
@Setter
public class EPGuildCreate extends BaseEventParam {
    private String name;

    public static EPGuildCreate instance(BaseEventParam ep,String name) {
        EPGuildCreate ev = new EPGuildCreate();
        ev.setValues(ep);
        ev.setName(name);
        return ev;
    }
}
