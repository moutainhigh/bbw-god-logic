package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 毒纹符图	1阶	战斗开始时，有30%概率（可升级）发动，己方卡牌造成的毒系技能伤害增加25%。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes231006 extends PlayerStatusRune {
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DU_WEN_PLAYER.getRunesId();
    }

    @Override
    Action handleStatus(CombatRunesParam param) {
        return duSeriesService.addInjure(getRunesId(), param, 0.25);
    }
}
