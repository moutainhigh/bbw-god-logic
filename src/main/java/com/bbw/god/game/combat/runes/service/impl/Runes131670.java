package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 军师符131670紫：己方所有阵位获得军师位效果。
 * ①　给除了军师位以外的阵位附加军师位效果。
 *
 * @author: suhq
 * @date: 2021/9/29 2:42 下午
 */
@Service
public class Runes131670 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JUN_SHI.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        return doRoundRunes(param, getRunesId());
    }

    public Action doRoundRunes(CombatRunesParam param, int runeId) {
        Action action = new Action();
        int seq = param.getNextSeq();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
                continue;
            }
            if (!isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            if (PositionService.isJunShiPos(effect.getTargetPos())) {
                continue;
            }
            if (effect.getResultType() != Effect.EffectResultType.CARD_VALUE_CHANGE){
                continue;
            }
            CardValueEffect ve = effect.toValueEffect();
            ve.setHp((int) (ve.getHp() * 1.5));
            ve.setRoundHp((int) (ve.getRoundHp() * 1.5));
            action.setTakeEffect(true);
            action.addClientAction(ClientAnimationService.getSkillAction(seq, runeId, param.getMyPlayerPos(), effect.getSourcePos()));
        }
//        AnimationSequence action= ClientAnimationService.getSkillAction(seq, getRunesId(), PositionService.getZhaoHuanShiPos(param.getPerformPlayer().getId()));
//        ar.addClientAction(action);
        return action;
    }

    private boolean check(int skillId) {
        SkillSection deploySection = SkillSection.getDeploySection();// 上场技能
        SkillSection skillSection = SkillSection.getSkillAttackSection();// 攻击技能
        SkillSection fightBackSection = SkillSection.getFightBackSection();
        return deploySection.contains(skillId) || skillSection.contains(skillId) || fightBackSection.contains(skillId);
    }
}
