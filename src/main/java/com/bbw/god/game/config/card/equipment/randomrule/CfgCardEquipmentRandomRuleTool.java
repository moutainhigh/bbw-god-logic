package com.bbw.god.game.config.card.equipment.randomrule;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.equipment.randomrule.CfgCardEquipmentRandomRuleEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 装备工具类
 * @author: hzf
 * @create: 2022-12-14 10:05
 **/
public class CfgCardEquipmentRandomRuleTool {

    public static CfgCardEquipmentRandomRuleEntity getCfg(Integer fightType) {
        return Cfg.I.get(fightType,CfgCardEquipmentRandomRuleEntity.class);
    }

    /**
     * 获取至宝
     * @return
     */
    public static List<CfgCardEquipmentRandomRuleEntity.CfgZhiBao> getCfgZhiBaos(Integer fightType){
        return getCfg(fightType).getZhiBaos();
    }

    /**
     * 获取仙决
     * @return
     */
    public static List<CfgCardEquipmentRandomRuleEntity.CfgXianJue> getXianJues(Integer fightType){
        return getCfg(fightType).getXianJues();
    }

    /**
     * 将Map转化成字符串
     * @param map
     * @return
     */
    public static String convertWithStream(Map<String, ?> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(","));
        return mapAsString;
    }

    /**
     * 将字符串转化成map
     * @param mapAsString
     * @return
     */
    public static Map<String, Integer> convertWithStream(String mapAsString) {
        Map<String, String> map = Arrays.stream(mapAsString.split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        Map<String,Integer> newMap = new HashMap<>();
        //转化成String,Integer
        map.forEach((key, value) -> newMap.put(key, Integer.parseInt(value)));
        return newMap;
    }


}
