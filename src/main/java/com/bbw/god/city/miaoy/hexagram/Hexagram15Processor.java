package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 水泽节卦
 * 接下来10次随机事件中，遇到负面事件不会受到影响
 * @author liuwenbin
 *
 */
@Service
public class Hexagram15Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 15;
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
