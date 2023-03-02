package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 火泽睽卦
 *
 * 损失当前铜钱携带量的10%
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram58Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 58;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser user = gameUserService.getGameUser(uid);
        Double val = user.getCopper() * 0.1;
        ResEventPublisher.pubCopperDeductEvent(uid,val.longValue(),getWay(),rd);
    }


}
