package com.bbw.god.city.yed;

import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.special.GameSpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 小白事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class XiaoBaiProcessor extends BaseYeDEventProcessor {
    @Autowired
    private GameSpecialService gameSpecialService;

    /**
     * 获取当前野地事件id
     */
    @Override
    public int getMyId() {
        return YdEventEnum.XIAO_BAI.getValue();
    }

    /**
     * 野地事件生效
     *
     * @param gameUser
     * @param rdArriveYeD
     * @param rd
     */
    @Override
    public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
        List<UserSpecial> userSpecials = userSpecialService.getRandomEventSpecials(gameUser.getId());
        if (userSpecials.size() == 0) {
            rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
            return;
        }
        int addedCopper = 0;
        int addedWeekCopper = 0;
        int cszCopper = 0;
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = new ArrayList<>();
        for (UserSpecial us : userSpecials) {
            CfgSpecialEntity specialEntity = SpecialTool.getSpecialById(us.getBaseId());
            int boughtPrice = specialEntity.getPrice() * us.getDiscount() / 100;
            int price = specialEntity.getPrice() * specialEntity.getMaxPriceRate() / 100;
            // 合成特产按高价区价格计算
            if (specialEntity.isSyntheticSpecialty()) {
                price = gameSpecialService.getMaxSellPrice(specialEntity.getId());
                boughtPrice = specialEntity.getPrice();
            }
            addedCopper += price;
            addedWeekCopper += (price - boughtPrice);
            if (userTreasureEffectService.isTreasureEffect(gameUser.getId(), TreasureEnum.CSZ.getValue())) {
                cszCopper += (price - boughtPrice);
            }
            EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(us.getId(), us.getBaseId(),
                    boughtPrice, price);
            specialInfoList.add(info);
        }

        if (monthLoginLogic.isExistEvent(gameUser.getId(), MonthLoginEnum.BAD_XB)) {
            addedCopper /= 2;
            addedWeekCopper /= 2;
            cszCopper /= 2;
        }

        BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
        EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(bep, gameUser.getLocation().getPosition(),
                specialInfoList);
        SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
        EPCopperAdd epCopperAdd = new EPCopperAdd(bep, addedCopper, addedWeekCopper);
        epCopperAdd.addCopper(ResWayType.CaiSZ, cszCopper);
        ResEventPublisher.pubCopperAddEvent(epCopperAdd);
        // 广播
        List<Integer> specialIds =
                specialInfoList.stream().map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());
        EPYeDTrigger epYeDTrigger = EPYeDTrigger.fromIncome(YdEventEnum.XIAO_BAI, addedCopper + cszCopper, specialIds);
        CityEventPublisher.pubYeDTrigger(bep, epYeDTrigger);
    }
}
