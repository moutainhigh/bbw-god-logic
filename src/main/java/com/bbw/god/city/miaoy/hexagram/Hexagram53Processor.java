package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 风天小畜卦
 *
 * 接下来5次遇到村庄不会触发村庄事件
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram53Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 53;
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
