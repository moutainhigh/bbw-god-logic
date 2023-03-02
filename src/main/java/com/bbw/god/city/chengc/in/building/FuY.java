package com.bbw.god.city.chengc.in.building;

import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.gameuser.GameUser;

/**
 * 府衙
 * 
 * @author suhq
 * @date 2018年11月29日 下午3:19:45
 */
public class FuY extends Building {
	public FuY(UserCity userCity) {
		this.userCity = userCity;
		this.bType = BuildingEnum.FY;
	}

	@Override
	public RDBuildingInfo getBuildingInfo() {
		RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getFy(), getNextLevelInfo());
		return buildingInfo;
	}

	@Override
	protected String getNextLevelInfo() {
		return "";
	}

	@Override
	protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
		userCity.setFy(userCity.getFy() + 1);
	}

	@Override
	protected int getNeedCopperForUpdate() {
		return 0;
	}

}
