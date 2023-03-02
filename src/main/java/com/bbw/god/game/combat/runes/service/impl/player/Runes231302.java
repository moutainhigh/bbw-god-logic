package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IParamInitStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 红砂符图	4阶	战斗开始时，有30%概率（可升级）发动，使敌方1个符图无法发动。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes231302 implements IParamInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.HONG_SHA_PLAYER.getRunesId();
    }

    @Override
    public void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam) {
        List<CombatBuff> oppBuffs = oppInitParam.getBuffs();
        //对方没有符图
        if (ListUtil.isEmpty(oppBuffs)) {
            return;
        }
        List<CombatBuff> oppFutus = oppBuffs.stream()
                .filter(tmp -> !isExcluded(tmp.getRuneId()))
                .collect(Collectors.toList());
        //对方没有符图
        if (ListUtil.isEmpty(oppFutus)) {
            return;
        }
        //对方有且仅有红砂符图
        if (oppFutus.size() == 1 && oppFutus.get(0).getRuneId() == getRunesId()) {
            return;
        }

        CombatBuff combatBuff = performInitParam.gainBuff(getRunesId());
        //符图生效触发及检测
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }

        //随机移除一个符图，且不能是红砂符图
        CombatBuff buffToRemove;
        int tryRandomLimit = 20;
        do {
            tryRandomLimit--;
            buffToRemove = PowerRandom.getRandomFromList(oppFutus);
        } while (buffToRemove.getRuneId() == getRunesId() && tryRandomLimit > 0);

        CombatBuff finalBuffToRemove = buffToRemove;
        oppInitParam.getBuffs().removeIf(tmp -> tmp.getRuneId() == finalBuffToRemove.getRuneId());

    }

    /**
     * 使敌方1个符图无法发动时，需要排除的的符图
     *
     * @param runeId
     * @return
     */
    private boolean isExcluded(int runeId) {
        if (runeId < 200000) {
            return true;
        }
        if (runeId >= 232000 && runeId <= 234999) {
            return true;
        }
        if (runeId == RunesEnum.CARD_EQUIPMENT.getRunesId()) {
            // 存在卡牌装备效果（不包含主角卡） 则排除
            return true;
        }
        return false;
    }
}