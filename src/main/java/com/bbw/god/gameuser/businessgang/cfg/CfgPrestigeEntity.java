package com.bbw.god.gameuser.businessgang.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 声望配置
 *
 * @author fzj
 * @date 2022/1/19 14:26
 */
@Data
public class CfgPrestigeEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 声望id */
    private Integer prestigeId;
    /** 商帮id */
    private Integer businessGangId;

    @Override
    public Serializable getId() {
        return this.getPrestigeId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
