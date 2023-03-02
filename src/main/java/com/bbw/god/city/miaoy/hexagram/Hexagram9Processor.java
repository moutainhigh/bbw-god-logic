package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 地风升卦
 *
 * 获得铜钱（0-30级5个骰子，30-60级5~6个骰子，60-90级6~7个骰子，90-120级6~8个骰子，120级以上7~8个骰子，获得的铜钱为骰子显示的数字）
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram9Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 9;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        GameUser user = gameUserService.getGameUser(uid);
        int lv=user.getLevel();
        int diceNum=0;
        if (lv<30){
            diceNum=5;
        }else if (lv<60){
            diceNum=5+ PowerRandom.randomInt(1);
        }else if (lv<90){
            diceNum=6+ PowerRandom.randomInt(1);
        }else if (lv<120){
            diceNum=6+ PowerRandom.randomInt(2);
        }else if (lv>=120){
            diceNum=7+ PowerRandom.randomInt(1);
        }
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < diceNum; i++) {
            int result = PowerRandom.getRandomBetween(1, 6);
            sb.append(result);
        }
        int addedCopper= Integer.parseInt(sb.toString());
        ResEventPublisher.pubCopperAddEvent(uid,addedCopper,getWay(),rd);
    }


}
