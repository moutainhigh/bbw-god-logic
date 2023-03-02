package com.bbw.god.road;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.CfgShakeProp;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路径服务
 *
 * @author suhq
 * @date 2020-10-14 11:59
 **/
@Slf4j
public class RoadPathTool {
    public final static RoadPathTool I = new RoadPathTool();
    private Map<String, List<List<PathRoad>>> pathCache = new HashMap<>();
    private Map<String, Map<Integer, List<PathRoad>>> typeRoadGroups = new HashMap<>();

    private RoadPathTool() {
    }

    public synchronized void init() {
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        for (CfgRoadEntity road : roads) {
            List<Integer> dirs = new ArrayList<>();
            if (road.getUpId() != null) {
                dirs.add(1);
            }
            if (road.getLeftId() != null) {
                dirs.add(2);
            }
            if (road.getRightId() != null) {
                dirs.add(3);
            }
            if (road.getDownId() != null) {
                dirs.add(4);
            }
            for (Integer dir : dirs) {
                for (int i = 1; i <= 18; i++) {
                    cachePath(road, dir, i);
                }
            }
        }
    }

    /**
     * 获得随机路径
     *
     * @param beginPos
     * @param dir
     * @param maxPathLength
     * @param minPathLength
     * @param shakePropConfig
     * @return
     */
    protected List<PathRoad> getRandomPath(int beginPos, int dir, int maxPathLength, int minPathLength, CfgShakeProp shakePropConfig) {
        //获取所有全路径
        List<List<PathRoad>> allPaths = getAllPaths(beginPos, dir, maxPathLength);
//        System.out.println("截止全路径执行时间：" + (System.currentTimeMillis() - begin));
        //获取目标位置
        PathRoad targetLocation = RoadPathTool.I.getTargetLocation(beginPos, dir, maxPathLength, minPathLength, shakePropConfig);
//        System.out.println("截止目标位置执行时间：" + (System.currentTimeMillis() - begin));
        // 目标路径
        List<PathRoad> targetFullPath = allPaths.get(targetLocation.getBelongToPath());
        List<PathRoad> realTargetPath = targetFullPath.subList(0, targetLocation.getIndexInPath() + 1);
        return realTargetPath;
    }

    /**
     * 获得目标路径
     *
     * @param beginPos
     * @param dir
     * @param pathLength
     * @return
     */
    public List<PathRoad> getTargetPath(int beginPos, int dir, int pathLength) {
        //获取所有全路径
        List<List<PathRoad>> allPaths = RoadPathTool.I.getAllPaths(beginPos, dir, pathLength);
        List<PathRoad> realTargetPath = allPaths.get(PowerRandom.getRandomBySeed(allPaths.size()) - 1);
        return realTargetPath;
    }

    private void cachePath(CfgRoadEntity road, int dir, int pathLength) {
        String key = road.getId() + "_" + dir + "_" + pathLength;
        List<List<PathRoad>> allPaths = generateAllPaths(road.getId(), dir, pathLength);
        Map<Integer, List<PathRoad>> typeRoadGroup = makeTypeRoadGroup(allPaths);
//        System.out.println(key);
        pathCache.put(key, allPaths);
        typeRoadGroups.put(key, typeRoadGroup);
    }

    /**
     * 获取全路径
     *
     * @param beginPos
     * @param dir
     * @param pathLength
     * @return
     */
    public List<List<PathRoad>> getAllPaths(int beginPos, int dir, int pathLength) {
        String key = beginPos + "_" + dir + "_" + pathLength;
        if (pathCache.containsKey(key)) {
            return pathCache.get(key);
        }
        log.error("不应执行到这里generateAllPaths:" + key);
        return generateAllPaths(beginPos, dir, pathLength);
    }

    /**
     * 获取目标位置
     *
     * @param beginPos
     * @param dir
     * @param maxLength
     * @param minLength
     * @param shakeProp
     * @return
     */
    private PathRoad getTargetLocation(int beginPos, int dir, int maxLength, int minLength, CfgShakeProp shakeProp) {
//        long begin = System.currentTimeMillis();
        Map<Integer, List<PathRoad>> typeRoadGroup = getTypeRoadGroup(beginPos, dir, maxLength);
        PathRoad targetLocation = null;
        do {
            int targetCityType = getTargetCityType(typeRoadGroup, shakeProp);
            int targetRoadIndex = PowerRandom.getRandomBySeed(typeRoadGroup.get(targetCityType).size()) - 1;
            targetLocation = typeRoadGroup.get(targetCityType).get(targetRoadIndex);
        } while (targetLocation.getIndexInPath() + 1 < minLength);
//        System.out.println("获取目标位置，时间：" + (System.currentTimeMillis() - begin));
        return targetLocation;
    }

