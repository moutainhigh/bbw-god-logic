package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

/**
 * @author suhq
 * @description: 强水
 * @date 2019-12-31 14:13
 **/
@Service
public class SkillQiangS extends SkillQiangJMSHT {
    @Override
    public int getPerformId() {
        return CombatSkillEnum.QS.getValue();
    }

    @Override
    public TypeEnum getType() {
        return TypeEnum.Water;
    }
}
