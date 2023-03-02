package com.bbw.god.city.chengc.in.building;

import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.gameuser.GameUser;

/**
 * 道场
 * 
 * @author suhq
 * @date 2018年11月29日 下午3:17:29
 */
public class DaoC extends Building {

	public DaoC(UserCity userCity) {
		this.userCity = userCity;
		this.bType = BuildingEnum.DC;
	}

	@Override
	public RDBuildingInfo getBuildingInfo() {
		RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getDc(), getNextLevelInfo());
		buildingInfo.setRemainTimes(1);
		return buildingInfo;
	}

	@Override
	protected String getNextLevelInfo() {
        int nextBuildingLevel = userCity.getDc() + 1;
        if (nextBuildingLevel > getBuildingMaxLevel()) {
            return "";
        }
        return "本城练兵经验+" + nextBuildingLevel * 10 + "%";
    }

	@Override
	protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
		userCity.setDc(userCity.getDc() + 1);
	}

	@Override
	protected int getNeedCopperForUpdate() {
		return 3000 * (int) Math.pow(2, userCity.getDc());
	}

}
