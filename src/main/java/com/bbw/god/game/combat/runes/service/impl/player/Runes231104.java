package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.impl.Runes131500;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 鼓舞符图	2阶	每回合开始时，有30%概率（可升级）对己方场上1张卡牌施放【鼓舞】。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/12 5:16 下午
 */
@Service
public class Runes231104 implements IRoundStageRunes {
    @Autowired
    private Runes131500 runes131500;

    @Override
    public int getRunesId() {
        return RunesEnum.GU_WU_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        return runes131500.doRoundRunes(param);
    }

}
