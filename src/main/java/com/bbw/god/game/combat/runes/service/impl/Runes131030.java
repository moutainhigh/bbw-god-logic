package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 天残符31030 敌方召唤师每回合损失最高体力20%的血量
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131030 implements IRoundStageRunes {

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action=new Action();
        Player player=param.getOppoPlayer();
        int hp=getInt(player.getBeginHp()*0.2f);
        CardValueEffect effect=CardValueEffect.getSkillEffect(getRunesId(), PositionService.getZhaoHuanShiPos(param.getOppoPlayer().getId()));
        effect.setHp(-hp);
        action.addEffect(effect);
        return action;
    }

    @Override
    public int getRunesId() {
        return 131030;
    }
}
