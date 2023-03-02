package com.bbw.god.game.config.city;

import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgBuff;
import com.bbw.god.game.config.TypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家无关的城池方法
 *
 * @author suhq
 * @date 2018年10月22日 上午11:22:36
 */
public class CityTool {
    private static Map<Integer, CfgCityEntity> roadCityMap = new HashMap<>();

    public static void init() {
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        for (CfgRoadEntity road : roads) {
            roadCityMap.put(road.getId(), road.getCity());
        }
    }

    /**
     * 获取城市
     *
     * @param id
     * @return
     */
    public static CfgCityEntity getCityById(int id) {
        CfgCityEntity cfgCity = Cfg.I.get(id, CfgCityEntity.class);
        if (cfgCity == null) {
            throw CoderException.high("无效的城池" + id);
        }
        return cfgCity;
    }

    /**
     * 获取城市
     *
     * @param name
     * @return
     */
    public static ChengC getChengCByName(String name) {
        if (StrUtil.isBlank(name)){
            throw CoderException.high("无效的城池" + name);
        }
        List<ChengC> chengCList = getChengCs();
        for (ChengC c:chengCList){
           if (c.getName().equals(name)){
               return c;
           }
        }
        throw CoderException.high("无效的城池" + name);
    }

    /**
     * 获取城池配置信息
     *
     * @param cityId
     * @return
     */
    public static ChengC getChengc(final int cityId) {
        Optional<ChengC> cc = getChengCs().stream().filter(tmp -> tmp.getId() == cityId).findFirst();
        if (!cc.isPresent()) {
            throw CoderException.high("城池" + cityId + "在CfgChengC没有配置数据");
        }
        return cc.get();
    }

    public static ChengC getNightmareChengC(final int cityId) {
        Optional<ChengC> cc = getNightmareChengCs().stream().filter(tmp ->
                tmp.getId() == cityId).findFirst();
        if (!cc.isPresent()) {
            throw CoderException.high("城池" + cityId + "在CfgNightmareChengC没有配置数据");
        }
        return cc.get();
    }

    public static List<CfgCityEntity> getCountryCities(int country) {
        return getCities().stream().filter(city -> RoadTool.getRoadById(city.getAddress1()).getCountry() == country).collect(Collectors.toList());
    }

    public static List<CfgCityEntity> getCCCities(int propertyType) {
        return getCities().stream()
                .filter(city ->city.getProperty() == propertyType && city.isCC())
                .collect(Collectors.toList());
    }

    public static CfgCityEntity getCityByRoadId(int roadId) {
        return roadCityMap.get(roadId);
    }

    public static int getCcCount() {
        return getChengCs().size();
    }

    public static List<CfgCityEntity> getCities() {
        return Cfg.I.get(CfgCityEntity.class);
    }

    public static List<ChengC> getChengCs() {
        return Cfg.I.getUniqueConfig(CfgChengC.class).getChengCs();
    }

    public static List<ChengC> getNightmareChengCs() {
        return Cfg.I.getUniqueConfig(CfgNightmareChengC.class).getChengCs();
    }

    /**
     * 根据拥有的梦魇城池 获取加成值
     * @param ownNightmareCities
     * @return
     */
    public static double getNightmareAdd(int ownNightmareCities){
        if (ownNightmareCities==0){
            return 0.0;
        }
        CfgNightmareChengC config = Cfg.I.getUniqueConfig(CfgNightmareChengC.class);
        for (CfgBuff buffAdd : config.getBuffAdds()) {
            if (buffAdd.getMin() <= ownNightmareCities && ownNightmareCities <= buffAdd.getMax()) {
                return buffAdd.getAdd();
            }
        }
        return 0.0;
    }


    public static Integer getCityId(int position) {
        List<CfgCityEntity> cities = getCities();
        CfgCityEntity cfgCityEntity = cities.stream().filter(c ->
                c.getAddress1().equals(position) || c.getAddress2().equals(position)).findFirst().orElse(null);
        if (cfgCityEntity != null) {
            return cfgCityEntity.getId();
        }
        return 0;
    }

    /**
     * 获取城池BUFF 没有则返回null
     *
     * @param cityId
     * @return
     */
    public static Integer getCityBuff(int cityId) {
        ChengC chengC = getNightmareChengC(cityId);
        return chengC.getBuff();
    }

    /**
     * 获取所有野怪的位置
     *
     * @return
     */
    public static List<Integer> getAllYeGuaiPos() {
        List<CfgCityEntity> yeGuaiPoslist = getCities().stream().filter(p -> p.isYG()).collect(Collectors.toList());
        List<Integer> posList = new ArrayList<>();
        for (CfgCityEntity cityEntity : yeGuaiPoslist) {
            posList.add(cityEntity.getAddress1());
        }
        return posList;
    }

    /**
     * 根据指定等级 获取所有城池的Id
     * @param lv
     * @return
     */
    public static List<Integer> getAllCityIdByLevel(int lv){
        List<CfgCityEntity> cities = getCities();
        List<Integer> ids=new ArrayList<>();
        for (CfgCityEntity cityEntity : cities) {
            if (cityEntity.isCC() && cityEntity.getLevel()==lv){
                ids.add(cityEntity.getId());
            }
        }
        return ids;
    }

    /**
     * 根据城池级别和属性  随机获取一个符合的城池
     * @param cityLv
     * @param type
     * @return
     */
    public static ChengC getRandomCityByLeveType(int cityLv, TypeEnum type){
        List<CfgCityEntity> cities = getCities();
        List<Integer> cityIds=new ArrayList<>();
        for (CfgCityEntity cityEntity : cities) {
            if (!cityEntity.isCC()){
                continue;
            }
            if (cityEntity.getLevel()==cityLv && cityEntity.getProperty()==type.getValue()){
                cityIds.add(cityEntity.getId());
            }
        }
        Integer randomCityId = PowerRandom.getRandomFromList(cityIds);
        return getChengc(randomCityId);
    }

    /**
     * 获取城池进阶阵容
     * @param cityId
     * @return
     */
    public static String getCfgPromoteSoliders(int cityId){
        CfgPromoteEntity entity = Cfg.I.get(cityId, CfgPromoteEntity.class);
        if (entity==null){
            return "";
        }
        return entity.getSoliders();
    }

    /**
     * 获取所有属性城池 按等级划分
     * @return
     */
    public static Map<Integer,List<Integer>> getAllCitiesId(int property){
        Map<Integer,List<Integer>> map=new HashMap<>();
        for (CfgCityEntity city : getCities()) {
            if (city.isCC() && city.getProperty().intValue()==property){
                List<Integer> list = map.get(city.getLevel());
                if (list==null) {
                    list=new ArrayList<>();
                }
                list.add(city.getId());
                map.put(city.getLevel(),list);
            }
        }
        return map;
    }
}
