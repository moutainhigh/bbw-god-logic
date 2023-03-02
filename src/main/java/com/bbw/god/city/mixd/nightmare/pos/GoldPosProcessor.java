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
 * 元宝格 调整为聚宝盆
 * 开启后，随机获得45~80元宝
 * @author lwb
 * date 2021-05-28
 */
@Service
public class GoldPosProcessor extends AbstractMiXianPosProcessor{
    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.GOLD.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        List<Award> awards = Arrays.asList(Award.instance(0, AwardEnum.YB, PowerRandom.getRandomBetween(45,80)));
        rd.setGainAwards(awards);
        nightmareMiXian.addAwardToBag(awards);
        for (MiXianLevelData.PosData data : nightmareMiXian.getTreasureHouseData().getPosDatas()) {
            if (data.getTye()==NightmareMiXianPosEnum.GOLD.getType()){
                data.setTye(NightmareMiXianPosEnum.EMPTY.getType());
            }
        }
    }
}
