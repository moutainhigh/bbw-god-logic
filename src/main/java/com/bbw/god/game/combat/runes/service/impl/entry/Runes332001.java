package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IParamInitStageRunes;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import org.springframework.stereotype.Service;

/**
 * 号令 我方卡牌等级增加[1]级。
 *
 * @author: suhq
 * @date: 2022/9/23 5:24 下午
 */
@Service
public class Runes332001 implements IParamInitStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.HAO_LING_ENTRY.getRunesId();
    }

    @Override
    public void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam) {
        CombatBuff combatBuff = performInitParam.gainBuff(getRunesId());
        int addLevel = 1 * combatBuff.getLevel();
        for (CCardParam card : performInitParam.getCards()) {
            if (card.getId() == CardEnum.LEADER_CARD.getCardId()) {
                continue;
            }
            card.setLv(card.getLv() + addLevel);
            CfgCardEntity cfgCard = CardTool.getCardById(card.getId());
            card.setAtk(CombatInitService.getAtk(cfgCard.getAttack(), card.getLv(), card.getHv()));
            card.setHp(CombatInitService.getHp(cfgCard.getHp(), card.getLv(), card.getHv()));
        }
    }
}
