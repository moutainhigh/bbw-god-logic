package com.bbw.god.game.config.card;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 群星册配置信息
 *
 * @author：lwb
 * @date: 2020/11/24 17:14
 * @version: 1.0
 */
@Data
public class CfgFlockStarBook implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 3744367961939915233L;
    /** 卡牌id */
    private Integer id;
    /** 卡牌名称 */
    private String name;

    @Override
    public int getSortId() {
        return getId();
    }
}
