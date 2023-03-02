package com.bbw.god.notify.rednotice;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家攻城红点（三倍返利）
 * @date 2020/11/13 17:15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAttackCityRedNotice extends UserSingleObj implements Serializable {
    private List<Integer> noticeLevel1 = new ArrayList<>();
    private List<Integer> noticeLevel2 = new ArrayList<>();
    private List<Integer> noticeLevel3 = new ArrayList<>();
    private List<Integer> noticeLevel4 = new ArrayList<>();
    private List<Integer> noticeLevel5 = new ArrayList<>();

    public static UserAttackCityRedNotice getInstance(long uid) {
        UserAttackCityRedNotice notice = new UserAttackCityRedNotice();
        notice.setId(ID.INSTANCE.nextId());
        notice.setGameUserId(uid);
        return notice;
    }

    public boolean ifNotice(int cityLevel, int activityId) {
        List<Integer> noticeActivityIds;
        switch (cityLevel) {
            case 1:
                noticeActivityIds = noticeLevel1;
                break;
            case 2:
                noticeActivityIds = noticeLevel2;
                break;
            case 3:
                noticeActivityIds = noticeLevel3;
                break;
            case 4:
                noticeActivityIds = noticeLevel4;
                break;
            case 5:
                noticeActivityIds = noticeLevel5;
                break;
            default:
                return false;
        }
        return noticeActivityIds.contains(activityId);
    }

    public void notice(int activityId, int cityLevel) {
        switch (cityLevel) {
            case 1:
                this.noticeLevel1.add(activityId);
                break;
            case 2:
                this.noticeLevel2.add(activityId);
                break;
            case 3:
                this.noticeLevel3.add(activityId);
                break;
            case 4:
                this.noticeLevel4.add(activityId);
                break;
            case 5:
                this.noticeLevel5.add(activityId);
                break;
        }
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ATTACK_CITY_RED_NOTICE;
    }
}
