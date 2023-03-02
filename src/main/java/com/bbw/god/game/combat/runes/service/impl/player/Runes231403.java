package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 长生符图
 * 阶级：5阶
 * 初始效果：己方召唤师受到致死伤害时，有30%概率（可升级）在该回合免疫所有伤害并恢复全部血量，一场战斗触发一次。
 * 成长效果：每级增加7%概率，满级100%。
 * （1）当己方召唤师受到的伤害≥当前召唤师血量时，会使该伤害无效，并将血量恢复至最大值。
 * （2）触发该效果后，直到本回合结束前，己方召唤师不会受到任何途径造成的血量减少。
 *
 * @author: suhq
 * @date: 2021/11/16 9:50 上午
 */
@Service
public class Runes231403 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.CHANG_SHENG_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        //是否能触发发送
        boolean cantToPerform = combatBuff.getLastPerformRound() >= 0 && param.getRound() != combatBuff.getLastPerformRound();
        if (cantToPerform) {
            return action;
        }
        CardValueEffect effect = (CardValueEffect) param.getReceiveEffect().get(0);
        int effectHp = effect.getRoundHp() + effect.getHp();
        //判断是不是对手先死
        if (effectHp + param.getOppoPlayer().getHp() < 0) {
            return action;
        }
        //发动处理
        if (combatBuff.getLastPerformRound() < 0) {
            //濒死判断
            if (effectHp + param.getPerformPlayer().getHp() > 0) {
                return action;
            }
            if (!combatBuff.ifToPerform(30, 7)) {
                return action;
            }
            combatBuff.setLastPerformRound(param.getRound());
            //免疫伤害
            effect.setHp(0);
            effect.setRoundHp(0);
            //发送时回满血
            CardValueEffect recoverEffect = CardValueEffect.getSkillEffect(getRunesId(), param.getMyPlayerPos());
            int hpToRecover = param.getPerformPlayer().getMaxHp() - param.getPerformPlayer().getHp();
            recoverEffect.setHp(hpToRecover);
            recoverEffect.setSequence(param.getNextSeq());
            action.addEffect(recoverEffect);
            return action;
        }
        //发动回合免疫所有伤害
        if (effectHp < 0) {
            effect.setHp(0);
            effect.setRoundHp(0);
            action.addEffect(effect);
            param.getReceiveEffect().clear();
            AnimationSequence as = new AnimationSequence(effect);
            AnimationSequence.Animation animation = new AnimationSequence.Animation();
            animation.setHp(0);
            animation.setPos(effect.getTargetPos());
            as.setSeq(effect.getSequence());
            as.add(animation);
            action.addClientAction(as);
        }
        return action;
    }
}
