package com.bbw.god.game.combat.runes;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.AcceptEffectService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.SkillRoundService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.service.*;
import com.bbw.god.game.combat.runes.service.impl.Runes131004;
import com.bbw.god.game.combat.runes.service.impl.Runes131005;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 符文释放服务
 * @author：lwb
 * @date: 2020/12/8 14:36
 * @version: 1.0
 */
@Service
public class CombatRunesPerformService {
    @Autowired
    private CombatRunesFactory factory;
    @Autowired
    private Runes131004 runes131004;
    @Autowired
    private Runes131005 runes131005;
    @Autowired
    private SkillRoundService skillRoundService;
    @Autowired
    private AcceptEffectService acceptEffectService;

    /**
     * 执行战斗参数构建阶段符文
     *
     * @param p1
     * @param p2
     * @param pveParam
     */
    public void runInitCombatParamRunes(CPlayerInitParam p1, CPlayerInitParam p2, CombatPVEParam pveParam) {
        CPlayerInitParam[] playerParams = {p1, p2};
        CombatStage paramInitRunes = CombatRunesStageTool.getCombatParamInitRunes();
        for (int i = 0; i < playerParams.length; i++) {
            CPlayerInitParam playerParam = playerParams[i];
            for (CombatBuff rune : playerParam.getBuffs()) {
                if (!paramInitRunes.exist(rune.getRuneId())) {
                    continue;
                }
                //执行符文
                IParamInitStageRunes paramInitStageRunes = factory.matchParamInitStageRunes(rune.getRuneId());
                if (null != paramInitStageRunes) {
                    paramInitStageRunes.doParamInitRunes(playerParam, playerParams[(i + 1) % 2], pveParam);
                }

            }
        }
    }


