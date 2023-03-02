package com.bbw.god.notify.push;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家推送
 * @date 2019/12/20 15:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserPush extends UserSingleObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Integer FIVE_MINUTES = 5;

    private List<Integer> ablePushList;
    private Date LastPushFriendMonsterTime;// 上次推送友怪功能的时间

    public UserPush(long uid, List<Integer> ablePushList) {
        this.id = ID.INSTANCE.nextId();
        this.gameUserId = uid;
        this.ablePushList = ablePushList;
    }

    /**
     * 初始化默认全部设置成可推送状态
     */
    public UserPush(Long uid) {
        List<Integer> ablePushList = new ArrayList<>();
        PushEnum[] values = PushEnum.values();
        for (PushEnum pushEnum : values) {
            ablePushList.add(pushEnum.getValue());
        }
        this.ablePushList = ablePushList;
        this.id = ID.INSTANCE.nextId();
        this.gameUserId = uid;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_PUSH;
    }

    public boolean ableToPush(PushEnum pushEnum) {
        // 未开启对应的推送功能，返回false
        if (!ablePushList.contains(pushEnum.getValue())) {
            return false;
        }
        switch (pushEnum) {
            case FRIEND_MONSTER:
                // 与上次推送事件间隔不超过5分钟的，返回false
                if (LastPushFriendMonsterTime != null) {
                    long minutesBetween = DateUtil.getMinutesBetween(LastPushFriendMonsterTime, DateUtil.now());
                    if (minutesBetween < FIVE_MINUTES) {
                        return false;
                    }
                }
                return true;
            default:
                return true;
        }
    }
}
