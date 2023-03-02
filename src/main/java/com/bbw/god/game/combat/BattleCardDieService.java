package com.bbw.god.game.combat;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.nskill.BattleSkill6004;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月10日 下午4:56:45 类说明 卡牌死亡处理: 死亡-》移入坟场/异次元
 */
@Service
@Slf4j
public class BattleCardDieService {
    @Autowired
    private AcceptEffectService acceptEffectService;
    @Autowired
    private SectionSkillService sectionSkillService;
    @Autowired
    private SkillRoundService skillRoundService;
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private BattleSkill6004 battleSkill6004;

    /**
     * 卡牌死亡处理（仍然处于战场位置）
     */
    public void runBattleCardDieEvent(PerformSkillParam dieSkillParams) {
        runDieAfterDieSectionSkillAll(dieSkillParams);
        runBattleCardDieAndMovePosition(dieSkillParams);
    }

    /**
     * 卡牌死亡处理（仍然处于战场位置）
     */
    public void runBattleCardDieEvent(PerformSkillParam dieSkillParams, Effect effect) {
        if (dieSkillParams.getPerformCard() == null) {
            return;
        }
        if (effect.getResultType() == EffectResultType.CARD_POSITION_CHANGE) {
            dieSkillParams.getPerformCard().setAlive(false);
        }
        runDieAfterDieSectionSkillAll(dieSkillParams);
        if (effect.getResultType() == EffectResultType.CARD_POSITION_CHANGE
                && dieSkillParams.getReceiveEffect() != null) {
            // 本身为移动死亡
            //来源卡带有绝仙移动到异次元
            boolean isHasJueXian = battleSkill6004.isHasJueXian(dieSkillParams);
            if (isHasJueXian) {
                CardPositionEffect cardPositionEffect = (CardPositionEffect) effect;
                cardPositionEffect.setToPositionType(PositionType.DEGENERATOR);
            }
            acceptEffectService.acceptEffect(dieSkillParams.getCombat(), effect);
            runMoveAfterDieSectionSkillAll(dieSkillParams, new ArrayList<Integer>(), true, effect.getSourcePos());
            dieSkillParams.setReceiveEffect(null);
            return;
        }
        runBattleCardDieAndMovePosition(dieSkillParams);
    }

    /**
     * 卡牌死亡处理结束并开始移动位置 <br>
     * 战场=》坟场/异次元
     */
    private void runBattleCardDieAndMovePosition(PerformSkillParam dieSkillParams) {
        BattleCard runCard = dieSkillParams.getPerformCard();
        if (null == runCard || !runCard.isKilled()) {
            return;
        }
        if (!runCard.isKilled()) {
            // 执行完死亡技能 需要二次确认 是否死亡，有可能卡牌使用了长生
            return;
        }
        // 卡牌移动到坟场
        int battlePos = runCard.getPos();
        Optional<BattleCard> targetCardOp = PositionService.getCard(dieSkillParams.getPerformPlayer(), battlePos);
        if (targetCardOp.isPresent()) {
            BattleCard targetCard = targetCardOp.get();
            dieSkillParams.updateSeriousInjuryStatus();
            CardPositionEffect myDieEffect = CardPositionEffect
                    .getSkillEffectToTargetPos(CombatSkillEnum.MOVE.getValue(), battlePos);
            myDieEffect.setSourcePos(battlePos);
            if (dieSkillParams.getReceiveEffect() != null) {
                myDieEffect.setFromCardId(dieSkillParams.getReceiveEffect().getFromCardId());
            }

            Optional<BattleCard> optional = dieSkillParams.getEffectSourceCard();
            //带诛仙buff
            boolean isZhuXianBuff = optional.isPresent() && optional.get().hasEffect(CombatSkillEnum.ZHU_XIAN);
            //绝仙
            boolean isHasJueXian = battleSkill6004.isHasJueXian(dieSkillParams);
            if (targetCard.getId() < 0 || isZhuXianBuff || isHasJueXian) {
                int toPos = battleCardService.getEmptyPos(dieSkillParams.getPerformPlayer(), PositionType.DEGENERATOR);
                myDieEffect.moveTo(PositionType.DEGENERATOR, toPos);
            } else {
                int toPos = battleCardService.getEmptyPos(dieSkillParams.getPerformPlayer(), PositionType.DISCARD);
                myDieEffect.moveTo(PositionType.DISCARD, toPos);
            }
            myDieEffect.setSequence(dieSkillParams.getCombat().getAnimationSeq());
            List<Integer> banSkillIds = targetCard.getBanSkills();
            acceptEffectService.acceptEffect(dieSkillParams.getCombat(), myDieEffect);
            // 必须在执行死亡技能前获取，否则可能被复活等技能移动位置
            runMoveAfterDieSectionSkillAll(dieSkillParams, banSkillIds, true, battlePos);
            dieSkillParams.setReceiveEffect(null);
        }
    }

