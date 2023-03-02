package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 毒纹符131630绿：己方卡牌造成的毒系技能伤害增加25%
 * ①　毒系技能：瘟君、入痘、流毒。
 * ②　瘟君、入痘需要增加的是中毒状态造成的伤害。
 *
 * @author fzj
 * @date 2021/9/26 11:28
 */
@Service
public class Runes131630 implements IRoundStageRunes {
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DU_WEN.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        return duSeriesService.addInjure(getRunesId(), param, 0.25);
    }
}
