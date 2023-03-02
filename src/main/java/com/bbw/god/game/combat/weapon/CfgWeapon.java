package com.bbw.god.game.combat.weapon;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 战斗法宝配置信息
 * @author：lwb
 * @date: 2020/11/24 17:14
 * @version: 1.0
 */
@Data
public class CfgWeapon  implements CfgEntityInterface, Serializable {
    private Integer id;
    private String name;
    //一场战斗可用次数
    private Integer totalTimes=1000;
    //每回合可用次数
    private Integer roundTimes=1000;

    @Override
    public int getSortId() {
        return getId();
    }
}
