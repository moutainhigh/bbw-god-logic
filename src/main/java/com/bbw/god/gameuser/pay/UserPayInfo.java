package com.bbw.god.gameuser.pay;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户充值信息
 *
 * @author suhq
 * @date 2021-05-17 16:53
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPayInfo extends UserSingleObj implements Serializable {
    private static Date FOREVER_DATE = DateUtil.fromDateTimeString("2099-01-01 00:00:00");
    private static final long serialVersionUID = 1L;
    /** 是否首充(直冲产品不算首充) */
    private boolean isFirstBought = false;
    /** 月卡结束时间 */
    private Date ykEndTime;
    /** 月卡奖励领取时间。 */
    private Date ykAwardTime;
    /** 季卡结束时间 */
    private Date jkEndTime;
    /** 季卡奖励领取时间 */
    private Date jkAwardTime;
    /** 速战卡购买时间 */
    private Date endFightBuyTime;

    public static UserPayInfo getInstance(GameUser gu) {
        UserPayInfo userPayInfo = new UserPayInfo();
        userPayInfo.setId(ID.INSTANCE.nextId());
        userPayInfo.setGameUserId(gu.getId());
        userPayInfo.setFirstBought(gu.getStatus().isFirstBought());
        userPayInfo.setYkEndTime(gu.getStatus().getYkEndTime());
        userPayInfo.setYkAwardTime(gu.getStatus().getYkAwardTime());
        userPayInfo.setJkEndTime(gu.getStatus().getJkEndTime());
        userPayInfo.setJkAwardTime(gu.getStatus().getJkAwardTime());
        userPayInfo.setEndFightBuyTime(gu.getStatus().getEndFightBuyTime());
        return userPayInfo;
    }

    /**
     * 是否有永久季卡:既到期时间为2099-01-01 00:00:00
     *
     * @return
     */
    public boolean hadForeverJiKa() {
        return jkEndTime != null && DateUtil.getYear(jkEndTime) == 2099;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_PAY_INFO;
    }
}
