package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuSlotRate;

import java.util.ArrayList;
import java.util.List;

/**
 * 防御符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
public abstract class PlayerDefenceRune implements IInitStageRunes {

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        // 符文阶数
        int runeHv = getRunesId() % 10;
        // 基础防御加成值
        int baseDefence = getMultipleTypeBaseDefence();
        // 等级步进值
        int levelStep = getMultipleTypeLevelStep(runeHv);
        List<TypeEnum> cardTypes = getCardTypes();
        if (!hasMultipleTypes()) {
            // 不存在多种作用属性
            TypeEnum cardType = TypeEnum.fromValue((getRunesId() - 232000) / 10 + 10);
            cardTypes.add(cardType);
            levelStep = getBaseLevelStep(runeHv);
            baseDefence = 30;
        }
        int buffAddHp = baseDefence + 10 * (runeHv - 1) + levelStep * combatBuff.getLevel();
        //计算符图槽属性加成
        final int finalAddHp = buffAddHp + buffAddHp * combatBuff.getExtraRate() / CfgFuTuSlotRate.FUTU_SLOT_BASE_RATE;
        param.getPerformPlayer().getDrawCards().stream().filter(tmp -> cardTypes.contains(tmp.getType())).forEach(p -> {
            p.setRoundHp(p.getRoundHp() + finalAddHp);
            p.setHp(p.getHp() + finalAddHp);
            p.setInitHp(p.getInitHp() + finalAddHp);
        });
    }

    /**
     * 是否存在多种属性
     * 注：攻击符图存在作用多种属性 必须重写
     *
     * @return
     */
    boolean hasMultipleTypes() {
        return false;
    }

    /**
     * 获取作用的卡牌属性
     * 注：攻击符图存在作用多种属性 必须重写
     *
     * @return
     */
    List<TypeEnum> getCardTypes() {
        return new ArrayList<>();
    }

    /**
     * 多属性基础防御值值
     *
     * @return
     */
    private int getMultipleTypeBaseDefence() {
        return 49;
    }


    /**
     * 多属性等级加成步进
     *
     * @param hv
     * @return
     */
    private int getMultipleTypeLevelStep(int hv) {
        switch (hv) {
            case 5:
                return 28;
            case 4:
            case 3:
            case 2:
            case 1:
                return 5 * hv;
        }
        return 0;
    }

    /**
     * 等级加成步进
     *
     * @param hv
     * @return
     */
    private int getBaseLevelStep(int hv) {
        switch (hv) {
            case 5:
                return 40;
            case 4:
                return 25;
            case 3:
            case 2:
            case 1:
                return 5 * hv;
        }
        return 0;
    }
}