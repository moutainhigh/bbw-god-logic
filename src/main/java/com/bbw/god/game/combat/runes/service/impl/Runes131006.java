package com.bbw.god.game.combat.runes.service.impl;


import com.bbw.god.game.combat.data.param.CombatPVEParam;
import org.springframework.stereotype.Service;

/**
 * 妖族来犯天道轮回（战本体时，所有神将属性减少30%。）
 *
 * id 131006
 * @author fzj
 * @date 2021/9/16 15:25
 */
@Service
public class Runes131006 {

    /**
     * 所有神将属性减少30%
     * @param param
     */
    public void doInitRunes(CombatPVEParam param) {
        param.setCardDisparityAtk(-0.15);
        param.setCardDisparityHp(-0.15);
    }

}
