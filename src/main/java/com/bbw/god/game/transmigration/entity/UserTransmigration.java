package com.bbw.god.game.transmigration.entity;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轮回城池信息
 *
 * @author: suhq
 * @date: 2021/9/10 4:21 下午
 */
@Data
@ToString(callSuper = true)
public class UserTransmigration extends UserSingleObj {
    @Deprecated
    private Map<Integer, Integer> cityScores;
    private Map<String, Integer> allCityScores;
    private List<Integer> awardedTargets = new ArrayList<>();

    public static UserTransmigration getInstance(long uid) {
        UserTransmigration userTransmigration = new UserTransmigration();
        userTransmigration.setId(ID.INSTANCE.nextId());
        userTransmigration.setGameUserId(uid);
        return userTransmigration;
    }

    /**
     * 将旧版格式的评分置null
     *
     * @return
     */
    public Map<Integer, Integer> getCityScores() {
        return null;
    }

    /**
     * 更新城池的分数
     *
     * @param cityId
     * @param score
     */
    public void updateScore(int cityId, int score) {
        if (null == allCityScores) {
            allCityScores = new HashMap<>();
        }
        allCityScores.put(cityId + "", score);
    }

    /**
     * 获取总分
     *
     * @return
     */
    public int gainTotalScore() {
        if (null == allCityScores) {
            return 0;
        }
        int totalScore = 0;
        for (Integer score : allCityScores.values()) {
            totalScore += score;
        }
        return totalScore;
    }

    /**
     * 获取成功挑战的数量
     *
     * @return
     */
    public int gainSuccessNum() {
        if (null == allCityScores) {
            return 0;
        }
        return allCityScores.keySet().size();
    }

    /**
     * 重置。新的一轮开始后，玩家第一次请求轮回主页信息时重置
     */
    public void reset() {
        allCityScores = null;
        awardedTargets = new ArrayList<>();
    }

    /**
     * 添加已领取的目标ID
     *
     * @param targetId
     */
    public void addAwardedTarget(int targetId) {
        if (awardedTargets.contains(targetId)) {
            return;
        }
        awardedTargets.add(targetId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_TRANSMIGRATION;
    }

}
