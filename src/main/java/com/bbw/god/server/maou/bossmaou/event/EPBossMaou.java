package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import lombok.Data;

/**
 * @author suhq
 * @description: 魔王击杀事件参数
 * @date 2020-01-02 16:33
 **/
@Data
public class EPBossMaou extends BaseEventParam {
    private ServerBossMaou bossMaou;

    public EPBossMaou(ServerBossMaou bossMaou, BaseEventParam bep) {
        this.bossMaou = bossMaou;
        setValues(bep);
    }
}
