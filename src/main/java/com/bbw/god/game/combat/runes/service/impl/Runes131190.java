package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 克火符 131190 己方全体卡牌受到火系法术的伤害减半。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131190 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131190;
    }
    private static int[] skillIds={CombatSkillEnum.YH.getValue(),CombatSkillEnum.Sheng4H.getValue(),CombatSkillEnum.HUO_QIU.getValue()};

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action=new Action();
        if (ListUtil.isEmpty(param.getReceiveEffect())){
            return action;
        }
        int seq=param.getNextSeq();
        for (Effect effect:param.getReceiveEffect()){
            int skillId=effect.getPerformSkillID();
            if (check(skillId) && isPerformSelf(effect.getTargetPos(),param.getPerformPlayer().getId())){
                CardValueEffect valueEffect=effect.toValueEffect();
                valueEffect.setHp(valueEffect.getHp()/2);
                valueEffect.setRoundHp(valueEffect.getRoundHp()/2);
                action.setTakeEffect(true);
                action.addClientAction(ClientAnimationService.getSkillAction(seq,getRunesId(),param.getMyPlayerPos(),effect.getSourcePos()));
            }
        }
        return action;
    }
    private boolean check(int skillId){
        for (int id:skillIds){
            if (id==skillId){
                return true;
            }
        }
        return false;
    }
}
