package com.bbw.god.gameuser.businessgang.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 礼物基础数据配置
 *
 * @author fzj
 * @date 2022/1/17 10:12
 */
@Data
public class CfgGiftEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 礼物id */
    private Integer giftId;
    /** 礼物类型 */
    private Integer type;
    /** 级别 */
    private Integer grade;
    /** 可提供好感度 */
    private Integer favorability;

    @Override
    public Serializable getId() {
        return this.getGiftId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
