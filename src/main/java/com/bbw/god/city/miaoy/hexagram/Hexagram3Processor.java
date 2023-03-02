package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 水地比卦
 * 接下来10回合可免费获得七香车效果
 * （每回合询问是否使用）
 * 本回合是否使用七香车效果【免费】？
 * @author liuwenbin
 *
 */
@Service
public class Hexagram3Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 3;
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
