package com.bbw.god.game.combat.runes.service;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 回合开始时，有**概率发动，使符文发动方卡牌**技能无法发动
 *
 * @author longwh
 * @date 2023/1/4 10:58
 */
public abstract class AbstractForbidSkillsRunes implements IRoundStageRunes {
    /**
     * 基础概率值: 10% 等价于 10
     *
     * @return
     */
    public abstract Integer getBaseProb();

    /**
     * 等级概率步进值: 10% 等价于 10
     *
     * @return
     */
    public abstract Integer getLevelProbStep();

    /**
     * 待禁用的技能
     *
     * @return
     */
    public abstract List<Integer> getSkillsToForbid();

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(getBaseProb(), getLevelProbStep())) {
            return action;
        }
        Player player = param.getPerformPlayer();
        // 获取符文发动方的手牌、牌堆、坟场、战场卡牌
        List<BattleCard> cardList = new ArrayList<>();
        cardList.addAll(player.getHandCardList());
        cardList.addAll(player.getDrawCards());
        cardList.addAll(player.getPlayingCards(true));
        cardList.addAll(player.getDiscard());
        for (BattleCard card : cardList) {
            if (null == card) {
                continue;
            }
            for (Integer skillToBan : getSkillsToForbid()) {
                Optional<BattleSkill> skillOp = card.getSkill(skillToBan);
                // 不存在
                if (!skillOp.isPresent()) {
                    continue;
                }
                // 禁用技能一个回合
                skillOp.get().getTimesLimit().forbidOneRound(getRunesId());
            }
        }
        //触发 补充一个动画
        AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), param.getMyPlayerPos());
        action.addClientAction(amin);
        return action;
    }
}