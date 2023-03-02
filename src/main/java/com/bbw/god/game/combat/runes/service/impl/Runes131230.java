package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatCardTools;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.pve.CombatPVEInitService;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 鬼画符 131230  每回合物理攻击开始前，敌方一张场上卡牌随机变成0级鬼兵。（含云台）
 * 不会变幻鬼兵
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131230 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return 131230;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        List<BattleCard> playingCards=param.getOppoPlayer().getPlayingCards(true, Arrays.asList(424));
        if (playingCards.isEmpty()){
            return ar;
        }
        BattleCard targetCard= PowerRandom.getRandomFromList(playingCards);
        BattleCard card=getNewCard(targetCard,param.getOppoPlayer().cardInitId());
        //初始化卡
        battleCardService.replaceCard(param.getOppoPlayer(),card);
        ar.addClientAction(ClientAnimationService.getSkillAction(param.getNextSeq(),getRunesId(),param.getMyPlayerPos(),targetCard.getPos()));
        AnimationSequence as = new AnimationSequence(param.getNextSeq(), Effect.EffectResultType.CARD_ADD);
        AnimationSequence.Animation action = new AnimationSequence.Animation();
        action.setPos1(param.getMyPlayerPos());
        action.setPos2(card.getPos());
        action.setSkill(getRunesId());
        action.setCards(CombatCardTools.getCardStr(card,"",card.getPos()));
        as.add(action);
        ar.addClientAction(as);
        ar.setTakeEffect(true);
        return ar;
    }

    private BattleCard getNewCard(BattleCard card, int id) {
        // 0级0阶级 424 鬼兵
        CCardParam bcip = CCardParam.init(424, 0, 0,null);
        BattleCard ncard = CombatPVEInitService.initBattleCard(bcip, id);
        ncard.setPos(card.getPos());
        return ncard;
    }
}
