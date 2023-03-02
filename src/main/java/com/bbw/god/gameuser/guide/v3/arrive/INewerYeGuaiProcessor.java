package com.bbw.god.gameuser.guide.v3.arrive;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yeg.IYegFightProcessor;
import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.city.yeg.YeGFightProcessorFactory;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.exaward.YeGExawardEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 新手引导野怪处理器
 * @date 2020/12/15 17:08
 **/
//@Service
//@Slf4j
public class INewerYeGuaiProcessor implements INewerGuideCityArriveProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.KS, CityTypeEnum.SL, CityTypeEnum.HP, CityTypeEnum.HuoS, CityTypeEnum.NZ);
    @Autowired
    private YeGFightProcessorFactory yeGFightProcessorFactory;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return this.cityTypes;
    }

    @Override
    public Class<RDArriveYeG> getRDArriveClass() {
        return RDArriveYeG.class;
    }

    @Override
    public RDArriveYeG arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
        IYegFightProcessor fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(YeGuaiEnum.YG_NORMAL);
        RDFightsInfo rdFightsInfo = fightProcessor.getFightsInfo(gameUser, city.getType() - 100);
        // 初始战斗为未结算
        TimeLimitCacheUtil.removeCache(gameUser.getId(), RDFightResult.class);
        RDArriveYeG rdArriveYeG = RDArriveYeG.getInstance(rdFightsInfo, city, YeGExawardEnum.WIN_6_ROUND.getVal());
        rdArriveYeG.setYeGuaiType(fightProcessor.getYeGEnum().getType());
        return rdArriveYeG;
    }
}
