package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.*;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatCardTools;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 幻术（3142）：只要场上有空位，每回合召唤一个与自身等阶等级的随机卡牌上场
 * 幻术说明：
 * ①.召唤的卡牌在全卡牌库中随机选，且不能是玩家卡组中的卡牌（避免重复）
 * ②.空位，包括云台。如召唤云台卡牌，必带飞行。
 * ③.召唤的卡牌为幻像，不是玩家真是拥有。与玉鼎真人同阶同级。战斗结束后即消失。战死也不能被回魂或封神
 * ④.召唤的卡牌不会发动上场技能（因为幻术的发动时间是在技能结算期）
 * <p>
 * 1、幻术，应有概率召唤到包含完整独有技能（专属技能）的封神卡。
 * 概率为：
 * 0阶：0%
 * 1阶：0.5%
 * 2阶：1%
 * 3阶：1.5%
 * 4阶：2%
 * 5阶：2.5%
 * 6阶：3%
 * 7阶：3.5%
 * 8阶：4%
 * 9阶：4.5%
 * 10阶 5%
 */
@Service
public class BattleSkill3142 extends BattleSkillService {
    private static final int SKILL_ID = CombatSkillEnum.HUAN_SHU.getValue();// 技能ID
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private UserCardService userCardService;

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action ar = new Action();
        ar.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos()));
        BattleCard battleCard = doSummonCard(ar, psp, false);
        if (null == battleCard) {
            ar.getClientActions().clear();
        }
        return ar;
    }

    /**
     * 召唤卡牌
     *
     * @param action
     * @param psp
     */
    public BattleCard doSummonCard(Action action, PerformSkillParam psp, boolean isuseUserSkill) {
        Player player = psp.getPerformPlayer();
        int toPos = getTargetPos(psp);
        if (toPos == -1) {
            return null;
        }
        BattleCard card = getRandomCard(psp, toPos, isuseUserSkill);
        if (card == null) {
            return null;
        }
        runesPerformService.runInitCardRunes(psp.getPerformPlayer(), card);
        if (psp.getCombat().getWxType() != null && psp.getCombat().getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
            card.getSkills().removeIf(p -> p.getId() == CombatSkillEnum.FH.getValue() || p.getId() == CombatSkillEnum.HH.getValue() || p.getId() == CombatSkillEnum.FS.getValue());
        }
        card.setPos(toPos);
        //初始化卡
        battleCardService.replaceCard(player, card);
        BattleCard performCard = psp.getPerformCard();
        AnimationSequence as = new AnimationSequence(psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_ADD);
        AnimationSequence.Animation animation = new AnimationSequence.Animation();
        animation.setPos1(performCard.getPos());
        animation.setPos2(toPos);
        animation.setSkill(getMySkillId());
        animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
        as.add(animation);
        action.addClientAction(as);
        return card;
    }

    /**
     * 获取目标位置
     *
     * @param psp
     * @return
     */
    private int getTargetPos(PerformSkillParam psp) {
        Player player = psp.getPerformPlayer();
        int[] emptyPos = player.getEmptyBattlePos(true);
        if (emptyPos.length == 0) {
            return -1;
        }

        int index = PowerRandom.getRandomBySeed(emptyPos.length) - 1;
        int toPos = emptyPos[index];
        return toPos;
    }

    /**
     * 获取随机卡牌
     *
     * @param psp
     * @param toPos
     * @return
     */
    private BattleCard getRandomCard(PerformSkillParam psp, int toPos, boolean isuseUserSkill) {
        BattleCard performCard = psp.getPerformCard();
        Player player = psp.getPerformPlayer();
        List<Integer> excludes = psp.getMyPlayingCards(true).stream().filter(p -> p != null).map(BattleCard::getImgId).collect(Collectors.toList());
        excludes.addAll(player.getDiscard().stream().filter(p -> p != null).map(BattleCard::getImgId).collect(Collectors.toList()));
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
                if (isuseUserSkill && scroll) {
                    updateCardSkills(player.getUid(), battleCard);
                }
            }
            return battleCard;
        }
        List<CfgCardEntity> cardEntities = CardTool.getAllCards();
        List<CfgCardEntity> randomCards = ListUtil.copyList(cardEntities, CfgCardEntity.class);
        randomCards = randomCards.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        BattleCard sumonCard = buildCard(isFly, randomCards, performCard);
        if (isuseUserSkill) {
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
        BattleCard hero = new BattleCard();
        hero.setId(-1000);
        Integer isUseSkillScroll = cfgCard.getPerfect() != null ? cfgCard.getPerfect() : 0;
        hero.setImgId(cfgCard.getId());
        hero.setStars(cfgCard.getStar());
        hero.setName(cfgCard.getName());
        hero.setType(TypeEnum.fromValue(cfgCard.getType()));
        hero.setHv(performCard.getHv());
        hero.setLv(performCard.getLv());
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
        Integer[] skillIds = {cfgCard.getZeroSkill(), cfgCard.getFiveSkill(), cfgCard.getTenSkill()};
        for (int i = 0; i < skillIds.length; i++) {
            Integer skillId = skillIds[i];
            if (null == skillId || 0 == skillId) {
                continue;
            }
            if (hero.getLv() < i * 5) {
                continue;
            }
            Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillId);
            if (!csOp.isPresent()) {
                continue;
            }
            CombatInitService.battleCardAddSKill(hero, skillId);
        }
        hero.setIsUseSkillScroll(isUseSkillScroll);
        return hero;
    }


    private void updateCardSkills(long performUid, BattleCard sumonCard) {
        boolean isPerformAi = performUid <= 0;
        if (isPerformAi) {
            return;
        }
        UserCard userCard = userCardService.getUserCard(performUid, sumonCard.getImgId());
        if (null == userCard) {
            return;
        }
        List<Integer> skillIds = userCard.gainActivedSkills();
        List<BattleSkill> battleSkills = new ArrayList<>();
        for (Integer skillId : skillIds) {
            Optional<CfgCardSkill> csOp = CardSkillTool.getCardSkillOpById(skillId);
            if (!csOp.isPresent()) {
                continue;
            }
            BattleSkill skill = BattleSkill.instanceBornSkill(csOp.get());
            battleSkills.add(skill);
        }
        sumonCard.setSkills(battleSkills);
        sumonCard.setIsUseSkillScroll(userCard.ifUseSkillScroll() ? 1 : 0);
    }
}
