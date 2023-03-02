package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import com.bbw.god.game.combat.runes.service.IRoundEndStageRunes;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 掩杀符图	2阶	战斗开始时，有30%概率（可升级）发动，对敌方召唤师造成最大生命值30%的伤害并降低血量上限。	每级额外+7%概率
 * 该符图的在所有血量增益结束后发动
 *
 * @author: suhq
 * @date: 2022/5/24 2:46 下午
 */
@Service
public class Runes231111 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.YAN_SHA_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return action;
        }
        if (param.getRound() != 1) {
            return action;
        }
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), param.getOppoPlayerPos());
        Player oppoPlayer = param.getOppoPlayer();
        effect.setRoundHp(-oppoPlayer.getMaxHp() * 30 / 100);
        action.addEffect(effect);
        return action;
    }

}
