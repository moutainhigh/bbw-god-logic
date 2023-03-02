package com.bbw.god.game.combat.runes.service;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;

/**
 * 回合结束时执行
 *
 * @author: suhq
 * @date: 2022/5/25 1:58 下午
 */
public interface IRoundEndStageRunes extends IBaseRunesService {

    Action doRoundEndRunes(CombatRunesParam param);
}
