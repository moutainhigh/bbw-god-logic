package com.bbw.god.gameuser.yaozu;

import com.bbw.god.game.config.Cfg;


import java.util.List;
import java.util.stream.Collectors;

/**
 * 妖族来犯工具类
 *
 * @author fzj
 * @date 2021/9/6 14:48
 */
public class YaoZuTool {
    /**
     * 获取全部妖族的配置信息
     * @return
     */
    public static List<CfgYaoZuEntity> getAllYaoZu() {
        return Cfg.I.get(CfgYaoZuEntity.class);
    }

    /**
     *  根据id获取对应妖族的配置信息
     * @param yaoZuId
     * @return
     */
    public static CfgYaoZuEntity getYaoZu(int yaoZuId) {
        return Cfg.I.get(yaoZuId,CfgYaoZuEntity.class);
    }

    /**
     * 根据妖族类型获取对应配置信息
     * @param yaoZuType
     * @return
     */
    public static List<CfgYaoZuEntity> getYaoZuByYaoZuType(int yaoZuType){
        List<CfgYaoZuEntity> cfgYaoZuEntities = getAllYaoZu().stream().filter(cfgYaoZu -> cfgYaoZu.getYaoZuType() == yaoZuType).collect(Collectors.toList());
        return cfgYaoZuEntities;
    };

}
