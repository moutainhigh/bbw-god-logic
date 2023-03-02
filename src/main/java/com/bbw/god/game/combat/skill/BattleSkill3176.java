package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 痊愈 3176：每回合，回复我方召唤师所有已损失的血量。
 * 召唤师损失多少血量则回复多少血量，而非直接回复上限数值。
 *
 * @author longwh
 * @date 2023/2/9 11:05
 */
@Service
public class BattleSkill3176 extends BattleSkillService {

    @Override
    public int getMySkillId() {
        return CombatSkillEnum.QUAN_YU.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action ar = new Action();
        int zhsPos = PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId());
        CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), zhsPos);
        // 回复我方召唤师所有已损失的血量
        int lostHp = psp.getPerformPlayer().getBeginHp() - psp.getPerformPlayer().getHp();
        effect.setHp(lostHp);
        effect.setSequence(psp.getNextAnimationSeq());
        ar.addEffect(effect);
        return ar;
    }
}