package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 风火家人卦
 * 接下来1次进入客栈，所有卡牌为5星，且只能用2个捆仙绳购买
 * @author liuwenbin
 *
 */
@Service
public class Hexagram57Processor extends AbstractHexagram{
    @Autowired
    private UserCardService userCardService;
    @Override
    public int getHexagramId() {
        return 57;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }

    @Override
    public boolean canEffect(long uid) {
        return CardTool.getRandomCardsWithKez(userCardService.getUserCards(uid),5,3).size()>=3;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        addHexagramBuff(uid,1,rd);
    }
}
