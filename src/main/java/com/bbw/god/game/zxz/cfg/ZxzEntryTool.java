package com.bbw.god.game.zxz.cfg;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.zxz.enums.ZxzEntryTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 诛仙阵词条工具类
 * @author: hzf
 * @create: 2022-09-22 22:09
 **/

public class ZxzEntryTool {

    /**
     * 获取配置
     * @return
     */
    public static List<CfgZxzEntryEntity> getZxzEntrys(){
        return Cfg.I.get(CfgZxzEntryEntity.class);
    }

    /**
     * 根据词条Id获取配置
     * @param entryId
     * @return
     */
    public static CfgZxzEntryEntity getEntryById(Integer entryId){
        return Cfg.I.get(entryId,CfgZxzEntryEntity.class);
    }

    /**
     * 根据词条档位获取信息
     * @param gear
     * @return
     */
    public static List<CfgZxzEntryEntity> getEntryByGear(Integer gear){
        return getZxzEntrys().stream().filter(entry -> entry.getGear().equals(gear) && entry.getType() != ZxzEntryTypeEnum.ENTRY_TYPE_40.getEntryType()).collect(Collectors.toList());
    }
    /**
     * 获取灵装词条效果
     * @param lv
     * @return
     */
    public static CfgLingZhuangEntryEntity getZxzLingZhuangEntry(Integer lv){
        return Cfg.I.get(lv,CfgLingZhuangEntryEntity.class);
    }

    /**
     * 根据类型获取词条
     * @param type
     * @return
     */
    public static List<CfgZxzEntryEntity> getEntryByType(Integer type){
        return getZxzEntrys().stream()
                .filter(entry -> entry.getType().equals(type))
                .collect(Collectors.toList());

    }

}
