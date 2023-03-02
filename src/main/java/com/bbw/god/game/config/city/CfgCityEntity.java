package com.bbw.god.game.config.city;

import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 一个建筑可能占两格 地址1 地址2
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
public class CfgCityEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //
	private String name; //
	private Integer address1; //
	private Integer address2; //
	private Integer type; //
	private Integer property; // 城市属性

	public String getSpecials() {
		return CityTool.getChengc(id).getSpecials();
	}

	public String getDropCards() {
		return CityTool.getChengc(id).getDropCards();
	}

	public Integer getSoliderLevel(GameUser gu) {
		if (WorldType.NIGHTMARE.getValue()==(gu.getStatus().getCurWordType())) {
			return CityTool.getNightmareChengC(id).getSoliderLevel();
		}
		return CityTool.getChengc(id).getSoliderLevel();
	}

	public String getSoliders(GameUser gu) {
		if (WorldType.NIGHTMARE.getValue()==(gu.getStatus().getCurWordType())) {
			return CityTool.getNightmareChengC(id).getSoliders();
		}
		return CityTool.getChengc(id).getSoliders();
	}

	public String getOldWordSoliders() {
		return CityTool.getChengc(id).getSoliders();
	}

	public int getLevel() {
		return (type - 200) / 10;
	}

	public int getBaseTax() {
		return 2500 * getLevel();
	}

	public int getCountry() {
		return RoadTool.getRoadById(getAddress1()).getCountry();
	}

	/**
	 * 是否为福地
	 *
	 * @return
	 */
	public boolean isFD() {
		return type >= CityTypeEnum.FD.getValue() && type <= CityTypeEnum.FD3.getValue();
	}

	/**
	 * 是否为野怪出没地
	 *
	 * @return
	 */
	public boolean isYG() {
		return type >= CityTypeEnum.KS.getValue() && type <= CityTypeEnum.NZ.getValue();
	}

	/**
	 * 是否为野地(问号处)
	 *
	 * @return
	 */
	public boolean isYeD() {
		return CityTypeEnum.YD.getValue() == type;
	}

	/**
	 * 是否为陈城池
	 *
	 * @return
	 */
	public boolean isCC() {
		return type >= CityTypeEnum.CC1.getValue() && type <= CityTypeEnum.CC5.getValue();
	}

	@Override
	public int getSortId() {
		return this.getId();
	}
}
