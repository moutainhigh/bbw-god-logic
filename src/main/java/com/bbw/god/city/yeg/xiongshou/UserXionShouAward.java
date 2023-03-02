package com.bbw.god.city.yeg.xiongshou;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月10日 上午10:36:30
 * 类说明
 */
@Data
public class UserXionShouAward extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer openBoxTimes = 0;// 累计开宝箱次数
    private boolean hitAward = false;// 是否命中唯一奖励
    private long lastUpdateTime = DateUtil.toDateTimeLong();// 上次更新时间

    public boolean randomHit() {
        if (DateUtil.toDateInt(DateUtil.fromDateLong(this.lastUpdateTime)) < DateUtil.getTodayInt()) {
            this.openBoxTimes = 0;
            this.hitAward = false;
            this.lastUpdateTime = DateUtil.toDateTimeLong();
        }
        if (hitAward) {
            return false;
        }
        openBoxTimes++;
        // 5~10次必中一次 概率分别为1/6,1/5,1/4,1/3,1/2,1;第10次的命中率为100%
        //int seed = 11 - openBoxTimes;
        //int hitNum = PowerRandom.getRandomBySeed(seed);
        //hitAward = hitNum == 1;
        int random = PowerRandom.getRandomBySeed(100);
        hitAward = random == 1;
        return hitAward;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.Xiong_Shou_Award;
    }

}
