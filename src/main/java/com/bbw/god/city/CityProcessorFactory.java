package com.bbw.god.city;

import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityProcessorFactory {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private List<ICityArriveProcessor> cityArriveProcessors;
    @Autowired
    private List<ICityHandleProcessor> cityHandleProcessors;
    @Autowired
    private List<ICityExpProcessor> cityExpProcessors;

    /**
     * 城市到达处理
     *
     * @param city
     * @return
     */
    public ICityArriveProcessor makeArriveProcessor(GameUser gu, CfgCityEntity city) {
        CityTypeEnum type = CityTypeEnum.fromValue(city.getType());
        ICityArriveProcessor cityProcessor = null;
        if (gu.getStatus().ifNotInFsdlWorld()) {
            cityProcessor = cityArriveProcessors.stream()
                    .filter(cp -> cp.isMatch(type) && cp.isNightmare()).findFirst().orElse(null);
        }
        if (null == cityProcessor) {
            cityProcessor = cityArriveProcessors.stream()
                    .filter(cp -> cp.isMatch(type)).findFirst().orElse(null);
        }
        if (cityProcessor == null) {
            throw CoderException.normal(String.format("【%s】没有相应的ArriveProcessor", city.getName()));
        }
        return cityProcessor;
    }

    /**
     * 城市内操作处理
     *
     * @param guId
     * @return
     */
    public ICityHandleProcessor makeHandleProcessor(GameUser gameUser) {
        CfgCityEntity city = gameUser.gainCurCity();
        CityTypeEnum type = CityTypeEnum.fromValue(city.getType());

        ICityHandleProcessor cityProcessor = null;
        //判定是否梦魇或者轮回世界
        if (gameUser.getStatus().ifNotInFsdlWorld()) {
            cityProcessor = cityHandleProcessors.stream()
                    .filter(cp -> cp.isMatch(type) && cp.isNightmare()).findFirst().orElse(null);
        }
        if (null == cityProcessor) {
            cityProcessor = cityHandleProcessors.stream()
                    .filter(cp -> cp.isMatch(type)).findFirst().orElse(null);
        }
        if (cityProcessor == null) {
            throw new ExceptionForClientTip("city.not.support", city.getName());
        }
        return cityProcessor;
    }

    /**
     * 城市到达处理
     *
     * @param city
     * @return
     */
    public ICityExpProcessor makeExpProcessor(GameUser gu, CfgCityEntity city) {
        CityTypeEnum type = CityTypeEnum.fromValue(city.getType());
        ICityExpProcessor cityProcessor = null;
        if (gu.getStatus().ifNotInFsdlWorld()) {
            cityProcessor = cityExpProcessors.stream()
                    .filter(cp -> cp.isMatch(type) && cp.isNightmare()).findFirst().orElse(null);
        }
        if (null == cityProcessor) {
            cityProcessor = cityExpProcessors.stream()
                    .filter(cp -> cp.isMatch(type)).findFirst().orElse(null);
        }
        if (cityProcessor == null) {
            throw CoderException.normal(String.format("【%s】没有相应的CityExpProcessor", city.getName()));
        }
        return cityProcessor;
    }
}
