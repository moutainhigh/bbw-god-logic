package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 离为火卦
 * 接下来1次进入城池领取收益翻倍
 * @author liuwenbin
 *
 */
@Service
public class Hexagram25Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 25;
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
