package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 乾为天卦
 * 接下来1次进入客栈，必定遇到一张5星卡牌
 * @author liuwenbin
 *
 */
@Service
public class Hexagram1Processor extends AbstractHexagram{
    @Autowired
    private UserCardService userCardService;
    @Override
    public int getHexagramId() {
        return 1;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }

    @Override
    public boolean canEffect(long uid) {
        return CardTool.getRandomCardsWithKez(userCardService.getUserCards(uid),5,1).size()>=1;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,1,rd);
    }
}
