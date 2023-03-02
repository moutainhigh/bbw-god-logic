package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 净化 3174：每回合，对我方全体卡牌施放【巫医】。
 *
 * @author longwh
 * @date 2023/1/12 15:41
 */
@Service
public class BattleSkill3174 extends BattleSkillService {
    @Autowired
    private BattleSkill3141 battleSkill3141;

    @Override
    public int getMySkillId() {
        return 3174;
//        return CombatSkillEnum.JING_HUA.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action ar = new Action();
        BattleCard card = psp.getPerformCard();
        if (card == null) {
            return ar;
        }
        List<BattleCard> cards = psp.getMyPlayingCards(true);
        if (ListUtil.isEmpty(cards)) {
            return ar;
        }
        int seq = psp.getNextAnimationSeq();
        //每回合随机回复1张卡牌受到的永久伤害（恢复到卡牌初始攻防*阵位加成），5阶回复受到的全部伤害（恢复到存活期间最高永久血量）。10阶回复所有攻防（恢复到存活期间最高的攻防）。
        int hv = card.getHv();
        if (hv < 5) {
            for (BattleCard battleCard : cards) {
                battleSkill3141.attackHv(battleCard, seq, ar);
            }
        } else if (hv < 10) {
            for (BattleCard battleCard : cards) {
                battleSkill3141.attack5Hv(battleCard, seq, ar);
            }
        } else {
            for (BattleCard battleCard : cards) {
                battleSkill3141.attack10Hv(battleCard, seq, ar);
            }
        }
        return ar;
    }
}