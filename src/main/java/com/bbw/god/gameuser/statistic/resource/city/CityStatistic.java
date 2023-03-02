package com.bbw.god.gameuser.statistic.resource.city;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbw.exception.CoderException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 城池统计
 *
 * @author suchaobin
 * @date 2020/4/16 8:52
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CityStatistic extends ResourceStatistic {
	private static final Logger logger = LoggerFactory.getLogger(CityStatistic.class);
	//各等级城池数量
	private static final int MAX_ONE_STAR = 25;
	private static final int MAX_TWO_STAR = 20;
	private static final int MAX_THREE_STAR = 20;
	private static final int MAX_FOUR_STAR = 15;
	private static final int MAX_FIVE_STAR = 5;

	private static final int MAX_ELE_STAR = 17;//按照元素分类最多17座

	private Integer oneStarCity = 0;
	private Integer twoStarCity = 0;
	private Integer threeStarCity = 0;
	private Integer fourStarCity = 0;
	private Integer fiveStarCity = 0;

	private Integer goldCountryCity = 0;
	private Integer woodCountryCity = 0;
	private Integer waterCountryCity = 0;
	private Integer fireCountryCity = 0;
	private Integer earthCountryCity = 0;

	private Integer allFiveLevelCity = 0;
	private Integer allSixLevelCity = 0;
	private Integer allSevenLevelCity = 0;
	private Integer allEightLevelCity = 0;
	private Integer allNineLevelCity = 0;
	private Integer allTenLevelCity = 0;
	@Override
	public Integer getTotal(){
		return this.getStarTotal();
	}
	//不可用，无法修正数值
	//	private boolean min(Integer v,Integer max,String caption){
	//		if(v>max){
	//			v= max;
	//			CoderException e = CoderException.fatal(caption+"城池数量大于"+max);
	//			logger.error(e.getMessage(), e);
	//			return false;
	//		}
	//		return true;
	//	}
	private int getStarTotal(){
		return oneStarCity+twoStarCity+threeStarCity+fourStarCity+fiveStarCity;
	}
	private int getEleTotal(){
		return goldCountryCity+woodCountryCity+waterCountryCity+fireCountryCity+earthCountryCity;
	}
	private boolean check() {
		boolean b = true;
		if (oneStarCity > MAX_ONE_STAR) {
			oneStarCity = MAX_ONE_STAR;
			CoderException e = CoderException.fatal("一级城池数量大于" + MAX_ONE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (twoStarCity > MAX_TWO_STAR) {
			twoStarCity = MAX_TWO_STAR;
			CoderException e = CoderException.fatal("二级城池数量大于" + MAX_TWO_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (threeStarCity > MAX_THREE_STAR) {
			threeStarCity = MAX_THREE_STAR;
			CoderException e = CoderException.fatal("三级城池数量大于" + MAX_THREE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (fourStarCity > MAX_FOUR_STAR) {
			fourStarCity = MAX_FOUR_STAR;
			CoderException e = CoderException.fatal("四级城池数量大于" + MAX_FOUR_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (fiveStarCity > MAX_FIVE_STAR) {
			fiveStarCity = MAX_FIVE_STAR;
			CoderException e = CoderException.fatal("五级城池数量大于" + MAX_FIVE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}

		if (goldCountryCity > MAX_ELE_STAR) {
			goldCountryCity = MAX_ELE_STAR;
			CoderException e = CoderException.fatal("金城池数量大于" + MAX_ELE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (woodCountryCity > MAX_ELE_STAR) {
			woodCountryCity = MAX_ELE_STAR;
			CoderException e = CoderException.fatal("木城池数量大于" + MAX_ELE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (waterCountryCity > MAX_ELE_STAR) {
			waterCountryCity = MAX_ELE_STAR;
			CoderException e = CoderException.fatal("水城池数量大于" + MAX_ELE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (fireCountryCity > MAX_ELE_STAR) {
			fireCountryCity = MAX_ELE_STAR;
			CoderException e = CoderException.fatal("火城池数量大于" + MAX_ELE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if (earthCountryCity > MAX_ELE_STAR) {
			earthCountryCity = MAX_ELE_STAR;
			CoderException e = CoderException.fatal("金城池数量大于" + MAX_ELE_STAR);
			logger.error(e.getMessage(), e);
			b = false;
		}
		if(getStarTotal()!=this.getEleTotal()){
			CoderException e = CoderException.fatal("星级城池数量 " + getStarTotal()+" 与属性城池数量"+getEleTotal()+"不相符！");
			logger.error(e.getMessage(), e);
			b = false;
		}
		if(!b){
			//TODO:触发检查，从已经攻占到城池重新初始化这些值
		}
		return b;
	}

	public CityStatistic() {
		this.setAwardEnum(AwardEnum.CITY);
	}

	public CityStatistic(Integer today, Integer total, Integer date, int type, Integer oneStarCity, Integer twoStarCity, Integer threeStarCity, Integer fourStarCity, Integer fiveStarCity, Integer goldCountryCity, Integer woodCountryCity, Integer waterCountryCity, Integer fireCountryCity, Integer earthCountryCity, Integer allFiveLevelCity, Integer allSixLevelCity, Integer allSevenLevelCity, Integer allEightLevelCity, Integer allNineLevelCity, Integer allTenLevelCity) {
		super(today, total, date, AwardEnum.CITY, type);
		this.oneStarCity = oneStarCity;
		this.twoStarCity = twoStarCity;
		this.threeStarCity = threeStarCity;
		this.fourStarCity = fourStarCity;
		this.fiveStarCity = fiveStarCity;
		this.goldCountryCity = goldCountryCity;
		this.woodCountryCity = woodCountryCity;
		this.waterCountryCity = waterCountryCity;
		this.fireCountryCity = fireCountryCity;
		this.earthCountryCity = earthCountryCity;
		this.allFiveLevelCity = allFiveLevelCity;
		this.allSixLevelCity = allSixLevelCity;
		this.allSevenLevelCity = allSevenLevelCity;
		this.allEightLevelCity = allEightLevelCity;
		this.allNineLevelCity = allNineLevelCity;
		this.allTenLevelCity = allTenLevelCity;
	}

	public boolean addCity(int cityLevel, int cityCountry) {
		switch (cityLevel) {
		case 1:
			this.oneStarCity += 1;
			break;
		case 2:
			this.twoStarCity += 1;
			break;
		case 3:
			this.threeStarCity += 1;
			break;
		case 4:
			this.fourStarCity += 1;
			break;
		case 5:
			this.fiveStarCity += 1;
			break;
		default:
			break;
		}
		TypeEnum typeEnum = TypeEnum.fromValue(cityCountry);
		switch (typeEnum) {
		case Gold:
			this.goldCountryCity += 1;
			break;
		case Wood:
			this.woodCountryCity += 1;
			break;
		case Water:
			this.waterCountryCity += 1;
			break;
		case Fire:
			this.fireCountryCity += 1;
			break;
		case Earth:
			this.earthCountryCity += 1;
			break;
		default:
			break;
		}
		boolean b=check();
		this.setToday(this.getToday() + 1);
		this.setTotal(this.getStarTotal());
		return b;
	}

	public void addAllLevelCity(int newLevel) {
		switch (newLevel) {
		case 5:
			this.allFiveLevelCity += 1;
			break;
		case 6:
			this.allSixLevelCity += 1;
			break;
		case 7:
			this.allSevenLevelCity += 1;
			break;
		case 8:
			this.allEightLevelCity += 1;
			break;
		case 9:
			this.allNineLevelCity += 1;
			break;
		case 10:
			this.allTenLevelCity += 1;
			break;
		default:
			break;
		}
	}
}
