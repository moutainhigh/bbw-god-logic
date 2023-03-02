package com.bbw.god.gameuser.yuxg.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 玉虚宫除外符图
 *
 * @author: huanghb
 * @date: 2023/2/25 14:02
 */
@Data
public class CfgYuXGExceptFuTuEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = -6331499741170594976L;
    /** 符图ID */
    private Integer futuId;

    @Override
    public Serializable getId() {
        return this.getFutuId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
