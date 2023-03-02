package com.bbw.god.game.config.city;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.TypeEnum;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 格子工具类
 * 
 * @author suhq
 * @date 2019-07-19 16:23:26
 */
public class RoadTool {

	private static final List<Integer> MainCityId= Arrays.asList(2725,2608,1024,4325);

	public static CfgRoadEntity getRoadById(int roadId) {
		CfgRoadEntity road = Cfg.I.get(roadId, CfgRoadEntity.class);
		if (road == null) {
			throw CoderException.high("无效的格子" + roadId);
		}
		return road;
	}

	@NonNull
	public static CfgRoadEntity getRandomRoad(int excludeRoadId) {
		List<CfgRoadEntity> roads = getRoads();
		List<CfgRoadEntity> randomRoads = PowerRandom.getRandomsFromList(roads, 2);
		return randomRoads.stream().filter(r -> r.getId() != excludeRoadId).findFirst().get();
	}

	/***
	 * 随机一座主城
	 * @return
	 */
	public static CfgRoadEntity getRandomMainCityRoad() {
		List<CfgRoadEntity> roads = getRoads();
		List<CfgRoadEntity> list = roads.stream().filter(p -> MainCityId.contains(p.getId())).collect(Collectors.toList());
		return PowerRandom.getRandomFromList(list);
	}

	/***
	 * 随机一座指定属性的区域
	 * @return
	 */
	public static CfgRoadEntity getRandomRoadByProperty(TypeEnum typeEnum) {
		List<CfgRoadEntity> roads = getRoads();
		List<CfgRoadEntity> list = roads.stream().filter(p -> p.getCountry()==typeEnum.getValue()).collect(Collectors.toList());
		return PowerRandom.getRandomFromList(list);
	}
	/***
	 * 随机野怪所在地
	 * @return
	 */
	public static CfgRoadEntity getRandomYdRoad() {
		List<Integer> allYeGuaiPos = CityTool.getAllYeGuaiPos();
		Integer id = PowerRandom.getRandomFromList(allYeGuaiPos);
		List<CfgRoadEntity> roads = getRoads();
		return roads.stream().filter(p -> p.getId().equals(id)).findFirst().get();
	}

	public static List<CfgRoadEntity> getRoads() {
		return Cfg.I.get(CfgRoadEntity.class);
	}

	/**
	 * 获取所有神仙可能生成的位置
	 * @return
	 */
	public static List<CfgGodRoadEntity> getGodRoads(){
		return Cfg.I.get(CfgGodRoadEntity.class);
	}

}
