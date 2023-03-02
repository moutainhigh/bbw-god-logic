package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 泽雷随卦
 *
 * 下回合在原地停留一次
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram41Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 41;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,1,rd);
    }


}
