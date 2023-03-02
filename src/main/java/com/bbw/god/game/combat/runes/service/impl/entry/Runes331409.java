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
 * 助威 战斗初始化时，敌方卡牌等级增加[3]级。
 *
 * @author: suhq
 * @date: 2022/9/23 5:24 下午
 */
@Service
public class Runes331409 implements IParamInitStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.ZHU_WEI_ENTRY.getRunesId();
    }

    @Override
    public void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam) {
        CombatBuff combatBuff = performInitParam.gainBuff(getRunesId());
        int addLevel = 3 * combatBuff.getLevel();
        for (CCardParam card : oppInitParam.getCards()) {
            if (card.getId() == CardEnum.LEADER_CARD.getCardId()) {
                continue;
            }
            card.setLv(card.getLv() + addLevel);
        }
    }
}
