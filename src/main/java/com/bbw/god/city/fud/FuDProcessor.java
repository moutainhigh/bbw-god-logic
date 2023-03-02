package com.bbw.god.city.fud;

import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 福地 - 元宝
 *
 * @author suhq
 * @date 2018年10月24日 下午5:34:07
 */
@Component
public class FuDProcessor implements ICityArriveProcessor {
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private HexagramBuffService hexagramBuffService;

    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.FD, CityTypeEnum.FD2, CityTypeEnum.FD3);

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return this.cityTypes;
    }

    @Override
    public Class<RDArriveFuD> getRDArriveClass() {
        return RDArriveFuD.class;
    }

    @Override
    public RDArriveFuD arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_63.getId())){
            HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId()),HexagramBuffEnum.HEXAGRAM_63.getId(),1);
            return RDArriveFuD.fromRDCommon(rd);
        }
        int addedGold = getFdGold(city);
        BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.FD, rd);
        EPGoldAdd goldAdd = new EPGoldAdd(bep, addedGold);
        int doubleTimes = this.privilegeService.getFuDiDoubleTime(gu);
        if (doubleTimes > 0) {
            goldAdd.addGold(ResWayType.LingY, (doubleTimes - 1) * addedGold);
        }
        ResEventPublisher.pubGoldAddEvent(goldAdd);

        return RDArriveFuD.fromRDCommon(rd);
    }

    /**
     * 福地元宝
     *
     * @param city
     * @return
     */
    private int getFdGold(CfgCityEntity city) {
        int addedGold = 5;
        if (city.getType() == 33) {
            addedGold = 10;
        } else if (city.getType() == 36) {
            addedGold = 30;
        }
        return addedGold;
    }

}
