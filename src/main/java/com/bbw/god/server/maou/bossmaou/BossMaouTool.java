package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgBossMaou;

import java.util.List;
import java.util.Optional;

/**
 * @author suhq
 * @description: 魔王boss工具类
 * @date 2019-12-20 14:15
 **/
public class BossMaouTool {
    /**
     * 获取魔王Boss的总配置信息
     *
     * @return
     */
    public static CfgBossMaou getConfig() {
        CfgBossMaou srcConfig = Cfg.I.getUniqueConfig(CfgBossMaou.class);
        return CloneUtil.clone(srcConfig);
    }

    /**
     * 获取魔王Boss信息
     *
     * @param baseMaouId
     * @return
     */
    public static CfgBossMaou.BossMaou getBossMaouConfig(int baseMaouId) {
        CfgBossMaou config = getConfig();
        return config.getMaous().stream().filter(tmp -> tmp.getId() == baseMaouId).findFirst().get();
    }

    /**
     * 获得血量变化
     *
     * @param attackContinueMinutes
     * @param lostBloodRate
     * @return
     */
    public static int getIncBlood(int attackContinueMinutes, int lostBloodRate) {
        CfgBossMaou cfg = getConfig();
        List<CfgBossMaou.BloodRule> bloodRules = cfg.getBloodRules();
        Optional<CfgBossMaou.BloodRule> bloodRuleOp = bloodRules.stream().filter(tmp -> {
            boolean isMatch = tmp.getMinTime() <= attackContinueMinutes && tmp.getMaxTime() >= attackContinueMinutes;
            isMatch = isMatch && tmp.getMinlostBloodRate() <= lostBloodRate && tmp.getMaxlostBloodRate() >= lostBloodRate;
            return isMatch;
        }).findFirst();
        if (bloodRuleOp.isPresent()) {
            return bloodRuleOp.get().getBloodInc();
        }
        return 0;
    }
}
