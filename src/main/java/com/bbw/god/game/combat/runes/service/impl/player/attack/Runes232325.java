package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 炎金符图 232325 5阶 己方火属性、金属性卡牌攻击值增加49点。额外+28点 满级增加329点
 *
 * @author longwh
 * @date 2023/3/1 14:00
 */
@Service
public class Runes232325 extends PlayerAttackRune {
    @Override
    public int getRunesId() {
        return RunesEnum.YAN_JIN_PLAYER_5.getRunesId();
    }

    @Override
    boolean hasMultipleTypes() {
        return true;
    }

    @Override
    List<TypeEnum> getCardTypes() {
        List<TypeEnum> typeEnums = new ArrayList<>();
        typeEnums.add(TypeEnum.Fire);
        typeEnums.add(TypeEnum.Gold);
        return typeEnums;
    }
}