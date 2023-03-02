package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 革土符图 233015 5阶 己方金属性、土属性卡牌攻击值增加49点。额外+28点 满级增加329点
 *
 * @author longwh
 * @date 2023/3/1 11:40
 */
@Service
public class Runes232015 extends PlayerAttackRune{

    @Override
    public int getRunesId() {
        return RunesEnum.GE_TU_PLAYER_5.getRunesId();
    }

    @Override
    boolean hasMultipleTypes() {
        return true;
    }

    @Override
    List<TypeEnum> getCardTypes() {
        List<TypeEnum> typeEnums = new ArrayList<>();
        typeEnums.add(TypeEnum.Gold);
        typeEnums.add(TypeEnum.Earth);
        return typeEnums;
    }
}