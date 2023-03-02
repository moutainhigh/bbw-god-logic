package com.bbw.god.gameuser.card;

import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.random.config.RandomStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 用户保底策略的卡牌记录
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-05 15:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserCardRandom extends UserData implements Serializable {
    private static final long serialVersionUID = -6194184608027601728L;
    private String strategyKey;//卡牌策略名

    private HashMap<String, Integer> selectorLoopTimes;//记录策略保底
    private RandomStrategy strategy;

    public static UserCardRandom instance(long uid, String strategyKey) {
        UserCardRandom userCardRandom = new UserCardRandom();
        userCardRandom.setId(UserRedisKey.getNewUserDataId());
        userCardRandom.setGameUserId(uid);
        userCardRandom.setStrategyKey(strategyKey);
        return userCardRandom;
    }

    /**
     * 设置策略保底进度
     *
     * @param randomStrategy
     */
    public void updateLoopTimes(RandomStrategy randomStrategy) {
        selectorLoopTimes = new HashMap<>();
        strategy = null;
        randomStrategy.getSelectors().forEach(tmp -> {
            String selectorKey = tmp.getKey();
            Integer loopTimes = tmp.getProbability().getLoopTimes();
            selectorLoopTimes.put(selectorKey, loopTimes);
        });
    }

    /**
     * 获取保底进度
     *
     * @param selectorKey
     * @return
     */
    public Integer gainLoopTimes(String selectorKey) {
        if (selectorLoopTimes == null) {
            return null;
        }
        return selectorLoopTimes.get(selectorKey);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CARD_RANDOM;
    }

}
