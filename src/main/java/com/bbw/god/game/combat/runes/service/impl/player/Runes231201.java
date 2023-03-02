package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RuneAddSkillService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 灵动符图	3阶	每回合开始时，有30%概率（可升级）使己方场上1张卡牌在本回合获得【灵动】。	每级额外+7%概率
 *
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes231201 implements IRoundStageRunes {
    @Autowired
    private RuneAddSkillService runeAddSkillService;

    @Override
    public int getRunesId() {
        return RunesEnum.LING_DONG_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        //该回合所有卡牌的攻击力提高50%。
        Action ar = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        List<BattleCard> cards = param.getPerformPlayer().getPlayingCards(true);
        if (ListUtil.isEmpty(cards)) {
            return ar;
        }
        BattleCard targetCard = PowerRandom.getRandomFromList(cards);
        runeAddSkillService.addSkillTOCard(getRunesId(), targetCard, CombatSkillEnum.LINGD);
        AnimationSequence action = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), targetCard.getPos());
        ar.addClientAction(action);
        return ar;
    }


}
