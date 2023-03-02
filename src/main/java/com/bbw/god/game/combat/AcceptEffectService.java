package com.bbw.god.game.combat;

import com.bbw.common.CloneUtil;
import com.bbw.common.JSONUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.cache.CombatCache;
import com.bbw.god.game.combat.cache.CombatCacheUtil;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.*;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect.BattleSkillEffectLimit;
import com.bbw.god.game.combat.data.attack.CardValueEffect.CardValueEffectType;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 22:07
 */
@Slf4j
@Service
public class AcceptEffectService {
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private BattleCardDieService battleCardDieService;
    @Autowired
    private AttackServiceFactory serviceFactory;
    @Autowired
    private SkillRoundService skillRoundService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private SectionSkillService sectionSkillService;

    public void acceptSkillAttackEffect(Combat combat, List<Effect> groupEffects) {
        if (null == groupEffects || groupEffects.isEmpty()) {
            return;
        }
        for (Effect effect : groupEffects) {
            if (PositionService.isPlayingPos(effect.getTargetPos())) {
                BattleCard card = combat.getBattleCard(effect.getTargetPos());
                if (null == card || card.isKilled()) {
                    // 战场卡牌已经死亡 场景如：该卡释放业火，由多个回光反弹，上一轮反弹伤害已杀死卡牌，则后面的伤害不再接受
                    continue;
                }
            }
            if (effect.getResultType() != EffectResultType.CARD_POSITION_CHANGE) {
                acceptEffect(combat, effect);
            }
            if (effect.getResultType() == EffectResultType.SKILL_STATUS_CHANGE) {
                continue;
            }
            if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            if (PositionType.BATTLE != PositionService.getPositionType(effect.getTargetPos())) {
                acceptEffect(combat, effect);
                continue;
            }
            // 设置效果影响的目标卡牌，发送技能的参数
            boolean needDieEvent = effect.getResultType().equals(EffectResultType.CARD_POSITION_CHANGE)
                    && (effect.toPositionEffect().getToPositionType() == PositionType.DISCARD
                    || effect.toPositionEffect().getToPositionType() == PositionType.DEGENERATOR);
            if (effect.getResultType().equals(EffectResultType.CARD_VALUE_CHANGE) || needDieEvent) {
                PerformSkillParam dieSkillParams = new PerformSkillParam(combat, effect);
                battleCardDieService.runBattleCardDieEvent(dieSkillParams, effect);
            } else {
                acceptEffect(combat, effect);
            }
        }
    }

    public void acceptNormalAttackEffect(Combat combat, List<Effect> groupEffects) {
        if (null == groupEffects || groupEffects.isEmpty()) {
            return;
        }
        for (Effect effect : groupEffects) {
            acceptEffect(combat, effect);
        }
        //接受普通攻击后要执行的buff
        runesPerformService.runAfterAcceptAttackRunes(combat, groupEffects);
        // 获取物理反制后
        SkillSection physicalCounterSection = SkillSection.getAfterPhysicalCounterSection();
        List<Effect> physicalCounterEffects = sectionSkillService.runAfterPysicalCounter(physicalCounterSection, combat, groupEffects);
        for (Effect effect : physicalCounterEffects) {
            acceptEffect(combat, effect);
        }
        // 处理死亡
        for (Effect effect : groupEffects) {
            // 只处理阵位卡牌死亡
            if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            // 设置效果影响的目标卡牌，发送技能的参数
            PerformSkillParam dieSkillParams = new PerformSkillParam(combat, effect);
            // 处理死亡
            battleCardDieService.runBattleCardDieEvent(dieSkillParams);
        }
    }

