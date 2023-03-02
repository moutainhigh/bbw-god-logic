package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import org.springframework.stereotype.Service;

/**
 * 说明：
 * 宝箱:从奖励列表中，根据概率收集1份奖励，收集到的奖励会显示在藏宝背包中。
 * @author lwb
 * date 2021-05-27
 */
@Service
public class EmptyProcessor extends AbstractMiXianPosProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.EMPTY.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        nightmareMiXian.takeCurrentPosToEmptyType();
    }
}
