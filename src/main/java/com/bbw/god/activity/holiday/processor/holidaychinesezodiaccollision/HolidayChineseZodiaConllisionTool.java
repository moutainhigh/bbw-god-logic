package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生肖对碰参数配置工具
 *
 * @author: huanghb
 * @date: 2023/2/9 17:44
 */
public class HolidayChineseZodiaConllisionTool {
    /**
     * 获得配置类
     *
     * @return
     */
    public static CfgChineseZodiacConllision getCfg() {
        return Cfg.I.getUniqueConfig(CfgChineseZodiacConllision.class);
    }

    /**
     * 获得所有生肖地图
     *
     * @return
     */
    public static List<CfgChineseZodiacConllision.ChineseZodiacMap> getChineseZodiacMaps() {
        return getCfg().getChineseZodiacMaps();
    }

    /**
     * 获得单个生肖地图
     *
     * @param mapLevel 地图等级
     * @return
     */
    public static CfgChineseZodiacConllision.ChineseZodiacMap getSingleChineseZodiacMap(int mapLevel) {
        return getChineseZodiacMaps().stream().filter(tmp -> tmp.isMatch(mapLevel)).findFirst().orElse(getChineseZodiacMaps().get(0));
    }

    /**
     * 生肖地图初始化
     *
     * @param mapLevel
     * @return
     */
    public static String[] initChineseZodiacMapByMapLevel(int mapLevel) {
        CfgChineseZodiacConllision.ChineseZodiacMap singleChineseZodiacMap = getSingleChineseZodiacMap(mapLevel);
        //总的生肖id
        List<String> totalChineseZodiacIds = Arrays.stream(ChineseZodiacEnum.values()).map(ChineseZodiacEnum::getId).collect(Collectors.toList());
        //根据地图等级获得对应数量的生肖id
        List<String> partChineseZodiacIds = PowerRandom.getRandomsFromList(totalChineseZodiacIds, singleChineseZodiacMap.getNeedChineseZodiacNum());
        List<String> partChineseZodiacIdsClones = CloneUtil.cloneList(partChineseZodiacIds);
        partChineseZodiacIdsClones.addAll(CloneUtil.cloneList(partChineseZodiacIds));
        Integer mapSize = singleChineseZodiacMap.getSize();
        /**  生成随机地图下标集合*/
        List<Integer> mapIndexList = PowerRandom.getRandomIndexsForList(mapSize, mapSize);
        String[] chineseZodiacMap = new String[mapSize];
        for (Integer mapIndex : mapIndexList) {
            if (ListUtil.isNotEmpty(partChineseZodiacIdsClones)) {
                chineseZodiacMap[mapIndex] = chineseZodiacInfoListToStr(partChineseZodiacIdsClones.get(0), FlipStatusEnum.NOT_OPEN.getStatus());
                partChineseZodiacIdsClones.remove(0);
                continue;
            }
            String randomOneChineseZodiacId = PowerRandom.getRandomFromList(partChineseZodiacIds);
            partChineseZodiacIdsClones.add(CloneUtil.clone(randomOneChineseZodiacId));
            String s =
                    chineseZodiacMap[mapIndex] = chineseZodiacInfoListToStr(randomOneChineseZodiacId, FlipStatusEnum.NOT_OPEN.getStatus());
        }
        return chineseZodiacMap;
    }

    /**
     * 获得生肖碰撞信息
     *
     * @return
     */
    public static CfgChineseZodiacConllision.Collision getCollisionInfo() {
        return getCfg().getCollisions().stream().filter(CfgChineseZodiacConllision.Collision::getValid)
                .collect(Collectors.toList()).get(0);
    }

    /**
     * 生肖信息list转str
     *
     * @param chineseZodiacId 生肖id
     * @param staus           状态 0 未翻开 1翻开 2 碰牌成功
     * @return
     */
    public static String chineseZodiacInfoListToStr(String chineseZodiacId, int staus) {
        return chineseZodiacId + "," + staus;
    }

    /**
     * 根据地图大肖获得地图等级
     *
     * @param mapSize 地图大小
     * @return
     */
    public static Integer getMapLevelBySize(int mapSize) {
        CfgChineseZodiacConllision.ChineseZodiacMap chineseZodiacMap = getChineseZodiacMaps().stream().filter(tmp -> tmp.getSize() == mapSize).findFirst().orElse(null);
        if (null != chineseZodiacMap) {
            return chineseZodiacMap.getLevel();
        }
        throw CoderException.high(String.format("程序员没有编写mapSize=%s的生肖地图", mapSize));
    }
}
