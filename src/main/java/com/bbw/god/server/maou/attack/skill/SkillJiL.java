package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * 激励（3156）：每回合随机将我方场上随机一张卡牌攻防翻倍。(可能为自身)
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillJiL implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        MaouAttackCard fromList = param.getRandomFromList();
        List<SkillPerformResult> results = new ArrayList<>();
        SkillPerformResult result = SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), fromList, fromList.getAtk());
        results.add(result);
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.JI_LI.getValue();
    }
}
