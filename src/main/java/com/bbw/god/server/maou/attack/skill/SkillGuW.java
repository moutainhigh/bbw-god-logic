package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 鼓舞
 *  * 鼓舞:每回合随机选择我方场上1张卡牌，该回合卡牌的攻击力提高50%。每升三阶，可多鼓舞1张卡牌。（最多4张）
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillGuW implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        int num = param.getPerformCard().getHv() / 3 + 1;
        num = Math.min(num, 4);
        List<MaouAttackCard> fromList = param.getRandomFromList(num);
        List<SkillPerformResult> results = new ArrayList<>();
        for (MaouAttackCard attackCard : fromList) {
            SkillPerformResult result = SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), attackCard, attackCard.getAtk()*50/100);
            results.add(result);
        }
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.GU_WU.getValue();
    }
}
