package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RuneAddSkillService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 自愈符图	3阶	每回合对战阶段前，有30%概率（可升级）对己方全体卡牌施放【自愈】。	每额外+7%概率
 *
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes231202 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.ZI_YU_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        Action ar = new Action();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        for (BattleCard card : player.getPlayingCards()) {
            if (card == null) {
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
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
            ar.addEffect(effect);
        }
        return ar;
    }
}
