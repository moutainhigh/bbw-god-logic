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
 * 震慑符 131510  每回合开始时，随机对敌方一张卡牌释放震慑（含云台）。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131510 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131510;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        // 每回合可随机指定敌方场上1张卡牌（含云台），该回合卡牌的攻击力减半
        Action ar = new Action();
        List<BattleCard> cards=param.getOppoPlayer().getPlayingCards(true);
        if (ListUtil.isEmpty(cards)){
            return ar;
        }
        BattleCard targetCard= PowerRandom.getRandomFromList(cards);
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
        effect.setAtk(-getInt(targetCard.getAtk()*0.5f));
        effect.setSequence(param.getNextSeq());
        ar.addEffect(effect);
        return ar;
    }

}
