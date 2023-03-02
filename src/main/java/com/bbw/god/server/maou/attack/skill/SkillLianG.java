package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 联攻
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillLianG implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        TypeEnum type = param.getPerformCard().getType();
        List<MaouAttackCard> attackCards = param.getAttackCards();
        Long typeSize = attackCards.stream().filter(tmp -> tmp.getType() == type).count();
        if (typeSize <= 1) {
            return new ArrayList<>();
        }
        int star = param.getPerformCard().getStars();
        int lv = param.getPerformCard().getLv();
        int hv = param.getPerformCard().getHv();
        Double ackBuf = (50 * star + 10 * lv) * (1 + 0.15 * hv);
        ackBuf = ackBuf * (typeSize - 1);
        List<SkillPerformResult> results = new ArrayList<>();
        SkillPerformResult result = SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), param.getPerformCard(), ackBuf.intValue());
        results.add(result);
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.LG.getValue();
    }
}
