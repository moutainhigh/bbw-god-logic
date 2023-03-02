package com.bbw.god.game.config.mall;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商城特惠礼包额外商品
 *
 * @author: huanghb
 * @date: 2021/12/14 15:30
 */
@Data
public class CfgMallExtraPackEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /*礼包ID*/
    private Integer id;
    /*额外奖励*/
    private List<Award> extraAwards;

    @Override
    public int getSortId() {
        return id;
    }
}
