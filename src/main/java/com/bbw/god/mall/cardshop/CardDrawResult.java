package com.bbw.god.mall.cardshop;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽卡结果
 *
 * @author suhq
 * @date 2020-05-30 17:00
 **/
@Data
public class CardDrawResult implements Serializable {
    private static final long serialVersionUID = 6399887036026608742L;
    private Integer drawTimes;//抽卡次数
    private List<EPCardAdd.CardAddInfo> addCards;
    private List<EVTreasure> treasures;
    private WayEnum way;

    public CardDrawResult(WayEnum way) {
        this.drawTimes = 1;
        this.way = way;
    }

    public CardDrawResult(int drawTimes, WayEnum way) {
        this.drawTimes = drawTimes;
        this.way = way;
    }

    public CardDrawResult(int drawTimes, List<EPCardAdd.CardAddInfo> addCards, List<EVTreasure> treasures, WayEnum way) {
        this.drawTimes = drawTimes;
        this.addCards = addCards;
        this.treasures = treasures;
        this.way = way;
    }

    public void addEvTreasure(int treasureId, int num) {
        if (treasures == null) {
            treasures = new ArrayList<>();
        }
        EVTreasure ev = treasures.stream().filter(tmp -> tmp.getId() == treasureId).findFirst().orElse(null);
        if (ev == null) {
            ev = new EVTreasure(treasureId, num);
            treasures.add(ev);
            return;
        }
        ev.setNum(ev.getNum() + num);
    }
}
