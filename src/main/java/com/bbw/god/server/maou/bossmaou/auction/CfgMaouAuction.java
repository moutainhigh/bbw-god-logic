package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 拍卖配置类
 * @date 2020/7/23 10:45
 **/
@Data
public class CfgMaouAuction implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = -878933385370827105L;
    private Integer id;
    private String name;
    private Integer num;
    // 底价
    private Integer minPrice;
    // 最低加价
    private Integer minAddPrice;
    // 概率
    private Integer prop;

    @Override
    public int getSortId() {
        return this.getId();
    }
}
