package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardFactory;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.card.CardDeifyCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.card.CfgDeifyCardEntity;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 幻化 1021：上场时，将自身移回手牌，随后在原先位置上召唤1张与施法者等级阶数相同的卡牌，召唤的卡牌从场上离开时将直接消失。
 */
@Service
public class BattleSkill1021 extends BattleSkillService {

    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private UserCardService userCardService;

    @Override
    public int getMySkillId() {
        return CombatSkillEnum.HUAN_HUA.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action action = new Action();
        BattleCard performCard = psp.getPerformCard();
        Player player = psp.getPerformPlayer();
        // 手牌是否已满
        if (player.handCardsIsFull()) {
            return action;
        }
        // 执行幻化-获取随机卡牌
        int oldPos = performCard.getPos();
        BattleCard romCard = getRandomCard(psp, oldPos, false);
        if (romCard == null) {
            return action;
        }
        Combat combat = psp.getCombat();
        // 执行幻化-初始化随机卡牌
        initRandomCard(player, combat, romCard, oldPos);
        // 原卡牌回到手牌
        int handCardIndex = player.addHandCard(psp.getPerformCard());
        int handPos = PositionService.getHandCardPos(player.getId(), handCardIndex);
        // 释放技能动画
        AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), oldPos);
        action.addClientAction(amin);
        // 卡牌回到手牌动画
        action.addClientAction(buildAnimationSequence(performCard, oldPos, handPos, psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_POSITION_CHANGE));
        // 幻化新卡牌动画
        action.addClientAction(buildAnimationSequence(romCard, oldPos, oldPos, psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_ADD));
        return action;
    }

    /**
     * 添加动画
     *
     * @param card
     * @param fromPos
     * @param toPos
     * @param nextAnimationSeq
     * @param resultType
     * @return
     */
    private AnimationSequence buildAnimationSequence(BattleCard card, int fromPos, int toPos, int nextAnimationSeq, Effect.EffectResultType resultType) {
        AnimationSequence as = new AnimationSequence(nextAnimationSeq, resultType);
        AnimationSequence.Animation animation = new AnimationSequence.Animation();
        animation.setPos1(fromPos);
        animation.setPos2(toPos);
        animation.setSkill(getMySkillId());
        if (Effect.EffectResultType.CARD_ADD.getValue() == resultType.getValue()) {
            animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
        }
        as.add(animation);
        return as;
    }

    /**
     * 初始化卡牌
     *
     * @param player
     * @param combat
     * @param card
     * @param oldPos
     * @return
     */
    public BattleCard initRandomCard(Player player, Combat combat, BattleCard card, int oldPos) {
        runesPerformService.runInitCardRunes(player, card);
        if (combat.getWxType() != null && combat.getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
            card.getSkills().removeIf(p -> p.getId() == CombatSkillEnum.FH.getValue() || p.getId() == CombatSkillEnum.HH.getValue() || p.getId() == CombatSkillEnum.FS.getValue());
        }
        card.setPos(oldPos);
        //初始化卡
        int battleIndex = PositionService.getBattleCardIndex(oldPos);
        player.getPlayingCards()[battleIndex] = card;
        battleCardService.replaceCard(player, card);
        return card;
    }

    /**
     * 获取随机卡牌
     *
     * @param psp
     * @param toPos
     * @param isUseUserSkill
     * @return
     */
    private BattleCard getRandomCard(PerformSkillParam psp, int toPos, boolean isUseUserSkill) {
        BattleCard performCard = psp.getPerformCard();
        Player player = psp.getPerformPlayer();
        List<Integer> excludes = psp.getMyPlayingCards(true).stream().map(BattleCard::getImgId).collect(Collectors.toList());
        excludes.addAll(player.getDiscard().stream().filter(Objects::nonNull).map(BattleCard::getImgId).collect(Collectors.toList()));
        for (BattleCard card : player.getHandCards()) {
            if (card != null) {
                excludes.add(card.getImgId());
            }
        }
        excludes.addAll(player.getDrawCards().stream().map(BattleCard::getImgId).collect(Collectors.toList()));

        boolean isFly = PositionService.isYunTaiPos(toPos);
        BattleCard battleCard = getRandomDeifyCard(isFly, excludes, performCard);
        if (battleCard != null && player.getUid() > 0) {
            UserCard userCard = userCardService.getUserCard(player.getUid(), battleCard.getImgId());
            if (userCard != null) {
                boolean scroll = userCard.ifUseSkillScroll();
                if (isUseUserSkill && scroll) {
                    updateCardSkills(player.getUid(), battleCard);
                }
            }
            return battleCard;
        }
        List<CfgCardEntity> cardEntities = CardTool.getAllCards();
        List<CfgCardEntity> randomCards = ListUtil.copyList(cardEntities, CfgCardEntity.class);
        randomCards = randomCards.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        BattleCard sumonCard = buildCard(isFly, randomCards, performCard);
        if (isUseUserSkill) {
            updateCardSkills(player.getUid(), sumonCard);
        }
        return sumonCard;
    }

    /**
     * 获得 封神卡牌
     *
     * @return
     */
    public BattleCard getRandomDeifyCard(boolean isFly, List<Integer> excludes, BattleCard performCard) {
        int hv = performCard.getHv();
        if (hv == 0) {
            return null;
        }
        boolean deifyCard = PowerRandom.hitProbability(50 * hv, 10000);
        if (!deifyCard) {
            return null;
        }
        List<CfgDeifyCardEntity> cards = CardTool.getAllDeifyCards();
        List<CfgDeifyCardEntity> collect = cards.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        List<CfgCardEntity> list = new ArrayList<>();
        for (CfgDeifyCardEntity deifyCardEntity : collect) {
            CfgCardEntity cardEntity = CfgCardEntity.instance(deifyCardEntity);
            CardDeifyCardParam param = CardTool.getPerfectDeifyCardSkills(cardEntity.getId());
            cardEntity.setZeroSkill(param.getSkills()[0]);
            cardEntity.setFiveSkill(param.getSkills()[1]);
            cardEntity.setTenSkill(param.getSkills()[2]);
            cardEntity.setPerfect(param.isChange() ? 1 : 0);
            list.add(cardEntity);
        }
        return buildCard(isFly, list, performCard);
    }

    private BattleCard buildCard(boolean isFly, List<CfgCardEntity> randomCards, BattleCard performCard) {
        if (isFly) {
            randomCards = randomCards.stream().filter(p -> p.getZeroSkill() == CombatSkillEnum.FX.getValue()
                            || p.getFiveSkill() == CombatSkillEnum.FX.getValue() || p.getTenSkill() == CombatSkillEnum.FX.getValue())
                    .collect(Collectors.toList());
        }
        if (randomCards.isEmpty()) {
            return null;
        }
        CfgCardEntity cfgCard = PowerRandom.getRandomFromList(randomCards);
        BattleCard hero = BattleCardFactory.buildCard(cfgCard, performCard.getLv(), performCard.getHv());
        return hero;
    }

    private void updateCardSkills(long performUid, BattleCard sumonCard) {
        boolean isPerformAi = performUid <= 0;
        if (isPerformAi) {
            return;
        }
        UserCard userCard = userCardService.getUserCard(performUid, sumonCard.getImgId());
        BattleCardFactory.updateCardSkills(sumonCard, userCard);
    }
}