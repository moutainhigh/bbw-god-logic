package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.rd.RDCommon;

public abstract class AbstractGodProcessor {
    protected GodEnum godType;

    public abstract void processor(GameUser gameUser, UserGod userGod, RDCommon rd);

    public boolean isMatch(int godId) {
        return this.godType.getValue() == godId;
    }

    public boolean isNewerGuideGodProcessor() {
        return false;
    }
}
