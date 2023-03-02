package com.bbw.god.game.config.city;

import com.bbw.BaseTest;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.CfgShakeProp;
import com.bbw.god.gameuser.shake.ShakeRandomParam;
import com.bbw.god.road.RoadPathService;
import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoadToolTest extends BaseTest {
    @Autowired
    private RoadPathService roadPathService;

    @Test
    public void getPath() {
        CityTool.init();
        int beginPos = 3724;
        int dir = 3;
        int maxPathLength = 6;
        ShakeRandomParam shakeRandomParam = new ShakeRandomParam();
        shakeRandomParam.setGuLevel(9);
        for (int i = 0; i < 100; i++) {
            roadPathService.getRandomPath(beginPos, dir, maxPathLength, 3, shakeRandomParam);
        }
//        String result = "路径长度：" + path.size() + " 路径：" + path.stream().map(tmp -> {
//            int tmpRoadId = tmp.getRoad().getId();
//            return CityTool.getCityByRoadId(tmpRoadId).getName() + ":" + tmpRoadId + ":" + tmp.getDir();
//        }).collect(Collectors.toList()).toString();
        System.out.println(beginPos);
    }

    public String getPath(int beginPos, int dir, int pathLength) {
        System.out.println("\n");
        System.out.println(CityTool.getCityByRoadId(beginPos).getName());
        long executeBegin = System.currentTimeMillis();
        long begin = System.currentTimeMillis();
        CfgShakeProp shakeProp = Cfg.I.get("默认", CfgShakeProp.class);
        CfgRoadEntity road = RoadTool.getRoadById(beginPos);
        List<List<Location>> allPaths = new ArrayList<>();
        //获取所有全路径
        generateAllPaths(allPaths, new ArrayList<>(), new Location(road, dir), pathLength);
        System.out.println("获取所有全路径执行时间：" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        // 使用flatMap+groupBy性能<<<<<传统写法
        //        Map<String, Long> cityMap = allPaths.stream().flatMap(Collection::stream).collect(Collectors.groupingBy(tmp -> CityTool.getCityByRoadId(tmp.getRoad().getId()).getName(), Collectors.counting()));
        //获取一次摇骰子所有可到达某一类型的位置对应的次数
        Map<Integer, List<Location>> cityRoadsMap = new HashMap<>(16);
        for (int i = 0; i < allPaths.size(); i++) {
            List<Location> path = allPaths.get(i);
            for (int j = 0; j < path.size(); j++) {
                Location location = path.get(j);
                location.setBelongToPath(i);
                location.setIndexInPath(j);
                //处理获取一次摇骰子所有可到达某一类型的位置对应的次数
                Integer type = CityTool.getCityByRoadId(location.getRoad().getId()).getType();
                List<Location> roads = cityRoadsMap.get(type);
                if (roads == null) {
                    roads = new ArrayList<>();
                }
                roads.add(location);
                cityRoadsMap.put(type, roads);
            }
        }
        System.out.println("获取一次摇骰子所有可到达某一类型城池对应的位置的执行时间：" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        List<CfgShakeProp.CfgCityProp> cityProps = shakeProp.getCityProps();
        Map<Integer, Integer> cityNumMap = new HashMap<>();
        int sumProp = 0;
        for (Map.Entry<Integer, List<Location>> cityRoadEntry : cityRoadsMap.entrySet()) {
            CfgShakeProp.CfgCityProp cfgCityProp = cityProps.stream().filter(cityProp -> cityProp.getType().intValue() == cityRoadEntry.getKey()).findFirst().orElse(null);
            int prop = 1;
            if (cfgCityProp != null) {
                prop = cfgCityProp.getProp();
            }
            int realProp = cityRoadEntry.getValue().size() * prop;
            cityNumMap.put(cityRoadEntry.getKey(), realProp);
            sumProp += realProp;
        }
        Integer targetType = null;
        int random = PowerRandom.getRandomBySeed(sumProp);
        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : cityNumMap.entrySet()) {
            sum += entry.getValue();
            if (sum >= random) {
                targetType = entry.getKey();
                break;
            }
        }
        System.out.println("获取目标类型:" + targetType + "执行时间：" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        Location targetLocation = cityRoadsMap.get(targetType).get(PowerRandom.getRandomBySeed(cityRoadsMap.get(targetType).size()) - 1);
        List<Location> targetFullPath = allPaths.get(targetLocation.getBelongToPath());
        List<Location> realTargetPath = targetFullPath.subList(0, targetLocation.getIndexInPath() + 1);
        System.out.println("获取真实路径执行时间：" + (System.currentTimeMillis() - begin));
        String result = "真实路径耗时：" + (System.currentTimeMillis() - executeBegin) + "，路径长度：" + realTargetPath.size() + "路径：" + realTargetPath.stream().map(tmp -> {
            int tmpRoadId = tmp.getRoad().getId();
            return CityTool.getCityByRoadId(tmpRoadId).getName() + ":" + tmpRoadId + ":" + tmp.getDir();
        }).collect(Collectors.toList()).toString();
        System.out.println(result);

        System.out.println("路径数：" + allPaths.size());
        for (List<Location> avaibleCells : allPaths) {
            System.out.println(avaibleCells.stream().map(tmp -> {
                int tmpRoadId = tmp.getRoad().getId();
                return CityTool.getCityByRoadId(tmpRoadId).getName() + ":" + tmpRoadId + ":" + tmp.getDir();
            }).collect(Collectors.toList()).toString());
        }
        System.out.println("可能到达的格子总数：" + allPaths.size() * pathLength);
//        cityNumMap.entrySet().forEach(entry -> System.out.println(entry.getKey() + ":" + entry.getValue()));
        return result;
    }

    public void generateAllPaths(List<List<Location>> allPaths, List<Location> path, Location location, int pathLength) {
        List<Location> nextRoads = new ArrayList<>();
        if (pathLength == 0) {
            allPaths.add(path);
            return;
        }
        int excludeDir = 5 - location.getDir();
        CfgRoadEntity road = location.getRoad();
        if (excludeDir != 1) {
            addRoad(nextRoads, road.getUpId(), 1);
        }
        if (excludeDir != 2) {
            addRoad(nextRoads, road.getLeftId(), 2);
        }
        if (excludeDir != 3) {
            addRoad(nextRoads, road.getRightId(), 3);
        }
        if (excludeDir != 4) {
            addRoad(nextRoads, road.getDownId(), 4);
        }
        int remainStep = pathLength - 1;
        for (Location nextRoad : nextRoads) {
            List<Location> expandPath = new ArrayList<>();
            expandPath.addAll(path);
            expandPath.add(nextRoad);
            generateAllPaths(allPaths, expandPath, nextRoad, remainStep);
        }
    }

    private void addRoad(List<Location> roads, Integer roadId, int dir) {
        if (roadId != null) {
            roads.add(new Location(RoadTool.getRoadById(roadId), dir));
        }
    }

    @Data
    public class Location {
        private CfgRoadEntity road;
        private Integer dir;
        private Integer belongToPath;
        private Integer indexInPath;

        public Location(CfgRoadEntity road, int dir) {
            this.road = road;
            this.dir = dir;
        }
    }

}