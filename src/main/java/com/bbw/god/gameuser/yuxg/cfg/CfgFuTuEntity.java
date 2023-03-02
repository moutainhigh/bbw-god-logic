package com.bbw.god.gameuser.yuxg.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 符图数据配置
 *
 * @author fzj
 * @date 2021/10/29 17:43
 */
@Data
public class CfgFuTuEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 符图id */
    private Integer fuTuId;
    /** 品阶 */
    private Integer quality;
    /** 符图类型 10 攻击 20防御 30血量 40技能 */
    private Integer type;

    @Override
    public Serializable getId() {
        return this.getFuTuId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
