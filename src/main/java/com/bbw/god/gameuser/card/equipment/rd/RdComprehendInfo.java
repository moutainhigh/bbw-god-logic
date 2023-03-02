package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单体参悟信息
 *
 * @author: huanghb
 * @date: 2022/9/15 10:22
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdComprehendInfo {
    private static final long serialVersionUID = -6407403326202663339L;
    /** 10攻 20防 30 强度 40 韧度 */
    private Integer type;
    private Integer value;

    /**
     * 初始化
     *
     * @param cardEquipmentAddition
     * @return
     */
    public static RdComprehendInfo instance(CardEquipmentAddition cardEquipmentAddition) {
        RdComprehendInfo info = new RdComprehendInfo();
        info.setType(cardEquipmentAddition.getType());
        info.setValue(cardEquipmentAddition.getValue());
        return info;

    }


}
