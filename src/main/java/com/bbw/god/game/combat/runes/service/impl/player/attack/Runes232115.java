package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 枝水符图 232115 5阶 己方木属性、水属性卡牌攻击值增加49点。额外+28点 满级增加329点
 *
 * @author longwh
 * @date 2023/3/1 12:01
 */
@Service
public class Runes232115 extends PlayerAttackRune {
    @Override
    public int getRunesId() {
        return RunesEnum.ZHI_SHUI_PLAYER_5.getRunesId();
    }

    @Override
    boolean hasMultipleTypes() {
        return true;
    }

    @Override
    List<TypeEnum> getCardTypes() {
        List<TypeEnum> typeEnums = new ArrayList<>();
        typeEnums.add(TypeEnum.Wood);
        typeEnums.add(TypeEnum.Water);
        return typeEnums;
    }
}