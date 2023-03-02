package com.bbw.god.login.repairdata;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.notify.push.UserPush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_PUSH_DATE;

/**
 * @author suchaobin
 * @description 修复推送数据
 * @date 2020/7/7 15:12
 **/
@Service
public class RepairPushService implements BaseRepairDataService {
    @Autowired
    private RedisSetUtil<String> userDataTypeSetRedis;// 玩家资源数据类型集合，存放某一数据类型的ID集
    @Autowired
    private RedisValueUtil<UserData> redisValueUtil;
    @Autowired
    private RedisSetUtil<Long> redisSetUtil;
    @Autowired
    private GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        // 删除原先在redis中的推送值
        if (lastLoginDate.before(REPAIR_PUSH_DATE)) {
            reInitUserPush(gu.getId());
        }
//        List<UserPush> userPushes = gameUserService.getMultiItems(gu.getId(), UserPush.class);
//        if (userPushes.size() > 1) {
//            List<UserPush> userPushesToDel = userPushes.subList(0, userPushes.size() - 1);
//            LogUtil.logDeletedUserDatas(userPushesToDel, "删除多余的数据");
//            gameUserService.deleteItems(gu.getId(), userPushesToDel);
//        }
    }

    /**
     * 重新在redis中初始化UserPush
     *
     * @param guId
     */
    public void reInitUserPush(long guId) {
        String key = "usr:" + guId + ":push";
        String typeKey = UserRedisKey.getDataTypeKey(guId, UserDataType.USER_PUSH);
        Set<String> typeKeys = userDataTypeSetRedis.members(typeKey);
        redisSetUtil.delete(key);
        redisValueUtil.delete(typeKeys);
        UserPush userPush = new UserPush(guId);
        gameUserService.addItem(guId, userPush);
    }
}
