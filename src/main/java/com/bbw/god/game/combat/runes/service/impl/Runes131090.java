package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自愈符31090  物理攻击开始前，己方全部卡牌施放一次自愈。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131090 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131090;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Player player=param.getPerformPlayer();
        //自愈 每回合恢复自身受到的所有非永久性伤害。
        Action ar = new Action();
        List<Effect> effects=new ArrayList<>();
        int seq=param.getNextSeq();
        for (BattleCard card:player.getPlayingCards()){
            if (card==null){
                continue;
            }
            int hp=card.getRoundHp()-card.getHp();
            hp = hp > card.getRoundHp() ? card.getRoundHp() : hp;
            if (hp <= 0) {
                // 没有可以恢复的血量则不发动
                continue;
            }
            CardValueEffect effect=CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setHp(hp);
            effect.setSequence(seq);
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
            effects.add(effect);
        }
        ar.addEffects(effects);
        return ar;
    }
}
