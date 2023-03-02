package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import org.springframework.stereotype.Service;

/**
 * 说明：
 * 大门:前往下一层的入口
 * @author lwb
 * date 2021-05-27
 */
@Service
public class GateProcessor extends AbstractMiXianPosProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.GATE.equals(miXianPosEnum)||NightmareMiXianPosEnum.TREASURE_HOUSE_GATE.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        return;
    }
}
