package com.bbw.god.gameuser.leadercard.beast;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 神兽业务服务
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Service
public class UserLeaderBeastService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获得玩家携带的神兽的技能列表
     *
     * @param uid
     * @return
     */
    public List<Integer> getSkills(long uid) {
        UserLeaderBeasts userLeaderBeasts = gameUserService.getSingleItem(uid, UserLeaderBeasts.class);
        if (null == userLeaderBeasts) {
            return new ArrayList<>();
        }
        return userLeaderBeasts.gainSkills();
    }

    /**
     * 获得已装备的ID。0位：飞天仙兽 1位：迅捷灵兽
     *
     * @param uid
     * @return
     */
    public int[] getTakedBeasts(long uid) {
        UserLeaderBeasts userLeaderBeasts = gameUserService.getSingleItem(uid, UserLeaderBeasts.class);
        if (null == userLeaderBeasts) {
            return new int[]{0, 0};
        }
        if (userLeaderBeasts.getBeasts() == null) {
            return new int[]{0, 0};
        }
        return userLeaderBeasts.getBeasts();
    }
}
