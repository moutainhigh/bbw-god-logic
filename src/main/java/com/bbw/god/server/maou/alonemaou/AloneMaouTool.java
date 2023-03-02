package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgAloneMaou;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 独战魔王工具类
 * @date 2019-12-20 14:15
 **/
public class AloneMaouTool {
    /**
     * 获取独战魔王的总配置信息
     *
     * @return
     */
    public static CfgAloneMaou getConfig() {
        CfgAloneMaou config = Cfg.I.getUniqueConfig(CfgAloneMaou.class);
        return CloneUtil.clone(config);
    }

    /**
     * 获取某个级别的独战魔王信息
     *
     * @param maouLevel
     * @return
     */
    public static CfgAloneMaou.AloneMaou getMaouConfig(int maouLevel) {
        CfgAloneMaou config = getConfig();
        CfgAloneMaou.AloneMaou aloneMaou = config.getMaous().stream().filter(tmp -> tmp.getLevel() == maouLevel).findFirst().get();
        return CloneUtil.clone(aloneMaou);
    }

    /**
     * 获得彩蛋魔王骰子概率
     *
     * @param accBlood
     * @return
     */
    public static int getMaouDiceProb(int accBlood) {
        CfgAloneMaou config = getConfig();
        CfgAloneMaou.MaouDiceProb maouDiceProb = config.getMaouDiceProbs().stream().filter(tmp -> accBlood >= tmp.getMinAccBlood() && accBlood <= tmp.getMaxAccBlood()).findFirst().get();
        return maouDiceProb.getProb();
    }

    /**
     * 获得魔王奖励
     *
     * @param type
     * @param maouLevel
     * @return
     */
    public static List<Award> getMaouLeveAward(int type, int maouLevel) {
        CfgAloneMaou config = getConfig();
        CfgAloneMaou.MaouLevelAward maouLevelAward = config.getMaouLevelAwards().stream()
                .filter(tmp -> tmp.getType() == type && tmp.getMaouLevel() == maouLevel)
                .findFirst().orElse(null);
        if (maouLevelAward == null) {
            return new ArrayList<>();
        }
        return maouLevelAward.getAwards();
    }

    public static int getMaouIndex(int type, int level) {
        return type * 10 + level;
    }
}
