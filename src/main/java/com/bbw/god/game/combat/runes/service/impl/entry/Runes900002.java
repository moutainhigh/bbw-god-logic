package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 长生词条 ：诛仙阵野怪血量专用：一级可复活一次，复活期间本回合内不会受到任何伤害
 * @author: hzf
 * @create: 2022-12-15 15:42
 **/
@Service
public class Runes900002 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return  RunesEnum.CHANG_SHENG_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {

        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int lv = combatBuff.getLevel();


        //是否能触发发送
        boolean cantToPerform =  combatBuff.getTriggeredNum() >= lv && param.getRound() != combatBuff.getLastPerformRound();
        if (cantToPerform) {
            return action;
        }
        CardValueEffect effect = (CardValueEffect) param.getReceiveEffect().get(0);
        int effectHp = effect.getRoundHp() + effect.getHp();
        if (effectHp > 0 ) {
            return action;
        }
        //判断是不是对手先死
        if (param.getOppoPlayer().getHp() <= 0) {
            return action;
        }
        //发动处理
        if (combatBuff.getTriggeredNum() <= lv) {
            //濒死判断
            if (effectHp + param.getPerformPlayer().getHp() <= 0) {
                param.getPerformPlayer().setBloodBarNum(param.getPerformPlayer().getBloodBarNum() -1);

                combatBuff.setLastPerformRound(param.getRound());
                combatBuff.setTriggeredNum(combatBuff.getTriggeredNum()+1);
                //免疫伤害
                int playerPos = PositionService.getZhaoHuanShiPos(param.getPerformPlayer().getId());
                //免疫伤害
                effect.setHp(0);
                effect.setRoundHp(0);

           

                CardValueEffect recoverEffect = CardValueEffect.getSkillEffect(getRunesId(), playerPos);
                recoverEffect.setSequence(param.getNextSeq());
                int hpToRecover = param.getPerformPlayer().getMaxHp() - param.getPerformPlayer().getHp();
                recoverEffect.setHp(hpToRecover);
                action.addEffect(recoverEffect);

                return action;
            }
            return action;

        }
        //发动回合免疫所有伤害
        if (effectHp < 0 && param.getRound() == combatBuff.getLastPerformRound() ) {
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
