package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.JuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 刃纹符131650绿：己方卡牌造成的狙系技能伤害增加25%。
 * ①　狙系技能：飞狙、双狙、狙杀。
 * ②　狙杀在增加伤害的同时也要增加触发技能的判定范围
 *
 * @author fzj
 * @date 2021/9/26 11:28
 */
@Service
public class Runes131650 implements IRoundStageRunes {
    @Autowired
    private JuSeriesService juSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.REN_WEN.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        return juSeriesService.addInjure(getRunesId(), param, 0.25);
    }
}
