package com.bbw.god.game.combat.runes;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 护身符配置
 * @author：lwb
 * @date: 2020/11/24 10:24
 * @version: 1.0
 */
@Data
public class CfgRunes implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 6075007863594413073L;
    private Integer id;
    private Integer runeType;
    private String name;

    @Override
    public int getSortId() {
        return this.getId();
    }
}
