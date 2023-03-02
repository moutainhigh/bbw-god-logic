package com.bbw.god.game.transmigration.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回卡牌
 *
 * @author: suhq
 * @date: 2021/9/10 11:47 上午
 */
@Data
public class TransmigrationCard implements Serializable {
    private static final long serialVersionUID = -8467069696355093326L;
    private Integer id;
    private Integer lv;
    private Integer hv;
    private List<Integer> skills;

    public String toSting() {
        String skillStr = "";
        for (Integer skill : skills) {
            if (skillStr.length() != 0) {
                skillStr += ",";
            }
            skillStr += skill;
        }
        return id + "@" + lv + "@" + hv + "@" + skillStr;
    }
}
