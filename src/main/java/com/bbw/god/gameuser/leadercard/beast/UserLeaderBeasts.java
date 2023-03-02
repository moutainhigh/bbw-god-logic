package com.bbw.god.gameuser.leadercard.beast;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 装配的神兽
 *
 * @author suhq
 * @date 2021-03-26 15:57
 **/
@Data
public class UserLeaderBeasts extends UserSingleObj {
    /** 装配的神兽 0位：飞天仙兽 1位：迅捷灵兽 */
    private int[] beasts;
    private String[] unactiveSkills = new String[]{"", ""};

    public static UserLeaderBeasts getInstance(long uid, int beastId) {
        UserLeaderBeasts instance = new UserLeaderBeasts();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.take(beastId);
        return instance;
    }

    /**
     * 获取装配的所有神兽的所有技能
     *
     * @return
     */
    public List<Integer> gainSkills() {
        List<Integer> skills = new ArrayList<>();
        if (beasts == null || beasts.length == 0) {
            return skills;
        }
        for (int i = 0; i < beasts.length; i++) {
            if (beasts[i] > 0) {
                List<Integer> cfgSkills = BeastTool.getSkills(beasts[i]);
                cfgSkills.removeAll(gainUnactiveSkills(beasts[i]));
                if (ListUtil.isNotEmpty(cfgSkills)) {
                    skills.addAll(cfgSkills);
                }
            }
        }
        return skills;
    }

    /**
     * 装备神兽
     *
     * @param beastId
     */
    public void take(int beastId) {
        if (beasts == null || beasts.length == 0) {
            beasts = new int[]{0, 0};
        }
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(beastId);
        if (treasure.getType() == TreasureType.BEAST_FTXS.getValue()) {
            beasts[0] = beastId;
            unactiveSkills[0] = "";
        } else if (treasure.getType() == TreasureType.BEAST_XJLS.getValue()) {
            beasts[1] = beastId;
            unactiveSkills[1] = "";
        }
    }

    /**
     * 卸下神兽
     *
     * @param beastId
     */
    public void takeOff(int beastId) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(beastId);
        if (treasure.getType() == TreasureType.BEAST_FTXS.getValue()) {
            beasts[0] = 0;
            unactiveSkills[0] = "";
        } else if (treasure.getType() == TreasureType.BEAST_XJLS.getValue()) {
            beasts[1] = 0;
            unactiveSkills[1] = "";
        }
    }

    /**
     * 禁用技能
     *
     * @param beastId
     * @param skillId
     */
    public void unactiveSkill(int beastId, int skillId) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(beastId);
        if (treasure.getType() == TreasureType.BEAST_FTXS.getValue() && !unactiveSkills[0].contains(skillId + "")) {
            if (StrUtil.isNotBlank(unactiveSkills[0])) {
                unactiveSkills[0] += ",";
            }
            unactiveSkills[0] += skillId;
        } else if (treasure.getType() == TreasureType.BEAST_XJLS.getValue() && !unactiveSkills[1].contains(skillId + "")) {
            if (StrUtil.isNotBlank(unactiveSkills[1])) {
                unactiveSkills[1] += ",";
            }
            unactiveSkills[1] += skillId;
        }
    }

    /**
     * 激活技能
     *
     * @param beastId
     * @param skillId
     */
    public void activeSkill(int beastId, int skillId) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(beastId);
        if (treasure.getType() == TreasureType.BEAST_FTXS.getValue()) {
            unactiveSkills[0] = removeSkill(unactiveSkills[0], skillId);
        } else if (treasure.getType() == TreasureType.BEAST_XJLS.getValue()) {
            unactiveSkills[1] = removeSkill(unactiveSkills[1], skillId);
        }
    }

    /**
     * 获取未激活的技能
     *
     * @param beastId
     * @return
     */
    public List<Integer> gainUnactiveSkills(int beastId) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(beastId);
        if (treasure.getType() == TreasureType.BEAST_FTXS.getValue()) {
            return ListUtil.parseStrToInts(unactiveSkills[0]);
        } else if (treasure.getType() == TreasureType.BEAST_XJLS.getValue()) {
            return ListUtil.parseStrToInts(unactiveSkills[1]);
        }
        return new ArrayList<>();
    }

    /**
     * 是否已装备该神兽
     *
     * @param beastId
     * @return
     */
    public boolean ifTaked(int beastId) {
        if (beasts == null || beasts.length == 0) {
            return false;
        }
        for (int i = 0; i < beasts.length; i++) {
            if (beasts[i] == beastId) {
                return true;
            }
        }
        return false;
    }

    private String removeSkill(String unactiveSkills, int skillId) {
        String result = unactiveSkills.replace(skillId + "", "");
        if (result.length() == 1) {
            result = "";
        }
        int start = 0;
        int end = result.length();
        if (result.startsWith(",")) {
            start = 1;
        }
        if (result.endsWith(",")) {
            end -= 1;
        }
        result = result.substring(start, end);
        return result;
    }

    @Override

    public UserDataType gainResType() {
        return UserDataType.USER_LEADER_BEASTS;
    }
}
