package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 风地观卦
 * 接下来1次城池交易必定15%折扣和溢价
 * @author liuwenbin
 *
 */
@Service
public class Hexagram22Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 22;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,1,rd);
    }
}
