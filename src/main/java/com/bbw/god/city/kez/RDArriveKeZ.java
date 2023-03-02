package com.bbw.god.city.kez;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 到达客栈
 *
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveKeZ extends RDCityInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RDCardPrice> cards = null;// 黑市可购买的卡牌

    public static RDArriveKeZ getInstance(List<CfgCardEntity> keZCards) {
        List<RDCardPrice> cardPrices = keZCards.stream().map(card -> new RDCardPrice(card.getId(), card.getPrice()))
                .collect(Collectors.toList());
        RDArriveKeZ rdArriveKeZ = new RDArriveKeZ();
        rdArriveKeZ.setCards(cardPrices);
        rdArriveKeZ.setHandleStatus("1");

        return rdArriveKeZ;
    }

    /**
     * 卡牌价格信息
     *
     * @author suhq
     * @date 2019年3月18日 下午4:02:23
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDCardPrice implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id = null;
        private Integer price = null;
        private Integer mustKxs = 0;

        public RDCardPrice(int id, int price) {
            this.id = id;
            this.price = price;
        }

    }

}
