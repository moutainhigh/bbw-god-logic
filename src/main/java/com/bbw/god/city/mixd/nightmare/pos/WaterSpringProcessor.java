package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.god.city.mixd.event.EPDrinkWater;
import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import org.springframework.stereotype.Service;

/**
 * 说明：
 * 泉水
 * @author lwb
 * date 2021-05-27
 */
@Service
public class WaterSpringProcessor extends AbstractMiXianPosProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.WATER_SPRING.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        nightmareMiXian.incBlood(1);
        nightmareMiXian.addTreasureHousePsb(100);
        nightmareMiXian.takeCurrentPosToEmptyType();
        rd.setAddedBlood(1);
        BaseEventParam bep = new BaseEventParam(nightmareMiXian.getGameUserId(), WayEnum.MXD_DRINK_WATER);
        EPDrinkWater ep = EPDrinkWater.instance(bep,nightmareMiXian.getBlood());
        MiXDEventPublisher.pubDrinkWaterEvent(ep);
    }
}
