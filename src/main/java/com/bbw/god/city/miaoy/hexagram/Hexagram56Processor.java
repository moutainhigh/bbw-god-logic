package com.bbw.god.city.miaoy.hexagram;

import org.springframework.stereotype.Service;

/**
 * 天山遁卦
 *
 * 15回合内无法通过随机摇骰子离开所属区域
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram56Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 56;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,15,rd);
    }


}
