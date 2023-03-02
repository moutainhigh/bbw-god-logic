package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 愈合 3178：每回合回复召唤师40%受损血量。每升一阶增加4%效果。
 *
 * @author longwh
 * @date 2023/2/9 10:15
 */
@Service
public class BattleSkill3178 extends BattleSkillService {

    @Override
    public int getMySkillId() {
        return 3177;
//        return CombatSkillEnum.YU_HE.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action ar = new Action();
        int zhsPos = PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId());
        CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), zhsPos);
        // 召唤师受损血量
        int lostHp = psp.getPerformPlayer().getMaxHp() - psp.getPerformPlayer().getHp();
        // 每回合回复召唤师40%受损血量。每升一阶增加4%效果
        int recoveryHp = (int) (lostHp * (0.4 + psp.getPerformCard().getHv() * 0.04));
        effect.setHp(recoveryHp);
        effect.setSequence(psp.getNextAnimationSeq());
        ar.addEffect(effect);
        return ar;
    }
}