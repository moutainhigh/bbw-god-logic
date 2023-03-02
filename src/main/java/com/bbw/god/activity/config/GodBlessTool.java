package com.bbw.god.activity.config;

import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 上仙祝福工具类
 * @date 2020/10/19 14:39
 **/
public class GodBlessTool {

    /**
     * 获取上仙祝福奖励集合
     *
     * @return
     */
    public static List<CfgGodBlessAward> getAwards() {
        return Cfg.I.get(CfgGodBlessAward.class);
    }

    public static List<CfgGodBlessAward> getAwards(Integer type) {
        return getAwards().stream().filter(tmp -> tmp.getType().equals(type)).collect(Collectors.toList());
    }

    public static CfgGodBlessAward getAwardsById(Integer id) {
        return getAwards().stream().filter(tmp -> tmp.getId().equals(id)).findFirst().orElse(null);
    }

    public static CfgGodBlessAward getAwardsByIndex(Integer index) {
        return getAwards().stream().filter(tmp -> null != tmp.getIndex() && tmp.getIndex().equals(index)).findFirst().orElse(null);
    }

}