    /**
     * 初始化战斗时候需要执行的符文
     *
     * @param first
     * @param second
     */
    public void runInitCombatRunes(Player first, Player second, long combatId) {
        CombatRunesParam[] params = {CombatRunesParam.instance(first, second, combatId), CombatRunesParam.instance(second, first, combatId)};
        CombatStage initCombatRunes = CombatRunesStageTool.getInitCombatRunes();
        CombatStage addSkillRunes = CombatRunesStageTool.getAddSkillRunes();
        for (CombatRunesParam param : params) {
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!initCombatRunes.exist(rune) && !addSkillRunes.exist(rune)) {
                    continue;
                }
                //执行符文
                IInitStageRunes stageRunes = factory.matchInitStageRunes(rune);
                if (null != stageRunes) {
                    stageRunes.doInitRunes(param);
                }

            }
        }
    }

    /**
     * 对单张卡执行初始化符文
     * @param player
     * @param battleCard
     */
    public void runInitCardRunes(Player player, BattleCard battleCard) {
        CombatStage addSkillRunes = CombatRunesStageTool.getAddSkillRunes();
        for (Integer buff : player.getBuffs()) {
            if (!addSkillRunes.exist(buff)) {
                continue;
            }
            AbstractAddSkillRunes abstractAddSkillRunes = factory.matchAddSkillRunes(buff);
            if (null == addSkillRunes) {
                continue;
            }
            abstractAddSkillRunes.addSkillTOCard(battleCard);
        }
    }

    /**
     * 每回合初始化时执行的=》重置手牌后执行
     * @param first
     * @param second
     */
    public void runInitRoundRunes(Player first,Player second,long combatId){
        CombatRunesParam[] params={CombatRunesParam.instance(first,second,combatId),CombatRunesParam.instance(second,first,combatId)};
        CombatStage initRoundRunes = CombatRunesStageTool.getInitRoundRunes();
        for (CombatRunesParam param : params) {
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!initRoundRunes.exist(rune)) {
                    continue;
                }
                //执行符文
                IInitStageRunes stageRunes = factory.matchInitStageRunes(rune);
                if (null != stageRunes) {
                    stageRunes.doInitRunes(param);
                }
            }
        }
    }

    /**
     * 每回合都会释放的符文
     * 布阵结束后释放
     */
    public void runRoundBeginRunes(Combat combat){
        long combatId=combat.getId();
        CombatRunesParam[] params={CombatRunesParam.instance(combat.getFirstPlayer(),combat.getSecondPlayer(),combatId),CombatRunesParam.instance(combat.getSecondPlayer(),combat.getFirstPlayer(),combatId)};
        CombatStage roundBeginRunes = CombatRunesStageTool.getRoundBeginRunes();
        for (CombatRunesParam param : params) {
            param.setRound(combat.getRound());
            if (param.getPerformPlayer().isOwnYZXM()){
                runes131005.doRoundRunes(combat.getFirstPlayer());
            }
            int seq = combat.getAnimationSeq();
            param.setSeq(combat.getAnimationSeq());
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!roundBeginRunes.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                Action action = roundStageRunes.doRoundRunes(param);
                addClientAnim(combat, seq, action, param);
                if (action.getTakeEffect() && action.existsEffect()) {
                    runPerformSkillRunes(combat, null, action.getEffects());
                    //接受效果
                    skillRoundService.produceEffects(combat, action.getEffects());
                }
            }

        }
    }

    public void runRoundEndRunes(Combat combat){
        long combatId=combat.getId();
        CombatRunesParam[] params = {CombatRunesParam.instance(combat.getFirstPlayer(), combat.getSecondPlayer(), combatId), CombatRunesParam.instance(combat.getSecondPlayer(), combat.getFirstPlayer(), combatId)};
        CombatStage roundEndRunes = CombatRunesStageTool.getRoundEndRunes();
        for (CombatRunesParam param : params) {
            param.setRound(combat.getRound());
            int seq = combat.getAnimationSeq();
            param.setSeq(combat.getAnimationSeq());
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!roundEndRunes.exist(rune)) {
                    continue;
                }
                IRoundEndStageRunes roundEndStageRunes = factory.matchRoundEndStageRunes(rune);
                if (null == roundEndStageRunes) {
                    continue;
                }
                Action action = roundEndStageRunes.doRoundEndRunes(param);
                addClientAnim(combat, seq, action, param);
                if (action.getTakeEffect() && action.existsEffect()) {
                    //接受效果
                    acceptEffectService.acceptSkillAttackEffect(combat, action.getEffects());
                }
            }
        }
    }

    /**
     * 当卡牌进入坟场时触发的符文效果
     *
     * @param dieSkillParams
     */
    public void runInToDiscardRunes(PerformSkillParam dieSkillParams, Integer dieCardSourcePos) {
        CombatStage discardRunes = CombatRunesStageTool.getIntoDiscardRunes();
        long combatId = dieSkillParams.getCombat().getId();
        CombatRunesParam performParam = CombatRunesParam.instance(dieSkillParams.getPerformPlayer(), dieSkillParams.getOppoPlayer(), dieSkillParams.getPerformCard(), combatId);
        performParam.setCardSourcePos(dieCardSourcePos);
        CombatRunesParam oppoParam = CombatRunesParam.instance(performParam.getOppoPlayer(), performParam.getPerformPlayer(), dieSkillParams.getPerformCard(), combatId);
        oppoParam.setCardSourcePos(dieCardSourcePos);
        CombatRunesParam[] params = {performParam, oppoParam};
        Combat combat = dieSkillParams.getCombat();
        for (CombatRunesParam param : params) {
            int seq = combat.getAnimationSeq();
            param.setSeq(combat.getAnimationSeq());
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!discardRunes.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    //有发动
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        acceptEffectService.acceptSkillAttackEffect(combat, action.getEffects());
                    }
                }
            }
        }
    }

    /**
     * 离开战场触发
     *
     * @param combat
     * @param leaveCard
     */
    public void runLeaveBattleRunes(Combat combat, BattleCard leaveCard, int leaveCardSourcePos) {
        CombatStage leaveBattleRunes = CombatRunesStageTool.getLeaveBattleRunes();
        long combatId = combat.getId();
        CombatRunesParam performParam = CombatRunesParam.instance(combat.getP1(), combat.getP2(), leaveCard, combatId);
        performParam.setCardSourcePos(leaveCardSourcePos);
        CombatRunesParam oppoParam = CombatRunesParam.instance(combat.getP2(), combat.getP1(), leaveCard, combatId);
        oppoParam.setCardSourcePos(leaveCardSourcePos);
        CombatRunesParam[] params = {performParam, oppoParam};
        for (CombatRunesParam param : params) {
            int seq = combat.getAnimationSeq();
            param.setSeq(combat.getAnimationSeq());
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!leaveBattleRunes.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    //有发动
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        acceptEffectService.acceptSkillAttackEffect(combat, action.getEffects());
                    }
                }
            }
        }
    }

    public void runAttackPlayHpRunes(Combat combat, Player targetPlayer, Effect effect) {
        if (!effect.isValueEffect()) {
            return;
        }
        long combatId = combat.getId();
        CombatRunesParam acceptParam = CombatRunesParam.instance(targetPlayer, combat.getOppoPlayer(targetPlayer.getId()), effect, combatId);
        CombatRunesParam attackParam = CombatRunesParam.instance(combat.getOppoPlayer(targetPlayer.getId()), targetPlayer, effect, combatId);
        CombatRunesParam[] params = {acceptParam, attackParam};
        CombatStage combatStage = CombatRunesStageTool.getAttackPlayerRunes();
        for (CombatRunesParam param : params) {
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!combatStage.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                int seq = combat.getAnimationSeq();
                param.setSeq(combat.getAnimationSeq());
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    //有发动
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        acceptEffectService.acceptSkillAttackEffect(combat, action.getEffects());
                    }
                }
            }
        }
    }

    /**
     * 当召唤师血量产生变化时发动的符文
     * @param combat
     * @param effect
     */
    public CardValueEffect runPlayHpChangeRunes(Combat combat, Player targetPlayer,CardValueEffect effect,long combatId){
        CombatRunesParam acceptParam=CombatRunesParam.instance(targetPlayer,combat.getOppoPlayer(targetPlayer.getId()),effect,combatId);
        acceptParam.setRound(combat.getRound());
        CombatRunesParam attackParam=CombatRunesParam.instance(combat.getOppoPlayer(targetPlayer.getId()),targetPlayer,effect,combatId);
        attackParam.setRound(combat.getRound());
        CombatRunesParam[] params={acceptParam,attackParam};
        //护符处理
        CombatStage combatStage = CombatRunesStageTool.getPlayerHpChangeRunes();
        for (CombatRunesParam param : params) {
            if (param.getPerformPlayer().isOwnGYJL()){
                runes131004.doRoundRunes(param);
            }
            if (param.getPerformPlayer().isOwnYZXM()){
                runes131005.doRoundRunes(param);
            }
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!combatStage.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                int seq = combat.getAnimationSeq();
                param.setSeq(combat.getAnimationSeq());
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    //有发动
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        acceptEffectService.acceptSkillAttackEffect(combat, action.getEffects());
                    }
                    if (ListUtil.isNotEmpty(param.getReceiveEffect())) {
                        effect=(CardValueEffect) param.getReceiveEffect().get(0);
                    }
                }
            }
        }
        return effect;
    }

    /**
     * 物理攻击之前
     *
     * @param combat
     */
    public void runBeforeNormalAttackRunes(Combat combat) {
        long combatId = combat.getId();
        CombatStage beforeNormalAttackRunes = CombatRunesStageTool.getBeforeNormalAttackRunes();
        CombatRunesParam[] params = {CombatRunesParam.instance(combat.getFirstPlayer(), combat.getSecondPlayer(), combatId), CombatRunesParam.instance(combat.getSecondPlayer(), combat.getFirstPlayer(), combatId)};
        doAttackSectionRunes(beforeNormalAttackRunes, params, combat);
    }

    /**
     * 物理攻击buff后
     *
     * @param combat
     */
    public void runAfterAttackBuffRunes(Combat combat, BattleCard performCard, List<Effect> effects) {
        long combatId = combat.getId();
        CombatStage afterAttackBuffRunes = CombatRunesStageTool.getAfterAttackBuffRunes();
        CombatRunesParam firstParam = CombatRunesParam.instance(combat.getFirstPlayer(), combat.getSecondPlayer(), effects, combatId);
        firstParam.setPerformCard(performCard);
        CombatRunesParam secondParam = CombatRunesParam.instance(combat.getSecondPlayer(), combat.getFirstPlayer(), effects, combatId);
        firstParam.setPerformCard(performCard);
        CombatRunesParam[] params = {firstParam, secondParam};
        doAttackSectionRunes(afterAttackBuffRunes, params, combat);
    }

    /**
     * 物理防御前
     *
     * @param combat
     */
    public void runBeforeNormalNormalDefenceRunes(Combat combat, List<Effect> effects) {
        long combatId = combat.getId();
        CombatStage beforeNormalNormalDefenceRunes = CombatRunesStageTool.getBeforeNormalNormalDefenceRunes();
        CombatRunesParam[] params = {
                CombatRunesParam.instance(combat.getFirstPlayer(), combat.getSecondPlayer(), effects, combatId),
                CombatRunesParam.instance(combat.getSecondPlayer(), combat.getFirstPlayer(), effects, combatId)
        };
        doAttackSectionRunes(beforeNormalNormalDefenceRunes, params, combat);
    }

    /**
     * 接受普通攻击后
     *
     * @param combat
     * @param effects
     * @return
     */
    public void runAfterAcceptAttackRunes(Combat combat, List<Effect> effects) {
        if (ListUtil.isEmpty(effects)) {
            return;
        }
        Player firstPlayer = combat.getFirstPlayer();
        Player secondPlayer = combat.getSecondPlayer();
        long combatId = combat.getId();
        CombatStage afterAcceptAttackRunes = CombatRunesStageTool.getAfterAcceptAttackRunes();
        CombatRunesParam param1 = CombatRunesParam.instance(firstPlayer, secondPlayer, combatId);
        param1.setReceiveEffect(effects);
        CombatRunesParam param2 = CombatRunesParam.instance(secondPlayer, firstPlayer, combatId);
        param2.setReceiveEffect(effects);
        CombatRunesParam[] params = {param1, param2};
        doAttackSectionRunes(afterAcceptAttackRunes, params, combat);
    }

    /**
     * 处理物理攻击前后的技能
     *
     * @param combatStage
     * @param params
     * @param combat
     */
    private void doAttackSectionRunes(CombatStage combatStage, CombatRunesParam[] params, Combat combat) {
        for (CombatRunesParam param : params) {
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!combatStage.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                int seq = combat.getAnimationSeq();
                param.setSeq(combat.getAnimationSeq());
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        runPerformSkillRunes(combat, null, action.getEffects());
                        //接受效果
                        skillRoundService.produceEffects(combat, action.getEffects());
                    }
                }
            }
        }
    }
    /**
     * 死亡技能执行前
     *
     * @param combat
     */
    public void runBeforeDieSectionRunes(Combat combat, BattleCard performCard, Player performPlayer, Player oppoPlayer) {
        // 卡牌不存在 不处理
        if (performCard == null) {
            return;
        }
        // 卡牌存活状态时 不处理
        if (performCard.isAlive()) {
            return;
        }
        long combatId = combat.getId();
        CombatStage beforeDieSectionRunes = CombatRunesStageTool.getBeforeDieSectionRunes();
        CombatRunesParam firstParam = CombatRunesParam.instance(performPlayer, oppoPlayer, combatId);
        firstParam.setPerformCard(performCard);
        CombatRunesParam[] params = {firstParam};
        doBeforeDieSectionRunes(beforeDieSectionRunes, params, combat);
    }
    /**
     * 处理死亡技能前的符文
     *
     * @param combatStage
     * @param params
     * @param combat
     */
    private void doBeforeDieSectionRunes(CombatStage combatStage, CombatRunesParam[] params, Combat combat) {
        for (CombatRunesParam param : params) {
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!combatStage.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                int seq = combat.getAnimationSeq();
                param.setSeq(combat.getAnimationSeq());
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        runPerformSkillRunes(combat, null, action.getEffects());
                        //接受效果
                        skillRoundService.produceEffects(combat, action.getEffects());
                    }
                }
            }
        }
    }
    public List<Effect> runPerformSkillRunes(Combat combat, BattleCard performCard, List<Effect> effects) {
        if (ListUtil.isEmpty(effects)) {
            return effects;
        }
        Player firstPlayer = combat.getFirstPlayer();
        Player secondPlayer = combat.getSecondPlayer();
        long combatId = combat.getId();
        CombatStage performSkillRunes = CombatRunesStageTool.getPerformSkillRunes();
        CombatRunesParam param1 = CombatRunesParam.instance(firstPlayer, secondPlayer, performCard, combatId);
        param1.setReceiveEffect(effects);
        CombatRunesParam param2 = CombatRunesParam.instance(secondPlayer, firstPlayer, performCard, combatId);
        param2.setReceiveEffect(effects);
        CombatRunesParam[] params = {param1, param2};
        List<Effect> newEffects = doSkillSectionRunes(performSkillRunes, params, combat);
        effects.addAll(newEffects);
        return effects;
    }

    /**
     * 法术防御前
     *
     * @param combat
     * @param effects
     */
    public List<Effect> runBeforeSkillDefenceRunes(Combat combat, List<Effect> effects) {
        return effects;
//        if (ListUtil.isEmpty(effects)) {
//            return effects;
//        }
//        Player firstPlayer = combat.getFirstPlayer();
//        Player secondPlayer = combat.getSecondPlayer();
//        long combatId = combat.getId();
//        CombatStage beforeSkillDefenceRunes = CombatRunesStageTool.getBeforeSkillDefenceRunes();
//        CombatRunesParam param1 = CombatRunesParam.instance(firstPlayer, secondPlayer, effects, combatId);
//        CombatRunesParam param2 = CombatRunesParam.instance(secondPlayer, firstPlayer, effects, combatId);
//        CombatRunesParam[] params = {param1, param2};
//        List<Effect> newEffects = doSkillSectionRunes(beforeSkillDefenceRunes, params, combat);
//        effects.addAll(newEffects);
//        return effects;
    }

    /**
     * 法术阶段效果
     *
     * @param combatStage
     * @param params
     * @param combat
     * @return
     */
    private List<Effect> doSkillSectionRunes(CombatStage combatStage, CombatRunesParam[] params, Combat combat) {
        List<Effect> effects = new ArrayList<>();
        for (CombatRunesParam param : params) {
            for (Integer rune : param.getPerformPlayerRunesId()) {
                if (!combatStage.exist(rune)) {
                    continue;
                }
                IRoundStageRunes roundStageRunes = factory.matchRoundStageRunes(rune);
                if (null == roundStageRunes) {
                    continue;
                }
                int seq = combat.getAnimationSeq();
                param.setSeq(combat.getAnimationSeq());
                Action action = roundStageRunes.doRoundRunes(param);
                if (action.getTakeEffect()) {
                    addClientAnim(combat, seq, action, param);
                    if (action.existsEffect()) {
                        effects.addAll(action.getEffects());
                    }
                }
            }
        }
        return effects;
    }

    private void addClientAnim(Combat combat, int seq, Action action, CombatRunesParam param) {
        // 技能内部自定义实现了动画
        combat.setAnimationSeq(param.getSeq());
        if (!action.getClientActions().isEmpty()) {
            combat.addAnimations(action.getClientActions());
            return;
        }
        //如果不需要动画，则不补加动画
        if (!action.isNeedAddAnimation()) {
            return;
        }
        Player performPlayer = param.getPerformPlayer();
        if (ListUtil.isEmpty(action.getEffects())) {
            if (action.getTakeEffect()) {
                for (Integer buff : performPlayer.getBuffs()) {
                    combat.addAnimation(ClientAnimationService.getSkillAction(seq, buff, PositionService.getZhaoHuanShiPos(performPlayer.getId())));
                }
            }
            return;
        }
        for (Effect effect : action.getEffects()) {
            int runesId = action.getEffects().get(0).getSourceID();
            AnimationSequence as = ClientAnimationService.getSkillAction(seq, runesId, PositionService.getZhaoHuanShiPos(performPlayer.getId()), effect.getTargetPos());
            combat.addAnimation(as);
        }
    }


}