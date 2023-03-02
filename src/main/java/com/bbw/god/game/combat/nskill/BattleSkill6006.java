package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.BattleSkillLog;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 莲座 6006: 在场时，下1张死亡的我方未拥有【长生】的卡牌立刻满状态回到场上，每五阶多作用1张卡牌。拥有【莲座】的该卡牌离场后，该卡牌上的【莲座】直到战斗结束前失效。
 *
 * @author longwh
 * @date 2023/1/13 9:18
 */
@Service
public class BattleSkill6006 extends BattleSkillService {

    @Override
    public int getMySkillId() {
        return 6006;
//        return CombatSkillEnum.LIAN_ZUO.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action action = new Action();
        BattleCard performCard = psp.getPerformCard();
        // 没有卡牌 或者 没有死亡 不处理
        if (null == performCard) {
            return action;
        }
        // 卡牌存在长生不处理
        Optional<BattleSkill> skill = performCard.getSkill(CombatSkillEnum.CS.getValue());
        if (skill.isPresent()) {
            return action;
        }
        if (performCard.isAlive()) {
            return action;
        }
        if (!canTrigger(psp)) {
            return action;
        }
        // 处理移动效果致卡牌死亡
        if (psp.getReceiveEffect().isPositionEffect()) {
            psp.getReceiveEffect().toPositionEffect().setValid(false);
        }
        // 触发【莲座】我方卡牌死亡 满状态回到场上
        recoveryInitialState(performCard, action);
        return action;
    }

    /**
     * 恢复为初始状态
     *
     * @param card
     * @param action
     */
    private void recoveryInitialState(BattleCard card, Action action) {
        // 攻击和血量 恢复为初始值
        int hp = card.getInitHp() - card.getHp() + BattleCardService.posGainHp(card);
        int atk = card.getInitAtk() - card.getAtk() + BattleCardService.posGainAtk(card);
        CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), card.getPos());
        effect.setRoundAtk(atk);
        effect.setRoundHp(hp);
        if (card.getNormalAttackSkill().getTimesLimit().getBanFrom() > 0) {
            card.getNormalAttackSkill().getTimesLimit().reset();
        }
        card.resetBuffStatus(false);
        card.setAlive(true);
        // 将 回合血量和攻击设置成 现有的血量和攻击，防止 hp和atk 加错
        card.setRoundHp(card.getHp());
        card.setRoundAtk(card.getAtk());
        action.addEffect(effect);
    }

    /**
     * 莲座是否可以触发
     *
     * @param psp
     * @return true 可触发， false 不可触发
     */
    private boolean canTrigger(PerformSkillParam psp) {
        // 获取玩家本回合战场、坟场卡牌
        List<BattleCard> doCards = psp.getPerformPlayer().getDiscard().stream().filter(card ->
                card.getRoundEndCardStatus().stream().anyMatch(status -> status.getRound() == psp.getCombat().getRound())).collect(Collectors.toList());
        BattleCard[] playingCards = psp.getPerformPlayer().getPlayingCards();
        doCards.addAll(Arrays.asList(playingCards));
        // 获取拥有莲座技能的卡牌
        List<BattleCard> skillCards = doCards.stream().filter(card ->
                card != null && card.getSkill(6006).isPresent()).collect(Collectors.toList());
        for (BattleCard skillCard : skillCards) {
            // 随机附加【莲座】效果（死亡触发） 每五阶多作用1张卡牌
            int canTriggerTimes = skillCard.getHv() / 5 + 1;
            // 技能回合生效次数
            List<BattleSkillLog> skillLogs = skillCard.getHistorySkillLogs().stream().filter(skillLog ->
                    skillLog.getSkillId() == getMySkillId() &&
                            skillLog.getRound() == psp.getCombat().getRound()).collect(Collectors.toList());
            int times = skillLogs.stream().mapToInt(value -> value.getTargetsPos().size()).sum();
            // 生效次数 不超过 可触发次数 可以执行
            if (times < canTriggerTimes) {
                Optional<BattleSkill> skill = skillCard.getSkill(getMySkillId());
                if (!skill.isPresent()) {
                    continue;
                }
                if (psp.getPerformCard().getImgId() != skillCard.getImgId()) {
                    // 卡牌未拥有莲座技能 莲座效果生效时 添加执行记录
                    skillCard.addSkillLog(getMySkillId(), psp.getCombat().getRound(), psp.getPerformCard().getPos());
                }
                return true;
            }
        }
        return false;
    }
}