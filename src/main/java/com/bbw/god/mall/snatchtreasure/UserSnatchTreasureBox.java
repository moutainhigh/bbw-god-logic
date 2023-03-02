package com.bbw.god.mall.snatchtreasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家夺宝开箱
 * @date 2020/6/30 9:51
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSnatchTreasureBox extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 6491505092543840853L;
    /** 已完成未领取 */
    private List<Integer> accomplishedIds = new ArrayList<>();
    /** 已领取 */
    private List<Integer> awardedIds = new ArrayList<>();
    /** 最近一次重置的时间 */
    private Date lastRestDate;

    public static UserSnatchTreasureBox getInstance(long uid) {
        UserSnatchTreasureBox box = new UserSnatchTreasureBox();
        box.setGameUserId(uid);
        box.setId(ID.INSTANCE.nextId());
        box.setLastRestDate(DateUtil.now());
        return box;
    }

    public void openBox(int boxId) {
        this.accomplishedIds.remove((Integer) boxId);
        if (!this.awardedIds.contains(boxId)) {
            this.awardedIds.add(boxId);
        }
    }

    public void accomplish(int boxId) {
        // 已领取
        if (this.awardedIds.contains(boxId)) {
            return;
        }
        if (!this.accomplishedIds.contains(boxId)) {
            this.accomplishedIds.add(boxId);
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_SNATCH_TREASURE_BOX;
    }
}