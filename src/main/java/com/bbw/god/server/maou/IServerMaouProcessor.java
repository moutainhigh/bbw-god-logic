package com.bbw.god.server.maou;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;

/**
 * @author suhq
 * @description: 魔王逻辑接口
 * @date 2019-12-20 17:05
 **/
public interface IServerMaouProcessor<T extends BaseServerMaou> {
    public RDSuccess getMaou(long uid, int sid);

    public ServerMaouStatusInfo getMaouStatus(T maou);

    public RDSuccess getAttackingInfo(long uid, int sid);

    public RDSuccess setMaouCards(long uid, String maouCards);

    public RDCommon attack(long uid, int sid, int attackType);

    public boolean isMatch(int sid);

    public boolean isMatchByMaouKind(int maouKind);
}
