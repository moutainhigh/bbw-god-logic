package com.bbw.god.mall.snatchtreasure;

import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.random.box.BoxGood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 全服夺宝卡牌对象
 * @date 2020/8/21 9:54
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class GameSnatchTreasureCard extends GameData {
    private List<BoxGood> boxGoods;
    private Date beginTime;
    private Date endTime;

    public static GameSnatchTreasureCard getInstance(List<BoxGood> boxGoods, Date beginTime, Date endTime) {
        GameSnatchTreasureCard gameSnatchTreasureCard = new GameSnatchTreasureCard();
        gameSnatchTreasureCard.setId(ID.INSTANCE.nextId());
        gameSnatchTreasureCard.setBoxGoods(boxGoods);
        gameSnatchTreasureCard.setBeginTime(beginTime);
        gameSnatchTreasureCard.setEndTime(endTime);
        return gameSnatchTreasureCard;
    }

    /**
     * 资源类型的字符串
     *
     * @return
     */
    @Override
    public GameDataType gainDataType() {
        return GameDataType.SNATCH_TREASURE_CARD;
    }
}
