package com.bbw.god.gameuser.card.equipment.cfg;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 卡牌装备加成
 *
 * @author: huanghb
 * @date: 2022/9/17 9:21
 */
@NoArgsConstructor
@Data
public class CardEquipmentAddition implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 10攻 20防 30 强度 40 韧度 */
    private Integer type;
    private Integer value;

    public CardEquipmentAddition(Integer type, int value) {
        this.type = type;
        this.value = value;
    }
}
