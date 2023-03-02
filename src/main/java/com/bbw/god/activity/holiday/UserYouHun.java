package com.bbw.god.activity.holiday;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家野怪游魂信息
 *
 * @author lwb
 * @date 2020/8/27 17:53
 */
@Data
public class UserYouHun extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = -412014014149583557L;
    private List<Integer> invalidYouHun = new ArrayList<>();//无效位置的游魂
    private Date lastRestTime = new Date();

    public static UserYouHun instance(long uid) {
        UserYouHun userYouHun = new UserYouHun();
        userYouHun.setGameUserId(uid);
        userYouHun.setId(ID.INSTANCE.nextId());
        userYouHun.lastRestTime = DateUtil.getDateBegin(new Date());
        userYouHun.updateLastRestTime();
        return userYouHun;
    }

    public boolean updateSpecialYeGuai() {
        return DateUtil.getHourBetween(lastRestTime, new Date()) >= 2;
    }

    public void updateLastRestTime() {
        Date beginDate = DateUtil.getDateBegin(new Date());
        for (int i = 0; i < 12; i++) {
            Date d = DateUtil.addHours(beginDate, i * 2);
            if (DateUtil.millisecondsInterval(d, new Date()) > 0) {
                lastRestTime = DateUtil.addHours(beginDate, (i - 1) * 2);
                return;
            }
        }
        lastRestTime = DateUtil.addHours(beginDate, 22);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YOU_HUN;
    }
}
