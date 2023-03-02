package com.bbw.god.game.config.treasure;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 限时道具配置类
 *
 * @author fzj
 * @date 2021/11/26 9:11
 */
@Data
public class CfgTimeLimitTreasureEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 限时道具id */
    private Integer treasureId;
    /** 到期时间 */
    private String timeLimit;

    @Override
    public Serializable getId() {
        return this.getTreasureId();
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
