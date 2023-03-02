package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 星术 我方军师位卡牌造成的技能伤害增加[10]%。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes332008 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.XING_SHU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int seq = param.getNextSeq();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
                continue;
            }
            //军师位才有加成
            if (!PositionService.isJunShiPos(effect.getSourcePos())) {
                continue;
            }
            //伤害才有加成
            if (!effect.isValueEffect()) {
                continue;
            }
            if (isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            double rate = 0.1 * combatBuff.getLevel();
            CardValueEffect ve = effect.toValueEffect();
            ve.setHp((int) (ve.getHp() * (1 + rate)));
            ve.setRoundHp((int) (ve.getRoundHp() * (1 + rate)));
            action.setTakeEffect(true);
            action.addClientAction(ClientAnimationService.getSkillAction(seq, getRunesId(), param.getMyPlayerPos(), effect.getSourcePos()));
        }
        return action;
    }

    private boolean check(int skillId) {
        SkillSection deploySection = SkillSection.getDeploySection();// 上场技能
        SkillSection skillSection = SkillSection.getSkillAttackSection();// 攻击技能
        SkillSection fightBackSection = SkillSection.getFightBackSection();
        return deploySection.contains(skillId) || skillSection.contains(skillId) || fightBackSection.contains(skillId);
    }
}
