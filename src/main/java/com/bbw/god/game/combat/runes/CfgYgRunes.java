package com.bbw.god.game.combat.runes;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author：lwb
 * @date: 2020/11/24 10:24
 * @version: 1.0
 */
@Data
public class CfgYgRunes implements CfgInterface {
    /**
     * 普通野怪符文集合
     */
    private List<String> normalRunes;
    /**
     * 精英野怪符文集合
     */
    private List<String> eliteRunes;

    @Override
    public Serializable getId() {
        return "唯一";
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
