package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 说明：
 * 元素格 调整为元素脉
 * 开启后，随机获得45~135元素（种类随机）
 * @author lwb
 * date 2021-05-28
 */
@Service
public class ElePosProcessor extends AbstractMiXianPosProcessor{
    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.ELE.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian, RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            int type=PowerRandom.getRandomBySeed(5)*10;
            Optional<Award> optional = awards.stream().filter(p -> p.getAwardId() == type).findFirst();
            if (optional.isPresent()){
                Award award = optional.get();
                award.setNum(award.getNum()+PowerRandom.getRandomBySeed(3));
            }else {
                awards.add(Award.instance(type, AwardEnum.YS, PowerRandom.getRandomBySeed(3)));
            }
        }
        rd.setGainAwards(awards);
        nightmareMiXian.addAwardToBag(awards);
        for (MiXianLevelData.PosData data : nightmareMiXian.getTreasureHouseData().getPosDatas()) {
            if (data.getTye()==NightmareMiXianPosEnum.ELE.getType()){
                data.setTye(NightmareMiXianPosEnum.EMPTY.getType());
            }
        }
        
    }
}
