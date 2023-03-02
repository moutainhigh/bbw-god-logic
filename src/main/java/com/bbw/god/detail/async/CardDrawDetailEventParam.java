package com.bbw.god.detail.async;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商城购买明细
 *
 * @author suhq
 * @date 2020-05-25 10:08
 **/
@Data
public class CardDrawDetailEventParam implements Serializable {
    private static final long serialVersionUID = 6878427491153506056L;
    private Long uid; //玩家ID
    private Integer drawTimes;//抽卡次数
    private List<EPCardAdd.CardAddInfo> addCards;
    private List<EVTreasure> treasures;
    private WayEnum way;

    public CardDrawDetailEventParam(long uid, int drawTimes, List<EPCardAdd.CardAddInfo> addCards, List<EVTreasure> treasures, WayEnum way) {
        this.uid = uid;
        this.drawTimes = drawTimes;
        this.addCards = addCards;
        this.treasures = treasures;
        this.way = way;
    }

    public CardDrawDetailEventParam(long uid, List<EPCardAdd.CardAddInfo> addCards, WayEnum way) {
        this.uid = uid;
        this.drawTimes = 1;
        this.addCards = addCards;
        this.way = way;
    }

}