    /**
     * 处理技能效果
     *
     * @param combat
     * @param effect
     */
    public void acceptEffect(Combat combat, Effect effect) {
        //无效的效果不做任何处理
        if (!effect.isValid()) {
            return;
        }
        List<Integer> ids = effect.getReceiveAllParticleSkillIds();
        boolean needAnimation = true;
        switch (effect.getResultType()) {
            case CARD_VALUE_CHANGE:// 属性
                if (combat.getWxType() == WanXianSpecialType.GONG_CHENG.getVal()) {
                    //神疗和治疗无效
                    if (effect.getSourceID() == CombatSkillEnum.SL.getValue() || effect.getSourceID() == CombatSkillEnum.ZHI_YU.getValue()) {
                        effect.toValueEffect().setHp(0);
                        effect.toValueEffect().setRoundHp(0);
                    }
                }
                if (effect.toValueEffect().getValueType().getType() == CardValueEffectType.LASTING.getType()) {
                    if (effect.toValueEffect().getBeginRound() != combat.getRound()) {
                        needAnimation = false;
                    }
                }
                acceptValueEffect(combat, effect.toValueEffect());
                break;
            case CARD_POSITION_CHANGE:// 位置
                CardPositionEffect pe = effect.toPositionEffect();
                effect = acceptPositionEffect(combat, pe);
                if (pe.isExchange()) {
                    needAnimation = false;
                    AnimationSequence animation1 = ClientAnimationService.
                            getPositionEffectAction(pe.getResultType().getValue(), pe.getSequence(), pe.getFromPos(), pe.getToPos());
                    combat.addAnimation(animation1);
                    AnimationSequence animation2 = ClientAnimationService.
                            getPositionEffectAction(pe.getResultType().getValue(), pe.getSequence(), pe.getToPos(), pe.getFromPos());
                    combat.addAnimation(animation2);
                }
                break;
            case SKILL_STATUS_CHANGE:// 卡牌技能
                acceptBattleSkillEffect(combat, effect.toBattleSkillEffect());
                break;
            case CARD_CHANGE_TO_CARD:
                acceptCardChangeEffect(combat, effect.toBattleCardChangeEffect());
                break;
            default:
                throw CoderException.high("无法处理[" + effect.getResultType() + "]类型的行动效果！");
        }
        int seq = effect.getSequence();
        if (ids != null && !ids.isEmpty() && effect.isParticleSkill() && !PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
            // 处理溅射效果
            PerformSkillParam psp = PerformSkillParam.getPspByAttakEffect(combat, effect);
            for (Integer skillId : ids) {
                BattleSkillService service = serviceFactory.getSkillAttackService(skillId);
                List<Effect> nEffects = service.attakParticleffects(psp);
                if (nEffects != null && !nEffects.isEmpty()) {
                    for (Effect eff : nEffects) {
                        eff.setSequence(seq);
                        eff.setFromParticleEffect(true);
                        eff.setNeedAnimation(false);
                    }
                    skillRoundService.produceEffects(combat, nEffects);
                }
            }
        }
        if (effect.getResultType().isNeedAnimation() && needAnimation) {
            AnimationSequence animation = ClientAnimationService.getEffectAnimation(effect);
            combat.addAnimation(animation);
        }
    }

    /**
     * 处理武器效果
     *
     * @param combat
     * @param effect
     */
    public void acceptWeaponEffect(Combat combat, Effect effect) {
        //无效的效果不做任何处理
        if (!effect.isValid()) {
            return;
        }
        boolean addAnimation = effect.getResultType().isNeedAnimation();
        switch (effect.getResultType()) {
            case CARD_VALUE_CHANGE:// 属性
                acceptValueEffect(combat, effect.toValueEffect());
                break;
            case CARD_POSITION_CHANGE:// 位置
                acceptPositionEffect(combat, effect.toPositionEffect());
                break;
            case SKILL_STATUS_CHANGE:// 卡牌技能
                addAnimation = false;
                acceptBattleSkillEffect(combat, effect.toBattleSkillEffect());
                break;
            default:
                throw CoderException.high("无法处理[" + effect.getResultType() + "]类型的行动效果！");
        }
        if (addAnimation) {
            AnimationSequence animation = ClientAnimationService.getEffectAnimation(effect);
            combat.addAnimation(animation);
        }
    }

    private void acceptBattleSkillEffect(Combat combat, BattleSkillEffect effect) {
        switch (effect.getSkillEffectType()) {
            case ADD:
                acceptBattleSkillEffectAdd(combat, effect);
                break;
            case LIMIT:
                acceptBattleSkillEffectLimit(combat, effect);
                // 补充一个状态结果动画
                AnimationSequence as2 = ClientAnimationService.getStautsEffectAction(effect);
                combat.addAnimation(as2);
                break;
            case CHANGE_TARGET:
                acceptBattleSkillEffectChangeTarget(combat, effect);
                // 补充一个状态结果动画
                AnimationSequence as3 = ClientAnimationService.getStautsEffectAction(effect);
                combat.addAnimation(as3);
                break;
            case LASTING:
                acceptBattleSkillEffectLastingPerform(combat, effect);
                if (BattleCardStatus.isNeedAddAnimation(effect.getLastingEffectType())) {
                    AnimationSequence as4 = ClientAnimationService.getStautsEffectAction(effect);
                    combat.addAnimation(as4);
                }
                break;
            case ADD_STATUS:
                acceptBattleSkillStatusEffect(combat, effect);
                AnimationSequence as5 = ClientAnimationService.getStautsEffectAction(effect);
                combat.addAnimation(as5);
                break;
            default:
                break;
        }
    }

