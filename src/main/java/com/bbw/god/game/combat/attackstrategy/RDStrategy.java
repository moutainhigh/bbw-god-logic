package com.bbw.god.game.combat.attackstrategy;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * @author：lwb
 * @date: 2020/11/27 16:46
 * @version: 1.0
 */
@Data
public class RDStrategy extends RDSuccess {
    private List<StrategyVO> list;
}
