package com.bbw.god.city.jieb;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.RDCityInfo;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;

/**
 * 村庄
 * 
 * @author suhq
 * @date 2018年10月24日 下午5:34:24
 */
@Component
public class JieBProcessor implements ICityArriveProcessor {
	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.JB);

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return cityTypes;
	}

	@Override
	public Class<JieBProcessor> getRDArriveClass() {
		return JieBProcessor.class;
	}

	@Override
	public RDCityInfo arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
		return new RDCityInfo();
	}
}
