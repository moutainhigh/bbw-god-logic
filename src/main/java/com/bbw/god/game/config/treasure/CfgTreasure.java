package com.bbw.god.game.config.treasure;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CfgTreasure implements CfgInterface, CfgPrepareListInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	// 财神珠有效步数
	private Integer treasureEffectCSZ;
	// 落宝金钱有效步数
	private Integer treasureEffectLBJQ;
	// 青鸾有效步数
	private Integer treasureEffectQL;
	// 四不像有效步数
	private Integer treasureEffectSBX;
	// 玉麒麟+10体力
	private Integer treasureEffectYQL;

	private List<CfgTreasureEntity> treasures;
	private List<CfgTreasureEntity> oldTreasures;
	private Map<Integer, List<CfgTreasureEntity>> treasureMapByStar;
	private Map<Integer, List<CfgTreasureEntity>> oldTreasureMapByStar;
	private Map<Integer, List<CfgTreasureEntity>> treasureByType;

	@Override
	public void prepare() {
		treasures = TreasureTool.getAllTreasures().stream().filter(t -> t.getType() <= 20).collect(Collectors.toList());
		oldTreasures = treasures.stream().filter(t -> t.getId() < 430).collect(Collectors.toList());
		treasureMapByStar = treasures.stream().collect(Collectors.groupingBy(CfgTreasureEntity::getStar));
		oldTreasureMapByStar = oldTreasures.stream().collect(Collectors.groupingBy(CfgTreasureEntity::getStar));
		treasureByType = TreasureTool.getAllTreasures().stream().collect(Collectors.groupingBy(CfgTreasureEntity::getType));
		log.info("道具预准备完成");

	}

	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}
}
