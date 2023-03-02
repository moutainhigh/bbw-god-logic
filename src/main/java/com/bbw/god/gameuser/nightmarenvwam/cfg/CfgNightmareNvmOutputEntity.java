package com.bbw.god.gameuser.nightmarenvwam.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 梦魇女娲庙产出道具配置
 *
 * @author fzj
 * @date 2022/5/5 14:22
 */
@Data
public class CfgNightmareNvmOutputEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 道具Id */
    private Integer treasureId;
    /** 对应卡id */
    private Integer cardId;
    /** 道具类型 10神格牌 20神将羁绊道具 30技能衍生残页 */
    private Integer type;

    @Override
    public Serializable getId() {
        return this.getTreasureId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
