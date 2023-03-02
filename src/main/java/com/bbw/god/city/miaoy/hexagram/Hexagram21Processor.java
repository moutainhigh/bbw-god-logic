package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 地泽临卦
 * 接下来10回合投骰子不消耗任何体力
 * @author liuwenbin
 *
 */
@Service
public class Hexagram21Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 21;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,10,rd);
    }
}
