package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 泽风大过卦
 *
 * 接下来3场战斗，招财技能无法生效。
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram47Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 47;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,3,rd);
    }


}
