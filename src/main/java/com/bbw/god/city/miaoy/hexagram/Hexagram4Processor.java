package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 火天大有卦
 *
 * 随机一张卡牌获得经验值（0-50级4个骰子，50-100级4~5个骰子，100级以上5~6个骰子，获得的经验值为骰子显示的数字）
 * 【文案】卡牌-xx（卡牌名）获得654312点经验
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram4Processor extends AbstractHexagram{
    @Autowired
    private UserCardService userCardService;
    @Override
    public int getHexagramId() {
        return 4;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }

    @Override
    public boolean canEffect(long uid) {
        List<UserCard> userCards = userCardService.getUserCards(uid);
        Optional<UserCard> optional = userCards.stream().filter(p -> !p.ifFullUpdate()).findFirst();
        return optional.isPresent();
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser user = gameUserService.getGameUser(uid);
        int lv=user.getLevel();
        int diceNum=0;
        if (lv<50){
            diceNum=4;
        }else if (lv<100){
            diceNum=4+ PowerRandom.randomInt(1);
        }else if (lv>=100){
            diceNum=5+ PowerRandom.randomInt(1);
        }
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < diceNum; i++) {
            int result = PowerRandom.getRandomBetween(1, 6);
            sb.append(result);
        }
        int addedExp= Integer.parseInt(sb.toString());
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<UserCard> list = userCards.stream().filter(p -> !p.ifFullUpdate()).collect(Collectors.toList());
        UserCard random = PowerRandom.getRandomFromList(list);
        BaseEventParam bp = new BaseEventParam(uid, getWay(), rd);
        CardEventPublisher.pubCardExpAddEvent(bp, random.getBaseId(), addedExp);
    }


}
