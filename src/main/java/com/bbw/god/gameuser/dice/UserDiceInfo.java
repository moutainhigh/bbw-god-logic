package com.bbw.god.gameuser.dice;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家体力信息
 *
 * @author suhq
 * @date 2021-05-17 15:33
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDiceInfo extends UserSingleObj implements Serializable {

    private static final long serialVersionUID = -7755677328469226662L;
    /** 体力购买纪录 */
    private Integer diceBuyTimes = 0;
    /** 体力最近一次购买时间 */
    private Date diceLastBuyTime;
    /** 体力最近体力增长时间 */
    private Date diceLastIncTime;

    public static UserDiceInfo getInstance(long uid, Date lastIncTime, Date lastBuyTime, int buyTimes) {
        UserDiceInfo userDiceInfo = new UserDiceInfo();
        userDiceInfo.setId(ID.INSTANCE.nextId());
        userDiceInfo.setGameUserId(uid);
        userDiceInfo.setDiceLastIncTime(lastIncTime);
        userDiceInfo.setDiceLastBuyTime(lastBuyTime);
        userDiceInfo.setDiceBuyTimes(buyTimes);
        return userDiceInfo;
    }

    /**
     * 添加体力购买次数
     */
    public void updateDiceBuyTimes(int diceBuyTimes) {
        this.diceBuyTimes = diceBuyTimes;
        diceLastBuyTime = DateUtil.now();
    }


    /**
     * 获得体力购买次数
     *
     * @return
     */
    public int gainDiceBuyNum() {
        if (getDiceLastBuyTime() == null || !DateUtil.isToday(getDiceLastBuyTime())) {
            return 0;
        }
        return getDiceBuyTimes();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_DICE_INFO;
    }
}
