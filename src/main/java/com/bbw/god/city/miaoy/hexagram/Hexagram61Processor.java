package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 雷泽归妹卦
 *
 * 接下来2次进入城池无法领取收益
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram61Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 61;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,2,rd);
    }

}
