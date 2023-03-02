package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;

/**
 * 刷新招财兽时返回招财兽位置
 *
 * @author: huanghb
 * @date: 2022/1/25 15:48
 */
@Data
public class RDRefreshLuckyBeastPos extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer position;

    public RDRefreshLuckyBeastPos(UserLuckyBeast userLuckyBeast) {
        this.id = userLuckyBeast.getLuckyBeastId();
        this.position = userLuckyBeast.getPosition();
    }
}
