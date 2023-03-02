package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author lwb
 * @description: 攻击魔王
 **/
@Data
public class EPAttackMaou extends BaseEventParam {

    private int blood=0;

    public static EPAttackMaou getInstance(int blood, BaseEventParam bep) {
        EPAttackMaou ep=new EPAttackMaou();
        ep.setBlood(blood);
        ep.setValues(bep);
        return ep;
    }
}
