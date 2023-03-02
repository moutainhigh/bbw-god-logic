package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 山水蒙卦
 *
 * 遗失1件随机战斗宝物
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram44Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 44;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_DOWN;
    }

    @Override
    public boolean canEffect(long uid) {
        List<Integer> fightTreasureIds = TreasureTool.getFightTreasureIds();
        for (Integer treasureId : fightTreasureIds) {
            if (TreasureChecker.hasTreasure(uid,treasureId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        List<Integer> fightTreasureIds = TreasureTool.getFightTreasureIds();
        PowerRandom.shuffle(fightTreasureIds);
        for (Integer treasureId : fightTreasureIds) {
            if (TreasureChecker.hasTreasure(uid,treasureId)){
                TreasureEventPublisher.pubTDeductEvent(uid,treasureId,1,getWay(),rd);
                return;
            }
        }
    }


}
