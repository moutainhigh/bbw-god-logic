package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.config.card.CardEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 普渡 1022：上场时，使我方坟场除鬼道士及鬼兵以外的卡牌回到牌库。
 *
 * @author longwh
 * @date 2023/1/12 15:29
 */
@Service
public class BattleSkill1022 extends BattleSkillService {

    @Override
    public int getMySkillId() {
        return 1022;
//        return CombatSkillEnum.PU_DU.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action action = new Action();
        // 获取指定的坟场卡牌
        List<BattleCard> appointDiscards = getAppointDiscards(psp.getPerformPlayer());
        for (BattleCard battleCard : appointDiscards) {
            // 构建拉回牌库的效果
            CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getMySkillId(), battleCard.getPos());
            effect.moveTo(PositionType.DRAWCARD);
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
            effect.setSequence(psp.getNextAnimationSeq());
            action.addEffect(effect);
        }
        return action;
    }

    /**
     * 获取指定坟场卡牌
     *
     * @param player
     * @return
     */
    private List<BattleCard> getAppointDiscards(Player player){
        // 过滤 鬼兵、鬼道士
        return player.getDiscard().stream().filter(card ->
                card.getId() != CardEnum.GUI_BING.getCardId()
//                card.getId() != CardEnum.GUI_DAO_SHI.getCardId()
        ).collect(Collectors.toList());
    }
}