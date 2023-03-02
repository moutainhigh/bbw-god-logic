package com.bbw.god.gameuser.leadercard.beast;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 神兽技能配置
 *
 * @author suhq
 * @date 2021-03-26 13:47
 **/
@Data
public class CfgBeastSkillEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer beastId;
    private String name;
    private List<Integer> skills;

    @Override
    public Serializable getId() {
        return beastId;
    }

    @Override
    public int getSortId() {
        return beastId;
    }
}
