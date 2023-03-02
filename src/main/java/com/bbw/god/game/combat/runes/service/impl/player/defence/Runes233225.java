package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 霖火符图 233225 5阶 己方水属性、火属性卡牌防御值增加49点。额外+28点 满级增加329点
 *
 * @author longwh
 * @date 2023/3/1 15:05
 */
@Service
public class Runes233225 extends PlayerDefenceRune {
    @Override
    public int getRunesId() {
        return RunesEnum.LIE_HUO_ENTRY.getRunesId();
    }

    @Override
    boolean hasMultipleTypes() {
        return true;
    }

    @Override
    List<TypeEnum> getCardTypes() {
        List<TypeEnum> typeEnums = new ArrayList<>();
        typeEnums.add(TypeEnum.Water);
        typeEnums.add(TypeEnum.Fire);
        return typeEnums;
    }
}