    private void acceptBattleSkillEffectAdd(Combat combat, BattleSkillEffect effect) {
        int cardPos = effect.getTargetPos();
        BattleCard card = combat.getBattleCardByPos(cardPos);
        int from = effect.getSourceID();
        for (BattleSkillEffectLimit limit : effect.getEffectLimits()) {
            BattleSkill skill = null;
            if (CombatSkillEnum.isWeaponSKill(limit.getSkillId())) {
                skill = BattleSkill.instanceSkill(from, limit.getSkillId(), true, limit.getTimesLimit());
            } else {
                Optional<CfgCardSkill> cardSkillOpById = CardSkillTool.getCardSkillOpById(limit.getSkillId());
                if (!cardSkillOpById.isPresent()) {
                    continue;
                }
                skill = BattleSkill.instanceSkill(from, cardSkillOpById.get());
                skill.setTimesLimit(limit.getTimesLimit());
            }
            if (skill != null) {
                card.addSkill(skill);
            }
        }
    }

    private void acceptBattleSkillEffectLimit(Combat combat, BattleSkillEffect effect) {
        int cardPos = effect.getTargetPos();
        BattleCard card = combat.getBattleCard(cardPos);
        int skillId = effect.getSourceID();
        if (skillId == CombatSkillEnum.SHENG_GUANG.getValue()) {
            skillId = effect.getPerformSkillID();
        }
        card.addCardStatus(effect.getLastRound(), skillId);
        for (BattleSkillEffectLimit limit : effect.getEffectLimits()) {
            List<BattleSkill> collect = card.getSkills().stream().filter(p -> p.getId() == limit.getSkillId() || p.ifParent(limit.getSkillId())).collect(Collectors.toList());
            if (limit.getSkillId() == CombatSkillEnum.NORMAL_ATTACK.getValue()) {
                collect.add(card.getNormalAttackSkill());
            }
            for (BattleSkill skill : collect) {
                if (skill.ifBornBuffSkill() && limit.getTimesLimit().isForbid()) {
                    //护身符技能 不能被永久封
                    continue;
                }
                if (skill.getTimesLimit().isForbid() && limit.getTimesLimit().isForbid()) {
                    //如果技能已经是永久封锁状态，则不在叠加永久封锁状态
                    continue;
                }
                if (limit.getTimesLimit().getBanFrom() > 0) {
                    limit.getTimesLimit().setBanFrom(effect.getSourceID());
                }
                skill.setTimesLimit(CloneUtil.clone(limit.getTimesLimit()));
            }
        }
    }

    private void acceptBattleSkillEffectChangeTarget(Combat combat, BattleSkillEffect effect) {
        int cardPos = effect.getTargetPos();
        BattleCard card = combat.getBattleCard(cardPos);
        card.addCardStatus(effect.getLastRound(), effect.getSourceID());
        for (BattleSkillEffectLimit limit : effect.getEffectLimits()) {
            Optional<BattleSkill> match = card.getSkill(limit.getSkillId());
            if (!match.isPresent()) {
                continue;
            }
            if (limit.isTargetAttack()) {
                match.get().setTargetPos(limit.getAttackPos());
            } else {
                match.get().setTimesLimit(limit.getTimesLimit());
            }
        }
    }

    private void acceptBattleSkillEffectLastingPerform(Combat combat, BattleSkillEffect effect) {
        int cardPos = effect.getTargetPos();
        BattleCard card = combat.getBattleCard(cardPos);
        if (effect.getSouceCard() != null) {
            card.addCardStatus(effect.getLastingEffectType(), effect.getLastRound(), effect.getSourceID(),
                    effect.getSouceCard());
        } else {
            card.addCardStatus(effect.getLastingEffectType(), effect.getLastRound(), effect.getSourceID());
        }
    }

