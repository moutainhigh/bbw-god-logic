package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import lombok.Data;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
@Data
public class EPCombatResultData extends BaseEventParam {
    private Integer killCardsNum=0;
    private Integer useTreasureNum=0;
    private FightTypeEnum fightType;

    public static EPCombatResultData getInstance(long uid, int fightType, int killCardsNum, int useTreasureNum){
        EPCombatResultData cd=new EPCombatResultData();
        cd.setValues(new BaseEventParam(uid));
        cd.setFightType(FightTypeEnum.fromValue(fightType));
        cd.setKillCardsNum(killCardsNum);
        cd.setUseTreasureNum(useTreasureNum);
        return cd;
    }
}
