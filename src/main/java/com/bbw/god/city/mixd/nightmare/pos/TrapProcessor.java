package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.event.BaseEventParam;
import org.springframework.stereotype.Service;

/**
 * 说明：
 * 陷阱
 * @author lwb
 * date 2021-05-27
 */
@Service
public class TrapProcessor extends AbstractMiXianPosProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.TRAP.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        nightmareMiXian.incBlood(-1);
        nightmareMiXian.addTreasureHousePsb(100);
        nightmareMiXian.takeCurrentPosToEmptyType();
        rd.setAddedBlood(-1);
        MiXDEventPublisher.pubStepTrapEvent(new BaseEventParam(nightmareMiXian.getGameUserId()));
        nightmareMiXian.setBeInjured(true);
    }
}
