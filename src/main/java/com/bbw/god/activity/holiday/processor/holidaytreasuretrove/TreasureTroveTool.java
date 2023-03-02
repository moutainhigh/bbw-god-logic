package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.Optional;

/**
 * 藏宝秘境工具类
 *
 * @author: huanghb
 * @date: 2021/12/22 15:27
 */
public class TreasureTroveTool {

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgTreasureTrove getTroveCfg() {
        return Cfg.I.getUniqueConfig(CfgTreasureTrove.class);
    }

    public static List<CfgTreasureTrove.TroveAward> getBigAwardPool() {
        return getTroveCfg().getBigAwardPool();
    }

    /**
     * 获得奖池
     *
     * @return
     */

    public static List<CfgTreasureTrove.TroveAward> getNormalAwardPool() {
        return getTroveCfg().getNormalAwardPool();
    }

    /**
     * 获得宝藏奖励
     *
     * @param mallId
     * @return
     */
    public static CfgTreasureTrove.TroveAward getTroveAward(int mallId) {
        CfgTreasureTrove troveCfg = getTroveCfg();
        Optional<CfgTreasureTrove.TroveAward> op = troveCfg.getNormalAwardPool().stream().filter(tmp -> tmp.getId() == mallId).findFirst();
        if (op.isPresent()) {
            return op.get();
        }
        op = troveCfg.getBigAwardPool().stream().filter(tmp -> tmp.getId() == mallId).findFirst();
        return op.get();
    }
}
