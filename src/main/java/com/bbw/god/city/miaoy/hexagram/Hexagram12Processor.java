package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 风山渐卦
 * 接下来10个路口可任意选择方向
 * @author liuwenbin
 *
 */
@Service
public class Hexagram12Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 12;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,10,rd);
    }
}
