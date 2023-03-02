package com.bbw.god.activity.holiday.processor.holidaybrocadegift;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家锦礼活动数据
 *
 * @author fzj
 * @date 2022/2/9 13:57
 */
@Data
public class UserBrocadeGift implements Serializable {
    private static final long serialVersionUID = -412014014149583557L;
    /** 投注id */
    private Integer betId;
    /** 投注次数 */
    private Integer betTimes;
    /** 投注号码 */
    private List<String> betNums = new ArrayList<>();

    public final static UserBrocadeGift getInstance(int giftsId) {
        UserBrocadeGift userBrocadeGift = new UserBrocadeGift();
        userBrocadeGift.setBetId(giftsId);
        userBrocadeGift.setBetTimes(0);
        return userBrocadeGift;
    }
}
