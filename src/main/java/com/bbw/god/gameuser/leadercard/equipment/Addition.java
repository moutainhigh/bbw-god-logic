package com.bbw.god.gameuser.leadercard.equipment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 加成
 *
 * @author suhq
 * @date 2021-03-26 13:55
 **/
@NoArgsConstructor
@Data
public class Addition implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 10攻 20防 30加召唤师血量 */
    private Integer type;
    private Integer value;

    public Addition(Integer type, int value) {
        this.type = type;
        this.value = value;
    }
}
