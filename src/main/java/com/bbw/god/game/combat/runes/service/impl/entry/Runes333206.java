package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SectionSkills;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 墓守词条 我方卡牌的死亡技能将有[25]%概率无法发动。
 *
 * @author longwh
 * @date 2023/1/4 15:48
 */
@Service
public class Runes333206 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.MU_SHOU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        BattleCard performCard = param.getPerformCard();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(25, 0)) {
            return action;
        }
        // 非死亡技能不处理
        int[] skills = SectionSkills.DIE_SKILLS.getSkills();
        for (int skill : skills) {
            Optional<BattleSkill> dieSkill = performCard.getSkill(skill);
            if (!dieSkill.isPresent()) {
                continue;
            }
            // 存在死亡技能 则禁用技能一个回合
            BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getRunesId(), performCard.getPos());
            effect.forbidOneRound(dieSkill.get().getId(), dieSkill.get().getTimesLimit(), getRunesId());
            effect.setAttackPower(Effect.AttackPower.L3);
            action.addEffect(effect);
        }
        return action;
    }
}