    private Map<Integer, List<PathRoad>> getTypeRoadGroup(int beginPos, int dir, int pathLength) {
        String key = beginPos + "_" + dir + "_" + pathLength;
        if (typeRoadGroups.containsKey(key)) {
            return typeRoadGroups.get(key);
        }
        log.error("不应执行到这里makeTypeRoadGroup:" + key);
        return makeTypeRoadGroup(getAllPaths(beginPos, dir, pathLength));
    }

    /**
     * 生成所有最大路径
     *
     * @param beginPos
     * @param dir
     * @param pathLength
     * @return
     */
    private List<List<PathRoad>> generateAllPaths(int beginPos, int dir, int pathLength) {
//        long begin = System.currentTimeMillis();
        CfgRoadEntity road = RoadTool.getRoadById(beginPos);
        List<List<PathRoad>> allPaths = new ArrayList<>();
        Integer nextRoadId = road.getCellByNextDirection(dir);
        if (nextRoadId == null) {
            dir = road.getNextDirection(dir, 5 - dir);
            nextRoadId = road.getCellByNextDirection(dir);
        }
        CfgRoadEntity nextRoad = RoadTool.getRoadById(nextRoadId);
        PathRoad fistRoad = new PathRoad(nextRoad, dir);
        List<PathRoad> roads = new ArrayList<>();
        roads.add(fistRoad);
        doGeneratePaths(allPaths, roads, fistRoad, pathLength - 1);
//        System.out.println("获取所有全路径执行时间：" + (System.currentTimeMillis() - begin));
        return allPaths;
    }

    private void doGeneratePaths(List<List<PathRoad>> allPaths, List<PathRoad> path, PathRoad prePathRoad, int pathLength) {

        List<PathRoad> nextRoads = new ArrayList<>();
        if (pathLength == 0) {
            allPaths.add(path);
            return;
        }
        addRoad(nextRoads, prePathRoad);
        int remainStep = pathLength - 1;
        for (PathRoad nextRoad : nextRoads) {
            List<PathRoad> expandPath = new ArrayList<>();
            expandPath.addAll(path);
            expandPath.add(nextRoad);
            doGeneratePaths(allPaths, expandPath, nextRoad, remainStep);
        }
    }


    /**
     * 获取一次摇骰子所有可到达的某一类型的位置分组
     *
     * @param allPaths
     * @return
     */
    private Map<Integer, List<PathRoad>> makeTypeRoadGroup(List<List<PathRoad>> allPaths) {
        // 使用flatMap+groupBy性能<<<<<传统写法
        //        Map<String, Long> cityMap = allPaths.stream().flatMap(Collection::stream).collect(Collectors.groupingBy(tmp -> CityTool.getCityByRoadId(tmp.getRoad().getId()).getName(), Collectors.counting()));
//        long begin = System.currentTimeMillis();
        Map<Integer, List<PathRoad>> cityRoadsMap = new HashMap<>(16);
        for (int i = 0; i < allPaths.size(); i++) {
            List<PathRoad> path = allPaths.get(i);
            for (int j = 0; j < path.size(); j++) {
                PathRoad location = path.get(j);
                location.setBelongToPath(i);
                location.setIndexInPath(j);
                //处理获取一次摇骰子所有可到达某一类型的位置对应的次数
                Integer type = CityTool.getCityByRoadId(location.getRoad().getId()).getType();
                List<PathRoad> roads = cityRoadsMap.get(type);
                if (roads == null) {
                    roads = new ArrayList<>();
                }
                roads.add(location);
                cityRoadsMap.put(type, roads);
            }
        }
//        System.out.println("获取一次摇骰子所有可到达某一类型城池对应的位置的执行时间：" + (System.currentTimeMillis() - begin));
        return cityRoadsMap;
    }

    /**
     * 获取目标城市类型
     *
     * @param typeRoadGroup
     * @return
     */
    private int getTargetCityType(Map<Integer, List<PathRoad>> typeRoadGroup, CfgShakeProp shakeProp) {
        List<CfgShakeProp.CfgCityProp> cityProps = shakeProp.getCityProps();
        Map<Integer, Integer> cityNumMap = new HashMap<>();
        int sumProp = 0;
        for (Map.Entry<Integer, List<PathRoad>> cityRoadEntry : typeRoadGroup.entrySet()) {
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
        return targetType;
    }

    private void addRoad(List<PathRoad> roads, PathRoad prePathRoad) {
        int excludeDir = 5 - prePathRoad.getDir();
        CfgRoadEntity road = prePathRoad.getRoad();
        if (excludeDir != 1) {
            addRoad(roads, road.getUpId(), 1);
        }
        if (excludeDir != 2) {
            addRoad(roads, road.getLeftId(), 2);
        }
        if (excludeDir != 3) {
            addRoad(roads, road.getRightId(), 3);
        }
        if (excludeDir != 4) {
            addRoad(roads, road.getDownId(), 4);
        }
    }

    private void addRoad(List<PathRoad> roads, Integer roadId, int dir) {
        if (roadId != null) {
            roads.add(new PathRoad(RoadTool.getRoadById(roadId), dir));
        }
    }

}
