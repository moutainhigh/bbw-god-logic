package com.bbw.god.game.combat.attackstrategy;

import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author：lwb
 * @date: 2020/12/30 18:35
 * @version: 1.0
 */
@Data
public class StrategyNightmareVO extends AbstractStrategyVO implements Serializable {
    private static final long serialVersionUID = -1L;
    private StrategyVO jinWei;

    public static StrategyNightmareVO instance(StrategyVO jinWei) {
        StrategyNightmareVO vo = new StrategyNightmareVO();
        vo.setJinWei(jinWei);
        vo.setDatetimeInt(DateUtil.toDateTimeLong());
        return vo;
    }
}
