package com.bbw.god.game.transmigration.entity;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.CardTool;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 轮回守卫
 *
 * @author: suhq
 * @date: 2021/9/10 11:47 上午
 */
@Data
public class TransmigrationDefender implements Serializable {
    private static final long serialVersionUID = -8467069696355093326L;
    private Integer lv;
    private List<Integer> runes;
    /** 卡牌集合 卡牌ID@lv@hv@skill0,skill5,skill10 */
    private List<String> cards;

    public List<TransmigrationCard> gainCards() {
        List<TransmigrationCard> tCards = new ArrayList<>();
        for (String card : cards) {
            String[] cardInfo = card.split("@");
            Integer cardId = Integer.valueOf(cardInfo[0]);
            Integer lv = Integer.valueOf(cardInfo[1]);
            Integer hv = Integer.valueOf(cardInfo[2]);
            List<Integer> skillIds = ListUtil.parseStrToInts(cardInfo[3]);
            TransmigrationCard tCard = new TransmigrationCard();
            tCard.setId(cardId);
            tCard.setLv(lv);
            tCard.setHv(hv);
            tCard.setSkills(skillIds);
            tCards.add(tCard);
        }
        return tCards;
    }

    /**
     * 守卫属性
     *
     * @return
     */
    public int gainDefenderType() {
        int cardId = Integer.valueOf(cards.get(0).split("@")[0]);
        return CardTool.getCardById(cardId).getType();
    }
}
