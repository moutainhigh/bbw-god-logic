package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 风泽中孚卦
 *
 * 接下来10回合固定走1步
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram64Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 64;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,10,rd);
    }

}
