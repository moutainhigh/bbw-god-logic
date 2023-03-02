package com.bbw.god.game.wanxianzhen;

import com.bbw.god.game.config.card.CardTool;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 万仙阵机器人
 * @author lwb
 * @date 2020/5/7 14:40
 */
@Data
public class WanXianRobot {
    /**
     * 机器人ID
     */
    private Long robotId;
    /**
     * 机器人卡牌
     */
    private List<WanXianCard> regularRaceCards=null;

    public void addCard(int cardId){
        if (regularRaceCards==null){
            regularRaceCards=new ArrayList<>();
        }
        regularRaceCards.add(WanXianCard.instance(cardId, CardTool.getCardById(cardId)));
    }
}
