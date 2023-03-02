package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 霖金符图 233215 5阶 己方水属性、金属性卡牌防御值增加49点。额外+28点 满级增加329点
 *
 * @author longwh
 * @date 2023/3/1 15:05
 */
@Service
public class Runes233215 extends PlayerDefenceRune {
    @Override
    public int getRunesId() {
        return RunesEnum.LIN_JIN_PLAYER_5.getRunesId();
    }

    @Override
    boolean hasMultipleTypes() {
        return true;
    }

    @Override
    List<TypeEnum> getCardTypes() {
        List<TypeEnum> typeEnums = new ArrayList<>();
        typeEnums.add(TypeEnum.Water);
        typeEnums.add(TypeEnum.Gold);
        return typeEnums;
    }
}