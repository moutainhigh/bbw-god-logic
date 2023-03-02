package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 续航词条	每回合开始时，敌方召唤师恢复[2]%最大血量。
 *
 * @author: suhq
 * @date: 2022/9/22 11:57 上午
 */
@Service
public class Runes331104 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.XU_HANG_ENTRY.getRunesId();
    }


    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        Player performPlayer = param.getPerformPlayer();
        Player oppoPlayer = param.getOppoPlayer();
        //处理需要回复的血量
        CombatBuff combatBuff = performPlayer.gainBuff(getRunesId());
        double addHpRate = 0.02 * combatBuff.getLevel();
        int addHp = (int) (oppoPlayer.getMaxHp() * addHpRate);
        //构建效果
        int playerPos = PositionService.getZhaoHuanShiPos(oppoPlayer.getId());
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), playerPos);
        effect.setHp(addHp);
        effect.setSequence(param.getNextSeq());
        action.addEffect(effect);
        return action;
    }
}
