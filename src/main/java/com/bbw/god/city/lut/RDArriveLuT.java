package com.bbw.god.city.lut;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.game.config.city.CityConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 到达鹿台
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveLuT extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer copperPerExp = null;// 鹿台一经验需要多少铜钱
	private Integer expPerEle = null;// 鹿台一元素可获得多少经验
	private boolean nightmare = false; //玩家攻占梦魇世界土区所有城池
	private Integer backExpLv = null;//材料退还功能卡牌等级限制
	private Integer backPercent = null;//材料退还功能退还比例，70 =》70%
	private boolean cardExpAdd = false; //是否已升级
	private boolean cardLvBack = false; //等级是否已重置
	private boolean cardHvBack = false; //等级是否已重置

	public static RDArriveLuT getInstance(CityConfig.OCData ocData) {
		RDArriveLuT rdArriveLuT = new RDArriveLuT();
		rdArriveLuT.setCopperPerExp(ocData.getLtCopperPerExp());
		rdArriveLuT.setExpPerEle(ocData.getLtExpPerEle());
		rdArriveLuT.setHandleStatus("1");
		rdArriveLuT.setBackExpLv(ocData.getLtBackExpLv());
		rdArriveLuT.setBackPercent(ocData.getLtLvBackPercent());
		return rdArriveLuT;
	}

}
