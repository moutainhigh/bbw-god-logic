package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.skill.BattleSkillSeriesTable;
import org.springframework.stereotype.Service;

/**
 * 避雷符 131380  敌方的闪电系伤害最高值为200。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131380 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131380;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar=new Action();
        if (ListUtil.isEmpty(param.getReceiveEffect())){
            return ar;
        }
        int toPos=-1;
        for (Effect effect:param.getReceiveEffect()){
            if (check(effect.getPerformSkillID()) && !isPerformSelf(effect.getSourcePos(),param.getPerformPlayer().getId())){
                CardValueEffect valueEffect=effect.toValueEffect();
                if (valueEffect.getHp()<-200){
                    valueEffect.setHp(-200);
                }
                if (valueEffect.getRoundHp()<-200){
                    valueEffect.setRoundHp(-200);
                }
                ar.setTakeEffect(true);
                ar.addClientAction(ClientAnimationService.getSkillAction(param.getNextSeq(),getRunesId(),param.getMyPlayerPos(),toPos));
            }
        }
        return ar;
    }

    private boolean check(int skillId){
        for (int id : BattleSkillSeriesTable.LEI_SERIES) {
            if (id == skillId) {
                return true;
            }
        }
        return false;
    }

}
