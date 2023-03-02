package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.impl.player.attack.PlayerAttackRune;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 鎏土符图 233015 5阶 己方金属性、土属性卡牌防御值增加49点。额外+28点 满级增加329点
 *
 * @author longwh
 * @date 2023/3/1 14:34
 */
@Service
public class Runes233015 extends PlayerDefenceRune {
    @Override
    public int getRunesId() {
        return RunesEnum.LIU_TU_PLAYER_5.getRunesId();
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