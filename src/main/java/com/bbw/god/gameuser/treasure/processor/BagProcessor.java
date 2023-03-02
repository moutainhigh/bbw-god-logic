package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.random.box.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 开宝箱、卡包等
 * 
 * @author suhq
 * @date 2018年11月29日 下午2:05:55
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BagProcessor extends TreasureUseProcessor {
	@Autowired
	private BoxService boxService;

	public BagProcessor() {
		this.treasureEnum = null;// 处理多种包类道具，该类实际未用到该参数
		this.isAutoBuy = false;
	}

	@Override
	public boolean isMatch(int treasureId) {
		return treasureId > 500 && treasureId < 1000;
	}

	/**
	 * 是否宝箱类
	 *
	 * @return
	 */
	@Override
	public boolean isChestType() {
		return true;
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		// 包裹开卡包、宝箱
		int treasureId = param.getProId();
		CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(treasureId);
		WayEnum way = WayEnum.fromName(treasureEntity.getName());
		boxService.open(gu.getId(), treasureId, way, rd);


	}
}
