package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 风水涣卦
 *
 * 接下来5次遇到元宝时无法获得奖励
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram63Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 63;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,5,rd);
    }

}
