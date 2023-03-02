package com.bbw.god.game.config.card;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 特殊封神卡牌
 *
 * @author fzj
 * @date 2022/5/11 14:17
 */
@Data
public class CfgSpecialGodCardEntity  implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 卡牌Id */
    private Integer cardId;
    /** 名称 */
    private String name;
    /** 星级 */
    private Integer star;

    @Override
    public Serializable getId() {
        return this.getCardId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
