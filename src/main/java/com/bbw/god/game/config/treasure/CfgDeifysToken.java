package com.bbw.god.game.config.treasure;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 群体封神令配置
 *
 * @author: huanghb
 * @date: 2022/11/17 11:52
 */
@Data
public class CfgDeifysToken implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    /** 卡牌Id集合 */
    private List<Integer> cardIds;
    /** 封神后的卡牌ID集合 */
    private List<Integer> deifyCardIds;

    @Override
    public int getSortId() {
        return this.getId();
    }
}
