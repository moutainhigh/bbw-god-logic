package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 鼓舞符 131500  每回合开始时，随机对己方一张卡牌释放鼓舞
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131500 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131500;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        // 每回合随机选择我方场上1张卡牌，该回合卡牌的攻击力提高50%。
        Action ar = new Action();
        List<BattleCard> cards=param.getPerformPlayer().getPlayingCards(true);
        if (ListUtil.isEmpty(cards)){
            return ar;
        }
        BattleCard targetCard=PowerRandom.getRandomFromList(cards);
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
        effect.setAtk(getInt(targetCard.getAtk()*0.5f));
        effect.setSequence(param.getNextSeq());
        ar.addEffect(effect);
        return ar;
    }

}
