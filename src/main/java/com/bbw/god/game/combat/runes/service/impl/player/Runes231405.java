package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundEndStageRunes;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 召魂符图	5阶	每回合开始时，有30%概率（可升级）召唤鬼兵填满我方阵位。回合结束后，鬼兵消失。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/25 10:21 上午
 */
@Service
public class Runes231405 implements IRoundStageRunes, IRoundEndStageRunes {
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;

    @Override
    public int getRunesId() {
        return RunesEnum.ZHAO_HUN_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        Action ar = new Action();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        int[] emptyBattlePos = player.getEmptyBattlePos(true);
        if (emptyBattlePos.length == 0) {
            return ar;
        }

        AnimationSequence as = ClientAnimationService.getSkillAction(0, getRunesId(), param.getMyPlayerPos());
        ar.addClientAction(as);

        for (int targetPos : emptyBattlePos) {
            doSummonCard(player, targetPos, ar);
        }
        return ar;
    }

    @Override
    public Action doRoundEndRunes(CombatRunesParam param) {
        Action ar = new Action();
        Player performPlayer = param.getPerformPlayer();
        List<BattleCard> playingCards = performPlayer.getPlayingCards(true);
        if (ListUtil.isEmpty(playingCards)) {
            return ar;
        }
        List<BattleCard> cardsToRemove = playingCards.stream().filter(tmp -> -CardEnum.GUI_BING.getCardId() == tmp.getId()).collect(Collectors.toList());
        if (ListUtil.isEmpty(cardsToRemove)) {
            return ar;
        }
        //移除
        for (BattleCard card : cardsToRemove) {
            CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(0, card.getPos());
            effect.setSequence(param.getNextSeq());
            effect.moveTo(PositionType.DEGENERATOR);
            ar.addEffect(effect);
        }
        return ar;
    }

    /**
     * 召唤卡牌
     *
     * @param player
     * @param targetPos
     * @param action
     * @return
     */
    public BattleCard doSummonCard(Player player, int targetPos, Action action) {

        BattleCard card = buildCard();
        runesPerformService.runInitCardRunes(player, card);
        card.setPos(targetPos);
        //初始化卡
        battleCardService.replaceCard(player, card);
        AnimationSequence as = new AnimationSequence(0, Effect.EffectResultType.CARD_ADD);
        AnimationSequence.Animation animation = new AnimationSequence.Animation();
        int playerPos = PositionService.getZhaoHuanShiPos(player.getId());
        animation.setPos1(playerPos);
        animation.setPos2(targetPos);
        animation.setSkill(getRunesId());
        animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
        as.add(animation);
        action.addClientAction(as);
        return card;
    }


    /**
     * 构建召唤的卡牌
     *
     * @return
     */
    private BattleCard buildCard() {
        CfgCardEntity cfgCard = CardTool.getCardById(CardEnum.GUI_BING.getCardId());
        BattleCard hero = new BattleCard();
        hero.setId(-cfgCard.getId());
        hero.setImgId(cfgCard.getId());
        hero.setStars(cfgCard.getStar());
        hero.setName(cfgCard.getName());
        hero.setType(TypeEnum.fromValue(cfgCard.getType()));
        hero.setHv(0);
        hero.setLv(0);
        if (null != cfgCard.getGroup()) {
            hero.setGroupId(cfgCard.getGroup());
        }
        int initAtk = CombatInitService.getAtk(cfgCard.getAttack(), hero.getLv(), hero.getHv());
        int initHp = CombatInitService.getHp(cfgCard.getHp(), hero.getLv(), hero.getHv());
        hero.setInitAtk(initAtk);
        hero.setInitHp(initHp);
        hero.setRoundAtk(initAtk);
        hero.setRoundHp(initHp);
        hero.setAtk(initAtk);
        hero.setHp(initHp);
        //0级鬼兵无技能
        return hero;
    }
}
