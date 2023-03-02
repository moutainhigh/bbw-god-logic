package com.bbw.god.gameuser.leadercard.beast;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * 神兽工具类
 *
 * @author suhq
 * @date 2021-03-26 14:34
 **/
public class BeastTool {

    /**
     * 获得神兽技能
     *
     * @param beast
     * @return
     */
    public static List<Integer> getSkills(int beast) {
        CfgBeastSkillEntity cfg = Cfg.I.get(beast, CfgBeastSkillEntity.class);
        return CloneUtil.cloneList(cfg.getSkills());
    }

}