    private void acceptBattleSkillStatusEffect(Combat combat, BattleSkillEffect effect) {
        int cardPos = effect.getTargetPos();
        BattleCard card = combat.getBattleCard(cardPos);
        if (effect.getSourceID() == CombatSkillEnum.SHENG_GUANG.getValue()) {
            card.addCardStatus(1, effect.getPerformSkillID());
        } else {
            card.addCardStatus(1, effect.getSourceID());
        }
    }


    private void acceptValueEffect(Combat combat, CardValueEffect effect) {
        boolean needMultiple = effect.getSourceID() > 3100 && effect.getSourceID() < 3200 && !PositionService.isZhaoHuanShiPos(effect.getTargetPos()) && combat.getWxType() != null && combat.getWxType() == WanXianSpecialType.MAGIC.getVal();
        if (needMultiple) {
            if (effect.getRoundHp() < 0) {
                effect.setRoundHp(effect.getRoundHp() * 2);
            } else if (effect.getHp() < 0) {
                effect.setHp(effect.getHp() * 2);
            }
        }
        if (effect.isMultiple() && !PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
            // 攻击卡牌
            PlayerId playerId = PositionService.getPlayerIdByPos(effect.getTargetPos());
            Player targetPlayer = combat.getPlayer(playerId);
            Optional<BattleCard> posCard = PositionService.getCard(targetPlayer, effect.getTargetPos());
            if (posCard.isPresent()) {
                BattleCard card = posCard.get();
                effect.incAtk(effect.getAtkTimes() * card.getAtk());
                effect.incRoundAtk(effect.getRoundAtkTimes() * card.getRoundAtk());
                effect.incHp(effect.getHpTmes() * card.getHp());
                effect.incRoundHp(effect.getRoundHpTmes() * card.getRoundHp());
                effect.setAtkTimes(0);
                effect.setRoundAtkTimes(0);
                effect.setHpTmes(0);
                effect.setRoundHpTmes(0);
            }
        }
        switch (effect.getValueType()) {
            case IN_TIME:// 立即生效
                acceptValueEffectInTime(combat, effect);
                break;
            case DELAY:// 延迟生效
                acceptValueEffectDelay(combat, effect);
                break;
            case LASTING:// 持续性
                acceptValueEffectLasting(combat, effect);
                break;
            default:
                break;
        }
    }

    private void acceptValueEffectLasting(Combat combat, CardValueEffect effect) {
        PositionService.getPositionType(effect.getTargetPos());
        // 攻击卡牌
        PlayerId playerId = PositionService.getPlayerIdByPos(effect.getTargetPos());
        Player targetPlayer = combat.getPlayer(playerId);
        Optional<BattleCard> targetCard = PositionService.getCard(targetPlayer, effect.getTargetPos());
        if (targetCard.isPresent()) {
            // 对于下一回合来说 该effect效果应改变为立即生效的，否则将会重复添加
            effect.setValueType(CardValueEffectType.IN_TIME);
            if (effect.getBeginRound() == combat.getRound()) {
                // acceptValueEffectLasting 该方法只是将效果挂载到目标卡，但是并未接收这个效果
                // 所以此处将通过acceptValueEffectInTime 将本次造成的实际伤害效果添加
                acceptValueEffectInTime(combat, effect);
            } else {
                effect.setBeginRound(effect.getBeginRound() - 1);
            }
            targetCard.get().getLastingEffects().add(effect);
            // 补充一个状态结果动画
            AnimationSequence as = ClientAnimationService.getStautsEffectAction(effect);
            combat.addAnimation(as);
        } else {
            log.error("无法获取到[{}]玩家[{}]位置卡牌！" + targetPlayer, targetPlayer.getName(), effect.getTargetPos());
            throw CoderException.high("无法获取到卡牌！pos=" + effect.getTargetPos());
        }
    }

