package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatCardTools;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardFactory;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 残影	我方卡牌进入坟场后，有[2.5]%概率在原先位置上召唤1张0阶0级的随机卡牌，该卡牌从场上离开时消失。
 *
 * @author: suhq
 * @date: 2022/9/23 3:43 下午
 */
@Service
public class Runes331403 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;

    @Override
    public int getRunesId() {
        return RunesEnum.CAN_YING_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        //敌方不处理
        if (param.isEnemyTargetCard()) {
            return action;
        }
        boolean isSummoned = param.getTargetCard().getId() < 0;
        if (isSummoned) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int rate = (int) 2.5 * combatBuff.getLevel();
        if (!PowerRandom.hitProbability(rate)) {
            return action;
        }
        AnimationSequence as = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), param.getMyPlayerPos());
        action.addClientAction(as);
        int targetPos = param.getCardSourcePos();
        doSummonCard(param.getPerformPlayer(), targetPos, param.getNextSeq(), action);
        return action;
    }

    /**
     * 召唤卡牌
     *
     * @param player
     * @param targetPos
     * @param action
     * @return
     */
    public BattleCard doSummonCard(Player player, int targetPos, int seq, Action action) {
        CfgCardEntity randomCard = CardTool.getRandomCard();
        BattleCard card = BattleCardFactory.buildCard(randomCard, 0, 0);
        runesPerformService.runInitCardRunes(player, card);
        card.setPos(targetPos);
        //初始化卡
        battleCardService.replaceCard(player, card);
        AnimationSequence as = new AnimationSequence(seq, Effect.EffectResultType.CARD_ADD);
        AnimationSequence.Animation animation = new AnimationSequence.Animation();
        int playerPos = PositionService.getZhaoHuanShiPos(player.getId());
        animation.setPos1(playerPos);
        animation.setPos2(targetPos);
        animation.setSkill(getRunesId());
        animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
        as.add(animation);
        action.addClientAction(as);
        action.setTakeEffect(true);
        return card;
    }
}
