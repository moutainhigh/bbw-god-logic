package com.bbw.god.game.zxz.entity;

import com.bbw.common.ID;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家诛仙阵难度数据
 * @author: hzf
 * @create: 2022-09-17 09:28
 **/
@Data
public class UserZxzInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 难度数据 */
    private List<UserZxzDifficulty> difficultyInfo;
    /** 进入的难度 */
    private Integer enterDifficulty;
    /** 第一次通关 */
    private Boolean firstClearance;

    /**
     * 获取难度信息
     *
     * @param difficulty
     * @return
     */
    public UserZxzDifficulty gainUserZxzLevel(Integer difficulty) {
        return difficultyInfo.stream()
                .filter(uLevel -> uLevel.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }

    /**
     * 更新进入的区域
     * @param difficulty
     * @param enterRegion
     * @return
     */
    public void updateEnterRegion(Integer difficulty,Integer enterRegion){
        UserZxzDifficulty userLevel = getDifficultyInfo().stream()
                .filter(uZxz -> uZxz.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
        userLevel.setEnterRegion(enterRegion);
    }
    /**
     * 解锁下个难度
     *
     * @param difficulty
     * @return
     */
    public void openNextDifficulty(Integer difficulty){
        UserZxzDifficulty userLevel = getDifficultyInfo().stream()
                .filter(uZxz -> uZxz.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
        userLevel.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
    }


    public static UserZxzInfo getInstance(List<UserZxzDifficulty> levelInfo, long uId) {
        UserZxzInfo userZxzInfo = new UserZxzInfo();
        userZxzInfo.setId(ID.INSTANCE.nextId());
        userZxzInfo.setGameUserId(uId);
        userZxzInfo.setDifficultyInfo(levelInfo);
        userZxzInfo.setFirstClearance(true);
        return userZxzInfo;
    }


    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ZXZ;
    }
}
