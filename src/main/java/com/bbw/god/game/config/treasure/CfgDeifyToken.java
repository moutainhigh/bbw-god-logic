package com.bbw.god.game.config.treasure;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 封神令配置
 * @author lwb
 * @date 2020/6/29 9:55
 */
@Data
public class CfgDeifyToken implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id; //
    private String name; //
    private Integer cardId;//卡牌Id
    private Integer deifyCardId;//封神后的卡牌ID
    @Override
    public int getSortId() {
        return this.getId();
    }
}
