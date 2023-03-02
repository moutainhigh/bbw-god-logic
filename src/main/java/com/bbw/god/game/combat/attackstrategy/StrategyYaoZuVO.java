package com.bbw.god.game.combat.attackstrategy;

import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author fzj
 * @date 2021/9/24 17:34
 */
@Data
public class StrategyYaoZuVO extends AbstractStrategyVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /** 本体攻略 */
    private StrategyVO ontology;
    /** 镜像攻略 */
    private StrategyVO mirroring;
    public static StrategyYaoZuVO instance(StrategyVO ontology, StrategyVO mirroring){
        StrategyYaoZuVO vo=new StrategyYaoZuVO();
        vo.setMirroring(mirroring);
        vo.setOntology(ontology);
        vo.setDatetimeInt(DateUtil.toDateTimeLong());
        return vo;
    }
}
