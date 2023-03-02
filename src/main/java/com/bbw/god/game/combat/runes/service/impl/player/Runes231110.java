package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RuneAddSkillService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 无相符图	2阶	己方卡牌受到攻击时，有30%概率（可升级）使该卡牌在本回合获得【无相】。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/24 2:46 下午
 */
@Service
public class Runes231110 implements IRoundStageRunes {
    @Autowired
    private RuneAddSkillService runeAddSkillService;

    @Override
    public int getRunesId() {
        return RunesEnum.WU_XIANG_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (null == combatBuff) {
            return action;
        }
        for (BattleCard card : player.getPlayingCards()) {
            if (card == null) {
                continue;
            }
            if (combatBuff.ifToPerform(30, 7)) {
                runeAddSkillService.addSkillTOCard(getRunesId(), card, CombatSkillEnum.WX);
            }
        }
        return action;
    }
}
