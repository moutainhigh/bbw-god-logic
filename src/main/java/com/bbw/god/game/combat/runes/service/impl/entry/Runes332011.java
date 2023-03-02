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
 * 调理词条	每回合开始时，我方召唤师恢复[5]%当前血量。
 *
 * @author: suhq
 * @date: 2022/9/22 11:57 上午
 */
@Service
public class Runes332011 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.TIAO_LI_ENTRY.getRunesId();
    }


    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        Player performPlayer = param.getPerformPlayer();
        //处理需要恢复的血量
        CombatBuff combatBuff = performPlayer.gainBuff(getRunesId());
        double addRate = 0.05 * combatBuff.getLevel();
        int addHp = (int) (performPlayer.getHp() * addRate);
        //构建效果
        int playerPos = PositionService.getZhaoHuanShiPos(performPlayer.getId());
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), playerPos);
        effect.setHp(addHp);
        effect.setSequence(param.getNextSeq());
        action.addEffect(effect);
        return action;
    }
}
