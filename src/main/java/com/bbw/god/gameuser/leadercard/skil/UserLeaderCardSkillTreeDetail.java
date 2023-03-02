package com.bbw.god.gameuser.leadercard.skil;

import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 技能树消耗明细
 */
@Data
public class UserLeaderCardSkillTreeDetail extends UserSingleObj {

    private List<Integer> scrolls=new ArrayList<>();


    public boolean ifUseScroll(int scrollId){
        return scrolls.contains(scrollId);
    }

    public void addUseScroll(int scrollId){
        scrolls.add(scrollId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_LEADER_CARD_SKILL_TREE_DETAIL;
    }
}
