package com.bbw.god.city.exp;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.CityProcessorFactory;
import com.bbw.god.city.ICityExpProcessor;
import com.bbw.god.city.RDCityInfo;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 体验逻辑
 *
 * @author: suhq
 * @date: 2021/11/9 3:39 下午
 */
@Service
public class CityExpLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private CityExpService cityExpService;
    @Autowired
    private CityProcessorFactory cityProcessorFactory;


    /**
     * 体验地图建筑
     *
     * @param uid
     * @return
     */
    public RDCityInfo exp(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        CfgCityEntity city = gu.gainCurCity();
        if (!cityExpService.isSupportExp(CityTypeEnum.fromValue(city.getType()))) {
            throw ExceptionForClientTip.fromi18nKey("city.exp.not.support");
        }
        if (!gu.getStatus().ifNotInFsdlWorld()) {
            throw ExceptionForClientTip.fromi18nKey("fsdl.not.support.city.exp");
        }
        UserCityExpRecords expRecords = cityExpService.getOrCreateExpRecords(uid);
        if (expRecords.getRecord(city.getId()) > 0) {
            throw ExceptionForClientTip.fromi18nKey("city.exp.no.times");
        }

        ICityExpProcessor processor = cityProcessorFactory.makeExpProcessor(gu, city);
        RDCityInfo rd = processor.exp(gu, city);
        TimeLimitCacheUtil.setArriveCache(uid, rd);

        expRecords.addRecord(city.getId());
        gameUserService.updateItem(expRecords);

        return rd;
    }
}
