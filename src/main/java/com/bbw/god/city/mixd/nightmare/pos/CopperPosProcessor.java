package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 说明：
 * 铜钱格 调整为摇钱树
 * 开启后，随机获得45~100万铜钱
 * @author lwb
 * date 2021-05-28
 */
@Service
public class CopperPosProcessor extends AbstractMiXianPosProcessor{
    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.COPPER.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        List<Award> awards = Arrays.asList(Award.instance(0, AwardEnum.TQ, PowerRandom.getRandomBetween(450000,1000000)));
        rd.setGainAwards(awards);
        nightmareMiXian.addAwardToBag(awards);
        for (MiXianLevelData.PosData data : nightmareMiXian.getTreasureHouseData().getPosDatas()) {
            if (data.getTye()==NightmareMiXianPosEnum.COPPER.getType()){
                data.setTye(NightmareMiXianPosEnum.EMPTY.getType());
            }
        }
    }
}
