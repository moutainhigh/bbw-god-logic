package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 自毙符 131270  敌方卡牌在释放伤害类法术时，施法者本身会受到等量伤害。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131270 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131270;
    }


    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar=new Action();
        if (ListUtil.isEmpty(param.getReceiveEffect())){
            return ar;
        }
        int hp=0;
        int roundHp=0;
        int seq=0;
        for (Effect effect:param.getReceiveEffect()){
            if (effect.getSourceID()<4000 && effect.isValueEffect() && !isPerformSelf(effect.getSourcePos(),param.getPerformPlayer().getId())){
                CardValueEffect valueEffect=effect.toValueEffect();
                hp+=valueEffect.getHp();
                roundHp+=valueEffect.getRoundHp();
                seq=valueEffect.getSequence();
            }
        }
        if (hp<0 || roundHp<0){
            int fromPos=param.getReceiveEffect().get(0).getSourcePos();
            CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),fromPos);
            valueEffect.setRoundHp(roundHp);
            valueEffect.setHp(hp);
            valueEffect.setSequence(seq);
            ar.addEffect(valueEffect);
         //   ar.addClientAction(ClientAnimationService.getSkillAction(param.getNextSeq(),getRunesId(),param.getMyPlayerPos(),fromPos));
        }
        return ar;
    }
}
