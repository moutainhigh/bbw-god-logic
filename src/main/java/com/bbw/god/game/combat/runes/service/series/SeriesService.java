package com.bbw.god.game.combat.runes.service.series;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;

/**
 * 系列服务
 *
 * @author: suhq
 * @date: 2021/11/16 11:22 上午
 */
public abstract class SeriesService {
    protected int[] series;

    /**
     * 对手卡牌造成的***系技能伤害增加***
     *
     * @param runeId
     * @param param
     * @param addRate
     * @return
     */
    public void addOpponentInjure(Action action, int runeId, CombatRunesParam param, double addRate) {
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
                continue;
            }
            if (!isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            doAddInjure(runeId, effect, addRate, param, action);
        }
    }
    /**
     *      * 对手卡牌造成的***系技能伤害增加***
     *
     * @param action
     * @param runeId
     * @param param
     * @param addValue 添加的值
     */
    public void addOpponentInjure(Action action, int runeId, CombatRunesParam param, int addValue) {
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
                continue;
            }
            if (!isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            doAddInjure(runeId, effect, addValue, param, action);
        }
    }



    /**
     * 己方卡牌造成的***系技能伤害增加***
     *
     * @param runeId
     * @param param
     * @param addRate
     * @return
     */
    public Action addInjure(int runeId, CombatRunesParam param, double addRate) {
        Action action = new Action();
        //序号+1
        param.getNextSeq();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
                continue;
            }
            if (!isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            doAddInjure(runeId, effect, addRate, param, action);
            if (action.isNeedAddAnimation()) {
                action.addClientAction(ClientAnimationService.getSkillAction(param.getSeq(), runeId, param.getMyPlayerPos(), effect.getSourcePos()));
            }
        }
        return action;
    }


    /**
     * 己方卡牌受到的***系技能伤害减少***
     *
     * @param runeId
     * @param param
     * @param deductRate
     * @return
     */
    public Action deductInjure(int runeId, CombatRunesParam param, double deductRate) {
        Action action = new Action();
        //序号+1
        param.getNextSeq();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
                continue;
            }
            if (isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            doDeductInjure(runeId, effect, deductRate, param, action);
            if (action.isNeedAddAnimation()) {
                action.addClientAction(ClientAnimationService.getSkillAction(param.getSeq(), runeId, param.getMyPlayerPos(), effect.getSourcePos()));
            }
        }
        return action;
    }

    /**
     * 检查是否是系列技能
     *
     * @param skillId
     * @return
     */
    public boolean check(int skillId) {
        for (int id : series) {
            if (id == skillId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行伤害增加
     *
     * @param runeId
     * @param effect
     * @param addRate
     * @param param
     * @param action
     */
    abstract void doAddInjure(int runeId, Effect effect, double addRate, CombatRunesParam param, Action action);

    /**
     * 执行伤害减少
     *
     * @param runeId
     * @param effect
     * @param deductRate
     * @param param
     * @param action
     */
    abstract void doDeductInjure(int runeId, Effect effect, double deductRate, CombatRunesParam param, Action action);
    /**
     * 执行伤害增加
     *
     * @param runeId
     * @param effect
     * @param addValue 增加多少值
     * @param param
     * @param action
     */
    abstract void doAddInjure(int runeId, Effect effect, int addValue, CombatRunesParam param, Action action);


    /**
     * 是否是己方发动的技能效果
     *
     * @param sourcePos
     * @param playerId
     * @return
     */
    private boolean isPerformSelf(int sourcePos, PlayerId playerId) {
        PlayerId source = PositionService.getPlayerIdByPos(sourcePos);
        return source.getValue() == playerId.getValue();
    }

    /**
     * 是否是地方发动的技能效果
     *
     * @param sourcePos
     * @param playerId
     * @return
     */
    private boolean isPerformOpponent(int sourcePos, PlayerId playerId) {
        PlayerId source = PositionService.getPlayerIdByPos(sourcePos);
        return source.getValue() != playerId.getValue();
    }
}
