package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.impl.Runes131680;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 不动符图	4阶	战斗开始时，有30%概率（可升级）发动，己方全体卡牌不会受到【威风】、【斥退】、【落羽】的效果影响。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes231301 extends PlayerStatusRune {
    @Autowired
    private Runes131680 runes131680;

    @Override
    public int getRunesId() {
        return RunesEnum.BU_DONG_PLAYER.getRunesId();
    }

    @Override
    Action handleStatus(CombatRunesParam param) {
        return runes131680.doRoundRunes(param);
    }
}
