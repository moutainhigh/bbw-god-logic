package com.bbw.god.gameuser.biyoupalace.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 碧游宫专属技能产出
 *
 * @author fzj
 * @date 2022/5/16 15:07
 */
@Data
public class CfgBYPalaceExclusiveSkillEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = -6331499741170594976L;
    /** 技能ID */
    private Integer skillId;

    @Override
    public Serializable getId() {
        return this.getSkillId();
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
