package com.bbw.god.activity.processor.rechargesign;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值签到奖励领取记录
 *
 * @author: suhq
 * @date: 2021/8/3 10:48 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserRechargeSignRecord extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Award> awarded;

    public static UserRechargeSignRecord instance(long uid, Award award) {
        UserRechargeSignRecord instance = new UserRechargeSignRecord();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.addAward(award);
        return instance;
    }

    public void addAward(Award award) {
        if (ListUtil.isEmpty(awarded)) {
            awarded = new ArrayList<>();
        }
        awarded.add(award);
    }

    public boolean isAwarded(Award award) {
        if (ListUtil.isEmpty(awarded)) {
            return false;
        }
        return awarded.stream().anyMatch(tmp -> tmp.getItem() == award.getItem() && tmp.gainAwardId() == award.gainAwardId());
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.RECHARGE_SIGN;
    }
}
