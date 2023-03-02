package com.bbw.god.login.repairdata;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;
import static com.bbw.god.login.repairdata.RepairDataConst.CLEAN_USER_HOLIDAY_LOTTERY;

/**
 * @author suchaobin
 * @description 清除UserHolidayLottery数据
 * @date 2020/9/28 10:24
 **/
@Service
public class CleanUserHolidayLotteryService implements BaseRepairDataService {
    @Autowired
    private RedisSetUtil<String> redisSetUtil;
    @Autowired
    private RedisValueUtil<String> redisValueUtil;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(CLEAN_USER_HOLIDAY_LOTTERY)) {
            String key = "usr" + SPLIT + gu.getId() + SPLIT + "holidayLottery";
            Set<String> members = redisSetUtil.members(key);
            for (String member : members) {
                redisValueUtil.delete(member);
            }
            redisSetUtil.delete(key);
        }
    }
}
