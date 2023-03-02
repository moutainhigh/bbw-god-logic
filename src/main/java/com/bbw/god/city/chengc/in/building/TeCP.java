package com.bbw.god.city.chengc.in.building;

import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUser;

import java.util.List;

/**
 * 特产铺
 * 
 * @author suhq
 * @date 2018年11月29日 下午3:27:31
 */
public class TeCP extends Building {

	public TeCP(UserCity userCity) {
		this.userCity = userCity;
		this.bType = BuildingEnum.TCP;
	}

	@Override
	public RDBuildingInfo getBuildingInfo() {
		RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getTcp(), getNextLevelInfo());
		return buildingInfo;
	}

	@Override
	public String getNextLevelInfo() {
		int nextBuildingLevel = userCity.getTcp() + 1;
		if (nextBuildingLevel > getBuildingMaxLevel()) {
			return "";
		}
		CfgCityEntity city = userCity.gainCity();
		int specialId = getSpecialByTcpLevel(city, nextBuildingLevel);
		if (nextBuildingLevel == 7) {
			return "买卖折扣、溢价5%";
		} else if (nextBuildingLevel == 9) {
			return "买卖折扣、溢价10%";
		} else {
			return "解锁【" + SpecialTool.getSpecialById(specialId).getName() + "】";
		}
	}

	@Override
	protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
		userCity.setTcp(userCity.getTcp() + 1);

		List<RDTradeInfo.RDCitySpecial> citySpecials = rdArriveChengC.getCitySpecials();
		int specialId = 0;
		for (RDTradeInfo.RDCitySpecial special : citySpecials) {
			if (special.getLevel()<=userCity.getTcp() && special.getStatus()==-1){
				special.setStatus(0);
				specialId=special.getId()%100;
			}
		}
		rdArriveChengC.setCitySpecials(citySpecials);// 更新缓存
		if (specialId > 0) {
			rd.setSpecial(specialId);
		}
	}

	@Override
	protected int getNeedCopperForUpdate() {
		return 2000 * (int) Math.pow(2, userCity.getTcp());
	}


	/**
	 * 某级特产铺对应的特产
	 *
	 * @param city
	 * @param tcpLevel
	 * @return
	 */
	private int getSpecialByTcpLevel(CfgCityEntity city, int tcpLevel) {
		int specialId = 0;
		int index = SpecialTool.getSpecialIndexByTcpLevel(city.getLevel(), tcpLevel);
		if (index >= 0) {
			specialId = Integer.valueOf(city.getSpecials().split(",")[index]);
		}

		return specialId;
	}

}
