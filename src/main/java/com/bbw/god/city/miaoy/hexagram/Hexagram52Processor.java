package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 水雷屯卦
 *
 * 投掷五个骰子，遗失所示铜钱数
 * 【文案】遗失63456铜钱
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram52Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 52;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int result = PowerRandom.getRandomBetween(1, 6);
            sb.append(result);
        }
        long deductCopper= Long.parseLong(sb.toString());
        ResEventPublisher.pubCopperDeductEvent(uid,deductCopper,getWay(),rd);
    }


}
