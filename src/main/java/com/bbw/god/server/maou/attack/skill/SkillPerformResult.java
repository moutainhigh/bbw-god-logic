package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author suhq
 * @description: 技能生效结果
 * @date 2019-12-31 13:25
 **/
@Slf4j
@Data
public class SkillPerformResult {
    private MaouAttackCard performCard;
    private Integer performSkill;//发动的技能
    private MaouAttackCard effectedCard;//受影响的卡牌
    private Integer ackBuf;//攻击buf

    public static SkillPerformResult getInstance(MaouAttackCard performCard, Integer performSkill, MaouAttackCard effectedCard, Integer ackBuf) {
        SkillPerformResult result = new SkillPerformResult();
        result.setPerformCard(performCard);
        result.setPerformSkill(performSkill);
        result.setEffectedCard(effectedCard);
        result.setAckBuf(ackBuf);
        return result;
    }

    public static SkillPerformResult getInstance(Integer performSkill, MaouAttackCard effectedCard, Integer ackBuf) {
        SkillPerformResult result = new SkillPerformResult();
        result.setPerformSkill(performSkill);
        result.setEffectedCard(effectedCard);
        result.setAckBuf(ackBuf);
        return result;
    }

    public int gainAckBuf() {
        String skillName = CombatSkillEnum.fromValue(this.performSkill).getName();
        String cardName = CardTool.getCardById(this.effectedCard.getId()).getName();
//        System.out.println("生效技能：" + skillName + ",受影响卡牌：" + cardName + ",伤害：" + this.ackBuf);
        return this.ackBuf;
    }

}
