package com.bbw.god.gameuser.yuxg.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 玉髓配置数据
 *
 * @author fzj
 * @date 2021/11/1 11:06
 */
@Data
public class CfgYuSuiEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 玉髓id */
    private Integer yuSuiId;
    /** 品阶 */
    private Integer quality;

    @Override
    public Serializable getId() {
        return this.getYuSuiId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
