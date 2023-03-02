package com.bbw.god.game.config.card;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 防守者卡牌
 * 当cardIds有值时，忽略stars、num
 */
@Data
public class FightCardGenerateRule implements Serializable {
    private static final long serialVersionUID = -129626970878301267L;
    private List<Integer> cardIds;
    private List<Integer> stars;
    private Integer num;
}
