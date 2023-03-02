package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 强金、木、水、火、土
 * @date 2019-12-31 13:46
 **/
public abstract class SkillQiangJMSHT implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        final int hv = param.getPerformCard().getHv();
        TypeEnum type = getType();
        List<MaouAttackCard> attackCards = param.getAttackCards();
        List<SkillPerformResult> results = attackCards.stream()
                .filter(tmp -> tmp.getType() == type)
                .map(tmp -> SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), tmp, (80 + (24 * hv)) * tmp.getStars()))
                .collect(Collectors.toList());
        return results;
    }

    public abstract TypeEnum getType();
}
