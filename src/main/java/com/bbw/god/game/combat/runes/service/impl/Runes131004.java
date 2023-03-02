package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 鬼蜮伎俩
 * 守军召唤师受到的负面效果减少50%
 * @author fzj
 * @date 2021/8/24 9:03
 */
@Service
public class Runes131004 {
    @Autowired
    GameUserService gameUserService;
    /** 负面效果技能，法宝ID */
    private static List<Integer> skills = Arrays.asList(1005,1202,3110,4112,4504,4201,2001,350,360);

    public void doRoundRunes(CombatRunesParam param) {
        if (param.isEffectToEnemy()){
            return ;
        }
        CardValueEffect effect = (CardValueEffect) param.getReceiveEffect().get(0);
        int reduce = 100;
        if (param.getPerformPlayer().isOwnGYJL()){
            reduce = 50;
        }
        if (skills.contains(effect.getSourceID())){
            effect.setRoundMp(effect.getRoundMp() * reduce/100);
            effect.setHp(effect.getHp() * reduce/100);
            effect.setAtk(effect.getAtk() * reduce/100);
        }
    }

}
