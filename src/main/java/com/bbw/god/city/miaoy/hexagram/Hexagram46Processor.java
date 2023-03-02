package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 山地剥卦
 *
 * 遗失2个青鸾
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram46Processor extends AbstractHexagram{
    @Autowired
    private UserTreasureService userTreasureService;
    @Override
    public int getHexagramId() {
        return 46;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_DOWN;
    }


    @Override
    public boolean canEffect(long uid) {
        UserTreasure userTreasure = userTreasureService.getUserTreasure(uid, TreasureEnum.QL.getValue());
        return userTreasure!=null && userTreasure.getOwnNum()>0;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        UserTreasure userTreasure = userTreasureService.getUserTreasure(uid, TreasureEnum.QL.getValue());
        int num=Math.min(userTreasure.getOwnNum(),2);
        TreasureEventPublisher.pubTDeductEvent(uid,TreasureEnum.QL.getValue(),num,getWay(),rd);
    }


}