    private void acceptValueEffectDelay(Combat combat, CardValueEffect effect) {
        PositionService.getPositionType(effect.getTargetPos());
        // 攻击卡牌
        PlayerId playerId = PositionService.getPlayerIdByPos(effect.getTargetPos());
        Player targetPlayer = combat.getPlayer(playerId);
        Optional<BattleCard> posCard = PositionService.getCard(targetPlayer, effect.getTargetPos());
        if (posCard.isPresent()) {
            try {
                BattleCard card = posCard.get();
                card.incHp(effect.getHp());
                card.incReduceRoundTempHp(effect.getHp());
                card.incAtk(effect.getAtk());
                card.incReduceRoundTempAtk(effect.getAtk());
                if (card.getRoundDelayEffects() == null) {
                    card.setRoundDelayEffects(new ArrayList<>());
                }
                card.getRoundDelayEffects().add(effect);
            } catch (NullPointerException exception) {
                exception.printStackTrace();
            }
        } else {
            log.error("无法获取到[{}]玩家[{}]位置卡牌！" + targetPlayer, targetPlayer.getName(), effect.getTargetPos());
            throw CoderException.high("无法获取到卡牌！pos=" + effect.getTargetPos());
        }
    }

    /**
     * 伤害结算
     *
     * @param combat
     * @param effect
     */
    private void acceptValueEffectInTime(Combat combat, CardValueEffect effect) {
        if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
            // 攻击召唤师结算
            acceptZhsValueEffectInTime(combat, effect);
        } else {
            // 攻击卡牌
            acceptCardValueEffectInTime(combat, effect);
        }
    }

    /**
     * 卡牌 实时伤害结算
     *
     * @param combat
     * @param effect
     */
    private void acceptCardValueEffectInTime(Combat combat, CardValueEffect effect) {
        PlayerId playerId = PositionService.getPlayerIdByPos(effect.getTargetPos());
        Player targetPlayer = combat.getPlayer(playerId);
        Player sourcePlayer = combat.getOppoPlayer(playerId);
        Optional<BattleCard> posCard = PositionService.getCard(targetPlayer, effect.getTargetPos());
        if (posCard.isPresent()) {
            BattleCard targetCard = posCard.get();
            targetCard.incHp(effect.getHp() + effect.getRoundHp());
            targetCard.incReduceRoundTempHp(effect.getHp());
            targetCard.incAtk(effect.getAtk() + effect.getRoundAtk());
            targetCard.incReduceRoundTempAtk(effect.getAtk());
            targetCard.incMp(effect.getMp());
            targetCard.incRoundAtk(effect.getRoundAtk());
            targetCard.incRoundHp(effect.getRoundHp());
            if (targetCard.getHp() > 0 && effect.hasExtraSkillEffect(CombatSkillEnum.NORMAL_ATTACK.getValue()) && sourcePlayer.hasBuff(RunesEnum.TIAN_ZHAN)) {
                targetCard.setHp(0);
                targetCard.setAlive(false);
                targetCard.banAllDieSKill();
                int fromPos = PositionService.getZhaoHuanShiPos(sourcePlayer.getId());
                combat.addAnimation(ClientAnimationService.getSkillAction(combat.getAnimationSeq(), RunesEnum.TIAN_ZHAN.getRunesId(), fromPos, targetCard.getPos()));
            }
        } else {
            log.error("无法获取到[{}]玩家[{}]位置卡牌！" + JSONUtil.toJson(effect), targetPlayer.getName(), effect.getTargetPos());
        }
    }

    /**
     * 召唤师 实时伤害结算
     *
     * @param combat
     * @param effect
     */
    private void acceptZhsValueEffectInTime(Combat combat, CardValueEffect effect) {
        // 攻击召唤师的，一定是ValueEffect！
        Player targetPlayer = combat.getPlayer(effect.getTargetPos());
        effect = runesPerformService.runPlayHpChangeRunes(combat, targetPlayer, effect, combat.getId());
        // 获取被攻击的召唤师
        targetPlayer.incHp(effect.getHp() + effect.getRoundHp());
        targetPlayer.incMaxHp(effect.getRoundHp());
        targetPlayer.incMp(effect.getMp());
        targetPlayer.incMaxMp(effect.getRoundMp());
        combat.hadEnded();
        //麒麟攻击召唤师
        if (targetPlayer.getHp() <= 0 && !PositionService.isZhaoHuanShiPos(effect.getSourcePos())) {
            try {
                Player oppoPlayer = combat.getOppoPlayer(targetPlayer.getId());
                long uid = oppoPlayer.getUid();
                if (uid > 0 && effect.getFromCardId().intValue() == CardEnum.QI_LIN.getCardId()) {
                    CombatCache cache = CombatCacheUtil.getCombatCache(uid, combat.getId());
                    cache.incQiLingAttackZHS(1);
                    CombatCacheUtil.setCombatCache(cache);
                    if (combat.getWinnerId() == oppoPlayer.getId().getValue()) {
                        CombatEventPublisher.pubCombatQiLinKillZhsEvent(uid);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return;
    }

    /**
     * 处理卡牌移动效果
     *
     * @param combat
     * @param effect
     */
    private Effect acceptPositionEffect(Combat combat, CardPositionEffect effect) {
        int toPos = -1;
        if (null == effect) {
            return null;
        }
        // 如果需要移动
        PlayerId playerId = PositionService.getPlayerIdByPos(effect.getFromPos());
        Player player = combat.getPlayer(playerId);
        Optional<BattleCard> effectCard = battleCardService.getCard(player, effect.getFromPos());
        if (!effectCard.isPresent()) {
            log.error("{}vs{}的战斗中，找不要要移动的卡牌,对应的效果为:{}", combat.getP1().getUid(), combat.getP2().getUid(), JSONUtil.toJson(effect));
            return null;
        }
        if (!(PositionType.BATTLE.equals(PositionService.getPositionType(effect.getFromPos())) && PositionType.BATTLE.equals(effect.getToPositionType()))) {
            //如果不是战场上移动到其他战场上位置 则需要重置卡牌
            effectCard.get().reset(!PositionType.BATTLE.equals(effect.getToPositionType()));
        }
        if (effectCard.get().getId() < 0 && !effect.getToPositionType().equals(PositionType.BATTLE)) {
            //卡牌临时id小于0的 都为召唤的卡，只要发生移动且不是上场都是移动到异次元
            effect.setToPositionType(PositionType.DEGENERATOR);
            effect.setToPos(-1);
        }
        if (combat.getWxType() == WanXianSpecialType.GONG_CHENG.getVal() && effectCard.get().getId() >= 0 && (effect.getToPositionType().equals(PositionType.DISCARD) || effect.getToPositionType().equals(PositionType.DEGENERATOR))) {
            //卡牌不是临时的 即id不小于0的 ，只要发生移动且不是上场都是移动到异次元
            effect.setToPositionType(PositionType.HAND);
            effect.setToPos(-1);
        }
        if (PositionType.DISCARD.equals(effect.getToPositionType())) {
            Player oppoPlayer = combat.getOppoPlayer(playerId);
            //灭魄符:敌方阵亡的卡牌将不进入坟场，移动到异次元.
            if (oppoPlayer.hasBuff(RunesEnum.MIE_PO)) {
                effect.setToPositionType(PositionType.DEGENERATOR);
                effect.setToPos(-1);
            }
        }
        if (effect.getToPositionType().equals(PositionType.DISCARD) || effect.getToPositionType().equals(PositionType.DEGENERATOR)) {
            Player oppoPlayer = combat.getOppoPlayer(playerId);
            if (oppoPlayer.getUid() > 0 && effect.getFromCardId().intValue() == CardEnum.LEADER_CARD.getCardId()) {
                CombatCache cache = CombatCacheUtil.getCombatCache(oppoPlayer.getUid(), combat.getId());
                cache.incLeaderCardKillNum(1);
                CombatCacheUtil.setCombatCache(cache);
            }
        }
        boolean isLeaveBattle = PositionType.BATTLE == effect.getFromPositionType() && PositionType.BATTLE != effect.getToPositionType();
        if (effect.hasToPos()) {
            if (effect.isExchange()) {
                //交换卡牌
                battleCardService.exchangeCard(player, effect.getFromPos(), effect.getToPos());
                return effect;
            }
            battleCardService.moveTo(player, effect.getFromPos(), effect.getToPos());
            effectCard.get().setPos(effect.getToPos());
            // 如果卡牌时离开则触发离开buff
            if (isLeaveBattle) {
                runesPerformService.runLeaveBattleRunes(combat, effectCard.get(), effect.getFromPos());
            }
            return effect;
        }
        switch (effect.getToPositionType()) {
            case BATTLE://
                // 移动到战场
                throw CoderException.high("移动到战场必须指定具体的阵位，上阵业务由技能具体实现!");
            case HAND:// 移动到手牌
                toPos = battleCardService.moveToHandCards(player, effect);
                break;
            case DRAWCARD:// 移动到牌堆
                toPos = battleCardService.moveToDrawcards(player, effect);
                break;
            case DISCARD:// 移动到坟场
                toPos = battleCardService.moveToDiscard(player, effect);
                break;
            case REINFORCEMENTS:// 移动到援军
                toPos = battleCardService.moveToReinforcements(player, effect);
                break;
            case DEGENERATOR:// 移动到异次元
                toPos = battleCardService.moveToDegenerator(player, effect);
                break;
            default:
                log.error("无效的牌堆！卡牌从" + effect.getFromPositionType() + "移动到" + effect.getToPositionType());
                log.error("" + effect);
                break;
        }
        if (-1 == toPos) {
            throw CoderException.high("无效的牌堆！卡牌从" + effect.getFromPositionType() + "移动到" + effect.getToPositionType());
        }
        effect.setToPos(toPos);
        effectCard.get().setPos(toPos);
        // 如果卡牌时离开则触发离开buff
        if (isLeaveBattle) {
            runesPerformService.runLeaveBattleRunes(combat, effectCard.get(), effect.getFromPos());
        }
        return effect;
    }

    public void acceptCanNotDefenseEffects(Combat combat, List<Effect> todoEffects) {
        // 如果是攻击召唤师的，直接结算，最多只有一个
        Optional<Effect> zhaoHuanShiAtk = todoEffects.stream()
                .filter(effect -> PositionService.isZhaoHuanShiPos(effect.getTargetPos())).findFirst();
        if (zhaoHuanShiAtk.isPresent()) {
            // 攻击召唤师的，一定是ValueEffect！
            CardValueEffect valueEffect = zhaoHuanShiAtk.get().toValueEffect();
            acceptEffect(combat, valueEffect);
            todoEffects.clear();
            return;
        }

        // 作用卡牌己方的增益
        List<Effect> selfEffects = todoEffects.stream().filter(effect -> effect.isEffectSelf())
                .collect(Collectors.toList());
        acceptSkillAttackEffect(combat, selfEffects);
        todoEffects.removeAll(selfEffects);

        // 行动效果针对非战场卡牌，则无法防御，直接接受结果
        List<Effect> notPlayingCardEffects = todoEffects.stream()
                .filter(effect -> PositionType.BATTLE != PositionService.getPositionType(effect.getTargetPos()))
                .collect(Collectors.toList());
        acceptSkillAttackEffect(combat, notPlayingCardEffects);
        todoEffects.removeAll(notPlayingCardEffects);

        // 最强攻击不可防御
        List<Effect> maxPowerEffects = todoEffects.stream().filter(effect -> effect.isMaxPower())
                .collect(Collectors.toList());
        acceptSkillAttackEffect(combat, maxPowerEffects);
        todoEffects.removeAll(maxPowerEffects);
    }

    public void acceptRoundEndSkillEffect(Combat combat, List<Effect> effects) {
        for (Effect effect : effects) {
            //无效的效果不做任何处理
            if (!effect.isValid()) {
                continue;
            }
            switch (effect.getResultType()) {
                case CARD_VALUE_CHANGE:// 属性
                    acceptValueEffect(combat, effect.toValueEffect());
                    break;
                case CARD_POSITION_CHANGE:// 位置
                    acceptPositionEffect(combat, effect.toPositionEffect());
                    break;
                case SKILL_STATUS_CHANGE:// 卡牌技能
                    acceptBattleSkillEffect(combat, effect.toBattleSkillEffect());
                    break;
                default:
                    throw CoderException.high("无法处理[" + effect.getResultType() + "]类型的行动效果！");
            }
        }
    }

    public void acceptCardChangeEffect(Combat combat, BattleCardChangeEffect effect) {
        PlayerId id = effect.getTargetPlayerId();
        Player player = combat.getPlayer(id);
        List<BattleCard> cards = effect.getChanges();
        int discardGuiB = 0;
        for (BattleCard card : cards) {
            battleCardService.replaceCard(player, card);
            if (card.getImgId() == 424 && PositionService.isDiscardPos(card.getPos())) {
                discardGuiB++;
            }
        }
        if (discardGuiB > 0) {
            Player performPlayer = combat.getOppoPlayer(id);
            try {
                FightAchievementCache cache = TimeLimitCacheUtil
                        .getOrCreateFightAchievementCache(performPlayer.getUid(), combat.getId());
                if (cache != null && !cache.isEffectDiscardToGuiB()) {
                    cache.setEffectDiscardToGuiB(discardGuiB >= 10);
                    TimeLimitCacheUtil.setFightAchievementCache(performPlayer.getUid(), cache);
                }
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}