    /**
     * 执行死亡后的死亡技能，此时还未发生死亡位移
     */
    private void runDieAfterDieSectionSkillAll(PerformSkillParam dieSkillParams) {
        if (dieSkillParams.getReceiveEffectSkillId() != null) {
            SkillSection section = SkillSection.getZhongDuEffectSection();
            if (section.contains(dieSkillParams.getReceiveEffectSkillId())) {
                try {
                    long uid = dieSkillParams.getOppoPlayer().getUid();
                    FightAchievementCache cache = TimeLimitCacheUtil.getOrCreateFightAchievementCache(uid,
                            dieSkillParams.getCombat().getId());
                    if (cache != null) {
                        cache.setEffectZhongDuDie(cache.getEffectZhongDuDie() + 1);
                        TimeLimitCacheUtil.setFightAchievementCache(uid, cache);
                    }
                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        // 死亡技能执行前的buff
        runesPerformService.runBeforeDieSectionRunes(dieSkillParams.getCombat(), dieSkillParams.getPerformCard()
                , dieSkillParams.getPerformPlayer(), dieSkillParams.getOppoPlayer());
        SkillSection[] sections = {SkillSection.getDyingBenefitSection(), SkillSection.getDyingAttakSection()};
        for (SkillSection section : sections) {
            runDieSectionSkill(section, dieSkillParams);
        }
    }

    /**
     * 执行死亡后且进入坟场之后的 死亡技能：如复活
     *
     * @param dieSkillParams
     */
    private void runMoveAfterDieSectionSkillAll(PerformSkillParam dieSkillParams, List<Integer> banSkills, boolean wasMove, Integer dieCardSourcePos) {
        if (dieSkillParams.getPerformCard().getId() == -CombatSkillEnum.GX.getValue()
                || dieSkillParams.getPerformCard().getId() == -CombatSkillEnum.YL.getValue()) {
            return;
        }
        int pos = dieSkillParams.getPerformCard().getPos();
        runesPerformService.runInToDiscardRunes(dieSkillParams, dieCardSourcePos);
        SkillSection inToDiscardSection = SkillSection.getInToDiscardSection();
        if (pos != dieSkillParams.getPerformCard().getPos()) {
            //卡牌位置已经发生变更
            return;
        }
        runDieSectionSkill(inToDiscardSection, dieSkillParams, banSkills, wasMove);
    }

    private void runDieSectionSkill(SkillSection section, PerformSkillParam dieSkillParams) {
        runDieSectionSkill(section, dieSkillParams, new ArrayList<>(), false);
    }

    /**
     * 执行死亡技能
     *
     * @param section
     * @param dieSkillParams
     */
    private void runDieSectionSkill(SkillSection section, PerformSkillParam dieSkillParams, List<Integer> banSkills,
                                    boolean wasMove) {
        // 没有对象，或者没有卡牌
        BattleCard runCard = dieSkillParams.getPerformCard();
        if (null == runCard || (!runCard.isKilled() && !wasMove)) {
            return;
        }
        List<BattleSkill> validSkills = dieSkillParams.getPerformCard().getEffectiveSkills(section);
        validSkills = validSkills.stream().filter(p -> !banSkills.contains(p.getId())).collect(Collectors.toList());
        if (validSkills.isEmpty()) {
            // 没有需要发动的技能
            return;
        }
        List<Integer> allReceiveEffectSkillId = dieSkillParams.getAllReceiveEffectSkillId();
        List<Integer> banList = new ArrayList<Integer>();
        for (Integer receiveId : allReceiveEffectSkillId) {
            banList.addAll(banPerformDieSkill(receiveId));
        }
        //灭破
        if (dieSkillParams.getOppoPlayer().hasBuff(RunesEnum.MIE_PO)) {
            banList.add(CombatSkillEnum.FH.getValue());
        }
        List<Integer> effctedDieSkill = new ArrayList<>();
        for (BattleSkill skill : validSkills) {
            if (banList.contains(skill.getId())) {
                continue;
            }
            if (effctedDieSkill.contains(CombatSkillEnum.GX.getValue())
                    || effctedDieSkill.contains(CombatSkillEnum.YL.getValue())) {
                continue;
            }
            // 执行技能
            Optional<Action> actionOp = sectionSkillService.runSkill(section, skill, dieSkillParams);
            if (!actionOp.isPresent() || !actionOp.get().getTakeEffect()) {
                continue;
            } else if (CombatSkillEnum.GX.getValue() == skill.getId()) {
                runCard.setId(-CombatSkillEnum.GX.getValue());
            } else if (CombatSkillEnum.YL.getValue() == skill.getId()) {
                runCard.setId(-CombatSkillEnum.YL.getValue());
            }
            effctedDieSkill.add(skill.getId());
            List<Effect> allEffects = actionOp.get().getEffects();
            // 不可防御技能
            if (!skill.isDefensible()) {
                acceptEffectService.acceptSkillAttackEffect(dieSkillParams.getCombat(), allEffects);
                continue;
            }
            skillRoundService.produceEffects(dieSkillParams.getCombat(), allEffects);
        }
    }

    private List<Integer> banPerformDieSkill(Integer receiveId) {
        if (receiveId == null) {
            return new ArrayList<>();
        }
        switch (receiveId) {
            case 1011:// 绝杀 禁用全部死亡技能
                List<Integer> bans = new ArrayList<Integer>();
                for (int skll : SkillSection.getDieSection().getSkills()) {
                    bans.add(skll);
                }
                return bans;
            case 4114:// 神剑 克制复活
                return Arrays.asList(1201, 3012);
            case 4304:
            case 4303:// 地劫 克制死亡技能
                List<Integer> bans2 = new ArrayList<Integer>();
                for (int skll : SkillSection.getDieSection().getSkills()) {
                    bans2.add(skll);
                }
                return bans2;
            case 1002:// 妖术克制复活
                return Arrays.asList(1201, 3012);
            default:
                break;
        }
        return new ArrayList<>();
    }
}