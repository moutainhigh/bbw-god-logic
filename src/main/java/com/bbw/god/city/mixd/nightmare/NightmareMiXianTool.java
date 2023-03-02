package com.bbw.god.city.mixd.nightmare;

import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
public class NightmareMiXianTool {

    public static CfgNightmareMiXian getCfg() {
        return Cfg.I.getUniqueConfig(CfgNightmareMiXian.class);
    }

    /**
     * 获得层主最小卡牌数
     *
     * @return
     */
    public static Integer getLevelOwnerMinCardNum() {
        return getCfg().getLevelOwnerMinCardNum();
    }

    /**
     * 获取指定层的 关卡生成规则
     *
     * @param level
     * @return
     */
    public static List<CfgNightmareMiXian.LevelData> getLevelData(int level) {
        for (CfgNightmareMiXian.LevelDataRule rule : getCfg().getLevelDataRules()) {
            if (!(rule.getMinLevel() <= level && level <= rule.getMaxLevel())) {
                continue;
            }
            return rule.getPosData();
        }
        return null;
    }

    /**
     * 获取迷仙洞 巡使ID
     * @param mxdLevel 迷仙洞层
     * @param pos 位置
     * @return  1 0000 00 0000
     */
    public static long buildMxdAiId(int mxdLevel,int pos,int posType){
        String condition="-1%04d%02d%04d";
        String val=String.format(condition,mxdLevel,pos,posType);
        return Long.valueOf(val);
    }

    /**
     * 通过ID获取类型
     * @param aiId
     * @return
     */
    public static NightmareMiXianPosEnum getTypeByAiId(long aiId){
        Long val = Math.abs(aiId % 10000);
        return NightmareMiXianPosEnum.fromType(val.intValue());
    }

    /**
     * 通过ID获取层
     * @param aiId
     * @return
     */
    public static int getLevelByAiId(long aiId){
        Long val = Math.abs(aiId / 1000000 %10000);
        return val.intValue();
    }
}
