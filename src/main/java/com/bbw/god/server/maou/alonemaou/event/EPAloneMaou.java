package com.bbw.god.server.maou.alonemaou.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import lombok.Data;

/**
 * @author suhq
 * @description: 魔王击杀事件参数
 * @date 2020-01-02 16:33
 **/
@Data
public class EPAloneMaou extends BaseEventParam {
    private ServerAloneMaou aloneMaou;
    private AloneMaouLevelInfo maouLevelInfo;
    private boolean firstKilled=false;

    public EPAloneMaou(ServerAloneMaou aloneMaou, AloneMaouLevelInfo maouLevelInfo,boolean firstKilled, BaseEventParam bep) {
        this.aloneMaou = aloneMaou;
        this.maouLevelInfo = maouLevelInfo;
        this.firstKilled=firstKilled;
        setValues(bep);
    }
}
