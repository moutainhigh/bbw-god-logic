package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 妖族血脉（妖族召唤师每回合可增加2点法力值，受到的负面效果减少50%，如负面效果为死亡技能，则减少80%）
 * id 131005
 *
 * @author fzj
 * @date 2021/9/16 15:52
 */
@Service
public class Runes131005 {
    @Autowired
    GameUserService gameUserService;
    /** 负面效果技能，法宝ID */
    private static List<Integer> skills = Arrays.asList(1005, 1202, 3110, 4112, 4504, 4201, 2001, 350, 360);

    /**
     * 妖族召唤师每回合可增加2点法力值
     * @param firstPlayer
     */
    public void doRoundRunes(Player firstPlayer) {
        int maxMp = firstPlayer.getMaxMp();
        firstPlayer.setMaxMp(maxMp + 1);
    }

    /**
     * 受到的负面效果减少50%，如负面效果为死亡技能，则减少80%
     * @param param
     */
    public void doRoundRunes(CombatRunesParam param) {
        if (param.isEffectToEnemy()){
            return ;
        }
        CardValueEffect effect = (CardValueEffect) param.getReceiveEffect().get(0);
        int reduce = 100;
        if (param.getPerformPlayer().isOwnYZXM()){
            reduce = 50;
        }
        if (skills.contains(effect.getSourceID())){
            if (effect.getSourceID() == skills.get(1)){
                reduce = 20;
            }
            effect.setRoundMp(effect.getRoundMp() * reduce/100);
            effect.setHp(effect.getHp() * reduce/100);
            effect.setAtk(effect.getAtk() * reduce/100);
        }
    }
}
