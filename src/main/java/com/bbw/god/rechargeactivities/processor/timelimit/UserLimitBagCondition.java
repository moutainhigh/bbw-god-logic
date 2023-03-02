package com.bbw.god.rechargeactivities.processor.timelimit;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 触发限时礼包的条件统计
 *
 * @author suhq
 * @date 2021/7/1 下午5:39
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserLimitBagCondition extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = -51807849281599L;
    private Integer cardStar5Num = 0;
    private Integer cardStar4Num = 0;
    /** 秘传解锁次数 */
    private Integer sbUnlockNum = 0;
    private Map<String, Integer> treasureConsume;

    public static UserLimitBagCondition instance(long uid) {
        UserLimitBagCondition instance = new UserLimitBagCondition();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        return instance;
    }

    /**
     * 添加卡牌统计
     *
     * @param star
     * @param num
     */
    public void addCardNum(int star, int num) {
        if (star == 4) {
            cardStar4Num += num;
        } else if (star == 5) {
            cardStar5Num += num;
        }
    }

    /**
     * 添加秘传解锁次数
     */
    public void addSecretBiographyNum() {
        sbUnlockNum++;
    }

    /**
     * 添加法宝统计
     *
     * @param treasureId
     * @param num
     */
    public void addTreasureNum(int treasureId, int num) {
        if (null == treasureConsume) {
            treasureConsume = new HashMap<>();
        }
        String key = treasureId + "";
        if (treasureConsume.containsKey(key)) {
            treasureConsume.put(key, treasureConsume.get(key) + num);
        } else {
            treasureConsume.put(key, num);
        }
    }

    /**
     * 获取消耗数量
     *
     * @param treasureId
     * @return
     */
    public int gainConsumeNum(int treasureId) {
        return treasureConsume.get(treasureId + "");
    }

    /**
     * 重置道具消耗数量
     *
     * @param treasureId
     */
    public void resetConsumeNum(int treasureId) {
        treasureConsume.put(treasureId + "", 0);
    }


    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_LIMIT_BAG_CONDITION;
    }

}
