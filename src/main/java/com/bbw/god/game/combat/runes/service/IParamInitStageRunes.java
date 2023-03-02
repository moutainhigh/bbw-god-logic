package com.bbw.god.game.combat.runes.service;

import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;

/**
 * 初始化参数阶段
 *
 * @author: suhq
 * @date: 2021/9/25 7:57 上午
 */
public interface IParamInitStageRunes extends IBaseRunesService {

    void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam);
}
