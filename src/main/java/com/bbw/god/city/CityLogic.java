package com.bbw.god.city;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 城市逻辑骨架
 *
 * @author suhq
 * @date 2018年11月19日 下午3:54:53
 */
@Service
public class CityLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private CityProcessorFactory cityProcessorFactory;

    /**
     * 城市操作处理器配发
     *
     * @param guId
     * @param param
     * @param cType
     * @return
     */
    public RDCommon cityHandleProcessorDispatch(long guId, Object param, CityTypeEnum cType) {
        GameUser gu = gameUserService.getGameUser(guId);
        CfgCityEntity city = gu.gainCurCity();
        // 是否在城市中
        CityChecker.checkIsCity(city, cType);

        ICityHandleProcessor processor = cityProcessorFactory.makeHandleProcessor(gu);
        // 是否已操作
        processor.checkIsHandle(gu, param);
        // 处理具体城市的操作逻辑
        RDCommon rd = processor.handleProcessor(gu, param);
        // 标记已处理
        processor.setHandleStatus(gu, param);

        return rd;
    }

}
