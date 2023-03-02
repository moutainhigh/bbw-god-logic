package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 强攻词条 敌方卡牌的属性克制效果增加[15]%。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes331207 implements IRoundStageRunes {
    private static List<Integer> XIANG_KE_SKILLS = Arrays.asList(
            CombatSkillEnum.JKM.getValue(),
            CombatSkillEnum.MKT.getValue(),
            CombatSkillEnum.TKS.getValue(),
            CombatSkillEnum.SKF.getValue(),
            CombatSkillEnum.HKJ.getValue()
    );

    @Override
    public int getRunesId() {
        return RunesEnum.QIANG_GONG_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        action.setNeedAddAnimation(false);
        if (ListUtil.isEmpty(param.getReceiveEffect())) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int seq = param.getNextSeq();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            boolean isXiangKe = XIANG_KE_SKILLS.contains(skillId);
            if (!isXiangKe) {
                continue;
            }
            if (isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            double rate = 0.15 * combatBuff.getLevel();
            int sourceIndex = PositionService.getBattleCardIndex(effect.getSourcePos());
            BattleCard sourceCard = param.getOppoPlayer().getPlayingCards(sourceIndex);
            int addAtk = (int) (sourceCard.getAtk() * rate);
            CardValueEffect ve = effect.toValueEffect();
            ve.setAtk(ve.getAtk() + addAtk);
        }
        return action;
    }
}
