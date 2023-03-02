package com.bbw.god.fight.fsfight;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 获得所有卡牌
 *
 * @author suhq
 * @date 2019-11-06 14:07:37
 */
@Getter
@Setter
@ToString
public class RDAllCards extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<RDFsFightCard> cards;
    private List<RDFsFightCard> fsCards;

    public void addCard(RDFsFightCard rdCard) {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        cards.add(rdCard);
    }

    public void addFsCard(RDFsFightCard rdCard) {
        if (fsCards == null) {
            fsCards = new ArrayList<>();
        }
        fsCards.add(rdCard);
    }

    @Data
    public static class RDFsFightCard {
        private Integer id;
        private Integer star;
        private Integer attack;
        private Integer hp;
        private Integer zero_skill;
        private Integer five_skill;
        private Integer ten_skill;
        private Integer group;
    }

}
